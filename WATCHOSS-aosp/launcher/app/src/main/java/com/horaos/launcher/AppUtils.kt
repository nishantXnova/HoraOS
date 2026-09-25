package com.horaos.launcher

/** Pure presentation logic for the watch face + drawer. Kept side-effect free so unit tests cover it. */

/** Status line under the clock, e.g. "100%  •  10 apps". batt < 0 means unknown. */
fun statusLine(batt: Int, appCount: Int?): String {
  val apps = "${appCount ?: "…"} apps"
  return if (batt >= 0) "$batt%  •  $apps" else apps
}

/** Case-insensitive label sort used by the app drawer. */
fun sortAppLabels(labels: List<String>): List<String> = labels.sortedBy { it.lowercase() }

/** Clamp helper for pager dots / progress indicators. */
fun clampPage(index: Int, pageCount: Int): Int = index.coerceIn(0, (pageCount - 1).coerceAtLeast(0))
