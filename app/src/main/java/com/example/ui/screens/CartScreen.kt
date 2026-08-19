package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItemDetail
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartBorder
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartNavyMedium
import com.example.ui.theme.ShopKartRed
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel
import org.json.JSONArray

@Composable
fun CartScreen(
    viewModel: ShopViewModel,
    onProceedToCheckout: () -> Unit,
    onProductClick: (Long) -> Unit,
    onContinueShopping: () -> Unit
) {
    val activeCartItems by viewModel.activeCartItems.collectAsState()
    val savedForLaterItems by viewModel.savedForLaterItems.collectAsState()
    val settings by viewModel.appSettings.collectAsState()

    val deliveryThreshold = settings["delivery_threshold"]?.toDoubleOrNull() ?: 100.0
    val standardDeliveryFee = settings["delivery_fee"]?.toDoubleOrNull() ?: 79.0

    val subtotal = activeCartItems.sumOf { it.itemTotal }
    val totalSavings = activeCartItems.sumOf { it.itemSavings }
    val isFreeDelivery = subtotal >= deliveryThreshold
    val amountNeededForFree = (deliveryThreshold - subtotal).coerceAtLeast(0.0)
    val progressToFree = if (deliveryThreshold > 0) (subtotal / deliveryThreshold).toFloat().coerceIn(0f, 1f) else 1f

    if (activeCartItems.isEmpty() && savedForLaterItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ShopKartBackground)
                .padding(24.dp)
                .testTag("empty_cart_container"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Empty Cart",
                        tint = Color.LightGray,
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your ShopKart Cart is empty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
                Text(
                    text = "Your shopping cart is waiting for you! Explore our wide range of products.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onContinueShopping,
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("continue_shopping_btn")
                ) {
                    Text("Explore Deals & Products", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("cart_screen_container")
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // 1. Free Delivery Threshold Banner
            if (activeCartItems.isNotEmpty()) {
                item {
                    Surface(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (isFreeDelivery) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ShopKartGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Your order qualifies for FREE Delivery!",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ShopKartGreen
                                    )
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = ShopKartAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Add ₹${"%,.0f".format(amountNeededForFree)} more for FREE Delivery",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ShopKartNavyDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { progressToFree },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = ShopKartAmber,
                                    trackColor = Color(0xFFE5E7EB),
                                )
                            }
                        }
                    }
                }
            }

            // 2. Subtotal & Proceed to Checkout Sticky summary card
            if (activeCartItems.isNotEmpty()) {
                item {
                    Surface(
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Subtotal (${activeCartItems.sumOf { it.quantity }} items):",
                                    fontSize = 15.sp,
                                    color = ShopKartNavyDark
                                )
                                Text(
                                    text = "₹${"%,.2f".format(subtotal)}",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ShopKartNavyDark
                                )
                            }

                            if (totalSavings > 0) {
                                Text(
                                    text = "You are saving ₹${"%,.2f".format(totalSavings)} on this order!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ShopKartGreen,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onProceedToCheckout,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ShopKartYellow,
                                    contentColor = ShopKartNavyDark
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("proceed_to_checkout_btn")
                            ) {
                                Text(
                                    text = "Proceed to Buy (${activeCartItems.sumOf { it.quantity }} items)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. Active Cart Items List
            items(activeCartItems, key = { it.cartItemId }) { item ->
                CartItemRow(
                    item = item,
                    onQuantityChange = { q -> viewModel.updateCartQuantity(item.cartItemId, q) },
                    onRemove = { viewModel.removeCartItem(item.cartItemId) },
                    onSaveForLater = { viewModel.toggleSaveForLater(item.cartItemId, true) },
                    onProductClick = { onProductClick(item.product.id) }
                )
            }

            // 4. Saved for Later Section
            if (savedForLaterItems.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Saved for Later (${savedForLaterItems.size} items)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                    }
                }

                items(savedForLaterItems, key = { it.cartItemId }) { item ->
                    SavedForLaterRow(
                        item = item,
                        onMoveToCart = { viewModel.toggleSaveForLater(item.cartItemId, false) },
                        onRemove = { viewModel.removeCartItem(item.cartItemId) },
                        onProductClick = { onProductClick(item.product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemDetail,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onSaveForLater: () -> Unit,
    onProductClick: () -> Unit
) {
    var imageUrl = ""
    try {
        val arr = JSONArray(item.product.imagesJson)
        if (arr.length() > 0) imageUrl = arr.getString(0)
    } catch (_: Exception) {}

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("cart_item_${item.product.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Product Thumbnail
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF9FAFB))
                        .clickable { onProductClick() }
                ) {
                    if (imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = item.product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.product.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ShopKartNavyDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { onProductClick() }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "₹${"%,.0f".format(item.product.price)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = ShopKartNavyDark
                        )
                        if (item.product.mrp > item.product.price) {
                            Text(
                                text = "₹${"%,.0f".format(item.product.mrp)}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    if (item.selectedSize.isNotBlank() || item.selectedColor.isNotBlank()) {
                        Text(
                            text = listOfNotNull(
                                if (item.selectedSize.isNotBlank()) "Size: ${item.selectedSize}" else null,
                                if (item.selectedColor.isNotBlank()) "Colour: ${item.selectedColor}" else null
                            ).joinToString(" | "),
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Text(
                        text = "In Stock",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartGreen,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(8.dp))

            // Stepper and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Quantity Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(6.dp))
                        .background(Color(0xFFF9FAFB))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { onQuantityChange(item.quantity - 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = if (item.quantity == 1) ShopKartRed else Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${item.quantity}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Delete",
                        color = ShopKartCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onRemove() }
                    )
                    Text(
                        text = "Save for later",
                        color = ShopKartCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onSaveForLater() }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedForLaterRow(
    item: CartItemDetail,
    onMoveToCart: () -> Unit,
    onRemove: () -> Unit,
    onProductClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ShopKartNavyDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "₹${"%,.0f".format(item.product.price)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onMoveToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Move to cart", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onRemove,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Delete", fontSize = 11.sp)
                }
            }
        }
    }
}
