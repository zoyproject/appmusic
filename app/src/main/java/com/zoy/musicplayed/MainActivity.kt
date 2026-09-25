package com.zoy.musicplayed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// -----------------------------------------------------------------------------
// DATA
// -----------------------------------------------------------------------------

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "Single",
    val duration: Long = 210_000L,
    val artworkUrl: String? = null
)

data class Playlist(
    val id: String,
    val name: String,
    val songs: List<Song> = emptyList()
)

data class AppSettings(
    val darkTheme: Boolean = false,
    val dataSaver: Boolean = true,
    val wifiOnly: Boolean = false,
    val cacheArtwork: Boolean = true,
    val cacheMetadata: Boolean = true,
    val avoidPreload: Boolean = true,
    val audioQuality: String = "Standard",
    val notifications: Boolean = true,
    val equalizerEnabled: Boolean = false,
    val crossfadeEnabled: Boolean = false
)

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 210_000L,
    val queue: List<Song> = emptyList(),
    val queueIndex: Int = 0,
    val shuffle: Boolean = false,
    val repeat: Boolean = false,
    val isLiked: Boolean = false,
    val sleepTimerMinutes: Int? = null
)

// -----------------------------------------------------------------------------
// SAMPLE REPOSITORY
// Replace this repository with your permitted/authorized music API later.
// -----------------------------------------------------------------------------

object DemoMusicRepository {

    val songs = listOf(
        Song("1", "Night Drive", "Demo Artist", "MusicPlayed", 198_000L),
        Song("2", "Purple Sky", "Demo Artist", "MusicPlayed", 224_000L),
        Song("3", "After School", "Demo Artist", "MusicPlayed", 205_000L),
        Song("4", "City Lights", "Demo Artist", "MusicPlayed", 231_000L),
        Song("5", "Dreaming", "Demo Artist", "MusicPlayed", 187_000L),
        Song("6", "Morning Rain", "Demo Artist", "MusicPlayed", 242_000L)
    )

    fun search(query: String): List<Song> {
        if (query.isBlank()) return songs

        return songs.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
        }
    }
}

// -----------------------------------------------------------------------------
// ACTIVITY
// -----------------------------------------------------------------------------

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var settings by remember {
                mutableStateOf(AppSettings())
            }

            var playerState by remember {
                mutableStateOf(
                    PlayerState(
                        queue = DemoMusicRepository.songs
                    )
                )
            }

            MusicPlayedTheme(
                darkTheme = settings.darkTheme
            ) {
                MusicPlayedApp(
                    settings = settings,
                    onSettingsChange = { settings = it },
                    playerState = playerState,
                    onPlayerStateChange = { playerState = it }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// THEME
// -----------------------------------------------------------------------------

private val PurpleLight = Color(0xFF4B0082)
private val PurpleDark = Color(0xFF6E00DB)

@Composable
fun MusicPlayedTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = PurpleDark,
            secondary = PurpleDark
        )
    } else {
        lightColorScheme(
            primary = PurpleLight,
            secondary = PurpleLight
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

// -----------------------------------------------------------------------------
// APP ROOT
// -----------------------------------------------------------------------------

private enum class Destination {
    HOME,
    SEARCH,
    LIBRARY,
    SETTINGS
}

@Composable
fun MusicPlayedApp(
    settings: AppSettings,
    onSettingsChange: (AppSettings) -> Unit,
    playerState: PlayerState,
    onPlayerStateChange: (PlayerState) -> Unit
) {
    var destination by remember {
        mutableStateOf(Destination.HOME)
    }

    var showFullPlayer by remember {
        mutableStateOf(false)
    }

    val playSong: (Song) -> Unit = { song ->
        val queue = if (playerState.queue.isEmpty()) {
            DemoMusicRepository.songs
        } else {
            playerState.queue
        }

        val index = queue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)

        onPlayerStateChange(
            playerState.copy(
                currentSong = song,
                isPlaying = true,
                position = 0L,
                duration = song.duration,
                queue = queue,
                queueIndex = index
            )
        )
    }

    if (showFullPlayer) {
        FullPlayerScreen(
            playerState = playerState,
            onPlayerStateChange = onPlayerStateChange,
            onClose = {
                showFullPlayer = false
            },
            onPlaySong = playSong
        )
        return
    }

    Scaffold(
        bottomBar = {
            Column {
                if (playerState.currentSong != null) {
                    MiniPlayer(
                        playerState = playerState,
                        onPlayerStateChange = onPlayerStateChange,
                        onOpenPlayer = {
                            showFullPlayer = true
                        }
                    )
                }

                NavigationBar {
                    NavigationBarItem(
                        selected = destination == Destination.HOME,
                        onClick = { destination = Destination.HOME },
                        icon = {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                        },
                        label = { Text("Home") }
                    )

                    NavigationBarItem(
                        selected = destination == Destination.SEARCH,
                        onClick = { destination = Destination.SEARCH },
                        icon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        label = { Text("Search") }
                    )

                    NavigationBarItem(
                        selected = destination == Destination.LIBRARY,
                        onClick = { destination = Destination.LIBRARY },
                        icon = {
                            Icon(Icons.Default.LibraryMusic, contentDescription = "Library")
                        },
                        label = { Text("Library") }
                    )

                    NavigationBarItem(
                        selected = destination == Destination.SETTINGS,
                        onClick = { destination = Destination.SETTINGS },
                        icon = {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        },
                        label = { Text("Settings") }
                    )
                }
            }
        }
    ) { paddingValues ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (destination) {
                Destination.HOME -> {
                    HomeScreen(
                        songs = DemoMusicRepository.songs,
                        onPlaySong = playSong
                    )
                }

                Destination.SEARCH -> {
                    SearchScreen(
                        onPlaySong = playSong
                    )
                }

                Destination.LIBRARY -> {
                    LibraryScreen(
                        songs = DemoMusicRepository.songs,
                        playerState = playerState,
                        onPlaySong = playSong
                    )
                }

                Destination.SETTINGS -> {
                    SettingsScreen(
                        settings = settings,
                        onSettingsChange = onSettingsChange,
                        playerState = playerState,
                        onPlayerStateChange = onPlayerStateChange
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// HOME
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    songs: List<Song>,
    onPlaySong: (Song) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MusicPlayed",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Good afternoon",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Listen to your music",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = null,
                            modifier = Modifier.size(46.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Your music",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Simple, clean and data-conscious",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            item {
                Text(
                    text = "Recently added",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(songs) { song ->
                SongCard(
                    song = song,
                    onClick = {
                        onPlaySong(song)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SEARCH
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onPlaySong: (Song) -> Unit
) {
    var query by remember {
        mutableStateOf("")
    }

    val results = remember(query) {
        DemoMusicRepository.search(query)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Search")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                query = ""
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                placeholder = {
                    Text("Search songs, artists, albums...")
                },
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results) { song ->
                    SongListItem(
                        song = song,
                        onClick = {
                            onPlaySong(song)
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// LIBRARY
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    songs: List<Song>,
    playerState: PlayerState,
    onPlaySong: (Song) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Library",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LibraryActionCard(
                    icon = Icons.Default.Favorite,
                    title = "Liked Songs",
                    subtitle = if (playerState.isLiked) {
                        "You have a liked song"
                    } else {
                        "No liked songs yet"
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                LibraryActionCard(
                    icon = Icons.Default.QueueMusic,
                    title = "Queue",
                    subtitle = "${playerState.queue.size} songs in queue"
                )

                Spacer(modifier = Modifier.height(10.dp))

                LibraryActionCard(
                    icon = Icons.Default.List,
                    title = "Playlists",
                    subtitle = "Create and manage playlists"
                )

                Spacer(modifier = Modifier.height(22.dp))
            }

            item {
                Text(
                    text = "All songs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(songs) { song ->
                SongListItem(
                    song = song,
                    onClick = {
                        onPlaySong(song)
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SETTINGS
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSettingsChange: (AppSettings) -> Unit,
    playerState: PlayerState,
    onPlayerStateChange: (PlayerState) -> Unit
) {
    var showAudioQualityDialog by remember {
        mutableStateOf(false)
    }

    var showSleepTimerDialog by remember {
        mutableStateOf(false)
    }

    var showAboutDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                SettingsSectionTitle("Appearance")
            }

            item {
                SettingSwitchRow(
                    icon = Icons.Default.DarkMode,
                    title = "Dark theme",
                    subtitle = "Use the dark purple theme",
                    checked = settings.darkTheme,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(darkTheme = it)
                        )
                    }
                )
            }

            item {
                SettingsSectionTitle("Data & Playback")
            }

            item {
                SettingSwitchRow(
                    icon = Icons.Default.Wifi,
                    title = "Data Saver",
                    subtitle = "Reduce unnecessary network usage",
                    checked = settings.dataSaver,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(dataSaver = it)
                        )
                    }
                )
            }

            item {
                SettingSwitchRow(
                    icon = if (settings.wifiOnly) {
                        Icons.Default.Wifi
                    } else {
                        Icons.Default.WifiOff
                    },
                    title = "Wi-Fi Only",
                    subtitle = "Only stream when connected to Wi-Fi",
                    checked = settings.wifiOnly,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(wifiOnly = it)
                        )
                    }
                )
            }

            item {
                SettingSwitchRow(
                    icon = Icons.Default.Album,
                    title = "Cache artwork",
                    subtitle = "Save artwork locally",
                    checked = settings.cacheArtwork,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(cacheArtwork = it)
                        )
                    }
                )
            }

            item {
                SettingSwitchRow(
                    icon = Icons.Default.List,
                    title = "Cache metadata",
                    subtitle = "Save song information locally",
                    checked = settings.cacheMetadata,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(cacheMetadata = it)
                        )
                    }
                )
            }

            item {
                SettingSwitchRow(
                    icon = Icons.Default.SkipNext,
                    title = "Avoid preload",
                    subtitle = "Do not preload unnecessary songs",
                    checked = settings.avoidPreload,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(avoidPreload = it)
                        )
                    }
                )
            }

            item {
                SettingClickableRow(
                    icon = Icons.Default.Equalizer,
                    title = "Audio quality",
                    subtitle = settings.audioQuality,
                    onClick = {
                        showAudioQualityDialog = true
                    }
                )
            }

            item {
                SettingSwitchRow(
                    icon = Icons.Default.Equalizer,
                    title = "Equalizer",
                    subtitle = "Enable equalizer controls",
                    checked = settings.equalizerEnabled,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(equalizerEnabled = it)
                        )
                    }
                )
            }

            item {
                SettingSwitchRow(
                    icon = Icons.Default.Repeat,
                    title = "Crossfade",
                    subtitle = "Smooth transition between songs",
                    checked = settings.crossfadeEnabled,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(crossfadeEnabled = it)
                        )
                    }
                )
            }

            item {
                SettingClickableRow(
                    icon = Icons.Default.Snooze,
                    title = "Sleep timer",
                    subtitle = playerState.sleepTimerMinutes?.let {
                        "$it minutes"
                    } ?: "Off",
                    onClick = {
                        showSleepTimerDialog = true
                    }
                )
            }

            item {
                SettingsSectionTitle("Notifications")
            }

            item {
                SettingSwitchRow(
                    icon = Icons.Default.Info,
                    title = "Notifications",
                    subtitle = "Show playback notifications",
                    checked = settings.notifications,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(notifications = it)
                        )
                    }
                )
            }

            item {
                SettingsSectionTitle("About")
            }

            item {
                SettingClickableRow(
                    icon = Icons.Default.Info,
                    title = "About MusicPlayed",
                    subtitle = "Project information",
                    onClick = {
                        showAboutDialog = true
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "This project was created by zoy (Zidni)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showAudioQualityDialog) {
        AudioQualityDialog(
            currentQuality = settings.audioQuality,
            onDismiss = {
                showAudioQualityDialog = false
            },
            onSelected = { quality ->
                onSettingsChange(
                    settings.copy(audioQuality = quality)
                )
                showAudioQualityDialog = false
            }
        )
    }

    if (showSleepTimerDialog) {
        SleepTimerDialog(
            currentValue = playerState.sleepTimerMinutes,
            onDismiss = {
                showSleepTimerDialog = false
            },
            onSelected = { minutes ->
                onPlayerStateChange(
                    playerState.copy(
                        sleepTimerMinutes = minutes
                    )
                )
                showSleepTimerDialog = false
            }
        )
    }

    if (showAboutDialog) {
        AboutDialog(
            onDismiss = {
                showAboutDialog = false
            }
        )
    }
}

// -----------------------------------------------------------------------------
// FULL PLAYER
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerScreen(
    playerState: PlayerState,
    onPlayerStateChange: (PlayerState) -> Unit,
    onClose: () -> Unit,
    onPlaySong: (Song) -> Unit
) {
    var showQueue by remember {
        mutableStateOf(false)
    }

    var showLyrics by remember {
        mutableStateOf(false)
    }

    val song = playerState.currentSong

    if (song == null) {
        onClose()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Close player"
                        )
                    }
                },
                title = {
                    Text(
                        "Now Playing",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            showQueue = true
                        }
                    ) {
                        Icon(
                            Icons.Default.QueueMusic,
                            contentDescription = "Queue"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            ArtworkPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        onPlayerStateChange(
                            playerState.copy(
                                isLiked = !playerState.isLiked
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = if (playerState.isLiked) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        contentDescription = "Favorite",
                        tint = if (playerState.isLiked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Slider(
                value = playerState.position
                    .coerceAtMost(playerState.duration)
                    .toFloat(),
                onValueChange = {
                    onPlayerStateChange(
                        playerState.copy(
                            position = it.toLong()
                        )
                    )
                },
                valueRange = 0f..playerState.duration.toFloat()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatDuration(playerState.position))

                Text(formatDuration(playerState.duration))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onPlayerStateChange(
                            playerState.copy(
                                shuffle = !playerState.shuffle
                            )
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playerState.shuffle) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                IconButton(
                    onClick = {
                        playPrevious(
                            playerState,
                            onPlaySong
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(34.dp)
                    )
                }

                IconButton(
                    onClick = {
                        onPlayerStateChange(
                            playerState.copy(
                                isPlaying = !playerState.isPlaying
                            )
                        )
                    },
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = if (playerState.isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(
                    onClick = {
                        playNext(
                            playerState,
                            onPlaySong
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(34.dp)
                    )
                }

                IconButton(
                    onClick = {
                        onPlayerStateChange(
                            playerState.copy(
                                repeat = !playerState.repeat
                            )
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (playerState.repeat) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        showLyrics = true
                    }
                ) {
                    Icon(
                        Icons.Default.Lyrics,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Lyrics")
                }

                OutlinedButton(
                    onClick = {
                        showQueue = true
                    }
                ) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Queue")
                }
            }
        }
    }

    if (showQueue) {
        QueueSheet(
            playerState = playerState,
            onDismiss = {
                showQueue = false
            },
            onPlaySong = {
                onPlaySong(it)
                showQueue = false
            }
        )
    }

    if (showLyrics) {
        LyricsSheet(
            song = song,
            onDismiss = {
                showLyrics = false
            }
        )
    }
}

// -----------------------------------------------------------------------------
// MINI PLAYER
// -----------------------------------------------------------------------------

@Composable
fun MiniPlayer(
    playerState: PlayerState,
    onPlayerStateChange: (PlayerState) -> Unit,
    onOpenPlayer: () -> Unit
) {
    val song = playerState.currentSong ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onOpenPlayer()
            },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtworkPlaceholder(
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = {
                    onPlayerStateChange(
                        playerState.copy(
                            isPlaying = !playerState.isPlaying
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    },
                    contentDescription = "Play"
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENTS
// -----------------------------------------------------------------------------

@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtworkPlaceholder(
                modifier = Modifier.size(62.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = song.album,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onClick) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play"
                )
            }
        }
    }
}

@Composable
fun SongListItem(
    song: Song,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkPlaceholder(
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${song.artist} • ${song.album}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Play"
            )
        }
    }
}

@Composable
fun ArtworkPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Album,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
fun LibraryActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(
    title: String
) {
    Text(
        text = title,
        modifier = Modifier.padding(
            start = 20.dp,
            top = 18.dp,
            bottom = 6.dp
        ),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingClickableRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null
        )
    }
}

// -----------------------------------------------------------------------------
// DIALOGS
// -----------------------------------------------------------------------------

@@Composable
fun QueueSheet(
    playerState: PlayerState,
    onDismiss: () -> Unit,
    onPlaySong: (Song) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Queue")
        },
        text = {
            if (playerState.queue.isEmpty()) {
                Text("Queue is empty.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(playerState.queue) { song ->
                        SongListItem(
                            song = song,
                            onClick = {
                                onPlaySong(song)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


@Composable
fun SleepTimerDialog(
    currentValue: Int?,
    onDismiss: () -> Unit,
    onSelected: (Int?) -> Unit
) {
    val values = listOf(
        null,
        10,
        15,
        30,
        45,
        60
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep timer")
        },
        text = {
            Column {
                values.forEach { value ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected(value)
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = value == currentValue,
                            onClick = {
                                onSelected(value)
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            value?.let { "$it minutes" } ?: "Off"
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Info,
                contentDescription = null
            )
        },
        title = {
            Text("MusicPlayed")
        },
        text = {
            Column {
                Text(
                    text = "A simple Android music player with a clean interface, data-conscious playback settings, queue controls and customizable themes."
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "This project was created by zoy (Zidni)",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun QueueSheet(
    playerState: PlayerState,
    onDismiss: () -> Unit,
    onPlaySong: (Song) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Queue")
        },
        text = {
            if (playerState.queue.isEmpty()) {
                Text("Queue is empty.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(playerState.queue) { song ->
                        SongListItem(
                            song = song,
                            onClick = {
                                onPlaySong(song)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun LyricsSheet(
    song: Song,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Lyrics")
        },
        text = {
            Column {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Lyrics are not available for this song yet."
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A lyrics provider can be connected later through a permitted API."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// -----------------------------------------------------------------------------
// PLAYER HELPERS
// -----------------------------------------------------------------------------

private fun playNext(
    playerState: PlayerState,
    onPlaySong: (Song) -> Unit
) {
    if (playerState.queue.isEmpty()) return

    val nextIndex = if (playerState.shuffle) {
        playerState.queue.indices.random()
    } else {
        (playerState.queueIndex + 1) % playerState.queue.size
    }

    onPlaySong(playerState.queue[nextIndex])
}

private fun playPrevious(
    playerState: PlayerState,
    onPlaySong: (Song) -> Unit
) {
    if (playerState.queue.isEmpty()) return

    val previousIndex =
        if (playerState.queueIndex - 1 < 0) {
            playerState.queue.lastIndex
        } else {
            playerState.queueIndex - 1
        }

    onPlaySong(playerState.queue[previousIndex])
}

private fun formatDuration(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%d:%02d".format(minutes, seconds)
}
