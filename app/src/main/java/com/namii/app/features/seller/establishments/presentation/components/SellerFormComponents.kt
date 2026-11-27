package com.namii.app.features.seller.establishments.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namii.app.features.client.establishments.domain.entities.EstablishmentCategory
import com.namii.app.features.seller.establishments.domain.entities.DailySchedule
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.DuraznoLight
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.ExitoGreen
import com.namii.app.ui.theme.ExitoSurface
import com.namii.app.ui.theme.InputBackground
import com.namii.app.ui.theme.NaranjaTostado
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.SellerCacaoInk
import com.namii.app.ui.theme.SellerSoftBrown
import com.namii.app.ui.theme.TextoMuted

@Composable
fun SellerStepHeader(
    title: String,
    subtitle: String,
    stepNumber: Int,
    totalSteps: Int = 3,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Atrás",
                    tint = SellerCacaoInk
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Paso $stepNumber de $totalSteps",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaranjaTostado
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = SellerCacaoInk
                )
            }
        }
        LinearProgressIndicator(
            progress = { stepNumber.toFloat() / totalSteps.toFloat() },
            color = NaranjaTostado,
            trackColor = DuraznoClear,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = SellerSoftBrown,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun SellerCategoryGrid(
    selectedCategory: EstablishmentCategory,
    onSelectCategory: (EstablishmentCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = remember { EstablishmentCategory.entries.chunked(2) }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        rows.forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                pair.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) cat.softColor else InputBackground,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) cat.color else BorderLight
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectCategory(cat) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(cat.color)
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = PureWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = cat.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = SellerCacaoInk,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = cat.color,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class TimeSlotTarget {
    OPEN,
    CLOSE
}

private data class ActiveTimePickerRequest(
    val dayOfWeek: Int,
    val dayLabel: String,
    val target: TimeSlotTarget,
    val initialHour: Int,
    val initialMinute: Int
)

/**
 * Editor de horario por día (Lunes a Domingo) independiente y en formato de 24 horas.
 */
@Composable
fun DailyScheduleEditorList(
    schedules: List<DailySchedule>,
    onToggleDayClosed: (dayOfWeek: Int, isClosed: Boolean) -> Unit,
    onUpdateTime: (dayOfWeek: Int, opensAt: String, closesAt: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activePicker by remember { mutableStateOf<ActiveTimePickerRequest?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        schedules.forEach { schedule ->
            val rangeValid = schedule.isTimeRangeValid()
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PureWhite,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (!rangeValid) ErrorRed else BorderLight
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = schedule.dayLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SellerCacaoInk
                            )
                            Text(
                                text = if (schedule.isClosed) {
                                    "Cerrado todo el día"
                                } else {
                                    "Abierto de ${schedule.opensAt} a ${schedule.closesAt} hrs"
                                },
                                fontSize = 11.sp,
                                color = if (schedule.isClosed) TextoMuted else ExitoGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Switch(
                            checked = !schedule.isClosed,
                            onCheckedChange = { isOpen ->
                                onToggleDayClosed(schedule.dayOfWeek, !isOpen)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = NaranjaTostado,
                                uncheckedThumbColor = PureWhite,
                                uncheckedTrackColor = BorderLight
                            )
                        )
                    }

                    if (!schedule.isClosed) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TimePillButton(
                                label = "Abre",
                                time24h = schedule.opensAt,
                                onClick = {
                                    val (h, m) = splitHourMinute(schedule.opensAt, 9, 0)
                                    activePicker = ActiveTimePickerRequest(
                                        dayOfWeek = schedule.dayOfWeek,
                                        dayLabel = schedule.dayLabel,
                                        target = TimeSlotTarget.OPEN,
                                        initialHour = h,
                                        initialMinute = m
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            TimePillButton(
                                label = "Cierra",
                                time24h = schedule.closesAt,
                                onClick = {
                                    val (h, m) = splitHourMinute(schedule.closesAt, 18, 0)
                                    activePicker = ActiveTimePickerRequest(
                                        dayOfWeek = schedule.dayOfWeek,
                                        dayLabel = schedule.dayLabel,
                                        target = TimeSlotTarget.CLOSE,
                                        initialHour = h,
                                        initialMinute = m
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (!rangeValid) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "La hora de cierre debe ser posterior a la de apertura",
                                fontSize = 11.sp,
                                color = ErrorRed
                            )
                        }
                    }
                }
            }
        }

        // Resumen visual rápido de horarios configurados
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = DuraznoLight,
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Resumen semanal (24h)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerCacaoInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                schedules.forEach { day ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = day.dayLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SellerSoftBrown
                        )
                        Text(
                            text = if (day.isClosed) {
                                "Cerrado"
                            } else {
                                "${day.opensAt} – ${day.closesAt}"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (day.isClosed) TextoMuted else SellerCacaoInk
                        )
                    }
                }
            }
        }
    }

    activePicker?.let { req ->
        Schedule24hTimePickerDialog(
            title = "${req.dayLabel} · ${if (req.target == TimeSlotTarget.OPEN) "Hora de apertura" else "Hora de cierre"}",
            initialHour = req.initialHour,
            initialMinute = req.initialMinute,
            onConfirm = { hour, minute ->
                val formatted = DailySchedule.formatMinutes24h(hour, minute)
                val targetSchedule = schedules.firstOrNull { it.dayOfWeek == req.dayOfWeek }
                if (targetSchedule != null) {
                    if (req.target == TimeSlotTarget.OPEN) {
                        onUpdateTime(req.dayOfWeek, formatted, targetSchedule.closesAt)
                    } else {
                        onUpdateTime(req.dayOfWeek, targetSchedule.opensAt, formatted)
                    }
                }
                activePicker = null
            },
            onDismiss = { activePicker = null }
        )
    }
}

@Composable
private fun TimePillButton(
    label: String,
    time24h: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ExitoSurface.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AccessTime,
                contentDescription = null,
                tint = NaranjaTostado,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = SellerSoftBrown
                )
                Text(
                    text = time24h,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SellerCacaoInk
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Schedule24hTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = SellerCacaoInk
            )
        },
        text = {
            TimePicker(state = state)
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) {
                Text(
                    text = "Confirmar",
                    color = NaranjaTostado,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = SellerSoftBrown
                )
            }
        }
    )
}

private fun splitHourMinute(time: String, fallbackHour: Int, fallbackMinute: Int): Pair<Int, Int> {
    val parts = time.trim().split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: fallbackHour
    val minute = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: fallbackMinute
    return hour to minute
}
