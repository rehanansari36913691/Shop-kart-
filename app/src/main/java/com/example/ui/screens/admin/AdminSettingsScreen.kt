package com.example.ui.screens.admin

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel

@Composable
fun AdminSettingsScreen(
    viewModel: ShopViewModel
) {
    val context = LocalContext.current
    val settings by viewModel.appSettings.collectAsState()

    var upiId by remember(settings) { mutableStateOf(settings["upi_id"] ?: "rehanbro@fam") }
    var upiName by remember(settings) { mutableStateOf(settings["upi_name"] ?: "Rehan alam") }
    var freeThreshold by remember(settings) { mutableStateOf(settings["free_shipping_threshold"] ?: "100.0") }
    var deliveryFee by remember(settings) { mutableStateOf(settings["standard_delivery_fee"] ?: "79.0") }

    var telegramToken by remember(settings) { mutableStateOf(settings["telegram_bot_token"] ?: "") }
    var telegramChatId by remember(settings) { mutableStateOf(settings["telegram_chat_id"] ?: "") }

    var isSendingTest by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
            .testTag("admin_settings_screen")
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Store Configuration & Telegram",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
                Text(
                    text = "Configure UPI payment gateway and instant Telegram alerts",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 1. Payment Settings
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, null, tint = ShopKartAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("UPI Gateway & Delivery Pricing", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ShopKartNavyDark)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("Store UPI ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = upiName,
                    onValueChange = { upiName = it },
                    label = { Text("Payee Merchant Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = freeThreshold,
                        onValueChange = { freeThreshold = it },
                        label = { Text("Free Delivery Above (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = deliveryFee,
                        onValueChange = { deliveryFee = it },
                        label = { Text("Standard Fee (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Telegram Bot Integration
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, null, tint = ShopKartCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Telegram Real-Time Alerts", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ShopKartNavyDark)
                }

                Text(
                    text = "Receive instant alerts in your Telegram channel or personal chat for new orders, UPI payments, order cancellations, and return requests.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = telegramToken,
                    onValueChange = { telegramToken = it },
                    label = { Text("Telegram Bot API Token") },
                    placeholder = { Text("e.g. 123456789:ABCDefGhIJklMnoPQrs") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = telegramChatId,
                    onValueChange = { telegramChatId = it },
                    label = { Text("Telegram Admin Chat ID") },
                    placeholder = { Text("e.g. -1001234567890 or @your_channel") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        isSendingTest = true
                        viewModel.testTelegramNotification(
                            botToken = telegramToken.trim(),
                            chatId = telegramChatId.trim(),
                            onResult = { success, msg ->
                                isSendingTest = false
                                Toast.makeText(context, if (success) "✓ $msg" else "✗ $msg", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    enabled = !isSendingTest && telegramToken.isNotBlank() && telegramChatId.isNotBlank(),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSendingTest) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = ShopKartNavyDark)
                    } else {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Test Telegram Alert")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                viewModel.updateSettings(
                    mapOf(
                        "upi_id" to upiId.trim(),
                        "upi_name" to upiName.trim(),
                        "free_shipping_threshold" to freeThreshold.trim(),
                        "standard_delivery_fee" to deliveryFee.trim(),
                        "telegram_bot_token" to telegramToken.trim(),
                        "telegram_chat_id" to telegramChatId.trim()
                    )
                ) {
                    Toast.makeText(context, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Save Store Settings", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
