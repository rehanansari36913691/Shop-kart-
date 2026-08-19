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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import com.example.data.local.entities.ProductEntity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    viewModel: ShopViewModel
) {
    val allProducts by viewModel.allActiveProducts.collectAsState()
    var showProductFormSheet by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(allProducts, searchQuery) {
        if (searchQuery.isBlank()) allProducts
        else allProducts.filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("admin_products_screen")
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
                Column {
                    Text(
                        text = "Product Catalog (${allProducts.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                    Text("Add, update stock and manage products", fontSize = 11.sp, color = Color.Gray)
                }

                Button(
                    onClick = {
                        editingProduct = null
                        showProductFormSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter products by title or brand...", fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            items(filtered, key = { it.id }) { product ->
                AdminProductRow(
                    product = product,
                    onEdit = {
                        editingProduct = product
                        showProductFormSheet = true
                    },
                    onDelete = { viewModel.deleteProduct(product) },
                    onToggleHidden = { viewModel.setProductHidden(product.id, !product.isHidden) }
                )
            }
        }
    }

    if (showProductFormSheet) {
        ProductFormBottomSheet(
            initialProduct = editingProduct,
            onDismiss = { showProductFormSheet = false },
            onSave = { prod ->
                viewModel.saveProduct(prod) {
                    showProductFormSheet = false
                }
            }
        )
    }
}

@Composable
fun AdminProductRow(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleHidden: () -> Unit
) {
    var imageUrl = ""
    try {
        val arr = JSONArray(product.imagesJson)
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
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF9FAFB))
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${product.brand} - ${product.name}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ShopKartNavyDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "₹${"%,.0f".format(product.price)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                    Text(
                        text = "MRP: ₹${"%,.0f".format(product.mrp)}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                Text(
                    text = "Stock: ${product.stock} units | Category: ${product.category}",
                    fontSize = 11.sp,
                    color = if (product.stock <= 5) ShopKartRed else ShopKartGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onToggleHidden, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (product.isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Visibility",
                        tint = if (product.isHidden) Color.Gray else ShopKartGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ShopKartCyan, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ShopKartRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormBottomSheet(
    initialProduct: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var brand by remember { mutableStateOf(initialProduct?.brand ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "Footwear") }
    var subcategory by remember { mutableStateOf(initialProduct?.subcategory ?: "Running Shoes") }
    var priceStr by remember { mutableStateOf(initialProduct?.price?.toString() ?: "") }
    var mrpStr by remember { mutableStateOf(initialProduct?.mrp?.toString() ?: "") }
    var stockStr by remember { mutableStateOf(initialProduct?.stock?.toString() ?: "50") }
    var imageUrl by remember {
        mutableStateOf(
            try {
                val arr = JSONArray(initialProduct?.imagesJson ?: "[]")
                if (arr.length() > 0) arr.getString(0) else "https://images.unsplash.com/photo-1542291026-7eec264c27ff"
            } catch (_: Exception) {
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff"
            }
        )
    }
    var description by remember { mutableStateOf(initialProduct?.description ?: "Premium high-quality product designed for daily comfort and durability.") }
    var sizesStr by remember { mutableStateOf("UK 7, UK 8, UK 9, UK 10") }
    var colorsStr by remember { mutableStateOf("Black, White, Blue") }
    var keywordsStr by remember { mutableStateOf(initialProduct?.keywords ?: "shoes, sneakers, running, juta") }
    var isDeal by remember { mutableStateOf(initialProduct?.isDeal ?: false) }
    var isBestSeller by remember { mutableStateOf(initialProduct?.isBestSeller ?: false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = if (initialProduct == null) "Add New Product" else "Edit Product Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ShopKartNavyDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Title / Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Selling Price (₹) *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = mrpStr,
                    onValueChange = { mrpStr = it },
                    label = { Text("MRP (₹) *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { stockStr = it },
                    label = { Text("Stock Qty") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Primary Image URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = sizesStr,
                onValueChange = { sizesStr = it },
                label = { Text("Sizes (comma separated)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = colorsStr,
                onValueChange = { colorsStr = it },
                label = { Text("Colors (comma separated)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = keywordsStr,
                onValueChange = { keywordsStr = it },
                label = { Text("Search Keywords / Synonyms (e.g. juta, phone)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mark as Deal of the Day")
                Switch(checked = isDeal, onCheckedChange = { isDeal = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mark as #1 Best Seller")
                Switch(checked = isBestSeller, onCheckedChange = { isBestSeller = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 999.0
                    val mrp = mrpStr.toDoubleOrNull() ?: (price * 1.5)
                    val stock = stockStr.toIntOrNull() ?: 50
                    val discount = if (mrp > price) (((mrp - price) / mrp) * 100).toInt() else 0

                    val imagesJson = JSONArray().put(imageUrl).toString()
                    val sizesJson = JSONArray(sizesStr.split(",").map { it.trim() }).toString()
                    val colorsJson = JSONArray(colorsStr.split(",").map { it.trim() }).toString()

                    val product = ProductEntity(
                        id = initialProduct?.id ?: 0L,
                        name = name.trim(),
                        brand = brand.trim(),
                        category = category.trim(),
                        subcategory = subcategory.trim(),
                        sku = initialProduct?.sku ?: ("SKU-" + System.currentTimeMillis()),
                        price = price,
                        mrp = mrp,
                        discountPercent = discount,
                        stock = stock,
                        imagesJson = imagesJson,
                        sizesJson = sizesJson,
                        colorsJson = colorsJson,
                        description = description.trim(),
                        featuresJson = "[\"100% Genuine Quality\", \"Lightweight & Durable\"]",
                        specsJson = "{\"Brand\":\"$brand\", \"Category\":\"$category\"}",
                        includedJson = "[\"1 x Main Item\", \"User Manual\"]",
                        keywords = keywordsStr.trim(),
                        isDeal = isDeal,
                        dealDiscountText = if (isDeal) "Save $discount%" else "",
                        isBestSeller = isBestSeller,
                        deliveryEstimate = "FREE Delivery by Tomorrow"
                    )

                    onSave(product)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save Product to Catalog", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
