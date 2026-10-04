package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.data.model.FamilyRole
import com.example.data.model.ResponsibilityEntity
import com.example.ui.theme.HeatCalm
import com.example.ui.theme.HeatHeated
import com.example.ui.theme.HeatTense
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletBridge
import kotlin.math.cos
import kotlin.math.sin

data class CanvasNode(
    val id: Long,
    val title: String,
    val role: FamilyRole,
    val pressureLevel: Int,
    var x: Float = 0f,
    var y: Float = 0f,
    val radius: Float = 24f
)

@Composable
fun ConstellationCanvas(
    responsibilities: List<ResponsibilityEntity>,
    selectedRole: FamilyRole?,
    onNodeSelected: (ResponsibilityEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ConstellationPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAnim"
    )

    // Cached node layout mapped to canvas bounds
    val nodes = remember(responsibilities, selectedRole) {
        val filtered = if (selectedRole == null) {
            responsibilities
        } else {
            responsibilities.filter { it.ownerRole == selectedRole.name }
        }

        filtered.mapIndexed { index, resp ->
            val role = runCatching { FamilyRole.valueOf(resp.ownerRole) }.getOrDefault(FamilyRole.STUDENT)
            CanvasNode(
                id = resp.id,
                title = resp.title,
                role = role,
                pressureLevel = resp.pressureLevel,
                radius = 18f + (resp.pressureLevel * 4f)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(nodes) {
                    detectTapGestures { tapOffset ->
                        val hit = nodes.firstOrNull { node ->
                            val dx = node.x - tapOffset.x
                            val dy = node.y - tapOffset.y
                            (dx * dx + dy * dy) <= (node.radius * node.radius * 2.5f)
                        }
                        if (hit != null) {
                            val originalResp = responsibilities.firstOrNull { it.id == hit.id }
                            if (originalResp != null) {
                                onNodeSelected(originalResp)
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // Calculate anchor centers for 3 family roles
            val studentCenter = Offset(width * 0.28f, height * 0.35f)
            val motherCenter = Offset(width * 0.72f, height * 0.35f)
            val fatherCenter = Offset(width * 0.50f, height * 0.75f)

            // Draw central role hubs
            drawCircle(
                color = IndigoPrimary.copy(alpha = 0.25f),
                radius = 34f * pulse,
                center = studentCenter
            )
            drawCircle(
                color = VioletBridge.copy(alpha = 0.25f),
                radius = 34f * pulse,
                center = motherCenter
            )
            drawCircle(
                color = HeatTense.copy(alpha = 0.25f),
                radius = 34f * pulse,
                center = fatherCenter
            )

            // Draw connecting constellation bridges between role anchors
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = studentCenter,
                end = motherCenter,
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = motherCenter,
                end = fatherCenter,
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = fatherCenter,
                end = studentCenter,
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Position and draw responsibility orbital nodes around each hub
            val studentNodes = nodes.filter { it.role == FamilyRole.STUDENT }
            val motherNodes = nodes.filter { it.role == FamilyRole.MOTHER }
            val fatherNodes = nodes.filter { it.role == FamilyRole.FATHER }

            fun layoutOrbit(group: List<CanvasNode>, center: Offset, baseRadius: Float) {
                val step = if (group.isNotEmpty()) (2 * Math.PI / group.size).toFloat() else 0f
                group.forEachIndexed { i, node ->
                    val angle = i * step + (if (center == studentCenter) 0.3f else 1.2f)
                    val r = baseRadius * pulse
                    node.x = center.x + r * cos(angle)
                    node.y = center.y + r * sin(angle)
                }
            }

            layoutOrbit(studentNodes, studentCenter, 72f)
            layoutOrbit(motherNodes, motherCenter, 72f)
            layoutOrbit(fatherNodes, fatherCenter, 72f)

            // Draw links from each responsibility node to its hub
            nodes.forEach { node ->
                val hub = when (node.role) {
                    FamilyRole.STUDENT -> studentCenter
                    FamilyRole.MOTHER -> motherCenter
                    FamilyRole.FATHER -> fatherCenter
                    else -> studentCenter
                }

                drawLine(
                    color = Color.White.copy(alpha = 0.12f),
                    start = hub,
                    end = Offset(node.x, node.y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw each individual node
            nodes.forEach { node ->
                val nodeColor = when {
                    node.pressureLevel >= 5 -> HeatHeated
                    node.pressureLevel >= 4 -> HeatTense
                    else -> HeatCalm
                }

                // Outer aura
                drawCircle(
                    color = nodeColor.copy(alpha = 0.22f),
                    radius = node.radius * 1.5f,
                    center = Offset(node.x, node.y)
                )

                // Inner core
                drawCircle(
                    color = nodeColor,
                    radius = node.radius,
                    center = Offset(node.x, node.y)
                )

                // Draw tiny center dot for tech-constellation look
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = 3.dp.toPx(),
                    center = Offset(node.x, node.y)
                )
            }

            // Draw Hub Labels using native Canvas
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 28f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            drawContext.canvas.nativeCanvas.drawText("STUDENT", studentCenter.x, studentCenter.y + 10f, paint)
            drawContext.canvas.nativeCanvas.drawText("MOTHER", motherCenter.x, motherCenter.y + 10f, paint)
            drawContext.canvas.nativeCanvas.drawText("FATHER", fatherCenter.x, fatherCenter.y + 10f, paint)
        }
    }
}
