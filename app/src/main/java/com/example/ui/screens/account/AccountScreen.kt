package com.example.ui.screens.account

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
fun AccountScreen(
    viewModel: ShopViewModel,
    onOrdersClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onAdminDashboardClick: () -> Unit,
    onAdminLoginClick: () -> Unit,
    onAuthClick: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val orders by viewModel.userOrders.collectAsState()
    val wishlist by viewModel.wishlistProducts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("account_screen_container")
    ) {
        // Top Profile Greeting Strip
        item {
            Surface(
                color = ShopKartNavyDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(ShopKartAmber),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = ShopKartNavyDark,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (currentUser != null) "Hello, ${currentUser!!.fullName}" else "Welcome to ShopKart",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (currentUser != null) currentUser!!.email else "Sign in for the best experience",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        if (currentUser == null) {
                            Button(
                                onClick = onAuthClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 4 Primary Amazon-Style Action Tiles
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccountTile(
                        title = "Your Orders",
                        subtitle = "${orders.size} orders placed",
                        icon = Icons.Default.ShoppingBag,
                        modifier = Modifier.weight(1f),
                        onClick = onOrdersClick
                    )
                    AccountTile(
                        title = "Your Wishlist",
                        subtitle = "${wishlist.size} saved items",
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.weight(1f),
                        onClick = onWishlistClick
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccountTile(
                        title = "Saved Addresses",
                        subtitle = "Manage delivery addresses",
                        icon = Icons.Default.LocationOn,
                        modifier = Modifier.weight(1f),
                        onClick = onAddressesClick
                    )
                    AccountTile(
                        title = "Admin Portal",
                        subtitle = "Orders, store & payments",
                        icon = Icons.Default.AdminPanelSettings,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (currentUser?.role == "ADMIN") {
                                onAdminDashboardClick()
                            } else {
                                onAdminLoginClick()
                            }
                        }
                    )
                }
            }
        }

        // Account Settings List
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "Account Settings & Support",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )

                    AccountRowItem(
                        icon = Icons.Default.Security,
                        title = "Security & Privacy Policy",
                        onClick = {}
                    )

                    AccountRowItem(
                        icon = Icons.Default.Help,
                        title = "Customer Support & FAQs",
                        onClick = {}
                    )

                    if (currentUser != null) {
                        AccountRowItem(
                            icon = Icons.Default.Logout,
                            title = "Sign Out",
                            titleColor = ShopKartRed,
                            onClick = { viewModel.logout() }
                        )
                    }
                }
            }
        }

        // Quick Switch to Admin demo shortcut
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Store Administration",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                    Text(
                        text = "Access ShopKart Admin Portal to manage orders, confirm UPI payments, add products, and configure Telegram bot notifications.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            viewModel.switchUserToAdmin {
                                onAdminDashboardClick()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopKartNavyDark),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Switch to Admin Dashboard (One-Click)")
                    }
                }
            }
        }
    }
}

@Composable
fun AccountTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ShopKartNavyDark,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ShopKartNavyDark
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AccountRowItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = ShopKartNavyDark,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = titleColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = titleColor)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
    HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(horizontal = 16.dp))
}
