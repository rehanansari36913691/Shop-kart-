package com.example.ui.screens.orders

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.OrderEntity
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartNavyMedium
import com.example.ui.theme.ShopKartRed
import com.example.viewmodel.ShopViewModel
import org.json.JSONArray

@Composable
fun OrdersScreen(
    viewModel: ShopViewModel,
    onOrderClick: (String) -> Unit,
    onExploreProducts: () -> Unit
) {
    val orders by viewModel.userOrders.collectAsState()
    var selectedTab by remember { mutableStateOf("All") }

    val filteredOrders = remember(orders, selectedTab) {
        when (selectedTab) {
            "In Transit" -> orders.filter { it.orderStatus in listOf("Processing", "Shipped", "Out for Delivery") }
            "Delivered" -> orders.filter { it.orderStatus == "Delivered" }
            "Cancelled & Returns" -> orders.filter { it.orderStatus == "Cancelled" || it.orderStatus.startsWith("Return") || it.orderStatus.startsWith("Replacement") }
            else -> orders
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("orders_screen_container")
    ) {
        // Header
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Your Orders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "In Transit", "Delivered", "Cancelled & Returns").forEach { tab ->
                        FilterChip(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            label = { Text(tab, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ShopKartAmberLight)
                        )
                    }
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No orders found",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                    Text(
                        text = "You haven't placed any orders in this category yet.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderCard(order = order, onClick = { onOrderClick(order.id) })
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderEntity,
    onClick: () -> Unit
) {
    var firstImageUrl = ""
    var firstItemName = ""
    var itemCount = 0
    try {
        val itemsArr = JSONArray(order.itemsJson)
        itemCount = itemsArr.length()
        if (itemCount > 0) {
            val first = itemsArr.getJSONObject(0)
            firstItemName = first.optString("productName", "Item")
            firstImageUrl = first.optString("imageUrl", "")
        }
    } catch (_: Exception) {}

    val statusColor = when (order.orderStatus) {
        "Delivered" -> ShopKartGreen
        "Cancelled" -> ShopKartRed
        "Payment Verification Pending" -> ShopKartAmber
        else -> ShopKartCyan
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
            .testTag("order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Status Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.orderStatus,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                Text(
                    text = "ID: ${order.id}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Item Thumbnail & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF9FAFB))
                ) {
                    if (firstImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = firstImageUrl,
                            contentDescription = firstItemName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = firstItemName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ShopKartNavyDark,
                        maxLines = 2
                    )
                    if (itemCount > 1) {
                        Text(
                            text = "+ ${itemCount - 1} more items",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = "Total: ₹${"%,.2f".format(order.finalTotal)} • ${order.paymentMethod}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Details",
                    tint = Color.Gray
                )
            }
        }
    }
}
