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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AddressEntity
import com.example.ui.screens.checkout.AddressFormBottomSheet
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel

@Composable
fun SavedAddressesScreen(
    viewModel: ShopViewModel
) {
    val addresses by viewModel.savedAddresses.collectAsState()
    var showAddressFormSheet by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<AddressEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("saved_addresses_screen")
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
                    text = "Saved Addresses",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
                Button(
                    onClick = {
                        editingAddress = null
                        showAddressFormSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (addresses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No addresses saved yet",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            editingAddress = null
                            showAddressFormSheet = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark)
                    ) {
                        Text("Add Address")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                items(addresses) { addr ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = addr.fullName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartNavyDark
                                )
                                Surface(
                                    color = if (addr.addressType == "Home") Color(0xFFE8F5E9) else Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = addr.addressType,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${addr.house}, ${addr.area}, ${addr.city}, ${addr.state} - ${addr.pincode}",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Mobile: ${addr.mobile}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                    text = "Edit",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartCyan,
                                    modifier = Modifier.clickable {
                                        editingAddress = addr
                                        showAddressFormSheet = true
                                    }
                                )
                                Text(
                                    text = "Remove",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red,
                                    modifier = Modifier.clickable { viewModel.deleteAddress(addr) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddressFormSheet) {
        AddressFormBottomSheet(
            initialAddress = editingAddress,
            onDismiss = { showAddressFormSheet = false },
            onSave = { newAddr ->
                viewModel.saveAddress(newAddr) {
                    showAddressFormSheet = false
                }
            }
        )
    }
}
