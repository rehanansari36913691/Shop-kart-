package com.example.ui.screens.admin

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.OrderEntity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    viewModel: ShopViewModel,
    onViewOrderDetail: (String) -> Unit
) {
    val orders by viewModel.allOrdersForAdmin.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    var selectedOrderForTracking by remember { mutableStateOf<OrderEntity?>(null) }
    var selectedOrderForStatus by remember { mutableStateOf<OrderEntity?>(null) }

    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "Pending Payments" -> orders.filter { it.paymentStatus == "Payment Verification Pending" }
            "Processing" -> orders.filter { it.orderStatus == "Processing" || it.orderStatus == "Payment Confirmed" }
            "Shipped" -> orders.filter { it.orderStatus == "Shipped" || it.orderStatus == "Out for Delivery" }
            "Delivered" -> orders.filter { it.orderStatus == "Delivered" }
            "Returns" -> orders.filter { it.orderStatus.startsWith("Return") || it.orderStatus.startsWith("Replacement") }
            else -> orders
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("admin_orders_screen")
    ) {
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Order Management & Fulfillment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "Pending Payments", "Processing", "Shipped", "Delivered", "Returns").forEach { tab ->
                        FilterChip(
                            selected = selectedFilter == tab,
                            onClick = { selectedFilter = tab },
                            label = { Text(tab, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ShopKartAmberLight)
                        )
                    }
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders match this filter.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(filteredOrders, key = { it.id }) { ord ->
                    AdminOrderCard(
                        order = ord,
                        onConfirmPayment = { viewModel.confirmPayment(ord.id) },
                        onRejectPayment = { viewModel.rejectPayment(ord.id) },
                        onUpdateTracking = { selectedOrderForTracking = ord },
                        onChangeStatus = { selectedOrderForStatus = ord },
                        onCardClick = { onViewOrderDetail(ord.id) }
                    )
                }
            }
        }
    }

    // Update Tracking Bottom Sheet
    if (selectedOrderForTracking != null) {
        val ord = selectedOrderForTracking!!
        var trackingId by remember { mutableStateOf(if (ord.trackingId.isNotBlank()) ord.trackingId else "BD-" + (100000..999999).random()) }
        var courier by remember { mutableStateOf(if (ord.courierName.isNotBlank()) ord.courierName else "BlueDart Express") }
        var shippingDate by remember { mutableStateOf("Today") }
        var expectedDelivery by remember { mutableStateOf("Delivery in 2 Days") }

        ModalBottomSheet(onDismissRequest = { selectedOrderForTracking = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Update Tracking & Dispatch Info for #${ord.id}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = courier,
                    onValueChange = { courier = it },
                    label = { Text("Courier / Delivery Partner") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trackingId,
                    onValueChange = { trackingId = it },
                    label = { Text("Tracking ID / AWB Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = expectedDelivery,
                    onValueChange = { expectedDelivery = it },
                    label = { Text("Expected Delivery Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.updateTrackingInfo(
                            orderId = ord.id,
                            trackingId = trackingId.trim(),
                            courier = courier.trim(),
                            shippingDate = shippingDate,
                            expectedDelivery = expectedDelivery
                        )
                        selectedOrderForTracking = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("Save & Mark as Shipped (Notifies Telegram)", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Change Order Status Dialog
    if (selectedOrderForStatus != null) {
        val ord = selectedOrderForStatus!!
        val statusOptions = listOf(
            "Payment Confirmed",
            "Processing",
            "Shipped",
            "Out for Delivery",
            "Delivered",
            "Return Approved & Refunded",
            "Replacement Shipped",
            "Cancelled"
        )

        AlertDialog(
            onDismissRequest = { selectedOrderForStatus = null },
            title = { Text("Update Status: #${ord.id}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    statusOptions.forEach { status ->
                        Text(
                            text = status,
                            fontSize = 14.sp,
                            fontWeight = if (ord.orderStatus == status) FontWeight.Bold else FontWeight.Normal,
                            color = if (ord.orderStatus == status) ShopKartCyan else ShopKartNavyDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateOrderStatus(ord.id, status)
                                    selectedOrderForStatus = null
                                }
                                .padding(vertical = 10.dp)
                        )
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedOrderForStatus = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminOrderCard(
    order: OrderEntity,
    onConfirmPayment: () -> Unit,
    onRejectPayment: () -> Unit,
    onUpdateTracking: () -> Unit,
    onChangeStatus: () -> Unit,
    onCardClick: () -> Unit
) {
    var customerName = ""
    var customerPhone = ""
    try {
        val addr = JSONObject(order.addressJson)
        customerName = addr.optString("fullName", "")
        customerPhone = addr.optString("mobile", "")
    } catch (_: Exception) {}

    val isPendingPayment = order.paymentStatus == "Payment Verification Pending"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.id}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                    Text(
                        text = "Customer: $customerName ($customerPhone)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "₹${"%,.2f".format(order.finalTotal)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = ShopKartNavyDark
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Payment & UTR Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mode: ${order.paymentMethod} ${if (order.upiAppUsed.isNotBlank()) "(${order.upiAppUsed})" else ""}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (order.upiTransactionId.isNotBlank()) {
                    Text(
                        text = "UTR: ${order.upiTransactionId}",
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
                Text(
                    text = "Payment: ${order.paymentStatus}",
                    fontSize = 12.sp,
                    color = if (order.paymentStatus == "Payment Confirmed") ShopKartGreen else ShopKartAmber,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Status: ${order.orderStatus}",
                    fontSize = 12.sp,
                    color = ShopKartNavyDark,
                    fontWeight = FontWeight.Bold
                )
            }

            if (order.trackingId.isNotBlank()) {
                Text(
                    text = "Tracking: ${order.courierName} - ${order.trackingId}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isPendingPayment) {
                    Button(
                        onClick = onConfirmPayment,
                        colors = ButtonDefaults.buttonColors(containerColor = ShopKartGreen),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(34.dp)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirm Pay", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onRejectPayment,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ShopKartRed),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(34.dp)
                    ) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = onUpdateTracking,
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Icon(Icons.Default.LocalShipping, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tracking", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onChangeStatus,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Status", fontSize = 11.sp)
                }
            }
        }
    }
}
