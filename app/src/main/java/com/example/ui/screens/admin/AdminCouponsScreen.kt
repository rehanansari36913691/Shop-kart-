package com.example.ui.screens.admin

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.example.data.local.entities.CouponEntity
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartRed
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCouponsScreen(
    viewModel: ShopViewModel
) {
    val coupons by viewModel.allCouponsForAdmin.collectAsState()
    var showCouponSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("admin_coupons_screen")
    ) {
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Coupons & Offers",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
                Button(
                    onClick = { showCouponSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Coupon", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            items(coupons, key = { it.id }) { coupon ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = ShopKartAmberLight,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = coupon.code,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ShopKartNavyDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (coupon.discountType == "PERCENTAGE") "${coupon.discountValue.toInt()}% OFF" else "₹${coupon.discountValue.toInt()} FLAT OFF",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartGreen
                                )
                            }

                            Text(
                                text = "Min Order: ₹${"%,.0f".format(coupon.minOrderAmount)} | Max Discount: ₹${"%,.0f".format(coupon.maxDiscountAmount)}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Used: ${coupon.timesUsed} times",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }

                        IconButton(onClick = { viewModel.deleteCoupon(coupon) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ShopKartRed)
                        }
                    }
                }
            }
        }
    }

    if (showCouponSheet) {
        var code by remember { mutableStateOf("") }
        var discountType by remember { mutableStateOf("FLAT") }
        var discountVal by remember { mutableStateOf("100") }
        var minOrder by remember { mutableStateOf("499") }
        var maxDiscount by remember { mutableStateOf("100") }

        ModalBottomSheet(onDismissRequest = { showCouponSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Create New Coupon Code",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Coupon Code (e.g. FLASH200)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = discountVal,
                        onValueChange = { discountVal = it },
                        label = { Text("Discount Value (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minOrder,
                        onValueChange = { minOrder = it },
                        label = { Text("Min Order Amount (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (code.isNotBlank()) {
                            viewModel.saveCoupon(
                                CouponEntity(
                                    code = code.trim(),
                                    discountType = "FLAT",
                                    discountValue = discountVal.toDoubleOrNull() ?: 100.0,
                                    minOrderAmount = minOrder.toDoubleOrNull() ?: 499.0,
                                    maxDiscountAmount = maxDiscount.toDoubleOrNull() ?: 500.0,
                                    isActive = true
                                )
                            ) {
                                showCouponSheet = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("Create Coupon", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
