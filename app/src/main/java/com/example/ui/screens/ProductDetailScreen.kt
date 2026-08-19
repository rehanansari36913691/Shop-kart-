package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.ProductEntity
import com.example.ui.components.RatingBar
import com.example.ui.components.ZoomableImageDialog
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
import org.json.JSONObject

@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: ShopViewModel,
    onAddToCart: (Long, String, String) -> Unit,
    onBuyNow: (Long, String, String) -> Unit,
    onRelatedProductClick: (Long) -> Unit
) {
    val productFlow = remember(productId) { viewModel.getProductByIdFlow(productId) }
    val product by productFlow.collectAsState(initial = null)
    val reviews by viewModel.getReviewsForProduct(productId).collectAsState(initial = emptyList())
    val isWishlisted by viewModel.isProductInWishlist(productId).collectAsState(initial = false)

    var selectedImageIndex by remember { mutableIntStateOf(0) }
    var showZoomDialog by remember { mutableStateOf(false) }
    var selectedSize by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("") }
    var pincodeInput by remember { mutableStateOf("110001") }
    var pincodeChecked by remember { mutableStateOf(true) }

    var showWriteReviewDialog by remember { mutableStateOf(false) }
    var newReviewRating by remember { mutableIntStateOf(5) }
    var newReviewTitle by remember { mutableStateOf("") }
    var newReviewComment by remember { mutableStateOf("") }

    // Record view
    LaunchedEffect(productId) {
        viewModel.recordRecentlyViewed(productId)
    }

    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ShopKartAmber)
        }
        return
    }

    val prod = product!!

    // Parse JSON data
    val imageList = remember(prod.imagesJson) {
        try {
            val arr = JSONArray(prod.imagesJson)
            List(arr.length()) { arr.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    val sizeList = remember(prod.sizesJson) {
        try {
            val arr = JSONArray(prod.sizesJson)
            List(arr.length()) { arr.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    val colorList = remember(prod.colorsJson) {
        try {
            val arr = JSONArray(prod.colorsJson)
            List(arr.length()) { arr.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    val featureList = remember(prod.featuresJson) {
        try {
            val arr = JSONArray(prod.featuresJson)
            List(arr.length()) { arr.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    val includedList = remember(prod.includedJson) {
        try {
            val arr = JSONArray(prod.includedJson)
            List(arr.length()) { arr.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    val specsMap = remember(prod.specsJson) {
        try {
            val obj = JSONObject(prod.specsJson)
            val keys = obj.keys()
            val map = mutableListOf<Pair<String, String>>()
            while (keys.hasNext()) {
                val key = keys.next()
                map.add(key to obj.getString(key))
            }
            map
        } catch (_: Exception) {
            emptyList()
        }
    }

    // Default selection
    LaunchedEffect(sizeList, colorList) {
        if (selectedSize.isEmpty() && sizeList.isNotEmpty()) selectedSize = sizeList.first()
        if (selectedColor.isEmpty() && colorList.isNotEmpty()) selectedColor = colorList.first()
    }

    val currentImage = if (imageList.isNotEmpty() && selectedImageIndex in imageList.indices) imageList[selectedImageIndex] else ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("product_detail_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // 1. Top Brand & Title Bar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Brand: ${prod.brand}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartCyan
                        )
                        Row {
                            IconButton(onClick = { viewModel.toggleWishlist(prod.id) }) {
                                Icon(
                                    imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Wishlist",
                                    tint = if (isWishlisted) ShopKartRed else Color.Gray
                                )
                            }
                        }
                    }

                    Text(
                        text = prod.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ShopKartNavyDark,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rating and Review Count
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingBar(rating = prod.rating, starSize = 14.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${prod.rating} (${prod.reviewCount} customer reviews)",
                            fontSize = 12.sp,
                            color = ShopKartCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 2. Interactive Image Gallery with Zoom
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFFFAFAFA))
                        .clickable { if (currentImage.isNotBlank()) showZoomDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (currentImage.isNotBlank()) {
                        AsyncImage(
                            model = currentImage,
                            contentDescription = prod.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Zoom overlay icon button
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                }

                // Image Thumbnails Carousel
                if (imageList.size > 1) {
                    LazyRow(
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(imageList.indices.toList()) { index ->
                            val isSelected = index == selectedImageIndex
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) ShopKartAmber else ShopKartBorder,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedImageIndex = index }
                            ) {
                                AsyncImage(
                                    model = imageList[index],
                                    contentDescription = "Thumbnail $index",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFE5E7EB))
            }

            // 3. Price & Deals Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    if (prod.isDeal) {
                        Surface(
                            color = ShopKartRed,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = if (prod.dealDiscountText.isNotBlank()) prod.dealDiscountText else "Limited Time Deal",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "-${prod.discountPercent}%",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Normal,
                            color = ShopKartRed
                        )
                        Text(
                            text = "₹${"%,.0f".format(prod.price)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = ShopKartNavyDark
                        )
                    }

                    if (prod.mrp > prod.price) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "M.R.P.: ",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "₹${"%,.0f".format(prod.mrp)}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "You Save ₹${"%,.0f".format(prod.mrp - prod.price)} (${prod.discountPercent}%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopKartGreen
                            )
                        }
                    }
                    Text(
                        text = "Inclusive of all taxes",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stock Status
                    if (prod.stock > 0) {
                        Text(
                            text = if (prod.stock <= 5) "Only ${prod.stock} left in stock - order soon." else "In Stock",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (prod.stock <= 5) ShopKartRed else ShopKartGreen
                        )
                    } else {
                        Text(
                            text = "Currently Out of Stock",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartRed
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFFE5E7EB))
            }

            // 4. Color Variant Selector
            if (colorList.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Colour: $selectedColor",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(colorList) { color ->
                                val isSelected = color == selectedColor
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) ShopKartAmberLight else Color(0xFFF2F4F8),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) ShopKartAmber else Color.Transparent)
                                    ),
                                    modifier = Modifier.clickable { selectedColor = color }
                                ) {
                                    Text(
                                        text = color,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = ShopKartNavyDark,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                }
            }

            // 5. Size Variant Selector
            if (sizeList.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Size: $selectedSize",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(sizeList) { size ->
                                val isSelected = size == selectedSize
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) ShopKartAmberLight else Color(0xFFF2F4F8),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) ShopKartAmber else Color.Transparent)
                                    ),
                                    modifier = Modifier.clickable { selectedSize = size }
                                ) {
                                    Text(
                                        text = size,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = ShopKartNavyDark,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                }
            }

            // 6. Delivery PIN Checker & Assurances
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
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
                            text = "Check Delivery to Your Area",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = pincodeInput,
                            onValueChange = { pincodeInput = it.take(6) },
                            singleLine = true,
                            placeholder = { Text("Enter 6-digit Pincode", fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { pincodeChecked = pincodeInput.length == 6 },
                            colors = ButtonDefaults.buttonColors(containerColor = ShopKartNavyDark),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Check")
                        }
                    }

                    if (pincodeChecked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ ${prod.deliveryEstimate} to Pincode $pincodeInput",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ShopKartGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Badges Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LocalShipping, null, tint = ShopKartCyan, modifier = Modifier.size(24.dp))
                            Text("Fast Delivery", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Redo, null, tint = ShopKartCyan, modifier = Modifier.size(24.dp))
                            Text("7 Days Return", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Security, null, tint = ShopKartCyan, modifier = Modifier.size(24.dp))
                            Text("100% Genuine", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFE5E7EB))
            }

            // 7. Product Overview & Key Features
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "About this item",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = prod.description,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color.DarkGray
                    )

                    if (featureList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        featureList.forEach { bullet ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ShopKartAmber)
                                Text(
                                    text = bullet,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = ShopKartNavyDark
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFE5E7EB))
            }

            // 8. Specifications Table
            if (specsMap.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Technical Details & Specifications",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        specsMap.forEachIndexed { idx, (key, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (idx % 2 == 0) Color(0xFFF9FAFB) else Color.White)
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = key,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Gray,
                                    modifier = Modifier.weight(0.4f)
                                )
                                Text(
                                    text = value,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = ShopKartNavyDark,
                                    modifier = Modifier.weight(0.6f)
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                }
            }

            // 9. What's in the Box
            if (includedList.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "What is in the box?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        includedList.forEach { inc ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = ShopKartGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = inc, fontSize = 13.sp, color = ShopKartNavyDark)
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                }
            }

            // 10. Customer Reviews Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Customer Reviews",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartNavyDark
                        )
                        OutlinedButton(
                            onClick = { showWriteReviewDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ShopKartNavyDark)
                        ) {
                            Icon(Icons.Default.RateReview, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Write a Review", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (reviews.isEmpty()) {
                        Text(
                            text = "No reviews yet. Be the first to review this product!",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        reviews.forEach { rev ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = rev.userName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ShopKartNavyDark
                                        )
                                        if (rev.isVerifiedPurchase) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = ShopKartGreen.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(3.dp)
                                            ) {
                                                Text(
                                                    text = "Verified Purchase",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ShopKartGreen,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RatingBar(rating = rev.rating.toDouble(), starSize = 13.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = rev.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ShopKartNavyDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = rev.comment,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = Color.DarkGray
                                    )
                                    if (rev.reviewDate.isNotBlank()) {
                                        Text(
                                            text = "Reviewed on ${rev.reviewDate}",
                                            fontSize = 10.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sticky Action Bar (ADD TO CART + BUY NOW)
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add to Cart
                Button(
                    onClick = { onAddToCart(prod.id, selectedSize, selectedColor) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShopKartYellow,
                        contentColor = ShopKartNavyDark
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("detail_add_to_cart_btn")
                ) {
                    Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Buy Now (Direct Checkout Flow)
                Button(
                    onClick = { onBuyNow(prod.id, selectedSize, selectedColor) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShopKartAmber,
                        contentColor = ShopKartNavyDark
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("detail_buy_now_btn")
                ) {
                    Text("Buy Now", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }

    // Zoom Image Dialog
    if (showZoomDialog && currentImage.isNotBlank()) {
        ZoomableImageDialog(imageUrl = currentImage, onDismiss = { showZoomDialog = false })
    }

    // Write Review Dialog
    if (showWriteReviewDialog) {
        AlertDialog(
            onDismissRequest = { showWriteReviewDialog = false },
            title = { Text("Write a Product Review", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Overall Rating:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    RatingBar(
                        rating = newReviewRating.toDouble(),
                        starSize = 24.dp,
                        onRatingChanged = { newReviewRating = it },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    OutlinedTextField(
                        value = newReviewTitle,
                        onValueChange = { newReviewTitle = it },
                        label = { Text("Headline / Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newReviewComment,
                        onValueChange = { newReviewComment = it },
                        label = { Text("Write your review") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newReviewComment.isNotBlank()) {
                            viewModel.submitReview(
                                productId = prod.id,
                                rating = newReviewRating,
                                title = if (newReviewTitle.isNotBlank()) newReviewTitle else "Verified Review",
                                comment = newReviewComment
                            )
                            showWriteReviewDialog = false
                            newReviewTitle = ""
                            newReviewComment = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark)
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWriteReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
