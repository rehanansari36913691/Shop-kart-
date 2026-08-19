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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.ShopKartNavyMedium
import com.example.ui.theme.ShopKartRed
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: ShopViewModel,
    onManageOrdersClick: () -> Unit,
    onManageProductsClick: () -> Unit,
    onManageCouponsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onViewOrderClick: (String) -> Unit
) {
    val metrics by viewModel.adminMetrics.collectAsState()
    val allOrders by viewModel.allOrdersForAdmin.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("admin_dashboard_screen")
    ) {
        // Header
        item {
            Surface(
                color = ShopKartNavyDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ShopKart Admin Console",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = ShopKartAmber
                    )
                    Text(
                        text = "Live Store Management & Fulfillment Dashboard",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Metrics Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "Live Store Analytics",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Revenue & Orders row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Total Revenue",
                        value = "₹${"%,.2f".format(metrics.totalRevenue)}",
                        icon = Icons.Default.AttachMoney,
                        color = ShopKartGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total Orders",
                        value = "${metrics.totalOrders}",
                        icon = Icons.Default.ShoppingBag,
                        color = ShopKartNavyDark,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pending & Returns row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Pending Payments",
                        value = "${metrics.pendingPayments}",
                        icon = Icons.Default.HourglassEmpty,
                        color = if (metrics.pendingPayments > 0) ShopKartRed else ShopKartNavyDark,
                        modifier = Modifier.weight(1f),
                        onClick = onManageOrdersClick
                    )
                    MetricCard(
                        title = "Return Requests",
                        value = "${metrics.returnRequests}",
                        icon = Icons.Default.Restore,
                        color = if (metrics.returnRequests > 0) ShopKartAmber else ShopKartNavyDark,
                        modifier = Modifier.weight(1f),
                        onClick = onManageOrdersClick
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Products & Customers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Active Products",
                        value = "${metrics.totalProducts}",
                        icon = Icons.Default.Inventory,
                        color = ShopKartCyan,
                        modifier = Modifier.weight(1f),
                        onClick = onManageProductsClick
                    )
                    MetricCard(
                        title = "Registered Users",
                        value = "${metrics.totalCustomers}",
                        icon = Icons.Default.People,
                        color = ShopKartNavyDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Management Navigation Hub
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "Management Portals",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                AdminNavCard(
                    title = "Orders & Fulfillment",
                    subtitle = "Verify UPI payments, update tracking, process returns",
                    icon = Icons.Default.LocalShipping,
                    onClick = onManageOrdersClick
                )

                AdminNavCard(
                    title = "Product Catalog Manager",
                    subtitle = "Add new items, update pricing, stock & variants",
                    icon = Icons.Default.Inventory,
                    onClick = onManageProductsClick
                )

                AdminNavCard(
                    title = "Coupons & Promotional Offers",
                    subtitle = "Create discount vouchers and promo codes",
                    icon = Icons.Default.LocalOffer,
                    onClick = onManageCouponsClick
                )

                AdminNavCard(
                    title = "Store & Telegram Settings",
                    subtitle = "UPI ID, delivery charges, Telegram notification bot",
                    icon = Icons.Default.Settings,
                    onClick = onSettingsClick
                )
            }
        }

        // Recent Orders list in Admin
        item {
            Text(
                text = "Recent Orders",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ShopKartNavyDark,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
            )
        }

        items(allOrders.take(5)) { ord ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable { onViewOrderClick(ord.id) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Order #${ord.id}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                        Text(
                            text = "₹${"%,.2f".format(ord.finalTotal)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = ShopKartNavyDark
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${ord.paymentMethod} • ${ord.paymentStatus}",
                            fontSize = 12.sp,
                            color = if (ord.paymentStatus == "Payment Confirmed") ShopKartGreen else ShopKartAmber,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (ord.paymentStatus == "Payment Verification Pending") {
                            Button(
                                onClick = { viewModel.confirmPayment(ord.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Confirm Payment", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 11.sp, color = Color.Gray)
                Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun AdminNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ShopKartAmberLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = ShopKartNavyDark, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ShopKartNavyDark)
                    Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
