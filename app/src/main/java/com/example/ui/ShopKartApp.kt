package com.example.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.repository.ShopRepository
import com.example.ui.components.ShopKartHeader
import com.example.ui.navigation.Screen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.account.AccountScreen
import com.example.ui.screens.account.AuthScreen
import com.example.ui.screens.account.SavedAddressesScreen
import com.example.ui.screens.account.WishlistScreen
import com.example.ui.screens.admin.AdminCouponsScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.AdminLoginScreen
import com.example.ui.screens.admin.AdminOrdersScreen
import com.example.ui.screens.admin.AdminProductsScreen
import com.example.ui.screens.admin.AdminSettingsScreen
import com.example.ui.screens.checkout.DeliveryAddressScreen
import com.example.ui.screens.checkout.OrderSuccessScreen
import com.example.ui.screens.checkout.OrderSummaryScreen
import com.example.ui.screens.checkout.PaymentScreen
import com.example.ui.screens.orders.OrderDetailScreen
import com.example.ui.screens.orders.OrdersScreen
import com.example.ui.theme.ShopKartAmber
import com.example.ui.theme.ShopKartAmberLight
import com.example.ui.theme.ShopKartBackground
import com.example.ui.theme.ShopKartNavyDark
import com.example.ui.theme.ShopKartYellow
import com.example.viewmodel.ShopViewModel
import com.example.viewmodel.ShopViewModelFactory

@Composable
fun ShopKartApp() {
    val context = LocalContext.current.applicationContext as Application
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { ShopRepository(database) }
    val viewModel: ShopViewModel = viewModel(factory = ShopViewModelFactory(context, repository))

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    val currentUser by viewModel.currentUser.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val defaultAddress = savedAddresses.firstOrNull { it.isDefault } ?: savedAddresses.firstOrNull()

    var headerSearchQuery by remember { mutableStateOf("") }

    // Listen for snackbar events
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val isTopLevelRoute = currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Cart.route,
        Screen.Orders.route,
        Screen.Account.route,
        Screen.Wishlist.route
    )

    val showHeader = currentRoute !in listOf(
        Screen.Auth.route,
        Screen.AdminLogin.route,
        Screen.AdminDashboard.route,
        Screen.AdminOrders.route,
        Screen.AdminProducts.route,
        Screen.AdminCoupons.route,
        Screen.AdminSettings.route,
        Screen.DeliveryAddress.route,
        Screen.OrderSummary.route,
        Screen.Payment.route,
        Screen.OrderSuccess.route
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (showHeader) {
                ShopKartHeader(
                    searchQuery = headerSearchQuery,
                    onQueryChange = { headerSearchQuery = it },
                    onSearchSubmit = { query ->
                        navController.navigate(Screen.Search.createRoute(query = query))
                    },
                    cartItemCount = cartCount,
                    currentUser = currentUser,
                    onCartClick = {
                        navController.navigate(Screen.Cart.route)
                    },
                    onWishlistClick = {
                        navController.navigate(Screen.Wishlist.route)
                    },
                    onAccountClick = {
                        navController.navigate(Screen.Account.route)
                    },
                    onAdminClick = {
                        navController.navigate(if (currentUser?.role == "ADMIN") Screen.AdminDashboard.route else Screen.AdminLogin.route)
                    },
                    onLogoClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    locationText = "Deliver to ${currentUser?.fullName?.split(" ")?.firstOrNull() ?: "Rehan"} - ${defaultAddress?.city ?: "New Delhi"} ${defaultAddress?.pincode ?: "110001"}",
                    onLocationClick = {
                        navController.navigate(Screen.SavedAddresses.route)
                    }
                )
            }
        },
        bottomBar = {
            if (isTopLevelRoute) {
                Surface(
                    color = Color.White,
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
                ) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(60.dp)
                    ) {
                        val isHome = currentRoute == Screen.Home.route
                        NavigationBarItem(
                            selected = isHome,
                            onClick = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(if (isHome) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home", modifier = Modifier.size(20.dp)) },
                            label = { Text("Home", fontSize = 10.sp, fontWeight = if (isHome) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFEA580C),
                                selectedTextColor = Color(0xFFEA580C),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8),
                                indicatorColor = Color(0xFFFB923C).copy(alpha = 0.15f)
                            )
                        )

                        val isSearch = currentRoute?.startsWith("search") == true
                        NavigationBarItem(
                            selected = isSearch,
                            onClick = {
                                navController.navigate(Screen.Search.createRoute()) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(if (isSearch) Icons.Filled.Search else Icons.Outlined.Search, contentDescription = "Search", modifier = Modifier.size(20.dp)) },
                            label = { Text("Search", fontSize = 10.sp, fontWeight = if (isSearch) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFEA580C),
                                selectedTextColor = Color(0xFFEA580C),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8),
                                indicatorColor = Color(0xFFFB923C).copy(alpha = 0.15f)
                            )
                        )

                        val isOrders = currentRoute == Screen.Orders.route
                        NavigationBarItem(
                            selected = isOrders,
                            onClick = {
                                navController.navigate(Screen.Orders.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(if (isOrders) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag, contentDescription = "Orders", modifier = Modifier.size(20.dp)) },
                            label = { Text("Orders", fontSize = 10.sp, fontWeight = if (isOrders) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFEA580C),
                                selectedTextColor = Color(0xFFEA580C),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8),
                                indicatorColor = Color(0xFFFB923C).copy(alpha = 0.15f)
                            )
                        )

                        val isCart = currentRoute == Screen.Cart.route
                        NavigationBarItem(
                            selected = isCart,
                            onClick = {
                                navController.navigate(Screen.Cart.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                BadgedBox(badge = {
                                    if (cartCount > 0) {
                                        Badge(containerColor = Color(0xFFEA580C), contentColor = Color.White) {
                                            Text("$cartCount", fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                        }
                                    }
                                }) {
                                    Icon(if (isCart) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart, contentDescription = "Cart", modifier = Modifier.size(20.dp))
                                }
                            },
                            label = { Text("Cart", fontSize = 10.sp, fontWeight = if (isCart) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFEA580C),
                                selectedTextColor = Color(0xFFEA580C),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8),
                                indicatorColor = Color(0xFFFB923C).copy(alpha = 0.15f)
                            )
                        )

                        val isAccount = currentRoute == Screen.Account.route
                        NavigationBarItem(
                            selected = isAccount,
                            onClick = {
                                navController.navigate(Screen.Account.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(if (isAccount) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle, contentDescription = "You", modifier = Modifier.size(20.dp)) },
                            label = { Text("You", fontSize = 10.sp, fontWeight = if (isAccount) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFEA580C),
                                selectedTextColor = Color(0xFFEA580C),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8),
                                indicatorColor = Color(0xFFFB923C).copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Home
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onProductClick = { pid -> navController.navigate(Screen.ProductDetail.createRoute(pid)) },
                    onCategoryClick = { cat -> navController.navigate(Screen.Search.createRoute(category = cat)) },
                    onSearchSuggestionClick = { q -> navController.navigate(Screen.Search.createRoute(query = q)) },
                    onSeeAllDealsClick = { navController.navigate(Screen.Search.createRoute(query = "deal")) }
                )
            }

            // Search
            composable(
                route = Screen.Search.route,
                arguments = listOf(
                    navArgument("query") { type = NavType.StringType; defaultValue = "" },
                    navArgument("category") { type = NavType.StringType; defaultValue = "All" }
                )
            ) { backStack ->
                val q = backStack.arguments?.getString("query") ?: ""
                val c = backStack.arguments?.getString("category") ?: "All"
                SearchScreen(
                    viewModel = viewModel,
                    initialQuery = q,
                    initialCategory = c,
                    onProductClick = { pid -> navController.navigate(Screen.ProductDetail.createRoute(pid)) }
                )
            }

            // Product Detail
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStack ->
                val pid = backStack.arguments?.getLong("productId") ?: 0L
                ProductDetailScreen(
                    productId = pid,
                    viewModel = viewModel,
                    onAddToCart = { id, size, color ->
                        viewModel.addToCart(id, 1, size, color)
                    },
                    onBuyNow = { id, size, color ->
                        navController.navigate(Screen.DeliveryAddress.createRoute(id, size, color))
                    },
                    onRelatedProductClick = { relId ->
                        navController.navigate(Screen.ProductDetail.createRoute(relId))
                    }
                )
            }

            // Cart
            composable(Screen.Cart.route) {
                CartScreen(
                    viewModel = viewModel,
                    onProceedToCheckout = {
                        navController.navigate(Screen.DeliveryAddress.createRoute())
                    },
                    onContinueShopping = {
                        navController.navigate(Screen.Home.route)
                    },
                    onProductClick = { pid ->
                        navController.navigate(Screen.ProductDetail.createRoute(pid))
                    }
                )
            }

            // Checkout Step 1: Address Selection
            composable(
                route = Screen.DeliveryAddress.route,
                arguments = listOf(
                    navArgument("buyNowProductId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("size") { type = NavType.StringType; defaultValue = "" },
                    navArgument("color") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStack ->
                val bPid = backStack.arguments?.getLong("buyNowProductId")?.takeIf { it > 0 }
                val size = backStack.arguments?.getString("size") ?: ""
                val color = backStack.arguments?.getString("color") ?: ""
                DeliveryAddressScreen(
                    viewModel = viewModel,
                    buyNowProductId = bPid,
                    selectedSize = size,
                    selectedColor = color,
                    onNext = { selectedAddressId ->
                        navController.navigate(Screen.OrderSummary.createRoute(selectedAddressId, bPid, size, color))
                    }
                )
            }

            // Checkout Step 2: Order Review & Coupon
            composable(
                route = Screen.OrderSummary.route,
                arguments = listOf(
                    navArgument("addressId") { type = NavType.LongType },
                    navArgument("buyNowProductId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("size") { type = NavType.StringType; defaultValue = "" },
                    navArgument("color") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStack ->
                val addrId = backStack.arguments?.getLong("addressId") ?: 0L
                val bPid = backStack.arguments?.getLong("buyNowProductId")?.takeIf { it > 0 }
                val size = backStack.arguments?.getString("size") ?: ""
                val color = backStack.arguments?.getString("color") ?: ""

                OrderSummaryScreen(
                    addressId = addrId,
                    buyNowProductId = bPid,
                    selectedSize = size,
                    selectedColor = color,
                    viewModel = viewModel,
                    onChangeAddress = { navController.popBackStack() },
                    onProceedToPayment = { couponCode ->
                        navController.navigate(Screen.Payment.createRoute(addrId, couponCode, bPid, size, color))
                    }
                )
            }

            // Checkout Step 3: Payment
            composable(
                route = Screen.Payment.route,
                arguments = listOf(
                    navArgument("addressId") { type = NavType.LongType },
                    navArgument("couponCode") { type = NavType.StringType; defaultValue = "" },
                    navArgument("buyNowProductId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("size") { type = NavType.StringType; defaultValue = "" },
                    navArgument("color") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStack ->
                val addrId = backStack.arguments?.getLong("addressId") ?: 0L
                val couponCode = backStack.arguments?.getString("couponCode") ?: ""
                val bPid = backStack.arguments?.getLong("buyNowProductId")?.takeIf { it > 0 }
                val size = backStack.arguments?.getString("size") ?: ""
                val color = backStack.arguments?.getString("color") ?: ""

                PaymentScreen(
                    addressId = addrId,
                    couponCode = couponCode,
                    buyNowProductId = bPid,
                    selectedSize = size,
                    selectedColor = color,
                    viewModel = viewModel,
                    onOrderSuccess = { orderId ->
                        navController.navigate(Screen.OrderSuccess.createRoute(orderId)) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            // Order Success
            composable(
                route = Screen.OrderSuccess.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStack ->
                val oid = backStack.arguments?.getString("orderId") ?: ""
                OrderSuccessScreen(
                    orderId = oid,
                    viewModel = viewModel,
                    onTrackOrder = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    },
                    onContinueShopping = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // Orders List
            composable(Screen.Orders.route) {
                OrdersScreen(
                    viewModel = viewModel,
                    onOrderClick = { oid -> navController.navigate(Screen.OrderDetail.createRoute(oid)) },
                    onExploreProducts = { navController.navigate(Screen.Home.route) }
                )
            }

            // Order Detail
            composable(
                route = Screen.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStack ->
                val oid = backStack.arguments?.getString("orderId") ?: ""
                OrderDetailScreen(
                    orderId = oid,
                    viewModel = viewModel,
                    onProductClick = { pid -> navController.navigate(Screen.ProductDetail.createRoute(pid)) }
                )
            }

            // Wishlist
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    viewModel = viewModel,
                    onProductClick = { pid -> navController.navigate(Screen.ProductDetail.createRoute(pid)) },
                    onExploreProducts = { navController.navigate(Screen.Home.route) }
                )
            }

            // Saved Addresses
            composable(Screen.SavedAddresses.route) {
                SavedAddressesScreen(viewModel = viewModel)
            }

            // Account
            composable(Screen.Account.route) {
                AccountScreen(
                    viewModel = viewModel,
                    onOrdersClick = { navController.navigate(Screen.Orders.route) },
                    onWishlistClick = { navController.navigate(Screen.Wishlist.route) },
                    onAddressesClick = { navController.navigate(Screen.SavedAddresses.route) },
                    onAdminDashboardClick = { navController.navigate(Screen.AdminDashboard.route) },
                    onAdminLoginClick = { navController.navigate(Screen.AdminLogin.route) },
                    onAuthClick = { navController.navigate(Screen.Auth.route) }
                )
            }

            // Auth
            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = { navController.popBackStack() }
                )
            }

            // Admin Login
            composable(Screen.AdminLogin.route) {
                AdminLoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.AdminLogin.route) { inclusive = true }
                        }
                    }
                )
            }

            // Admin Dashboard
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onManageOrdersClick = { navController.navigate(Screen.AdminOrders.route) },
                    onManageProductsClick = { navController.navigate(Screen.AdminProducts.route) },
                    onManageCouponsClick = { navController.navigate(Screen.AdminCoupons.route) },
                    onSettingsClick = { navController.navigate(Screen.AdminSettings.route) },
                    onViewOrderClick = { oid -> navController.navigate(Screen.OrderDetail.createRoute(oid)) }
                )
            }

            // Admin Orders
            composable(Screen.AdminOrders.route) {
                AdminOrdersScreen(
                    viewModel = viewModel,
                    onViewOrderDetail = { oid -> navController.navigate(Screen.OrderDetail.createRoute(oid)) }
                )
            }

            // Admin Products
            composable(Screen.AdminProducts.route) {
                AdminProductsScreen(viewModel = viewModel)
            }

            // Admin Coupons
            composable(Screen.AdminCoupons.route) {
                AdminCouponsScreen(viewModel = viewModel)
            }

            // Admin Settings
            composable(Screen.AdminSettings.route) {
                AdminSettingsScreen(viewModel = viewModel)
            }
        }
    }
}
