package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartRed

@Composable
fun TrackingTimeline(
    orderStatus: String,
    paymentStatus: String,
    modifier: Modifier = Modifier
) {
    val isCancelled = orderStatus == "Cancelled"
    val isReturnOrReplacement = orderStatus.startsWith("Return") || orderStatus.startsWith("Replacement") || orderStatus.startsWith("Refund")

    if (isCancelled) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ShopKartRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Order Cancelled",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartRed
                )
                Text(
                    text = "This order was cancelled. No further action needed.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        return
    }

    if (isReturnOrReplacement) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ShopKartAmber),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = null,
                    tint = ShopKartNavyDark,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = orderStatus,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
                Text(
                    text = "Your request is currently being processed by ShopKart team.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        return
    }

    // Standard progression steps
    val steps = listOf(
        "Payment Verification",
        "Payment Confirmed",
        "Processing",
        "Shipped",
        "Out for Delivery",
        "Delivered"
    )

    val currentStepIndex = when (orderStatus) {
        "Payment Verification Pending" -> 0
        "Payment Confirmed" -> 1
        "Processing" -> 2
        "Shipped" -> 3
        "Out for Delivery" -> 4
        "Delivered" -> 5
        else -> 0
    }

    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, stepName ->
            val isCompleted = index < currentStepIndex
            val isCurrent = index == currentStepIndex
            val isUpcoming = index > currentStepIndex

            val circleColor = when {
                isCompleted -> ShopKartGreen
                isCurrent -> ShopKartAmber
                else -> Color.LightGray
            }

            val iconColor = when {
                isCompleted -> Color.White
                isCurrent -> ShopKartNavyDark
                else -> Color.White
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Indicator Node & Vertical Line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(circleColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (isCurrent) {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.HourglassEmpty
                                    1 -> Icons.Default.Payment
                                    3, 4 -> Icons.Default.LocalShipping
                                    else -> Icons.Default.Check
                                },
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(32.dp)
                                .background(if (index < currentStepIndex) ShopKartGreen else Color.LightGray)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Text Description
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (index < steps.lastIndex) 16.dp else 4.dp)
                ) {
                    Text(
                        text = stepName,
                        fontSize = 14.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isCurrent) ShopKartNavyDark else if (isCompleted) ShopKartNavyDark else Color.Gray
                    )
                    if (isCurrent) {
                        Text(
                            text = when (index) {
                                0 -> "Awaiting payment verification by Admin."
                                1 -> "Payment verified successfully. Preparing your package."
                                2 -> "Items are being packed and dispatched from warehouse."
                                3 -> "Package has departed facility and is in transit."
                                4 -> "Our delivery courier is on the way to your address."
                                5 -> "Package was successfully delivered."
                                else -> ""
                            },
                            fontSize = 11.sp,
                            color = ShopKartGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
