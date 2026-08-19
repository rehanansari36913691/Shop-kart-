package com.example.ui.screens.checkout

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel
import org.json.JSONObject

@Composable
fun OrderSuccessScreen(
    orderId: String,
    viewModel: ShopViewModel,
    onTrackOrder: (String) -> Unit,
    onContinueShopping: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val orderFlow = remember(orderId) { viewModel.getOrderByIdFlow(orderId) }
    val order by orderFlow.collectAsState(initial = null)

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ShopKartAmber)
        }
        return
    }

    val ord = order!!

    var customerName = ""
    var addressSummary = ""
    try {
        val addr = JSONObject(ord.addressJson)
        customerName = addr.optString("fullName", "")
        addressSummary = "${addr.optString("house", "")}, ${addr.optString("area", "")}, ${addr.optString("city", "")} - ${addr.optString("pincode", "")}"
    } catch (_: Exception) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("order_success_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Big Success Badge
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(ShopKartGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Order Placed Successfully!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = ShopKartNavyDark
        )

        Text(
            text = "Thank you for shopping with ShopKart",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Order Details Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Order ID & Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Order ID:", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = ord.id,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = ShopKartNavyDark
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(ord.id))
                            Toast.makeText(context, "Order ID copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy ID", fontSize = 11.sp)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Payment Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Payment Status:", fontSize = 13.sp, color = Color.DarkGray)
                    Surface(
                        color = ShopKartAmberLight,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.HourglassEmpty, null, tint = ShopKartNavyDark, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = ord.paymentStatus,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartNavyDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Paid:", fontSize = 13.sp, color = Color.DarkGray)
                    Text(
                        text = "₹${"%,.2f".format(ord.finalTotal)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = ShopKartNavyDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Payment Mode:", fontSize = 13.sp, color = Color.DarkGray)
                    Text(
                        text = ord.paymentMethod,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                }

                if (ord.upiTransactionId.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("UPI UTR / Ref ID:", fontSize = 13.sp, color = Color.DarkGray)
                        Text(
                            text = ord.upiTransactionId,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ShopKartCyan
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Delivery Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, null, tint = ShopKartGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Expected Delivery: ${ord.expectedDeliveryDate}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartGreen
                    )
                }

                if (addressSummary.isNotBlank()) {
                    Text(
                        text = "Shipping to: $customerName\n$addressSummary",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Button(
            onClick = { onTrackOrder(ord.id) },
            colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("track_order_success_btn")
        ) {
            Text("Track & View Order Details", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onContinueShopping,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            Text("Continue Shopping", fontWeight = FontWeight.SemiBold)
        }
    }
}
