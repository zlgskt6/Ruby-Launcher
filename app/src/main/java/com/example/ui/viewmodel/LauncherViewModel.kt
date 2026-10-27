package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LauncherRepository
import com.example.model.CardSize
import com.example.model.KnownService
import com.example.model.LauncherCardItem
import com.example.model.LauncherTab
import com.example.model.LauncherThemeOption
import com.example.model.WatchProgressItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LauncherUiState(
    val currentTab: LauncherTab = LauncherTab.HOME,
    val homeCards: List<LauncherCardItem> = emptyList(),
    val allInstalledApps: List<LauncherCardItem> = emptyList(),
    val continueWatchingList: List<WatchProgressItem> = emptyList(),
    val selectedWatchItem: WatchProgressItem? = null,
    val focusedAppIndex: Int = 0,
    val focusedWatchIndex: Int = 0,
    val focusedRow: Int = 1, // 0: Tabs, 1: Hero/Continue Watching, 2: Your apps
    val theme: LauncherThemeOption = LauncherThemeOption.CINEMATIC_DARK,
    val cardSize: CardSize = CardSize.STANDARD,
    val is24hClock: Boolean = true,
    val showDate: Boolean = true,
    val showUninstalledStreaming: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val isAllAppsOpen: Boolean = false,
    val isSubscriptionManagerOpen: Boolean = false,
    val uninstalledAppToPrompt: LauncherCardItem? = null,
    val isLoading: Boolean = true
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LauncherRepository(application)

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
        loadData()
    }

    private fun loadPreferences() {
        _uiState.update {
            it.copy(
                theme = repository.getTheme(),
                cardSize = repository.getCardSize(),
                is24hClock = repository.is24HourClock(),
                showDate = repository.isShowDate(),
                showUninstalledStreaming = repository.isShowUninstalledStreaming()
            )
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val installed = repository.getInstalledApps()
            val home = repository.getHomeCards()
            val queue = repository.getContinueWatchingQueue(installed)

            val effectiveHome = if (_uiState.value.showUninstalledStreaming) {
                home
            } else {
                home.filter { it.isInstalled }
            }

            _uiState.update {
                it.copy(
                    homeCards = effectiveHome,
                    allInstalledApps = installed,
                    continueWatchingList = queue,
                    selectedWatchItem = queue.firstOrNull(),
                    focusedAppIndex = it.focusedAppIndex.coerceIn(0, (effectiveHome.size - 1).coerceAtLeast(0)),
                    isLoading = false
                )
            }
        }
    }

    fun selectTab(tab: LauncherTab) {
        _uiState.update { it.copy(currentTab = tab) }
        if (tab == LauncherTab.APPS) {
            setAllAppsOpen(true)
        }
    }

    fun onAppFocused(index: Int) {
        if (index in _uiState.value.homeCards.indices) {
            val app = _uiState.value.homeCards[index]
            // If the app matches a continue watching item, highlight that in the hero spotlight
            val matchingWatch = _uiState.value.continueWatchingList.firstOrNull { it.service == app.knownService }
            _uiState.update {
                it.copy(
                    focusedAppIndex = index,
                    focusedRow = 2,
                    selectedWatchItem = matchingWatch ?: it.selectedWatchItem
                )
            }
        }
    }

    fun onWatchItemFocused(index: Int) {
        if (index in _uiState.value.continueWatchingList.indices) {
            val watchItem = _uiState.value.continueWatchingList[index]
            _uiState.update {
                it.copy(
                    focusedWatchIndex = index,
                    focusedRow = 1,
                    selectedWatchItem = watchItem
                )
            }
        }
    }

    fun navigateHorizontal(direction: Int) {
        val state = _uiState.value
        when (state.focusedRow) {
            1 -> { // Continue watching row
                val next = (state.focusedWatchIndex + direction).coerceIn(0, state.continueWatchingList.size - 1)
                onWatchItemFocused(next)
            }
            2 -> { // Your apps row
                val next = (state.focusedAppIndex + direction).coerceIn(0, state.homeCards.size - 1)
                onAppFocused(next)
            }
        }
    }

    fun navigateVertical(direction: Int) {
        val state = _uiState.value
        val nextRow = (state.focusedRow + direction).coerceIn(0, 2)
        _uiState.update { it.copy(focusedRow = nextRow) }
    }

    fun moveCard(fromIndex: Int, toIndex: Int) {
        val currentCards = _uiState.value.homeCards.toMutableList()
        if (fromIndex in currentCards.indices && toIndex in currentCards.indices) {
            val item = currentCards.removeAt(fromIndex)
            currentCards.add(toIndex, item)
            _uiState.update {
                it.copy(
                    homeCards = currentCards,
                    focusedAppIndex = toIndex
                )
            }
            viewModelScope.launch {
                repository.saveHomeCards(currentCards)
            }
        }
    }

    fun addAppToHome(item: LauncherCardItem) {
        val current = _uiState.value.homeCards.toMutableList()
        if (current.none { it.id == item.id || (it.packageName != null && it.packageName == item.packageName) }) {
            current.add(item)
            _uiState.update { it.copy(homeCards = current) }
            viewModelScope.launch {
                repository.saveHomeCards(current)
            }
            Toast.makeText(getApplication(), "${item.title} added to Your Apps", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeCardFromHome(item: LauncherCardItem) {
        val current = _uiState.value.homeCards.toMutableList()
        current.removeAll { it.id == item.id }
        _uiState.update {
            it.copy(
                homeCards = current,
                focusedAppIndex = it.focusedAppIndex.coerceIn(0, (current.size - 1).coerceAtLeast(0))
            )
        }
        viewModelScope.launch {
            repository.saveHomeCards(current)
        }
        Toast.makeText(getApplication(), "${item.title} removed", Toast.LENGTH_SHORT).show()
    }

    fun toggleServiceSubscription(service: KnownService, isSubscribed: Boolean) {
        val current = _uiState.value.homeCards.toMutableList()
        if (isSubscribed) {
            if (current.none { it.knownService == service }) {
                val installed = _uiState.value.allInstalledApps.firstOrNull {
                    service.possiblePackages.contains(it.packageName)
                }
                current.add(
                    LauncherCardItem(
                        id = "service_${service.id}",
                        title = service.displayName,
                        packageName = installed?.packageName ?: service.possiblePackages.first(),
                        knownService = service,
                        isInstalled = installed != null,
                        isPinned = true
                    )
                )
            }
        } else {
            current.removeAll { it.knownService == service }
        }
        _uiState.update {
            it.copy(
                homeCards = current,
                focusedAppIndex = it.focusedAppIndex.coerceIn(0, (current.size - 1).coerceAtLeast(0))
            )
        }
        viewModelScope.launch {
            repository.saveHomeCards(current)
        }
    }

    fun launchCard(card: LauncherCardItem) {
        val context = getApplication<Application>()
        val pm = context.packageManager

        var launchIntent: Intent? = null
        if (!card.packageName.isNullOrEmpty()) {
            launchIntent = pm.getLaunchIntentForPackage(card.packageName)
            if (launchIntent == null && card.knownService != null) {
                for (alt in card.knownService.possiblePackages) {
                    launchIntent = pm.getLaunchIntentForPackage(alt)
                    if (launchIntent != null) break
                }
            }
        }

        if (launchIntent != null) {
            try {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Could not open ${card.title}: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            _uiState.update { it.copy(uninstalledAppToPrompt = card) }
        }
    }

    fun launchWatchItem(item: WatchProgressItem) {
        val matchingApp = _uiState.value.homeCards.firstOrNull { it.knownService == item.service }
        if (matchingApp != null) {
            launchCard(matchingApp)
        } else {
            openWebStreaming(item.service)
        }
    }

    fun dismissAppPrompt() {
        _uiState.update { it.copy(uninstalledAppToPrompt = null) }
    }

    fun openPlayStore(packageName: String?) {
        if (packageName.isNullOrEmpty()) return
        val context = getApplication<Application>()
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=$packageName"))
            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try { context.startActivity(fallbackIntent) } catch (_: Exception) { }
        }
        dismissAppPrompt()
    }

    fun openWebStreaming(service: KnownService?) {
        val url = when (service) {
            KnownService.JELLYFIN -> "https://app.jellyfin.org"
            KnownService.YOUTUBE -> "https://www.youtube.com/tv"
            KnownService.NETFLIX -> "https://www.netflix.com"
            KnownService.SVT_PLAY -> "https://www.svtplay.se"
            KnownService.PLEX -> "https://app.plex.tv"
            KnownService.PRIME_VIDEO -> "https://www.primevideo.com"
            KnownService.DISNEY_PLUS -> "https://www.disneyplus.com"
            KnownService.SPOTIFY -> "https://open.spotify.com"
            KnownService.APPLE_TV -> "https://tv.apple.com"
            KnownService.HULU -> "https://www.hulu.com"
            KnownService.TUBI -> "https://tubitv.com"
            KnownService.MAX -> "https://www.max.com"
            null -> "https://duckduckgo.com"
        }
        val context = getApplication<Application>()
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No web browser found: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        dismissAppPrompt()
    }

    fun openSystemSettings() {
        val context = getApplication<Application>()
        try {
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Cannot open System Settings", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWifiSettings() {
        val context = getApplication<Application>()
        try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            openSystemSettings()
        }
    }

    fun setSettingsOpen(isOpen: Boolean) = _uiState.update { it.copy(isSettingsOpen = isOpen) }
    fun setAllAppsOpen(isOpen: Boolean) = _uiState.update { it.copy(isAllAppsOpen = isOpen) }
    fun setSubscriptionManagerOpen(isOpen: Boolean) = _uiState.update { it.copy(isSubscriptionManagerOpen = isOpen) }

    fun updateTheme(theme: LauncherThemeOption) {
        repository.setTheme(theme)
        _uiState.update { it.copy(theme = theme) }
    }

    fun updateCardSize(size: CardSize) {
        repository.setCardSize(size)
        _uiState.update { it.copy(cardSize = size) }
    }

    fun update24hClock(is24: Boolean) {
        repository.set24HourClock(is24)
        _uiState.update { it.copy(is24hClock = is24) }
    }

    fun updateShowDate(show: Boolean) {
        repository.setShowDate(show)
        _uiState.update { it.copy(showDate = show) }
    }

    fun updateShowUninstalledStreaming(show: Boolean) {
        repository.setShowUninstalledStreaming(show)
        _uiState.update { it.copy(showUninstalledStreaming = show) }
        loadData()
    }
}
