package com.horaos.launcher

import org.junit.Assert.assertEquals
import org.junit.Test

class AppUtilsTest {

  @Test fun statusLine_withBatteryAndCount() {
    assertEquals("100%  •  10 apps", statusLine(100, 10))
  }

  @Test fun statusLine_unknownBattery() {
    assertEquals("10 apps", statusLine(-1, 10))
  }

  @Test fun statusLine_unknownCount() {
    assertEquals("… apps", statusLine(-1, null))
  }

  @Test fun sortAppLabels_isCaseInsensitive() {
    assertEquals(
      listOf("Aurora Store", "Clock", "music", "Timer"),
      sortAppLabels(listOf("Timer", "music", "Clock", "Aurora Store"))
    )
  }

  @Test fun clampPage_keepsDotsInRange() {
    assertEquals(0, clampPage(-1, 3))
    assertEquals(1, clampPage(1, 3))
    assertEquals(2, clampPage(9, 3))
  }
}
