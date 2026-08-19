package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.entities.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartBorder
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartOrangeDeep
import com.example.ui.theme.ShopKartRed
import com.example.ui.theme.ShopKartTextPrimary
import com.example.ui.theme.ShopKartTextSecondary
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel

data class CategoryItem(
    val name: String,
    val icon: ImageVector,
    val tag: String,
    val emoji: String = "🛍️"
)

@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    onProductClick: (Long) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchSuggestionClick: (String) -> Unit,
    onSeeAllDealsClick: () -> Unit
) {
    val deals by viewModel.deals.collectAsState()
    val bestSellers by viewModel.bestSellers.collectAsState()
    val recommended by viewModel.allActiveProducts.collectAsState()
    val recentlyViewed by viewModel.recentlyViewedProducts.collectAsState()
    val wishlistProducts by viewModel.wishlistProducts.collectAsState()

    val wishlistedIds = wishlistProducts.map { it.id }.toSet()

    val categories = listOf(
        CategoryItem("Mobiles", Icons.Default.Devices, "Mobiles & Electronics", "📱"),
        CategoryItem("Fashion", Icons.Default.LocalOffer, "Fashion & Clothing", "👟"),
        CategoryItem("Electronics", Icons.Default.Devices, "Mobiles & Electronics", "💻"),
        CategoryItem("Home", Icons.Default.Home, "Home & Kitchen", "🏠"),
        CategoryItem("Beauty", Icons.Default.Face, "Beauty & Grooming", "✨"),
        CategoryItem("Footwear", Icons.Default.SportsScore, "Footwear", "👞")
    )

    val quickSearches = listOf(
        "juta",
        "mobile under 10000",
        "red t shirt",
        "white sneakers",
        "earbuds",
        "smartwatch",
        "coffee maker"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("home_screen_content")
    ) {
        // 1. High Density Hero Sale Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFB923C), // orange-400
                                Color(0xFFEA580C)  // orange-600
                            )
                        )
                    )
                    .clickable { onSeeAllDealsClick() }
                    .testTag("hero_promo_banner")
            ) {
                // Background decorative soft light circles
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.BottomEnd)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "SEASON SALE",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "UP TO 60% OFF",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 28.sp
                    )
                    Text(
                        text = "Everything under ₹999",
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Button(
                        onClick = onSeeAllDealsClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = ShopKartOrangeDeep
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Shop Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. High Density Category Square Tiles
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onCategoryClick(cat.tag) }
                            .padding(vertical = 2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
                            shadowElevation = 0.5.dp,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = cat.emoji,
                                    fontSize = 22.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cat.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ShopKartTextPrimary
                        )
                    }
                }
            }
        }

        // 3. Quick Search Keywords / Synonym Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trending:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartTextSecondary
                )
                quickSearches.forEach { keyword ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
                        modifier = Modifier.clickable { onSearchSuggestionClick(keyword) }
                    ) {
                        Text(
                            text = keyword,
                            fontSize = 11.sp,
                            color = ShopKartCyan,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 4. Value Assurance Strip (Free Delivery, Secure UPI, Easy Returns)
        item {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = ShopKartGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("FREE Delivery", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShopKartTextPrimary)
                            Text("Orders ₹100+", fontSize = 9.sp, color = ShopKartTextSecondary)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ShopKartAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Secure UPI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShopKartTextPrimary)
                            Text("Fast verification", fontSize = 9.sp, color = ShopKartTextSecondary)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = null,
                            tint = ShopKartCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Easy Returns", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ShopKartTextPrimary)
                            Text("7 Days policy", fontSize = 9.sp, color = ShopKartTextSecondary)
                        }
                    }
                }
            }
        }

        // 5. Lightning Deals Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = ShopKartAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lightning Deals",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopKartTextPrimary
                        )
                    }

                    Text(
                        text = "View all",
                        color = ShopKartCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onSeeAllDealsClick() }
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items = deals) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { viewModel.addToCart(product.id) },
                            isWishlisted = wishlistedIds.contains(product.id),
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }
        }

        // 6. Best Sellers Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Best Sellers",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartTextPrimary
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items = bestSellers) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { viewModel.addToCart(product.id) },
                            isWishlisted = wishlistedIds.contains(product.id),
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }
        }

        // 7. Recently Viewed (if available)
        if (recentlyViewed.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Recently Viewed",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartTextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(items = recentlyViewed) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onAddToCart = { viewModel.addToCart(product.id) },
                                isWishlisted = wishlistedIds.contains(product.id),
                                onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                                modifier = Modifier.width(150.dp)
                            )
                        }
                    }
                }
            }
        }

        // 8. Recommended For You (Grid Showcase)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Recommended For You",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartTextPrimary
                )
                Text(
                    text = "Handpicked based on your shopping preferences",
                    fontSize = 11.sp,
                    color = ShopKartTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }

        val chunkedRecommended = recommended.chunked(2)
        items(items = chunkedRecommended) { rowProducts ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (product in rowProducts) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { viewModel.addToCart(product.id) },
                            isWishlisted = wishlistedIds.contains(product.id),
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) }
                        )
                    }
                }
                if (rowProducts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
