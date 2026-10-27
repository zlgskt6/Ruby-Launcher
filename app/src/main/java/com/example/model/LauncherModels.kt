package com.example.model

/**
 * Navigation tabs for the TV launcher
 */
enum class LauncherTab(val label: String) {
    HOME("Home"),
    APPS("Apps"),
    LIBRARY("Library"),
    LIVE("Live")
}

/**
 * Known streaming services with custom styling and packages.
 */
enum class KnownService(
    val id: String,
    val displayName: String,
    val possiblePackages: List<String>,
    val circleBgColor: Long,
    val circleFgColor: Long,
    val defaultSubscribed: Boolean = true
) {
    NETFLIX(
        id = "netflix",
        displayName = "Netflix",
        possiblePackages = listOf("com.netflix.ninja", "com.netflix.mediaclient"),
        circleBgColor = 0xFFFFFFFF,
        circleFgColor = 0xFFE50914
    ),
    PRIME_VIDEO(
        id = "prime_video",
        displayName = "Prime Video",
        possiblePackages = listOf("com.amazon.amazonvideo.livingroom", "com.amazon.avod.thirdpartyclient"),
        circleBgColor = 0xFF1A98FF,
        circleFgColor = 0xFFFFFFFF
    ),
    DISNEY_PLUS(
        id = "disney_plus",
        displayName = "Disney+",
        possiblePackages = listOf("com.disney.disneyplus"),
        circleBgColor = 0xFF0E7090,
        circleFgColor = 0xFFFFFFFF
    ),
    HULU(
        id = "hulu",
        displayName = "Hulu",
        possiblePackages = listOf("com.hulu.livingroomplus", "com.hulu.plus"),
        circleBgColor = 0xFF1CE783,
        circleFgColor = 0xFF051D14
    ),
    TUBI(
        id = "tubi",
        displayName = "Tubi",
        possiblePackages = listOf("com.tubitv"),
        circleBgColor = 0xFF7C3AED,
        circleFgColor = 0xFFFBBF24
    ),
    YOUTUBE(
        id = "youtube",
        displayName = "YouTube",
        possiblePackages = listOf("com.google.android.youtube.tv", "com.google.android.youtube"),
        circleBgColor = 0xFFFFFFFF,
        circleFgColor = 0xFFFF0000
    ),
    JELLYFIN(
        id = "jellyfin",
        displayName = "Jellyfin",
        possiblePackages = listOf("org.jellyfin.androidtv", "org.jellyfin.mobile"),
        circleBgColor = 0xFF0D1B3E,
        circleFgColor = 0xFF00B4D8
    ),
    PLEX(
        id = "plex",
        displayName = "Plex",
        possiblePackages = listOf("com.plexapp.android", "com.plexapp.plex"),
        circleBgColor = 0xFF1F2429,
        circleFgColor = 0xFFE5A00D
    ),
    MAX(
        id = "max",
        displayName = "Max",
        possiblePackages = listOf("com.wbd.stream", "com.hbo.hbonow"),
        circleBgColor = 0xFF002BE7,
        circleFgColor = 0xFFFFFFFF
    ),
    SVT_PLAY(
        id = "svtplay",
        displayName = "SVT Play",
        possiblePackages = listOf("se.svt.android.svtplay", "se.svt.svtplay"),
        circleBgColor = 0xFF0E131B,
        circleFgColor = 0xFF00D14E
    ),
    SPOTIFY(
        id = "spotify",
        displayName = "Spotify",
        possiblePackages = listOf("com.spotify.tv.android", "com.spotify.music"),
        circleBgColor = 0xFF121212,
        circleFgColor = 0xFF1DB954
    ),
    APPLE_TV(
        id = "apple_tv",
        displayName = "Apple TV",
        possiblePackages = listOf("com.apple.atve.androidtv.appletv"),
        circleBgColor = 0xFF000000,
        circleFgColor = 0xFFFFFFFF
    );

    companion object {
        fun findByPackage(packageName: String): KnownService? {
            return entries.firstOrNull { service ->
                service.possiblePackages.any { it.equals(packageName, ignoreCase = true) }
            }
        }
    }
}

/**
 * User's personal continue watching or jump back in item (no ads)
 */
data class WatchProgressItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val service: KnownService,
    val progress: Float, // 0.0 to 1.0 (for red playback bar)
    val isLive: Boolean = false,
    val primaryColor: Long = 0xFF202535,
    val secondaryColor: Long = 0xFF0F121C
)

/**
 * Launcher Card / App Item
 */
data class LauncherCardItem(
    val id: String,
    val title: String,
    val packageName: String?,
    val activityName: String? = null,
    val knownService: KnownService? = null,
    val isInstalled: Boolean = false,
    val isPinned: Boolean = true
)

enum class LauncherThemeOption(val displayName: String, val primaryHex: Long, val bgHex: Long) {
    CINEMATIC_DARK("Cinematic Dark", 0xFFE2E8F0, 0xFF0B0E17),
    RUBY_CRIMSON("Ruby Crimson", 0xFFE0115F, 0xFF0B0205),
    OLED_PURE_BLACK("OLED Pure Black", 0xFFFFFFFF, 0xFF000000)
}

enum class CardSize(val label: String, val widthDp: Int, val heightDp: Int) {
    COMPACT("Compact", 220, 124),
    STANDARD("Standard", 270, 152),
    EXPANDED("Expanded", 320, 180)
}
