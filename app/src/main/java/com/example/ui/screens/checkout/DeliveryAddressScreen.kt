package com.example.ui.screens.checkout

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryAddressScreen(
    viewModel: ShopViewModel,
    buyNowProductId: Long?,
    selectedSize: String,
    selectedColor: String,
    onNext: (Long) -> Unit
) {
    val addresses by viewModel.savedAddresses.collectAsState()
    var selectedAddressId by remember(addresses) {
        mutableLongStateOf(addresses.firstOrNull { it.isDefault }?.id ?: addresses.firstOrNull()?.id ?: 0L)
    }

    var showAddressFormSheet by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<AddressEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("delivery_address_screen")
    ) {
        // Step Indicator: Step 1 of 3: Delivery Information
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Step 1 of 3: Select Delivery Address",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
                Text(
                    text = "Choose where you would like your order delivered",
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
            // Add New Address Card Button
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(ShopKartCyan)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            editingAddress = null
                            showAddressFormSheet = true
                        }
                        .testTag("add_new_address_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Address",
                            tint = ShopKartCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add a new delivery address",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartCyan
                        )
                    }
                }
            }

            // Saved Addresses List
            items(addresses) { addr ->
                val isSelected = addr.id == selectedAddressId

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) ShopKartAmber else Color(0xFFE5E7EB))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedAddressId = addr.id }
                        .testTag("address_card_${addr.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedAddressId = addr.id },
                                    colors = RadioButtonDefaults.colors(selectedColor = ShopKartAmber)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = addr.fullName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartNavyDark
                                )
                            }

                            Surface(
                                color = if (addr.addressType == "Home") Color(0xFFE8F5E9) else Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (addr.addressType == "Home") Icons.Default.Home else Icons.Default.Work,
                                        contentDescription = null,
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = addr.addressType,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }

                        // Address details
                        Column(modifier = Modifier.padding(start = 36.dp, top = 2.dp)) {
                            Text(
                                text = "${addr.house}, ${addr.area}",
                                fontSize = 13.sp,
                                color = ShopKartNavyDark
                            )
                            if (addr.landmark.isNotBlank()) {
                                Text(
                                    text = "Landmark: ${addr.landmark}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = "${addr.city}, ${addr.state} - ${addr.pincode}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ShopKartNavyDark
                            )
                            Text(
                                text = "Phone: ${addr.mobile} ${if (addr.altPhone.isNotBlank()) "(Alt: ${addr.altPhone})" else ""}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            if (addr.deliveryInstructions.isNotBlank()) {
                                Text(
                                    text = "Instructions: ${addr.deliveryInstructions}",
                                    fontSize = 11.sp,
                                    color = ShopKartCyan,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                    text = "Edit",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartCyan,
                                    modifier = Modifier.clickable {
                                        editingAddress = addr
                                        showAddressFormSheet = true
                                    }
                                )
                                Text(
                                    text = "Delete",
                                    fontSize = 12.sp,
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

        // Bottom Deliver to this Address Button
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(14.dp)) {
                Button(
                    onClick = {
                        if (selectedAddressId > 0) {
                            onNext(selectedAddressId)
                        }
                    },
                    enabled = selectedAddressId > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("deliver_to_address_btn")
                ) {
                    Text("Deliver to this address → Next", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }

    // Add / Edit Address Bottom Sheet Form
    if (showAddressFormSheet) {
        AddressFormBottomSheet(
            initialAddress = editingAddress,
            onDismiss = { showAddressFormSheet = false },
            onSave = { newAddr ->
                viewModel.saveAddress(newAddr) { savedId ->
                    selectedAddressId = savedId
                    showAddressFormSheet = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressFormBottomSheet(
    initialAddress: AddressEntity?,
    onDismiss: () -> Unit,
    onSave: (AddressEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(initialAddress?.fullName ?: "") }
    var mobile by remember { mutableStateOf(initialAddress?.mobile ?: "") }
    var email by remember { mutableStateOf(initialAddress?.email ?: "") }
    var altPhone by remember { mutableStateOf(initialAddress?.altPhone ?: "") }
    var house by remember { mutableStateOf(initialAddress?.house ?: "") }
    var area by remember { mutableStateOf(initialAddress?.area ?: "") }
    var landmark by remember { mutableStateOf(initialAddress?.landmark ?: "") }
    var pincode by remember { mutableStateOf(initialAddress?.pincode ?: "") }
    var city by remember { mutableStateOf(initialAddress?.city ?: "") }
    var state by remember { mutableStateOf(initialAddress?.state ?: "") }
    var addressType by remember { mutableStateOf(initialAddress?.addressType ?: "Home") }
    var deliveryInstructions by remember { mutableStateOf(initialAddress?.deliveryInstructions ?: "") }

    var errorMessage by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = if (initialAddress == null) "Add a new delivery address" else "Edit delivery address",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ShopKartNavyDark
            )

            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("address_fullname_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it.take(10) },
                    label = { Text("Mobile Number (10-digits) *") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("address_mobile_input")
                )
                OutlinedTextField(
                    value = altPhone,
                    onValueChange = { altPhone = it.take(10) },
                    label = { Text("Alternate Phone") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email for delivery updates") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = house,
                onValueChange = { house = it },
                label = { Text("Flat, House no., Building, Apartment *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("address_house_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                label = { Text("Area, Street, Sector, Village *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("address_area_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = landmark,
                onValueChange = { landmark = it },
                label = { Text("Landmark (e.g. Near Apollo Hospital)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = pincode,
                    onValueChange = { pincode = it.take(6) },
                    label = { Text("6-digit Pincode *") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.4f)
                        .testTag("address_pincode_input")
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Town / City *") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.6f)
                        .testTag("address_city_input")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("address_state_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Address Type", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Home", "Work").forEach { type ->
                    FilterChip(
                        selected = addressType == type,
                        onClick = { addressType = type },
                        label = { Text(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = deliveryInstructions,
                onValueChange = { deliveryInstructions = it },
                label = { Text("Delivery Instructions (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (fullName.isBlank() || mobile.length < 10 || house.isBlank() || area.isBlank() || pincode.length < 6 || city.isBlank() || state.isBlank()) {
                        errorMessage = "Please fill in all mandatory fields correctly."
                        return@Button
                    }
                    onSave(
                        AddressEntity(
                            id = initialAddress?.id ?: 0L,
                            userId = initialAddress?.userId ?: 0L,
                            fullName = fullName.trim(),
                            mobile = mobile.trim(),
                            email = email.trim(),
                            altPhone = altPhone.trim(),
                            house = house.trim(),
                            area = area.trim(),
                            landmark = landmark.trim(),
                            pincode = pincode.trim(),
                            city = city.trim(),
                            state = state.trim(),
                            addressType = addressType,
                            deliveryInstructions = deliveryInstructions.trim(),
                            isDefault = true
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_address_submit_btn")
            ) {
                Text("Save & Use this Address", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
