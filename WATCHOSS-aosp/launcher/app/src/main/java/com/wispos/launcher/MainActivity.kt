package com.wispos.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

data class AppEntry(
  val label: String,
  val packageName: String,
  val launchIntent: Intent,
  val icon: ImageBitmap?
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { WatchOS() }
  }
}

suspend fun loadRealApps(context: Context): List<AppEntry> = withContext(Dispatchers.IO) {
  val pm = context.packageManager
  val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
  val infos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    pm.queryIntentActivities(intent, android.content.pm.PackageManager.ResolveInfoFlags.of(0))
  } else {
    @Suppress("DEPRECATION") pm.queryIntentActivities(intent, 0)
  }
  infos.mapNotNull { ri ->
    // Explicit component intent — no reliance on getLaunchIntentForPackage quirks,
    // NEW_TASK baked in at creation so clicks never mutate shared state.
    val cn = ComponentName(ri.activityInfo.packageName, ri.activityInfo.name)
    val launch = Intent(Intent.ACTION_MAIN).apply {
      component = cn
      addCategory(Intent.CATEGORY_LAUNCHER)
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val label = ri.loadLabel(pm)?.toString() ?: ri.activityInfo.packageName
    val icon = try {
      ri.loadIcon(pm)?.toBitmap(96, 96)?.asImageBitmap()
    } catch (_: Exception) { null }
    AppEntry(label, ri.activityInfo.packageName, launch, icon)
  }.sortedBy { it.label.lowercase() }
}

fun batteryPct(context: Context): Int {
  val f = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return -1
  val level = f.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
  val scale = f.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
  return if (level >= 0) (level * 100 / scale) else -1
}

// ---- Custom face wallpaper (Storage Access Framework: no permissions, survives reboot)
private const val WISP_PREFS = "wisp"
private const val KEY_WALLPAPER = "wallpaper_uri"

fun loadWallpaperUri(context: Context): Uri? {
  val s = context.getSharedPreferences(WISP_PREFS, Context.MODE_PRIVATE)
    .getString(KEY_WALLPAPER, null) ?: return null
  val uri = Uri.parse(s)
  val kept = context.contentResolver.persistedUriPermissions
    .any { it.uri == uri && it.isReadPermission }
  return if (kept) uri else null
}

fun saveWallpaperUri(context: Context, uri: Uri?) {
  context.getSharedPreferences(WISP_PREFS, Context.MODE_PRIVATE).edit().apply {
    if (uri == null) remove(KEY_WALLPAPER) else putString(KEY_WALLPAPER, uri.toString())
  }.apply()
}

/** Decode sampled to max 512px — a watch face never needs more, saves RAM. */
suspend fun decodeWallpaper(context: Context, uri: Uri): ImageBitmap? = withContext(Dispatchers.IO) {
  try {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    var sample = 1
    while (bounds.outWidth / sample > 512 || bounds.outHeight / sample > 512) sample *= 2
    val opts = BitmapFactory.Options().apply { inSampleSize = sample }
    context.contentResolver.openInputStream(uri)?.use {
      BitmapFactory.decodeStream(it, null, opts)?.asImageBitmap()
    }
  } catch (_: Exception) { null }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WatchOS() {
  val context = LocalContext.current
  var apps by remember { mutableStateOf<List<AppEntry>?>(null) }
  LaunchedEffect(Unit) { apps = loadRealApps(context) }
  // Ticking clock, recomposes once per second — cheap, one Text
  var now by remember { mutableStateOf(LocalDateTime.now()) }
  LaunchedEffect(Unit) { while (true) { delay(1000); now = LocalDateTime.now() } }
  // Wallpaper: URI persisted in prefs, bitmap decoded once per change and cached.
  var wallUri by remember { mutableStateOf(loadWallpaperUri(context)) }
  var wallBmp by remember { mutableStateOf<ImageBitmap?>(null) }
  LaunchedEffect(wallUri) {
    wallBmp = if (wallUri != null) decodeWallpaper(context, wallUri!!) else null
  }
  val pickWallpaper = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
    if (uri != null) {
      try {
        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        saveWallpaperUri(context, uri)
        wallUri = uri
      } catch (e: Exception) {
        Toast.makeText(context, "Can't keep that image", Toast.LENGTH_SHORT).show()
      }
    }
  }
  val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })

  MaterialTheme {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
        when (page) {
          0 -> QuickSettingsPage(
            hasWallpaper = wallUri != null,
            onPickWallpaper = { pickWallpaper.launch(arrayOf("image/*")) },
            onClearWallpaper = { saveWallpaperUri(context, null); wallUri = null }
          )
          1 -> WatchFacePage(now, context, apps?.size, wallBmp)
          2 -> AppDrawerPage(apps)
        }
      }
    }
  }
}

// Rounded heavy sans (Nunito ExtraBold, SIL OFL, bundled) — closest legal
// stand-in for a compact rounded watch typeface. Tabular numerals kill jitter.
val WispFace = FontFamily(
  Font(R.font.nunito_light, FontWeight.Light),
  Font(R.font.nunito_extrabold, FontWeight.ExtraBold)
)

@Composable
fun WatchFacePage(now: LocalDateTime, context: Context, appCount: Int?, wallpaper: ImageBitmap?) {
  val time = now.format(DateTimeFormatter.ofPattern("HH:mm"))
  val date = now.format(DateTimeFormatter.ofPattern("EEE, MMM d")).uppercase()
  val batt = remember { batteryPct(context) }
  val apps = (appCount ?: 0).coerceAtLeast(0)
  Box(
    Modifier.fillMaxSize().background(Color.Black),
    contentAlignment = Alignment.Center
  ) {
    // Wallpaper under everything, dimmed hard so numerals stay readable
    // and OLED keeps most of its power savings.
    if (wallpaper != null) {
      Image(
        wallpaper, contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
      Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)))
    }
    Column(
      Modifier.fillMaxSize().padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = date,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF8E8E93),
        letterSpacing = 2.sp
      )
      Spacer(Modifier.height(2.dp))
      Text(
        text = time,
        fontFamily = WispFace,
        fontWeight = FontWeight.Light,
        fontSize = 72.sp,
        color = Color.White,
        style = TextStyle(fontFeatureSettings = "tnum"),
        letterSpacing = (-2).sp
      )
      Spacer(Modifier.height(12.dp))
      // Icon-only complication row — values live inside the rings, no labels.
      Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        MiniRing(
          progress = if (batt >= 0) batt / 100f else 0f,
          value = if (batt >= 0) "$batt" else "–"
        )
        MiniRing(
          progress = (apps / 20f).coerceIn(0f, 1f),
          value = "$apps"
        )
        MiniRing(
          progress = now.dayOfMonth / 31f,
          value = "${now.dayOfMonth}"
        )
      }
    }
  }
}

@Composable
fun MiniRing(progress: Float, value: String) {
  Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.fillMaxSize()) {
      drawArc(
        Color.White.copy(alpha = 0.12f), 0f, 360f, false,
        style = Stroke(width = 3.dp.toPx())
      )
      drawArc(
        Color.White, -90f, progress.coerceIn(0f, 1f) * 360f, false,
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
      )
    }
    Text(value, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.White)
  }
}

@Composable
fun QuickSettingsPage(
  hasWallpaper: Boolean,
  onPickWallpaper: () -> Unit,
  onClearWallpaper: () -> Unit
) {
  val context = LocalContext.current
  val batt = remember { batteryPct(context) }
  fun openSettings(action: String) {
    try { context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    catch (_: Exception) {
      try { context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
      catch (_: Exception) {}
    }
  }
  ScalingLazyColumn(
    state = rememberScalingLazyListState(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
  ) {
    item { ListHeader { Text("Settings") } }
    item {
      Chip(
        onClick = { openSettings(Settings.ACTION_WIFI_SETTINGS) },
        label = { Text("Wi-Fi") }, modifier = Modifier.fillMaxWidth()
      )
    }
    item {
      Chip(
        onClick = { openSettings(Settings.ACTION_BLUETOOTH_SETTINGS) },
        label = { Text("Bluetooth") }, modifier = Modifier.fillMaxWidth()
      )
    }
    item {
      Chip(
        onClick = { openSettings(Settings.ACTION_DISPLAY_SETTINGS) },
        label = { Text("Brightness") }, modifier = Modifier.fillMaxWidth()
      )
    }
    item {
      Chip(
        onClick = { openSettings(Settings.ACTION_SETTINGS) },
        label = { Text(if (batt >= 0) "Battery $batt%" else "Battery") },
        modifier = Modifier.fillMaxWidth()
      )
    }
    item {
      Chip(
        onClick = onPickWallpaper,
        label = { Text(if (hasWallpaper) "Change wallpaper" else "Face wallpaper") },
        modifier = Modifier.fillMaxWidth()
      )
    }
    if (hasWallpaper) {
      item {
        Chip(
          onClick = onClearWallpaper,
          label = { Text("Clear wallpaper") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

@Composable
fun AppDrawerPage(apps: List<AppEntry>?) {
  val context = LocalContext.current
  if (apps == null) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Loading apps…") }
    return
  }
  ScalingLazyColumn(
    state = rememberScalingLazyListState(),
    contentPadding = PaddingValues(12.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
  ) {
    item { ListHeader { Text("${apps.size} apps") } }
    items(apps, key = { it.packageName }) { app ->
      Chip(
        onClick = {
          try {
            context.startActivity(app.launchIntent)
          } catch (e: Exception) {
            Toast.makeText(context, "Can't open ${app.label}", Toast.LENGTH_SHORT).show()
          }
        },
        label = { Text(app.label, maxLines = 1) },
        icon = app.icon?.let { bmp ->
          {
            Image(
              bmp, contentDescription = null,
              modifier = Modifier.size(24.dp).clip(CircleShape)
            )
          }
        },
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}
