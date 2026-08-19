package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search?query={query}&category={category}") {
        fun createRoute(query: String = "", category: String = "All") =
            "search?query=${query}&category=${category}"
    }
    data object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: Long) = "product/$productId"
    }
    data object Cart : Screen("cart")
    data object Wishlist : Screen("wishlist")
    data object Account : Screen("account")
    data object Auth : Screen("auth")
    data object SavedAddresses : Screen("saved_addresses")

    // Checkout Flow
    data object DeliveryAddress : Screen("checkout/address?buyNowProductId={buyNowProductId}&size={size}&color={color}") {
        fun createRoute(buyNowProductId: Long? = null, size: String = "", color: String = "") =
            "checkout/address?buyNowProductId=${buyNowProductId ?: -1L}&size=$size&color=$color"
    }
    data object OrderSummary : Screen("checkout/summary?addressId={addressId}&buyNowProductId={buyNowProductId}&size={size}&color={color}") {
        fun createRoute(addressId: Long, buyNowProductId: Long? = null, size: String = "", color: String = "") =
            "checkout/summary?addressId=$addressId&buyNowProductId=${buyNowProductId ?: -1L}&size=$size&color=$color"
    }
    data object Payment : Screen("checkout/payment?addressId={addressId}&couponCode={couponCode}&buyNowProductId={buyNowProductId}&size={size}&color={color}") {
        fun createRoute(addressId: Long, couponCode: String = "", buyNowProductId: Long? = null, size: String = "", color: String = "") =
            "checkout/payment?addressId=$addressId&couponCode=$couponCode&buyNowProductId=${buyNowProductId ?: -1L}&size=$size&color=$color"
    }
    data object OrderSuccess : Screen("checkout/success/{orderId}") {
        fun createRoute(orderId: String) = "checkout/success/$orderId"
    }

    // Orders
    data object Orders : Screen("orders")
    data object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }

    // Admin
    data object AdminLogin : Screen("admin/login")
    data object AdminDashboard : Screen("admin/dashboard")
    data object AdminOrders : Screen("admin/orders")
    data object AdminProducts : Screen("admin/products")
    data object AdminCoupons : Screen("admin/coupons")
    data object AdminSettings : Screen("admin/settings")
}
