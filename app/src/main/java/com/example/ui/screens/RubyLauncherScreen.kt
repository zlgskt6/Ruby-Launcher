package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LauncherTab
import com.example.ui.components.AllAppsDrawer
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.HeroSpotlight
import com.example.ui.components.LauncherCard
import com.example.ui.components.RubyTopBar
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SubscriptionManagerDialog
import com.example.ui.components.TvAppCircle
import com.example.ui.components.UninstalledAppPromptDialog
import com.example.ui.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

@Composable
fun RubyLauncherScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val cwListState = rememberLazyListState()
    val appsListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Keep active continue watching item scrolled into view
    LaunchedEffect(uiState.focusedWatchIndex) {
        if (uiState.continueWatchingList.isNotEmpty()) {
            coroutineScope.launch {
                cwListState.animateScrollToItem(
                    index = (uiState.focusedWatchIndex - 1).coerceAtLeast(0)
                )
            }
        }
    }

    // Keep active app circle scrolled into view
    LaunchedEffect(uiState.focusedAppIndex) {
        if (uiState.homeCards.isNotEmpty()) {
            coroutineScope.launch {
                appsListState.animateScrollToItem(
                    index = (uiState.focusedAppIndex - 2).coerceAtLeast(0)
                )
            }
        }
    }

    // Handle back button
    BackHandler(
        enabled = uiState.isSettingsOpen || uiState.isAllAppsOpen ||
                uiState.isSubscriptionManagerOpen || uiState.uninstalledAppToPrompt != null ||
                uiState.currentTab != LauncherTab.HOME
    ) {
        when {
            uiState.uninstalledAppToPrompt != null -> viewModel.dismissAppPrompt()
            uiState.isSubscriptionManagerOpen -> viewModel.setSubscriptionManagerOpen(false)
            uiState.isAllAppsOpen -> viewModel.setAllAppsOpen(false)
            uiState.isSettingsOpen -> viewModel.setSettingsOpen(false)
            uiState.currentTab != LauncherTab.HOME -> viewModel.selectTab(LauncherTab.HOME)
        }
    }

    val focusedApp = uiState.homeCards.getOrNull(uiState.focusedAppIndex)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090B10))
            .testTag("ruby_launcher_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Minimalist Top Bar
            RubyTopBar(
                currentTab = uiState.currentTab,
                onTabSelected = { viewModel.selectTab(it) },
                onSearchClicked = { viewModel.setAllAppsOpen(true) },
                onSettingsClicked = { viewModel.setSettingsOpen(true) }
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(40.dp))
                }
            } else {
                when (uiState.currentTab) {
                    LauncherTab.HOME -> {
                        // 1. Hero Spotlight (Powered strictly by user's content, no ads)
                        HeroSpotlight(
                            watchItem = uiState.selectedWatchItem,
                            focusedApp = focusedApp,
                            onPlayClicked = {
                                val item = uiState.selectedWatchItem
                                if (item != null) {
                                    viewModel.launchWatchItem(item)
                                } else if (focusedApp != null) {
                                    viewModel.launchCard(focusedApp)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Continue Watching / Jump back in (Replaces promotional ads)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Continue watching",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .padding(horizontal = 48.dp, vertical = 6.dp)
                                    .testTag("continue_watching_header")
                            )

                            LazyRow(
                                state = cwListState,
                                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(
                                    items = uiState.continueWatchingList,
                                    key = { _, item -> item.id }
                                ) { index, item ->
                                    val isFocused = uiState.focusedRow == 1 && index == uiState.focusedWatchIndex
                                    ContinueWatchingCard(
                                        item = item,
                                        isFocused = isFocused,
                                        onClick = {
                                            viewModel.onWatchItemFocused(index)
                                            viewModel.launchWatchItem(item)
                                        },
                                        onFocused = {
                                            viewModel.onWatchItemFocused(index)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3. "Your apps" Row (Circular app pills)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Your apps",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .padding(horizontal = 48.dp, vertical = 6.dp)
                                    .testTag("your_apps_header")
                            )

                            LazyRow(
                                state = appsListState,
                                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(22.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(
                                    items = uiState.homeCards,
                                    key = { _, item -> item.id }
                                ) { index, item ->
                                    val isFocused = uiState.focusedRow == 2 && index == uiState.focusedAppIndex
                                    LauncherCard(
                                        item = item,
                                        isFocused = isFocused,
                                        cardSize = uiState.cardSize,
                                        theme = uiState.theme,
                                        onClick = {
                                            viewModel.onAppFocused(index)
                                            viewModel.launchCard(item)
                                        },
                                        onFocused = {
                                            viewModel.onAppFocused(index)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    LauncherTab.APPS -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "All Installed Apps & Services",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    LauncherTab.LIBRARY -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Your Personal Library & Saved Streams (Ad-free)",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    LauncherTab.LIVE -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Live Streams & Channels (No Bloat)",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Dialogs & Sheets
        if (uiState.isSettingsOpen) {
            SettingsDialog(
                currentTheme = uiState.theme,
                cardSize = uiState.cardSize,
                is24h = uiState.is24hClock,
                showDate = uiState.showDate,
                showUninstalledStreaming = uiState.showUninstalledStreaming,
                onThemeSelected = { viewModel.updateTheme(it) },
                onCardSizeSelected = { viewModel.updateCardSize(it) },
                on24hToggled = { viewModel.update24hClock(it) },
                onShowDateToggled = { viewModel.updateShowDate(it) },
                onShowUninstalledStreamingToggled = { viewModel.updateShowUninstalledStreaming(it) },
                onOpenSubscriptions = { viewModel.setSubscriptionManagerOpen(true) },
                onOpenSystemSettings = { viewModel.openSystemSettings() },
                onOpenWifiSettings = { viewModel.openWifiSettings() },
                onDismiss = { viewModel.setSettingsOpen(false) }
            )
        }

        if (uiState.isSubscriptionManagerOpen) {
            SubscriptionManagerDialog(
                homeCards = uiState.homeCards,
                currentTheme = uiState.theme,
                onToggleService = { service, isSubscribed ->
                    viewModel.toggleServiceSubscription(service, isSubscribed)
                },
                onMoveCard = { from, to -> viewModel.moveCard(from, to) },
                onRemoveCard = { card -> viewModel.removeCardFromHome(card) },
                onDismiss = { viewModel.setSubscriptionManagerOpen(false) }
            )
        }

        if (uiState.isAllAppsOpen) {
            AllAppsDrawer(
                installedApps = uiState.allInstalledApps,
                homeCards = uiState.homeCards,
                theme = uiState.theme,
                onLaunchApp = { app -> viewModel.launchCard(app) },
                onTogglePinToHome = { app ->
                    val isPinned = uiState.homeCards.any { it.packageName == app.packageName }
                    if (isPinned) {
                        viewModel.removeCardFromHome(app)
                    } else {
                        viewModel.addAppToHome(app)
                    }
                },
                onDismiss = {
                    viewModel.setAllAppsOpen(false)
                    if (uiState.currentTab == LauncherTab.APPS) {
                        viewModel.selectTab(LauncherTab.HOME)
                    }
                }
            )
        }

        uiState.uninstalledAppToPrompt?.let { card ->
            UninstalledAppPromptDialog(
                item = card,
                theme = uiState.theme,
                onInstallFromPlayStore = { pkg -> viewModel.openPlayStore(pkg) },
                onOpenWebPlayer = { service -> viewModel.openWebStreaming(service) },
                onDismiss = { viewModel.dismissAppPrompt() }
            )
        }
    }
}
