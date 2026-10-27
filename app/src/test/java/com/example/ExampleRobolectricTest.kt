package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LauncherRepository
import com.example.model.KnownService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ruby Launcher", appName)
  }

  @Test
  fun `verify default cards include concept streaming services`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = LauncherRepository(context)
    val cards = repository.getHomeCards()

    assertTrue("Home cards should not be empty", cards.isNotEmpty())
    val titles = cards.map { it.title }
    assertTrue("Should include Jellyfin", titles.contains("Jellyfin"))
    assertTrue("Should include YouTube", titles.contains("YouTube"))
    assertTrue("Should include Netflix", titles.contains("Netflix"))
  }

  @Test
  fun `verify continue watching queue has user personal items`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = LauncherRepository(context)
    val queue = repository.getContinueWatchingQueue()

    assertTrue("Continue watching queue should not be empty", queue.isNotEmpty())
    assertTrue("Progress should be between 0 and 1", queue.all { it.progress in 0f..1f })
  }

  @Test
  fun `verify known streaming services find package`() {
    val jellyfinService = KnownService.findByPackage("org.jellyfin.androidtv")
    assertNotNull(jellyfinService)
    assertEquals(KnownService.JELLYFIN, jellyfinService)

    val youtubeService = KnownService.findByPackage("com.google.android.youtube.tv")
    assertNotNull(youtubeService)
    assertEquals(KnownService.YOUTUBE, youtubeService)
  }
}
