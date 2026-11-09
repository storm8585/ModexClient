package com.modex.vpn.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.modex.vpn.R
import com.modex.vpn.ui.theme.GoogleSans
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ModexConnectButton(
    vpnState: VpnState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(vpnState) {
        if (vpnState == VpnState.Connected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                )
            } else {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(40)
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ScaleAnimation"
    )

    val contentColor by animateColorAsState(
        targetValue = if (vpnState == VpnState.Disconnected) {
            Color(0xFF8E8E93)
        } else {
            Color(0xFFFFFFFF)
        },
        animationSpec = tween(durationMillis = 300),
        label = "ContentColorAnimation"
    )

    val morphProgress by animateFloatAsState(
        targetValue = if (vpnState == VpnState.Connected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "MorphProgressAnimation"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "StarRotationTransition")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 6000
                0f at 0 using CubicBezierEasing(0.2f, 0f, 0f, 1f)
                90f at 1500 using CubicBezierEasing(0.2f, 0f, 0f, 1f)
                180f at 3000 using CubicBezierEasing(0.2f, 0f, 0f, 1f)
                270f at 4500 using CubicBezierEasing(0.2f, 0f, 0f, 1f)
                360f at 6000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "StarRotationAngle"
    )

    val buttonText = when (vpnState) {
        VpnState.Disconnected -> "Connect"
        VpnState.Connecting -> "Connecting..."
        VpnState.Connected -> "Connected"
    }

    val buttonBgColor = Color(0xFF2563EB)

    Box(
        modifier = modifier
            .size(190.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAngle * morphProgress)
        ) {
            val path = createExpressiveMorphPath(
                size = size,
                points = 12,
                morph = morphProgress
            )
            drawPath(path = path, color = buttonBgColor)
        }

        Column(
            modifier = Modifier.offset(y = (-2).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_modex_symbol),
                contentDescription = null,
                colorFilter = ColorFilter.tint(contentColor),
                modifier = Modifier.size(82.dp, 55.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            BasicText(
                text = buttonText,
                style = TextStyle(
                    color = contentColor,
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    letterSpacing = 0.3.sp
                )
            )
        }
    }
}

private fun createExpressiveMorphPath(size: Size, points: Int, morph: Float): Path {
    val path = Path()
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val outerRadius = minOf(centerX, centerY)
    val scallopDepth = 0.16f * morph
    val innerRadius = outerRadius * (1f - scallopDepth)
    val totalVertices = points * 2
    val angleStep = (2.0 * Math.PI) / totalVertices

    var firstX = 0f
    var firstY = 0f

    for (i in 0 until totalVertices) {
        val angle = i * angleStep
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val x = centerX + (radius * cos(angle)).toFloat()
        val y = centerY + (radius * sin(angle)).toFloat()

        if (i == 0) {
            firstX = x
            firstY = y
            path.moveTo(x, y)
        } else {
            val prevAngle = (i - 1) * angleStep
            val prevRadius = if ((i - 1) % 2 == 0) outerRadius else innerRadius
            val prevX = centerX + (prevRadius * cos(prevAngle)).toFloat()
            val prevY = centerY + (prevRadius * sin(prevAngle)).toFloat()

            val midAngle = prevAngle + (angleStep / 2.0)
            val midRadius = (prevRadius + radius) / 2f * (1f + 0.05f * morph)
            val controlX = centerX + (midRadius * cos(midAngle)).toFloat()
            val controlY = centerY + (midRadius * sin(midAngle)).toFloat()

            path.quadraticTo(controlX, controlY, x, y)
        }
    }

    val lastAngle = (totalVertices - 1) * angleStep
    val lastRadius = innerRadius
    val lastX = centerX + (lastRadius * cos(lastAngle)).toFloat()
    val lastY = centerY + (lastRadius * sin(lastAngle)).toFloat()

    val closeMidAngle = lastAngle + (angleStep / 2.0)
    val closeMidRadius = (lastRadius + outerRadius) / 2f * (1f + 0.05f * morph)
    val closeControlX = centerX + (closeMidRadius * cos(closeMidAngle)).toFloat()
    val closeControlY = centerY + (closeMidRadius * sin(closeMidAngle)).toFloat()

    path.quadraticTo(closeControlX, closeControlY, firstX, firstY)
    path.close()
    return path
}
