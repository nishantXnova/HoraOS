package com.wispos.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

private const val GRID = 14

private data class Cell(val x: Int, val y: Int)

private class SnakeGame {
  var body: ArrayDeque<Cell> = ArrayDeque(
    listOf(Cell(7, 7), Cell(6, 7), Cell(5, 7))
  )
  var dir = Cell(1, 0)
  var food = Cell(10, 7)
    private set
  var score = 0
    private set
  var alive = true
    private set

  fun steer(dx: Float, dy: Float) {
    val next = if (abs(dx) > abs(dy)) Cell(if (dx > 0) 1 else -1, 0)
    else Cell(0, if (dy > 0) 1 else -1)
    // No 180° turns into your own neck.
    if (next.x != -dir.x || next.y != -dir.y) dir = next
  }

  fun step() {
    if (!alive) return
    val head = Cell(body.first().x + dir.x, body.first().y + dir.y)
    if (head.x !in 0 until GRID || head.y !in 0 until GRID || head in body) {
      alive = false
      return
    }
    body.addFirst(head)
    if (head == food) {
      score++
      do {
        food = Cell(Random.nextInt(GRID), Random.nextInt(GRID))
      } while (food in body)
    } else {
      body.removeLast()
    }
  }

  fun reset() {
    body = ArrayDeque(listOf(Cell(7, 7), Cell(6, 7), Cell(5, 7)))
    dir = Cell(1, 0)
    food = Cell(10, 7)
    score = 0
    alive = true
  }
}

class GameActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { SnakeScreen() }
  }
}

@Composable
fun SnakeScreen() {
  val context = LocalContext.current
  val prefs = remember { context.getSharedPreferences(WISP_PREFS, android.content.Context.MODE_PRIVATE) }
  var best by remember { mutableStateOf(prefs.getInt("snake_best", 0)) }
  val game = remember { SnakeGame() }
  var frame by remember { mutableIntStateOf(0) } // drives redraw each step

  LaunchedEffect(game) {
    while (true) {
      delay((230 - game.score * 6).coerceAtLeast(90).toLong())
      game.step()
      if (!game.alive && game.score > best) {
        best = game.score
        prefs.edit().putInt("snake_best", best).apply()
      }
      frame++
    }
  }

  MaterialTheme {
    Box(
      Modifier
        .fillMaxSize()
        .background(Color.Black)
        .pointerInput(Unit) {
          detectDragGestures { _, drag -> game.steer(drag.x, drag.y) }
        },
      contentAlignment = Alignment.Center
    ) {
      key(frame) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("SCORE ${game.score}   BEST $best", fontSize = 11.sp, color = Color(0xFF8E8E93))
        Spacer(Modifier.height(4.dp))
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f).padding(24.dp)) {
          val cell = size.minDimension / GRID
          // Food
          drawCircle(
            Color.White,
            radius = cell * 0.38f,
            center = Offset((game.food.x + 0.5f) * cell, (game.food.y + 0.5f) * cell)
          )
          // Snake, head brightest
          game.body.forEachIndexed { i, s ->
            drawCircle(
              Color.White.copy(alpha = 1f - (i.toFloat() / game.body.size) * 0.65f),
              radius = cell * 0.44f,
              center = Offset((s.x + 0.5f) * cell, (s.y + 0.5f) * cell)
            )
          }
        }
      }
      if (!game.alive) {
        Box(
          Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f))
            .clickable { game.reset() },
          contentAlignment = Alignment.Center
        ) {
          Text(
            "Score ${game.score}\nTap to retry",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
      } // key(frame): redraw board + score every step
    }
  }
}
