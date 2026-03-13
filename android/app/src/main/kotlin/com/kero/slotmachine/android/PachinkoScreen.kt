package com.kero.slotmachine.android

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ── Shared brushes ─────────────────────────────────────────────────────────────

private val chromeBrush = Brush.verticalGradient(
    0.0f to Color(0xFFCCCCCC),
    0.4f to Color(0xFF888888),
    0.6f to Color(0xFFAAAAAA),
    1.0f to Color(0xFF666666)
)
private val darkPanelBrush = Brush.verticalGradient(
    listOf(Color(0xFF2A2A2A), Color(0xFF111111))
)

// ── Reel content (60 symbols = 10 cycles, wrap-around keeps it infinite) ───────

private val baseSymbols = listOf("🍒", "⭐", "🔔", "7", "💎", "♠")
private val reelContent = (0 until 60).map { baseSymbols[it % baseSymbols.size] }
private const val SYMBOL_H_DP = 56

// ── Root screen ────────────────────────────────────────────────────────────────

@Composable
fun PachinkoScreen() {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val leverY = remember { Animatable(0f) }
    val maxLeverPx = with(density) { 48.dp.toPx() }

    val reelAnimatables = remember { List(3) { Animatable(0f) } }
    var isSpinning by remember { mutableStateOf(false) }

    fun triggerSpin() {
        if (isSpinning) return
        isSpinning = true
        val symH = with(density) { SYMBOL_H_DP.dp.toPx() }
        val cyclePx = baseSymbols.size * symH
        reelAnimatables.forEachIndexed { i, anim ->
            scope.launch {
                val steps = 9 + i * 3 + (2..7).random()
                anim.animateTo(
                    anim.value - symH * steps,
                    tween(durationMillis = 1500 + i * 450, easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f))
                )
                // Wrap so column never runs out of symbols
                anim.snapTo(anim.value % (-cyclePx))
                if (i == reelAnimatables.lastIndex) isSpinning = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A))) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            BonusPanel()
            Spacer(Modifier.height(10.dp))
            ReelZone(reelOffsets = reelAnimatables.map { it.value })
            PaylineBar()
            Spacer(Modifier.height(8.dp))
            StopButtonsRow()
            Spacer(Modifier.height(8.dp))
            ChromePanel()
            Spacer(Modifier.height(8.dp))
            ControlRow(
                leverYPx = leverY.value,
                onLeverDrag = { delta ->
                    if (!isSpinning) {
                        scope.launch {
                            leverY.snapTo((leverY.value + delta).coerceIn(0f, maxLeverPx))
                        }
                    }
                },
                onLeverRelease = {
                    val wasPulled = leverY.value > maxLeverPx * 0.2f
                    scope.launch {
                        leverY.animateTo(
                            0f,
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                    if (wasPulled) triggerSpin()
                }
            )
            Spacer(Modifier.height(8.dp))
            BrandPanel()
            Spacer(Modifier.height(8.dp))
            CoinTray()
            Spacer(Modifier.height(18.dp))
        }
    }
}

// ── Bonus Panel ────────────────────────────────────────────────────────────────

@Composable
private fun BonusPanel() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF8B0000), Color(0xFF5A0000))))
            .border(2.dp, Color(0xFF888888), RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(80.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "777",
                style = TextStyle(
                    brush = Brush.verticalGradient(listOf(Color(0xFFFFE566), Color(0xFFFFD700), Color(0xFFCC8800))),
                    fontSize = 26.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp
                )
            )
            Text("BIG BONUS", fontSize = 7.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xCCFFD700), letterSpacing = 1.sp)
        }
        Box(modifier = Modifier.width(1.dp).height(64.dp).background(Color(0x22FFFFFF)))
        Row(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("1", "2", "3").forEach { num ->
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFFFFEE00), Color(0xFFFF8800))))
                        .border(2.dp, Color(0x44FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(num, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }
        Column(
            modifier = Modifier.width(72.dp).fillMaxHeight().padding(end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("REGULAR", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFFAAAAAA), letterSpacing = 1.5.sp)
            Text("BONUS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF6600), letterSpacing = 1.sp)
            Spacer(Modifier.height(3.dp))
            Box(
                modifier = Modifier.width(56.dp).height(18.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF003300)),
                contentAlignment = Alignment.Center
            ) {
                Text("REPLAY", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00CC00), letterSpacing = 1.sp)
            }
        }
    }
}

// ── Reel Zone ──────────────────────────────────────────────────────────────────

@Composable
private fun ReelZone(reelOffsets: List<Float>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .background(
                Brush.verticalGradient(
                    0.0f to Color(0xFFCC7700), 0.3f to Color(0xFFFF9900),
                    0.5f to Color(0xFFFFBB00), 0.7f to Color(0xFFFF9900), 1.0f to Color(0xFFCC7700)
                )
            )
            .border(3.dp, Color(0xFFAAAAAA))
    ) {
        // LED Column
        Column(
            modifier = Modifier.width(36.dp).fillMaxHeight()
                .background(Color(0xFF0D0D0D)).border(1.dp, Color(0xFF333333)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LedDot(Color(0xFFFF4444), Color(0xFF880000))
            Spacer(Modifier.height(14.dp))
            LedDot(Color(0xFFFFEE44), Color(0xFF886600))
            Spacer(Modifier.height(14.dp))
            LedDot(Color(0xFF44FF44), Color(0xFF006600))
        }

        // Reels
        Row(
            modifier = Modifier
                .weight(1f).height(170.dp).align(Alignment.CenterVertically)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reelOffsets.forEach { offset ->
                Box(
                    modifier = Modifier
                        .weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White)
                        .border(2.dp, Color(0xFFAAAAAA), RoundedCornerShape(3.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { translationY = offset },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        reelContent.forEach { sym ->
                            Box(
                                modifier = Modifier.fillMaxWidth().height(SYMBOL_H_DP.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(sym, fontSize = 36.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LedDot(colorStart: Color, colorEnd: Color) {
    Box(
        modifier = Modifier.size(18.dp).clip(CircleShape)
            .background(Brush.radialGradient(listOf(colorStart, colorEnd)))
    )
}

// ── Payline Bar ────────────────────────────────────────────────────────────────

@Composable
private fun PaylineBar() {
    Box(
        modifier = Modifier.fillMaxWidth().height(30.dp)
            .background(Color(0xFF111111)).border(1.dp, Color(0xFF333333)),
        contentAlignment = Alignment.Center
    ) {
        Text("— STOP —  — STOP —  — STOP —", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF3300), letterSpacing = 2.sp)
    }
}

// ── Stop Buttons Row ───────────────────────────────────────────────────────────

@Composable
private fun StopButtonsRow() {
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp)
            .background(darkPanelBrush).border(1.dp, Color(0xFF888888)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val btnBg = Brush.verticalGradient(listOf(Color(0xFF444444), Color(0xFF222222), Color(0xFF333333)))
        repeat(3) {
            Box(
                modifier = Modifier.width(90.dp).height(36.dp)
                    .clip(RoundedCornerShape(4.dp)).background(btnBg)
                    .border(2.dp, Color(0xFF888888), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("STOP", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFEEEEEE), letterSpacing = 2.sp)
            }
        }
    }
}

// ── Chrome Panel ───────────────────────────────────────────────────────────────

@Composable
private fun ChromePanel() {
    Row(
        modifier = Modifier.fillMaxWidth().height(28.dp)
            .background(chromeBrush).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "OLYMPIA",
            style = TextStyle(
                brush = Brush.verticalGradient(listOf(Color(0xFF444444), Color(0xFF111111))),
                fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp
            )
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier.size(8.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color.White, Color(0xFF888888))))
                )
            }
        }
    }
}

// ── Control Row (with interactive lever) ───────────────────────────────────────

@Composable
private fun ControlRow(
    leverYPx: Float,
    onLeverDrag: (Float) -> Unit,
    onLeverRelease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(80.dp)
            .background(darkPanelBrush).border(1.dp, Color(0xFF888888))
    ) {
        // Lever Column — gesture target
        Box(
            modifier = Modifier
                .width(80.dp).fillMaxHeight()
                .background(Color(0xFF0A0A0A))
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = { onLeverRelease() },
                        onDragCancel = { onLeverRelease() },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            onLeverDrag(dragAmount)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Knob + Shaft move together with finger
                Column(
                    modifier = Modifier.graphicsLayer { translationY = leverYPx },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0xFF333333), Color.Black)))
                            .border(2.dp, Color(0xFF888888), CircleShape)
                    )
                    Box(
                        modifier = Modifier.width(8.dp).height(24.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFDDDDDD), Color(0xFF888888), Color(0xFFCCCCCC), Color(0xFF777777))
                                )
                            )
                    )
                }
                // Base stays fixed
                Box(
                    modifier = Modifier.width(32.dp).height(18.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFFBBBBBB), Color(0xFF777777))))
                        .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(3.dp))
                )
                Spacer(Modifier.height(4.dp))
                Text("▲  PULL", fontSize = 7.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF888888), letterSpacing = 1.sp)
            }
        }

        // Center Dials
        Row(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFF444444), Color(0xFF111111))))
                        .border(2.dp, Color(0xFF888888), CircleShape)
                )
            }
        }

        // Credit Panel
        Column(
            modifier = Modifier.width(100.dp).fillMaxHeight()
                .background(Color(0xFF0A0A0A)).border(1.dp, Color(0xFF222222)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("CREDIT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF666666), letterSpacing = 2.sp)
            Text(
                text = "100",
                style = TextStyle(
                    brush = Brush.verticalGradient(listOf(Color(0xFFFF6600), Color(0xFFCC4400))),
                    fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp
                )
            )
            Text("BET  1", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF888888), letterSpacing = 1.sp)
        }
    }
}

// ── Brand Panel ────────────────────────────────────────────────────────────────

@Composable
private fun BrandPanel() {
    Box(
        modifier = Modifier.fillMaxWidth().height(160.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF1A0A00), Color(0xFF0D0500))))
            .border(2.dp, Color(0xFF888888), RoundedCornerShape(4.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0x22FF8800), Color(0x00000000)))))
        Text("🌸", fontSize = 32.sp, modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp))
        Text("🌸", fontSize = 32.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp))
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("SunSet", fontSize = 16.sp, color = Color(0xFFFFAA44), letterSpacing = 3.sp)
            Text(
                text = "MARINE",
                style = TextStyle(
                    brush = Brush.linearGradient(listOf(Color(0xFFFF8800), Color(0xFFFF4400), Color(0xFFCC2200), Color(0xFFFF6600))),
                    fontSize = 52.sp, fontWeight = FontWeight.Black, letterSpacing = (-2).sp
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(40.dp).align(Alignment.BottomCenter).background(Color(0x44000000)),
            contentAlignment = Alignment.Center
        ) {
            Text("SUNSET MARINE · OLYMPIA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0x66FF8844), letterSpacing = 2.sp)
        }
    }
}

// ── Coin Tray ──────────────────────────────────────────────────────────────────

@Composable
private fun CoinTray() {
    val shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
    Row(
        modifier = Modifier.fillMaxWidth().height(52.dp)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF333333), Color(0xFF1A1A1A))))
            .border(1.dp, Color(0xFF888888), shape)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(80.dp).height(20.dp)
                .clip(RoundedCornerShape(3.dp)).background(Color(0xFF0A0A0A))
                .border(1.dp, Color(0xFF444444), RoundedCornerShape(3.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("⊙  INSERT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555555), letterSpacing = 1.sp)
        }
        Box(
            modifier = Modifier.width(120.dp).height(24.dp)
                .clip(RoundedCornerShape(2.dp)).background(Color(0xFF0A0A0A))
                .border(1.dp, Color(0xFF444444), RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("COIN  ●  PAYOUT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF444444), letterSpacing = 1.sp)
        }
    }
}
