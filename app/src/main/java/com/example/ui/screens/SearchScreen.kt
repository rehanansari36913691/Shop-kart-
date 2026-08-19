package com.example.ui.screens

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.SortOption
import com.example.ui.components.ProductCard
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartCyan
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartNavyMedium
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: ShopViewModel,
    initialQuery: String = "",
    initialCategory: String = "All",
    onProductClick: (Long) -> Unit
) {
    LaunchedEffect(initialQuery, initialCategory) {
        if (initialQuery.isNotBlank()) {
            viewModel.updateSearchQuery(initialQuery)
        }
        if (initialCategory.isNotBlank() && initialCategory != "All") {
            viewModel.setCategoryFilter(initialCategory)
        }
    }

    val searchResults by viewModel.searchResults.collectAsState()
    val filterState by viewModel.searchFilterState.collectAsState()
    val wishlistProducts by viewModel.wishlistProducts.collectAsState()
    val wishlistedIds = wishlistProducts.map { it.id }.toSet()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }

    val categories = listOf("All", "Footwear", "Mobiles & Electronics", "Fashion & Clothing", "Home & Kitchen", "Beauty & Grooming")
    val brands = listOf("All", "AeroStride", "UrbanVibe", "CrownCraft", "VoltX", "SonicPod", "PulseFit", "AeroWear", "DenimCraft", "NexBook", "BaristaPro", "AromaLux", "GroomMaster")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShopKartBackground)
            .testTag("search_screen_container")
    ) {
        // Filter & Sort Sticky Bar
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Results count and query tag
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (filterState.query.isNotBlank()) "Results for \"${filterState.query}\"" else "All Products",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark,
                        maxLines = 1
                    )
                    Text(
                        text = "${searchResults.size} products found",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Sort Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF2F4F8),
                        modifier = Modifier.clickable { showSortSheet = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = filterState.sortBy.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Filters Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (filterState.selectedCategory != "All" || filterState.minRating > 0 || filterState.minDiscount > 0 || filterState.selectedBrand != "All") ShopKartAmber else Color(0xFFF2F4F8),
                        modifier = Modifier.clickable { showFilterSheet = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Filters",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Search Results List
        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No products found",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                    Text(
                        text = "Try searching for 'juta', 'phone', 'red t shirt', or clear your filters.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.clearSearchFilter() },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopKartNavyDark)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
            ) {
                val chunked = searchResults.chunked(2)
                items(chunked) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(
                                product = pair[0],
                                onClick = { onProductClick(pair[0].id) },
                                onAddToCart = { viewModel.addToCart(pair[0].id) },
                                isWishlisted = wishlistedIds.contains(pair[0].id),
                                onWishlistToggle = { viewModel.toggleWishlist(pair[0].id) }
                            )
                        }
                        if (pair.size > 1) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = pair[1],
                                    onClick = { onProductClick(pair[1].id) },
                                    onAddToCart = { viewModel.addToCart(pair[1].id) },
                                    isWishlisted = wishlistedIds.contains(pair[1].id),
                                    onWishlistToggle = { viewModel.toggleWishlist(pair[1].id) }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Sort Bottom Sheet
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Sort By",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopKartNavyDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                SortOption.entries.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setSortOption(option)
                                showSortSheet = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option.displayName,
                            fontSize = 15.sp,
                            fontWeight = if (filterState.sortBy == option) FontWeight.Bold else FontWeight.Normal,
                            color = if (filterState.sortBy == option) ShopKartNavyDark else Color.DarkGray
                        )
                        if (filterState.sortBy == option) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = ShopKartAmber
                            )
                        }
                    }
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            var selectedCategory by remember { mutableStateOf(filterState.selectedCategory) }
            var selectedBrand by remember { mutableStateOf(filterState.selectedBrand) }
            var selectedMinRating by remember { mutableStateOf(filterState.minRating) }
            var selectedMinDiscount by remember { mutableStateOf(filterState.minDiscount) }
            var inStockOnly by remember { mutableStateOf(filterState.inStockOnly) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filters",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopKartNavyDark
                    )
                    Text(
                        text = "Clear All",
                        fontSize = 13.sp,
                        color = ShopKartCyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            selectedCategory = "All"
                            selectedBrand = "All"
                            selectedMinRating = 0.0
                            selectedMinDiscount = 0
                            inStockOnly = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Section
                Text("Category", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        categories.forEach { cat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (selectedCategory == cat) Icons.Default.Check else Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = if (selectedCategory == cat) ShopKartAmber else Color.LightGray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(cat, fontSize = 14.sp, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Rating Filter
                Text("Customer Rating", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(4.0 to "4★ & above", 3.0 to "3★ & above", 0.0 to "Any").forEach { (stars, label) ->
                        FilterChip(
                            selected = selectedMinRating == stars,
                            onClick = { selectedMinRating = stars },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ShopKartAmberLight)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Discount Filter
                Text("Discount", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10 to "10%+", 30 to "30%+", 50 to "50%+", 0 to "Any").forEach { (disc, label) ->
                        FilterChip(
                            selected = selectedMinDiscount == disc,
                            onClick = { selectedMinDiscount = disc },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ShopKartAmberLight)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Apply Button
                Button(
                    onClick = {
                        viewModel.updateSearchFilter(
                            filterState.copy(
                                selectedCategory = selectedCategory,
                                selectedBrand = selectedBrand,
                                minRating = selectedMinRating,
                                minDiscount = selectedMinDiscount,
                                inStockOnly = inStockOnly
                            )
                        )
                        showFilterSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopKartYellow, contentColor = ShopKartNavyDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
