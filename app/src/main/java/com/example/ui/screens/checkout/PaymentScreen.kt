package com.example.ui.screens.checkout

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.CouponEntity
import com.example.data.model.CartItemDetail
import com.example.data.model.OrderPricingSummary
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartBorder
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartNavyMedium
import com.example.ui.theme.ShopKartOrangeDeep
import com.example.ui.theme.ShopKartRed
import com.example.ui.theme.ShopKartTextPrimary
import com.example.ui.theme.ShopKartTextSecondary
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel

enum class PaymentOption {
    UPI,
    CARD,
    NET_BANKING
}

data class UpiAppConfig(
    val name: String,
    val packageName: String,
    val iconEmoji: String
)

@Composable
fun PaymentScreen(
    addressId: Long,
    couponCode: String,
    buyNowProductId: Long?,
    selectedSize: String,
    selectedColor: String,
    viewModel: ShopViewModel,
    onOrderSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val addresses by viewModel.savedAddresses.collectAsState()
    val activeCartItems by viewModel.activeCartItems.collectAsState()
    val settings by viewModel.appSettings.collectAsState()

    val selectedAddress = addresses.firstOrNull { it.id == addressId } ?: addresses.firstOrNull()
    val upiId = settings["upi_id"] ?: "rehanbro@fam"
    val upiName = settings["upi_name"] ?: "Rehan alam"

    var checkoutItems by remember { mutableStateOf<List<CartItemDetail>>(emptyList()) }
    var appliedCoupon by remember { mutableStateOf<CouponEntity?>(null) }
    var pricingSummary by remember { mutableStateOf(OrderPricingSummary(0.0, 0.0, 0.0, 0.0, 0.0, false)) }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Form inputs
    var selectedPaymentOption by remember { mutableStateOf(PaymentOption.UPI) }
    var upiTransactionId by remember { mutableStateOf("") }
    var selectedUpiApp by remember { mutableStateOf("Any UPI App") }

    // Card inputs
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }

    // Net Banking
    var selectedBank by remember { mutableStateOf("HDFC Bank") }

    LaunchedEffect(buyNowProductId, activeCartItems, couponCode) {
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

        if (couponCode.isNotBlank()) {
            appliedCoupon = viewModel.getCouponByCode(couponCode)
        }
        if (checkoutItems.isNotEmpty()) {
            pricingSummary = viewModel.calculatePricing(checkoutItems, appliedCoupon)
        }
    }

    val upiAppsList = listOf(
        UpiAppConfig("Google Pay", "com.google.android.apps.nbu.paisa.user", "🟢 GPay"),
        UpiAppConfig("PhonePe", "com.phonepe.app", "🟣 PhonePe"),
        UpiAppConfig("Paytm", "net.one97.paytm", "🔵 Paytm"),
        UpiAppConfig("FamPay", "com.fampay.in", "🟡 FamPay"),
        UpiAppConfig("BHIM UPI", "in.org.npci.upiapp", "🇮🇳 BHIM")
    )

    fun launchUpiIntent(targetPackage: String? = null) {
        try {
            val note = "ShopKart Order Payment"
            val uriString = "upi://pay?pa=$upiId&pn=${java.net.URLEncoder.encode(upiName, "UTF-8")}&am=${pricingSummary.finalTotal}&cu=INR&tn=${java.net.URLEncoder.encode(note, "UTF-8")}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
                if (!targetPackage.isNullOrBlank()) {
                    setPackage(targetPackage)
                }
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                // Fallback to general chooser without strict package constraint
                val fallbackUri = "upi://pay?pa=$upiId&pn=${java.net.URLEncoder.encode(upiName, "UTF-8")}&am=${pricingSummary.finalTotal}&cu=INR&tn=ShopKart%20Order"
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUri))
                context.startActivity(Intent.createChooser(fallbackIntent, "Pay ₹${pricingSummary.finalTotal} with UPI"))
            } catch (_: Exception) {
                Toast.makeText(context, "No UPI app installed. Please scan QR Code or copy UPI ID $upiId", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun submitOrder() {
        if (selectedAddress == null) {
            errorMessage = "Please select a valid delivery address"
            return
        }

        when (selectedPaymentOption) {
            PaymentOption.UPI -> {
                if (upiTransactionId.isBlank()) {
                    errorMessage = "Please enter the 12-digit UTR / Reference ID from your payment receipt."
                    return
                }
                if (upiTransactionId.length < 6) {
                    errorMessage = "Please enter a valid Transaction / UTR number."
                    return
                }
            }
            PaymentOption.CARD -> {
                if (cardNumber.length < 15) {
                    errorMessage = "Please enter a valid 16-digit Card Number"
                    return
                }
                if (cardExpiry.length < 4) {
                    errorMessage = "Please enter valid card expiry (MM/YY)"
                    return
                }
                if (cardCvv.length < 3) {
                    errorMessage = "Please enter valid 3-digit CVV"
                    return
                }
            }
            PaymentOption.NET_BANKING -> {
                // Selected bank is used
            }
        }

        errorMessage = ""
        isPlacingOrder = true

        val cardLast4 = if (cardNumber.length >= 4) cardNumber.takeLast(4) else ""

        viewModel.placeOrder(
            items = checkoutItems,
            address = selectedAddress,
            paymentMethod = selectedPaymentOption.name,
            upiTransactionId = upiTransactionId,
            upiAppUsed = selectedUpiApp,
            cardLast4 = cardLast4,
            bankName = selectedBank,
            appliedCoupon = appliedCoupon,
            onSuccess = { order ->
                isPlacingOrder = false
                onOrderSuccess(order.id)
            },
            onError = { error ->
                isPlacingOrder = false
                errorMessage = error
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .padding(horizontal = 14.dp)
            .testTag("payment_screen_content")
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // Step Indicator Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Select Payment Method",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark
                )
                Surface(
                    color = ShopKartGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, null, tint = ShopKartGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("256-Bit SSL", fontSize = 11.sp, color = ShopKartGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Order Total Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ShopKartBorder)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Payable Amount:", fontSize = 12.sp, color = ShopKartTextSecondary)
                        Text(
                            text = "₹${"%,.2f".format(pricingSummary.finalTotal)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = ShopKartNavyDark
                        )
                    }
                    if (pricingSummary.totalSavings > 0) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Saved ₹${"%,.0f".format(pricingSummary.totalSavings)}",
                                color = ShopKartGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Error Message Alert
            if (errorMessage.isNotBlank()) {
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = ShopKartRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        // 1. UPI Payment Option (Primary & Recommended)
        item {
            val isSelected = selectedPaymentOption == PaymentOption.UPI

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color.White else Color(0xFFFCFCFD)
                ),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) ShopKartAmber else ShopKartBorder)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedPaymentOption = PaymentOption.UPI }
                    .testTag("payment_option_upi")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedPaymentOption = PaymentOption.UPI },
                            colors = RadioButtonDefaults.colors(selectedColor = ShopKartOrangeDeep)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.QrCode2, null, tint = ShopKartOrangeDeep, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "UPI (Instant App & QR Code)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartNavyDark
                            )
                            Text(
                                text = "Google Pay, PhonePe, Paytm, FamPay, BHIM",
                                fontSize = 11.sp,
                                color = ShopKartTextSecondary
                            )
                        }
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "RECOMMENDED",
                                color = ShopKartGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Expanded UPI Details
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        // A. One-Tap Instant Redirection Button
                        Button(
                            onClick = { launchUpiIntent() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ShopKartOrangeDeep,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("pay_via_upi_intent_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Pay ₹${"%,.2f".format(pricingSummary.finalTotal)} via UPI App",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // App-Specific Direct Quick Triggers
                        Text("Or choose specific app to launch:", fontSize = 11.sp, color = ShopKartTextSecondary)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            upiAppsList.take(4).forEach { appConfig ->
                                Surface(
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(8.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedUpiApp = appConfig.name
                                            launchUpiIntent(appConfig.packageName)
                                        }
                                ) {
                                    Text(
                                        text = appConfig.iconEmoji,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ShopKartNavyDark,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // B. Authentic Golden FamPay Trio Theme QR Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFDE8AD)) // Authentic FamPay Golden Card Color
                                .border(1.dp, Color(0xFFE6C875), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Payee Name
                                Text(
                                    text = upiName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFB45309) // Rich warm bronze/amber
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // UPI ID Header
                                Text(
                                    text = upiId,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Dynamic QR Code
                                val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=350x350&margin=8&data=" +
                                        java.net.URLEncoder.encode("upi://pay?pa=$upiId&pn=${upiName.replace(" ", "%20")}&am=${pricingSummary.finalTotal}&cu=INR&tn=ShopKart%20Order", "UTF-8")

                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFDF0D0))
                                        .border(2.dp, Color(0xFFD4AF37), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = qrUrl,
                                        contentDescription = "ShopKart FamPay UPI QR Code",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Trio & UPI Badge at bottom
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "triö",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "|",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UPI",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F766E)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Copy Details Actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                clipboardManager.setText(AnnotatedString(upiId))
                                                Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Default.ContentCopy, null, tint = ShopKartCyan, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy UPI ID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShopKartCyan)
                                        }
                                    }

                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                clipboardManager.setText(AnnotatedString("${pricingSummary.finalTotal}"))
                                                Toast.makeText(context, "Amount copied: ₹${pricingSummary.finalTotal}", Toast.LENGTH_SHORT).show()
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Default.ContentCopy, null, tint = ShopKartNavyDark, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy ₹${"%.2f".format(pricingSummary.finalTotal)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShopKartNavyDark)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // C. 3-Step Confirmation Guide
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "3 Easy Steps to Complete Order:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ShopKartNavyDark
                                )
                                Text("1. Tap 'Pay via UPI App' above (or scan QR in app)", fontSize = 10.sp, color = ShopKartTextSecondary)
                                Text("2. Complete ₹${"%,.2f".format(pricingSummary.finalTotal)} payment in Google Pay / PhonePe / Paytm", fontSize = 10.sp, color = ShopKartTextSecondary)
                                Text("3. Copy 12-digit UTR/Ref No. from receipt and paste below:", fontSize = 10.sp, color = ShopKartTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // D. Mandatory UTR / Transaction ID Input
                        Text(
                            text = "Enter 12-Digit UTR / Transaction Ref ID *",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                        Text(
                            text = "Found under transaction details in your UPI app",
                            fontSize = 11.sp,
                            color = ShopKartTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        OutlinedTextField(
                            value = upiTransactionId,
                            onValueChange = { upiTransactionId = it.trim() },
                            placeholder = { Text("e.g. 402918239012 (12 digits)", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upi_utr_input_field")
                        )
                    }
                }
            }
        }

        // 2. Credit / Debit Card Option
        item {
            val isSelected = selectedPaymentOption == PaymentOption.CARD

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color.White else Color(0xFFFCFCFD)
                ),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) ShopKartAmber else ShopKartBorder)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedPaymentOption = PaymentOption.CARD }
                    .testTag("payment_option_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedPaymentOption = PaymentOption.CARD },
                            colors = RadioButtonDefaults.colors(selectedColor = ShopKartOrangeDeep)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.CreditCard, null, tint = ShopKartNavyDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Credit / Debit / ATM Card",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                    }

                    if (isSelected) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it.take(16).filter { c -> c.isDigit() } },
                            label = { Text("Card Number", fontSize = 12.sp) },
                            placeholder = { Text("16-digit card number", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = { cardExpiry = it.take(5) },
                                label = { Text("Expiry", fontSize = 12.sp) },
                                placeholder = { Text("MM/YY", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cardCvv,
                                onValueChange = { cardCvv = it.take(4).filter { c -> c.isDigit() } },
                                label = { Text("CVV", fontSize = 12.sp) },
                                placeholder = { Text("3-4 digits", fontSize = 12.sp) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Net Banking Option
        item {
            val isSelected = selectedPaymentOption == PaymentOption.NET_BANKING

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color.White else Color(0xFFFCFCFD)
                ),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) ShopKartAmber else ShopKartBorder)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedPaymentOption = PaymentOption.NET_BANKING }
                    .testTag("payment_option_netbanking")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedPaymentOption = PaymentOption.NET_BANKING },
                            colors = RadioButtonDefaults.colors(selectedColor = ShopKartOrangeDeep)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.AccountBalance, null, tint = ShopKartNavyDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Net Banking",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                    }

                    if (isSelected) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("HDFC Bank", "SBI", "ICICI", "Axis").forEach { bank ->
                                Surface(
                                    color = if (selectedBank == bank) Color(0xFFFDE8AD) else Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(8.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(if (selectedBank == bank) ShopKartAmber else Color(0xFFE2E8F0))
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedBank = bank }
                                ) {
                                    Text(
                                        text = bank,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ShopKartNavyDark,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. CASH ON DELIVERY DISABLED NOTICE (MANDATORY REQUIREMENT)
        item {
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Cash on Delivery (COD) is Disabled",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "ShopKart operates on 100% secure pre-paid payments to offer lowest factory prices & zero fake orders.",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // 5. Submit Order Button
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { submitOrder() },
                enabled = !isPlacingOrder,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ShopKartYellow,
                    contentColor = ShopKartNavyDark
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_and_place_order_button")
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ShopKartNavyDark,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verifying & Submitting...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Lock, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Confirm & Place Order (₹${"%,.2f".format(pricingSummary.finalTotal)})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
