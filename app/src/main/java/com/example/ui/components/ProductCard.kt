package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.ShopKartBorder
import com.example.ui.theme.ShopKartGreen
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartRed
import com.example.ui.theme.ShopKartTextPrimary
import com.example.ui.theme.ShopKartTextSecondary
import com.example.ui.theme.ShopKartYellow
import org.json.JSONArray

@Composable
fun ProductCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    isWishlisted: Boolean = false,
    onWishlistToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var imageUrl = ""
    try {
        val images = JSONArray(product.imagesJson)
        if (images.length() > 0) imageUrl = images.getString(0)
    } catch (_: Exception) {}

    // High Density Product Card Style: 16.dp rounded, crisp border, compact layout
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Image Container + Badges + Wishlist (Rounded-12.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Discount Badge (Top Left)
                if (product.discountPercent > 0) {
                    Surface(
                        color = ShopKartRed,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "${product.discountPercent}% OFF",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                } else if (product.isBestSeller) {
                    Surface(
                        color = ShopKartAmberLight,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "Best Seller",
                            color = ShopKartNavyDark,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // Wishlist Icon Button (Top Right)
                IconButton(
                    onClick = onWishlistToggle,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .testTag("wishlist_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) ShopKartRed else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Product Title (Line Clamp 1 / Compact High Density)
            Text(
                text = product.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ShopKartTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Brand & Rating Subtext
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
            ) {
                Text(
                    text = product.brand,
                    fontSize = 10.sp,
                    color = ShopKartTextSecondary,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★ ${product.rating}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Price Row: Bold ₹Price + Strike MRP
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "₹${"%,.0f".format(product.price)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartTextPrimary
                )
                if (product.mrp > product.price) {
                    Text(
                        text = "₹${"%,.0f".format(product.mrp)}",
                        fontSize = 9.sp,
                        color = ShopKartTextSecondary,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Add to Cart Button (Compact Pill)
            ElevatedButton(
                onClick = onAddToCart,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = ShopKartYellow,
                    contentColor = ShopKartNavyDark
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .testTag("add_to_cart_${product.id}")
            ) {
                Text(
                    text = "Add to Cart",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
