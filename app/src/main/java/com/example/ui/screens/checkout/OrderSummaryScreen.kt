package com.example.ui.screens.checkout

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.model.CartItemDetail
import com.example.data.model.OrderPricingSummary
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

@Composable
fun OrderSummaryScreen(
    addressId: Long,
    buyNowProductId: Long?,
    selectedSize: String,
    selectedColor: String,
    viewModel: ShopViewModel,
    onChangeAddress: () -> Unit,
    onProceedToPayment: (String) -> Unit
) {
    val addresses by viewModel.savedAddresses.collectAsState()
    val activeCartItems by viewModel.activeCartItems.collectAsState()
    val activeCoupons by viewModel.activeCoupons.collectAsState()

    val selectedAddress = addresses.firstOrNull { it.id == addressId } ?: addresses.firstOrNull()

    // Determine checkout items: either Buy Now product or Active Cart
    var checkoutItems by remember { mutableStateOf<List<CartItemDetail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(buyNowProductId, activeCartItems) {
        if (buyNowProductId != null && buyNowProductId > 0) {
            val singleProd = viewModel.getProductById(buyNowProductId)
            if (singleProd != null) {
                checkoutItems = listOf(
                    CartItemDetail(
                        cartItemId = -1,
                        product = singleProd,
                        quantity = 1,
                        selectedSize = selectedSize,
                        selectedColor = selectedColor,
                        isSavedForLater = false
                    )
                )
            }
        } else {
            checkoutItems = activeCartItems
        }
        isLoading = false
    }

    var couponInput by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<CouponEntity?>(null) }
    var couponMessage by remember { mutableStateOf("") }
    var pricingSummary by remember { mutableStateOf(OrderPricingSummary(0.0, 0.0, 0.0, 0.0, 0.0, false)) }

    LaunchedEffect(checkoutItems, appliedCoupon) {
        if (checkoutItems.isNotEmpty()) {
            pricingSummary = viewModel.calculatePricing(checkoutItems, appliedCoupon)
        }
    }

    if (isLoading || selectedAddress == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ShopKartAmber)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("order_summary_screen")
    ) {
        // Step Header
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Step 2 of 3: Review Your Order",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
                Text(
                    text = "Verify delivery address, items, and apply promo codes",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // 1. Delivery Address Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = ShopKartCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Delivering to ${selectedAddress.fullName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartNavyDark
                                )
                            }
                            Text(
                                text = "Change",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartCyan,
                                modifier = Modifier.clickable { onChangeAddress() }
                            )
                        }

                        Text(
                            text = "${selectedAddress.house}, ${selectedAddress.area}, ${selectedAddress.city}, ${selectedAddress.state} - ${selectedAddress.pincode}",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Phone: ${selectedAddress.mobile}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // 2. Coupon Code Box
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
                            Icon(Icons.Default.LocalOffer, null, tint = ShopKartAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Coupons & Promotional Codes",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it.uppercase() },
                                placeholder = { Text("Enter Promo Code", fontSize = 13.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("coupon_input_field")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val matched = activeCoupons.firstOrNull { it.code.equals(couponInput.trim(), ignoreCase = true) }
                                    if (matched != null) {
                                        if (pricingSummary.subtotal >= matched.minOrderAmount) {
                                            appliedCoupon = matched
                                            couponMessage = "✓ Coupon ${matched.code} applied successfully!"
                                        } else {
                                            couponMessage = "Min order of ₹${matched.minOrderAmount} required for this coupon."
                                        }
                                    } else {
                                        couponMessage = "Invalid or expired promo coupon code."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ShopKartNavyDark),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("apply_coupon_btn")
                            ) {
                                Text("Apply")
                            }
                        }

                        if (couponMessage.isNotBlank()) {
                            Text(
                                text = couponMessage,
                                fontSize = 12.sp,
                                color = if (appliedCoupon != null) ShopKartGreen else ShopKartRed,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Quick Coupon Suggestion Pills
                        if (activeCoupons.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Available Offers:", fontSize = 11.sp, color = Color.Gray)
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                activeCoupons.forEach { c ->
                                    Surface(
                                        color = ShopKartAmberLight,
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.clickable {
                                            couponInput = c.code
                                            appliedCoupon = c
                                            couponMessage = "✓ Applied ${c.code}"
                                        }
                                    ) {
                                        Text(
                                            text = c.code,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ShopKartNavyDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Price Breakdown
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
                            text = "Price Details (${checkoutItems.sumOf { it.quantity }} items)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Items Subtotal", fontSize = 13.sp, color = Color.DarkGray)
                            Text("₹${"%,.2f".format(pricingSummary.subtotal)}", fontSize = 13.sp, color = ShopKartNavyDark)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Fee", fontSize = 13.sp, color = Color.DarkGray)
                            if (pricingSummary.isFreeDelivery) {
                                Row {
                                    Text("₹79.00", fontSize = 12.sp, color = Color.Gray, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("FREE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ShopKartGreen)
                                }
                            } else {
                                Text("₹${"%,.2f".format(pricingSummary.deliveryCharge)}", fontSize = 13.sp, color = ShopKartNavyDark)
                            }
                        }

                        if (pricingSummary.discount > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Coupon Discount", fontSize = 13.sp, color = ShopKartGreen, fontWeight = FontWeight.SemiBold)
                                Text("-₹${"%,.2f".format(pricingSummary.discount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ShopKartGreen)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Order Total", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ShopKartNavyDark)
                            Text("₹${"%,.2f".format(pricingSummary.finalTotal)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ShopKartNavyDark)
                        }

                        if (pricingSummary.totalSavings > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "You will save ₹${"%,.2f".format(pricingSummary.totalSavings)} on this order",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartGreen
                            )
                        }
                    }
                }
            }

            // 4. Order Items
            item {
                Text(
                    text = "Items in Order",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            items(checkoutItems) { item ->
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
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF9FAFB))
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
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ShopKartNavyDark,
                                maxLines = 2
                            )
                            Row(
                                modifier = Modifier.padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Qty: ${item.quantity}", fontSize = 12.sp, color = Color.Gray)
                                Text("₹${"%,.0f".format(item.itemTotal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ShopKartNavyDark)
                            }
                        }
                    }
                }
            }
        }

        // Bottom Proceed to Payment Button
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(14.dp)) {
                Button(
                    onClick = {
                        onProceedToPayment(appliedCoupon?.code ?: "")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("proceed_to_payment_btn")
                ) {
                    Text(
                        text = "Proceed to Payment (₹${"%,.2f".format(pricingSummary.finalTotal)}) →",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
