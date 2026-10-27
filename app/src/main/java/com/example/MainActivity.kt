package com.example

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.screens.RubyLauncherScreen
import com.example.ui.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF8AB4F8),
                    background = Color(0xFF090B10),
                    surface = Color(0xFF141720)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF090B10)
                ) {
                    RubyLauncherScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    /**
     * TV Remote and D-Pad hardware navigation handler
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val state = viewModel.uiState.value

        if (state.isSettingsOpen || state.isAllAppsOpen ||
            state.isSubscriptionManagerOpen || state.uninstalledAppToPrompt != null
        ) {
            return super.onKeyDown(keyCode, event)
        }

        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                viewModel.navigateHorizontal(-1)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                viewModel.navigateHorizontal(1)
                true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                viewModel.navigateVertical(-1)
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                viewModel.navigateVertical(1)
                true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                when (state.focusedRow) {
                    1 -> {
                        val item = state.continueWatchingList.getOrNull(state.focusedWatchIndex)
                        if (item != null) viewModel.launchWatchItem(item)
                    }
                    2 -> {
                        val app = state.homeCards.getOrNull(state.focusedAppIndex)
                        if (app != null) viewModel.launchCard(app)
                    }
                }
                true
            }
            KeyEvent.KEYCODE_MENU -> {
                viewModel.setSettingsOpen(true)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
