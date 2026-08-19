package com.example.ui.screens.orders

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.OrderedItem
import com.example.ui.components.TrackingTimeline
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartRed
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: ShopViewModel,
    onProductClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val orderFlow = remember(orderId) { viewModel.getOrderByIdFlow(orderId) }
    val order by orderFlow.collectAsState(initial = null)

    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }

    var showReturnDialog by remember { mutableStateOf(false) }
    var returnReason by remember { mutableStateOf("") }

    var showReplacementDialog by remember { mutableStateOf(false) }
    var replacementReason by remember { mutableStateOf("") }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ShopKartAmber)
        }
        return
    }

    val ord = order!!

    // Parse ordered items
    val itemsList = remember(ord.itemsJson) {
        val list = mutableListOf<OrderedItem>()
        try {
            val arr = JSONArray(ord.itemsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    OrderedItem(
                        productId = obj.optLong("productId"),
                        productName = obj.optString("productName"),
                        brand = obj.optString("brand"),
                        price = obj.optDouble("price"),
                        mrp = obj.optDouble("mrp"),
                        quantity = obj.optInt("quantity", 1),
                        selectedSize = obj.optString("selectedSize"),
                        selectedColor = obj.optString("selectedColor"),
                        imageUrl = obj.optString("imageUrl")
                    )
                )
            }
        } catch (_: Exception) {}
        list
    }

    // Parse Address
    var customerName = ""
    var addressDetails = ""
    var customerPhone = ""
    try {
        val addr = JSONObject(ord.addressJson)
        customerName = addr.optString("fullName", "")
        customerPhone = addr.optString("mobile", "")
        addressDetails = "${addr.optString("house", "")}, ${addr.optString("area", "")}, ${addr.optString("city", "")}, ${addr.optString("state", "")} - ${addr.optString("pincode", "")}"
    } catch (_: Exception) {}

    val canCancel = ord.orderStatus in listOf("Payment Verification Pending", "Payment Confirmed", "Processing")
    val canReturn = ord.orderStatus == "Delivered"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("order_detail_screen")
    ) {
        // Header Bar
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Order Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Order #${ord.id}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Copy ID",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartCyan,
                        modifier = Modifier.clickable {
                            clipboardManager.setText(AnnotatedString(ord.id))
                            Toast.makeText(context, "Order ID copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // 1. Live Tracking Timeline Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Delivery Tracking",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )

                        if (ord.trackingId.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Courier: ${ord.courierName} | AWB / Tracking ID: ${ord.trackingId}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ShopKartCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        TrackingTimeline(
                            orderStatus = ord.orderStatus,
                            paymentStatus = ord.paymentStatus
                        )
                    }
                }
            }

            // 2. Cancellation / Return Actions
            if (canCancel || canReturn) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Order Actions & Support",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartNavyDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (canCancel) {
                                OutlinedButton(
                                    onClick = { showCancelDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ShopKartRed),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Cancel, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cancel Order", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (canReturn) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showReturnDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Request Return")
                                    }
                                    OutlinedButton(
                                        onClick = { showReplacementDialog = true },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Request Replacement")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Shipping Address Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = ShopKartCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Shipping Address",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartNavyDark
                            )
                        }
                        Text(
                            text = customerName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ShopKartNavyDark,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = addressDetails,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Phone: $customerPhone",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // 4. Payment Information Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payment, null, tint = ShopKartAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Payment Information",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment Method:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = ord.paymentMethod + if (ord.upiAppUsed.isNotBlank()) " (${ord.upiAppUsed})" else "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (ord.upiTransactionId.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("UPI UTR / Ref ID:", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    text = ord.upiTransactionId,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment Status:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = ord.paymentStatus,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ord.paymentStatus == "Payment Confirmed") ShopKartGreen else ShopKartAmber
                            )
                        }
                    }
                }
            }

            // 5. Items Ordered
            item {
                Text(
                    text = "Items in this Order (${itemsList.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(itemsList) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onProductClick(item.productId) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF9FAFB))
                        ) {
                            if (item.imageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.productName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.productName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ShopKartNavyDark,
                                maxLines = 2
                            )
                            if (item.selectedSize.isNotBlank() || item.selectedColor.isNotBlank()) {
                                Text(
                                    text = listOfNotNull(
                                        if (item.selectedSize.isNotBlank()) "Size: ${item.selectedSize}" else null,
                                        if (item.selectedColor.isNotBlank()) "Color: ${item.selectedColor}" else null
                                    ).joinToString(" | "),
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = "Qty: ${item.quantity} • ₹${"%,.2f".format(item.price * item.quantity)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartNavyDark,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // 6. Price Summary
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Order Summary",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Item(s) Subtotal:", fontSize = 12.sp, color = Color.Gray)
                            Text("₹${"%,.2f".format(ord.subtotal)}", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Shipping & Handling:", fontSize = 12.sp, color = Color.Gray)
                            Text(if (ord.deliveryCharge == 0.0) "FREE" else "₹${"%,.2f".format(ord.deliveryCharge)}", fontSize = 12.sp)
                        }
                        if (ord.discountAmount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Promotion (${ord.couponCode}):", fontSize = 12.sp, color = ShopKartGreen)
                                Text("-₹${"%,.2f".format(ord.discountAmount)}", fontSize = 12.sp, color = ShopKartGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Grand Total:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ShopKartNavyDark)
                            Text("₹${"%,.2f".format(ord.finalTotal)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ShopKartNavyDark)
                        }
                    }
                }
            }
        }
    }

    // Cancel Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Order #${ord.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Please select or enter the reason for cancellation:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        placeholder = { Text("e.g. Ordered by mistake, wrong address") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cancelReason.isNotBlank()) {
                            viewModel.cancelOrder(ord.id, cancelReason)
                            showCancelDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartRed)
                ) {
                    Text("Confirm Cancellation")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Back")
                }
            }
        )
    }

    // Return Dialog
    if (showReturnDialog) {
        AlertDialog(
            onDismissRequest = { showReturnDialog = false },
            title = { Text("Request Return for #${ord.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Please describe the issue with the item(s):", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = returnReason,
                        onValueChange = { returnReason = it },
                        placeholder = { Text("e.g. Size didn't fit, defective product") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (returnReason.isNotBlank()) {
                            viewModel.requestReturn(ord.id, returnReason)
                            showReturnDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark)
                ) {
                    Text("Submit Return Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDialog = false }) {
                    Text("Back")
                }
            }
        )
    }

    // Replacement Dialog
    if (showReplacementDialog) {
        AlertDialog(
            onDismissRequest = { showReplacementDialog = false },
            title = { Text("Request Replacement for #${ord.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Please describe what size/color replacement you need:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = replacementReason,
                        onValueChange = { replacementReason = it },
                        placeholder = { Text("e.g. Exchange for size UK 9") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (replacementReason.isNotBlank()) {
                            viewModel.requestReplacement(ord.id, replacementReason)
                            showReplacementDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark)
                ) {
                    Text("Submit Replacement")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReplacementDialog = false }) {
                    Text("Back")
                }
            }
        )
    }
}
