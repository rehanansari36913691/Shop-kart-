package com.example.data.util

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AddressEntity
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.CouponEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ReviewEntity
import com.example.data.local.entities.UserEntity

object InitialDataSeeder {

    suspend fun seedDatabaseIfEmpty(db: AppDatabase) {
        val userCount = db.userDao().getUserByEmail("admin@shopkart.com")
        if (userCount != null) {
            return // Already seeded
        }

        // 1. Seed Users
        val adminId = db.userDao().insertUser(
            UserEntity(
                fullName = "ShopKart Super Admin",
                email = "admin@shopkart.com",
                phone = "9876543210",
                passwordHash = SecurityUtils.hashPassword("admin123"),
                role = "ADMIN"
            )
        )

        val customerId = db.userDao().insertUser(
            UserEntity(
                fullName = "Rehan Ansari",
                email = "rehan@example.com",
                phone = "9998887776",
                passwordHash = SecurityUtils.hashPassword("password123"),
                role = "CUSTOMER"
            )
        )

        // 2. Seed Default Address for Customer
        db.addressDao().insertAddress(
            AddressEntity(
                userId = customerId,
                fullName = "Rehan Ansari",
                mobile = "9998887776",
                email = "rehan@example.com",
                altPhone = "9876543211",
                house = "Flat 402, Royal Palms Residency",
                area = "MG Road, Sector 14",
                landmark = "Near City Center Metro Station",
                pincode = "110001",
                city = "New Delhi",
                state = "Delhi",
                addressType = "Home",
                deliveryInstructions = "Please ring the bell and leave with security if unavailable",
                isDefault = true
            )
        )

        // 3. Seed Default App Settings
        db.appSettingsDao().setSettings(
            listOf(
                AppSettingsEntity("delivery_threshold", "100.0"),
                AppSettingsEntity("delivery_fee", "79.0"),
                AppSettingsEntity("upi_id", "rehanbro@fam"),
                AppSettingsEntity("upi_payee_name", "ShopKart E-Commerce"),
                AppSettingsEntity("telegram_bot_token", ""),
                AppSettingsEntity("telegram_chat_id", "")
            )
        )

        // 4. Seed Coupons
        db.couponDao().insertCoupon(
            CouponEntity(
                code = "SHOPKART10",
                description = "Get 10% instant discount on any order",
                discountType = "PERCENTAGE",
                discountValue = 10.0,
                minOrderAmount = 0.0,
                maxDiscountAmount = 200.0,
                expiryDate = "31 Dec 2026",
                applicableCategory = "ALL",
                usageLimit = 5000
            )
        )
        db.couponDao().insertCoupon(
            CouponEntity(
                code = "FIRSTBUY50",
                description = "Flat ₹50 OFF on your cart",
                discountType = "FIXED",
                discountValue = 50.0,
                minOrderAmount = 199.0,
                maxDiscountAmount = 50.0,
                expiryDate = "31 Dec 2026",
                applicableCategory = "ALL",
                usageLimit = 1000
            )
        )
        db.couponDao().insertCoupon(
            CouponEntity(
                code = "SUPERDEAL",
                description = "20% Super Savings up to ₹500 on electronics & fashion",
                discountType = "PERCENTAGE",
                discountValue = 20.0,
                minOrderAmount = 499.0,
                maxDiscountAmount = 500.0,
                expiryDate = "31 Dec 2026",
                applicableCategory = "ALL",
                usageLimit = 2000
            )
        )
        db.couponDao().insertCoupon(
            CouponEntity(
                code = "MEGA100",
                description = "Flat ₹100 discount on orders above ₹999",
                discountType = "FIXED",
                discountValue = 100.0,
                minOrderAmount = 999.0,
                maxDiscountAmount = 100.0,
                expiryDate = "31 Dec 2026",
                applicableCategory = "ALL",
                usageLimit = 3000
            )
        )

        // 5. Seed Comprehensive Catalog Products
        val products = getInitialProducts()
        db.productDao().insertProducts(products)

        // 6. Seed Sample Reviews
        db.reviewDao().insertReview(
            ReviewEntity(
                productId = 1,
                userId = customerId,
                userName = "Aman Sharma",
                rating = 5,
                title = "Incredible comfort and build quality!",
                comment = "I use these shoes daily for running and gym. Cushioning is top notch and the grip is exceptional. Great value for money!",
                reviewDate = "12 Aug 2026"
            )
        )
        db.reviewDao().insertReview(
            ReviewEntity(
                productId = 1,
                userId = 2,
                userName = "Priya Verma",
                rating = 4,
                title = "Looks stylish and fits perfectly",
                comment = "Lightweight breathable mesh. Highly recommended for daily morning walks.",
                reviewDate = "05 Aug 2026"
            )
        )
        db.reviewDao().insertReview(
            ReviewEntity(
                productId = 4,
                userId = customerId,
                userName = "Rohit Patel",
                rating = 5,
                title = "Best budget phone under 10000 by far!",
                comment = "Battery backup lasts easily 2 full days. Camera is sharp for the price and 90Hz display makes it super smooth.",
                reviewDate = "15 Aug 2026"
            )
        )
    }

    private fun getInitialProducts(): List<ProductEntity> {
        return listOf(
            // Product 1: Men's Black Running Shoes (Juta / Joota)
            ProductEntity(
                id = 1,
                name = "AeroStride Pro Ultra Lightweight Men's Running Shoes (Black)",
                brand = "AeroStride",
                category = "Footwear",
                subcategory = "Running Shoes",
                sku = "AS-RUN-BLK-01",
                price = 1299.0,
                mrp = 2999.0,
                discountPercent = 56,
                stock = 45,
                rating = 4.6,
                reviewCount = 428,
                description = "Engineered with responsive cloud-foam cushioning and breathable air-mesh upper. Designed for maximum endurance, high shock absorption, and all-day walking comfort.",
                imagesJson = """["https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&q=80","https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=800&q=80","https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&q=80"]""",
                featuresJson = """["High-density memory foam insole for cloud-like cushioning","Breathable dual-layer mesh upper keeps feet cool","Anti-slip high-traction rubber outsole for wet and dry surfaces","Lightweight aerodynamic chassis weighing only 240g"]""",
                specsJson = """{"Brand":"AeroStride","Material":"Engineered Air Mesh & EVA","Sole":"Durable Rubber","Closure":"Lace-Up","Toe Style":"Round Toe","Ideal For":"Men Running, Walking, Gym"}""",
                includedJson = """["1 Pair of AeroStride Running Shoes","Extra Replacement Laces","Warranty & Care Guide"]""",
                deliveryEstimate = "FREE Delivery by Tomorrow, 9 PM",
                isDeal = true,
                dealDiscountText = "Limited Time Deal",
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["UK 6","UK 7","UK 8","UK 9","UK 10","UK 11"]""",
                colorsJson = """["Black","Navy Blue","All Black","Carbon Grey"]""",
                keywords = "juta, joota, shoe, shoes, sneakers, black shoes, men shoes, running shoes, footwear, athletic, gym sneakers, sports shoes",
                frequentlyBoughtIdsJson = """[2, 7]"""
            ),

            // Product 2: Men's Casual White Sneakers (Juta / Joota)
            ProductEntity(
                id = 2,
                name = "UrbanVibe Classic White Minimalist Street Sneakers",
                brand = "UrbanVibe",
                category = "Footwear",
                subcategory = "Sneakers",
                sku = "UV-SNK-WHT-02",
                price = 899.0,
                mrp = 2199.0,
                discountPercent = 59,
                stock = 60,
                rating = 4.4,
                reviewCount = 312,
                description = "Sleek and versatile street sneakers crafted from premium synthetic vegan leather with padded collar and cushioned arch support.",
                imagesJson = """["https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=800&q=80","https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=800&q=80"]""",
                featuresJson = """["Water-resistant synthetic leather upper with micro-perforations","Padded ankle collar prevents chafing","Flexible vulcanized rubber sole with superior grip","Timeless retro white silhouette suits jeans, shorts, and chinos"]""",
                specsJson = """{"Brand":"UrbanVibe","Upper Material":"Synthetic Vegan Leather","Sole":"Vulcanized TPR","Closure":"Lace-Up","Weight":"310g"}""",
                includedJson = """["1 Pair of UrbanVibe Street Sneakers","Dust Bag"]""",
                deliveryEstimate = "FREE Delivery in 2 Days",
                isDeal = false,
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["UK 7","UK 8","UK 9","UK 10"]""",
                colorsJson = """["White","White-Green","White-Navy"]""",
                keywords = "juta, joota, shoe, shoes, sneakers, white shoes, casual shoes, footwear, men shoes, street style",
                frequentlyBoughtIdsJson = """[1, 7]"""
            ),

            // Product 3: Men's Formal Oxford Leather Shoes (Juta)
            ProductEntity(
                id = 3,
                name = "CrownCraft Handcrafted Formal Derby Oxford Shoes (Dark Brown)",
                brand = "CrownCraft",
                category = "Footwear",
                subcategory = "Formal Shoes",
                sku = "CC-FRM-BRN-03",
                price = 1699.0,
                mrp = 3999.0,
                discountPercent = 57,
                stock = 30,
                rating = 4.5,
                reviewCount = 184,
                description = "Sophisticated formal derby shoes made with glossy burnished finish and orthotic memory cushion insole for office and weddings.",
                imagesJson = """["https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=800&q=80","https://images.unsplash.com/photo-1533867617858-e7b97e060509?w=800&q=80"]""",
                featuresJson = """["Hand-burnished dual-tone finish","Orthopedic memory cushion footbed","Non-skid reinforced stacked heel","Breathable sweat-wicking leather lining"]""",
                specsJson = """{"Brand":"CrownCraft","Material":"High-Grade Faux Burnished Leather","Sole":"Anti-Slip Resin","Heel Height":"1 inch"}""",
                includedJson = """["1 Pair of CrownCraft Derby Shoes","Shoe Horn","Shoe Polish Sponge"]""",
                deliveryEstimate = "FREE Delivery by Friday",
                isDeal = true,
                dealDiscountText = "Save ₹2300",
                isBestSeller = false,
                isRecommended = true,
                sizesJson = """["UK 7","UK 8","UK 9","UK 10","UK 11"]""",
                colorsJson = """["Dark Brown","Jet Black","Tan Brown"]""",
                keywords = "juta, joota, shoe, shoes, formal shoes, leather shoes, office shoes, brown shoes, men shoes, derby",
                frequentlyBoughtIdsJson = """[8]"""
            ),

            // Product 4: Budget 5G Smartphone under 10000 (Mobile / Phone)
            ProductEntity(
                id = 4,
                name = "VoltX Prime 5G Smartphone (6GB RAM, 128GB Storage | 5000mAh Battery)",
                brand = "VoltX",
                category = "Mobiles & Electronics",
                subcategory = "Smartphones",
                sku = "VX-P5G-128-BLU",
                price = 9499.0,
                mrp = 13999.0,
                discountPercent = 32,
                stock = 85,
                rating = 4.5,
                reviewCount = 1890,
                description = "Blazing-fast 5G smartphone powered by Octa-Core Processor, 6.6-inch FHD+ 90Hz Smooth Display, 50MP AI Dual Camera, and 5000mAh battery with 18W Fast Charging.",
                imagesJson = """["https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800&q=80","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80","https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800&q=80"]""",
                featuresJson = """["6.6-inch Full HD+ IPS LCD with buttery smooth 90Hz Refresh Rate","Powerful 6nm Octa-Core 5G Processor with 6GB RAM + 6GB Virtual RAM","50MP High-Resolution Primary AI Camera with Night Sight & Portrait mode","Massive 5000mAh 2-Day Battery with 18W Fast USB Type-C Charger","Side-Mounted Instant Fingerprint Scanner & Face Unlock"]""",
                specsJson = """{"Brand":"VoltX","Model":"Prime 5G","RAM":"6 GB","Storage":"128 GB (Expandable to 1TB)","Battery":"5000 mAh","Processor":"Dimensity 6020 Octa-Core","Display":"6.6-inch FHD+ 90Hz","OS":"Android 14 (Stock UI)"}""",
                includedJson = """["VoltX Prime 5G Smartphone","18W Fast Power Adapter","USB-C to USB-A Cable","SIM Ejector Tool","Clear TPU Protective Case","Quick Start Guide"]""",
                deliveryEstimate = "FREE Delivery Tomorrow by 1 PM",
                isDeal = true,
                dealDiscountText = "Under ₹10,000 Special Deal",
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["6GB + 128GB","8GB + 256GB"]""",
                colorsJson = """["Ocean Blue","Midnight Black","Emerald Green"]""",
                keywords = "mobile, phone, smartphone, 5g, mobile under 10000, phone under 10000, android phone, budget 5g, voltx, dual sim",
                frequentlyBoughtIdsJson = """[5, 6]"""
            ),

            // Product 5: Premium Wireless ANC Earbuds
            ProductEntity(
                id = 5,
                name = "SonicPod Pro Active Noise Cancelling True Wireless Earbuds",
                brand = "SonicPod",
                category = "Mobiles & Electronics",
                subcategory = "Audio",
                sku = "SP-TWS-ANC-05",
                price = 1799.0,
                mrp = 4999.0,
                discountPercent = 64,
                stock = 120,
                rating = 4.7,
                reviewCount = 950,
                description = "Immersive audio with 35dB Active Noise Cancellation, quad-mic ENC for crystal clear calls, 40 hours total playtime, and IPX5 water resistance.",
                imagesJson = """["https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800&q=80","https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?w=800&q=80"]""",
                featuresJson = """["Hybrid Active Noise Cancellation up to 35dB eliminates ambient background noise","13mm Titanium Dynamic Drivers with Deep Bass Boost","40 Hours total playback with compact magnetic charging case","Low 45ms Latency Gaming Mode for synchronized sound","Quad Mics with AI Environmental Noise Cancellation for clear calls"]""",
                specsJson = """{"Brand":"SonicPod","Connectivity":"Bluetooth 5.3","Battery Life":"40 Hours with Case","Water Resistance":"IPX5","Driver Size":"13mm Titanium","Charging Port":"Type-C Fast Charge"}""",
                includedJson = """["SonicPod Pro Earbuds (L/R)","Charging Case","3 Pairs Silicone Ear Tips (S/M/L)","Type-C Charging Cable","User Manual"]""",
                deliveryEstimate = "FREE Delivery by Tomorrow",
                isDeal = true,
                dealDiscountText = "64% OFF Deal of the Day",
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["Standard"]""",
                colorsJson = """["Carbon Black","Pearl White","Navy Blue"]""",
                keywords = "earbuds, earphones, tws, wireless earbuds, bluetooth, sonicpod, headphones, airpods, anc, noise cancelling",
                frequentlyBoughtIdsJson = """[4, 6]"""
            ),

            // Product 6: Smart Fitness Tracker Watch
            ProductEntity(
                id = 6,
                name = "PulseFit Horizon 1.95-inch AMOLED Bluetooth Calling Smartwatch",
                brand = "PulseFit",
                category = "Mobiles & Electronics",
                subcategory = "Smartwatches",
                sku = "PF-HOR-BLK-06",
                price = 1499.0,
                mrp = 4499.0,
                discountPercent = 67,
                stock = 75,
                rating = 4.4,
                reviewCount = 670,
                description = "Stunning 1.95\" High-Brightness AMOLED display with Always-On screen, Bluetooth calling with loud speaker, continuous SpO2 & Heart Rate tracking, and 120+ sports modes.",
                imagesJson = """["https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80","https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800&q=80"]""",
                featuresJson = """["1.95\" HD AMOLED Display with 600 nits peak brightness","Single-chip Bluetooth 5.2 Calling with HD speaker & microphone","24/7 Real-Time Heart Rate, SpO2 Blood Oxygen & Sleep Stage Tracking","7 Days battery life on typical use, 20 days standby","100+ Cloud Watch Faces & 120+ Sport Tracking Modes"]""",
                specsJson = """{"Brand":"PulseFit","Screen":"1.95 inch AMOLED","Battery":"7 Days","Sensors":"Heart Rate, SpO2, Pedometer, Sleep","Waterproof":"IP68 Rating"}""",
                includedJson = """["PulseFit Horizon Smartwatch","Silicone Strap","Magnetic Fast Charging Cable","User Manual"]""",
                deliveryEstimate = "FREE Delivery in 2 Days",
                isDeal = false,
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["1.95 inch Standard"]""",
                colorsJson = """["Jet Black","Active Silver","Rose Gold"]""",
                keywords = "smartwatch, watch, pulsefit, fitness band, bluetooth calling, amoled watch, smart watch, tracker",
                frequentlyBoughtIdsJson = """[4, 5]"""
            ),

            // Product 7: Men's Pure Cotton Red T-Shirt
            ProductEntity(
                id = 7,
                name = "AeroWear Classic Crewneck 100% Bio-Washed Cotton Red T-Shirt",
                brand = "AeroWear",
                category = "Fashion & Clothing",
                subcategory = "T-Shirts",
                sku = "AW-TSH-RED-07",
                price = 399.0,
                mrp = 999.0,
                discountPercent = 60,
                stock = 150,
                rating = 4.5,
                reviewCount = 820,
                description = "Premium 180 GSM combed organic cotton t-shirt with bio-wash silicone finish for ultra-soft hand feel, zero shrinkage, and vibrant red color that lasts.",
                imagesJson = """["https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800&q=80","https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=800&q=80"]""",
                featuresJson = """["100% Combed Ringspun Bio-Washed Cotton (180 GSM)","Pre-shrunk fabric ensures no fit distortion after washes","Reinforced double-stitched collar and hem for maximum durability","Tagless comfort collar design to prevent neck irritation"]""",
                specsJson = """{"Brand":"AeroWear","Material":"100% Combed Cotton","Fit":"Regular Fit","Neck":"Crew Neck","Sleeve":"Short Sleeve","Care":"Machine Wash Cold"}""",
                includedJson = """["1 AeroWear Classic Cotton T-Shirt"]""",
                deliveryEstimate = "FREE Delivery by Tomorrow",
                isDeal = true,
                dealDiscountText = "Best Seller in Men's Clothing",
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["S","M","L","XL","XXL"]""",
                colorsJson = """["Crimson Red","Jet Black","Navy Blue","Olive Green","White"]""",
                keywords = "red t shirt, t shirt, shirt, red shirt, cotton tshirt, men clothing, kapda, casual wear, top wear, aerowear",
                frequentlyBoughtIdsJson = """[1, 8]"""
            ),

            // Product 8: Men's Slim Fit Stretch Denim Jeans
            ProductEntity(
                id = 8,
                name = "DenimCraft Urban Slim Fit Stretchable Denim Jeans (Dark Indigo)",
                brand = "DenimCraft",
                category = "Fashion & Clothing",
                subcategory = "Jeans",
                sku = "DC-JNS-IND-08",
                price = 999.0,
                mrp = 2499.0,
                discountPercent = 60,
                stock = 90,
                rating = 4.3,
                reviewCount = 510,
                description = "Modern slim-fit jeans made with 98% cotton and 2% elastane for unrestricted stretch, natural whiskering wash, and heavy-duty brass zip fly.",
                imagesJson = """["https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=800&q=80","https://images.unsplash.com/photo-1542272604-780c96856592?w=800&q=80"]""",
                featuresJson = """["Super-stretch premium denim provides all-day flexing comfort","Classic 5-pocket construction with reinforced bar-tacks","Subtle enzyme stone wash with authentic faded whiskers","Durable metal shank button and smooth YKK zipper"]""",
                specsJson = """{"Brand":"DenimCraft","Fabric":"98% Cotton, 2% Elastane","Rise":"Mid Rise","Fit":"Slim Fit","Wash":"Dark Indigo Wash"}""",
                includedJson = """["1 DenimCraft Stretch Jeans"]""",
                deliveryEstimate = "FREE Delivery in 2 Days",
                isDeal = false,
                isBestSeller = false,
                isRecommended = true,
                sizesJson = """["30","32","34","36","38"]""",
                colorsJson = """["Dark Indigo","Faded Blue","Jet Black"]""",
                keywords = "jeans, denim, pants, trousers, men jeans, blue jeans, stretch jeans, kapda, bottom wear",
                frequentlyBoughtIdsJson = """[7, 1]"""
            ),

            // Product 9: 15.6-inch Thin & Light Laptop
            ProductEntity(
                id = 9,
                name = "NexBook Slim 15.6\" FHD Laptop (Core i5 12th Gen, 16GB RAM, 512GB NVMe SSD, Win 11)",
                brand = "NexBook",
                category = "Mobiles & Electronics",
                subcategory = "Laptops",
                sku = "NB-SLM-I5-09",
                price = 38990.0,
                mrp = 54990.0,
                discountPercent = 29,
                stock = 25,
                rating = 4.6,
                reviewCount = 290,
                description = "Ultra-sleek lightweight laptop engineered for professionals and students. Features 12th Gen Intel Core i5 processor, 16GB High-Speed DDR4 RAM, 512GB Fast NVMe SSD, Backlit Keyboard, and 8-hour battery.",
                imagesJson = """["https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&q=80","https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80"]""",
                featuresJson = """["Intel Core i5 12th Gen 10-Core Processor (up to 4.4GHz Turbo)","16GB DDR4 3200MHz RAM + 512GB PCIe M.2 NVMe SSD Storage","15.6\" FHD (1920x1080) Anti-Glare IPS Display with 300 nits brightness","Full-sized Backlit Ergonomic Keyboard with integrated Precision Touchpad","Windows 11 Home & MS Office Pre-Installed"]""",
                specsJson = """{"Brand":"NexBook","Processor":"Intel Core i5-1235U","RAM":"16 GB DDR4","Storage":"512 GB NVMe SSD","Display":"15.6 inch FHD IPS","Weight":"1.65 kg","Battery":"Up to 8.5 Hours"}""",
                includedJson = """["NexBook Slim 15.6 Laptop","65W Fast Type-C Power Adapter","Laptop Sleeve Bag","User Manual & Warranty Card"]""",
                deliveryEstimate = "FREE Express Delivery Tomorrow",
                isDeal = true,
                dealDiscountText = "Save ₹16,000 with Exchange Offers",
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["16GB RAM + 512GB SSD","16GB RAM + 1TB SSD"]""",
                colorsJson = """["Space Grey","Arctic Silver"]""",
                keywords = "laptop, computer, notebook, intel i5, windows 11, electronics, pc, thin and light laptop, nexbook",
                frequentlyBoughtIdsJson = """[5]"""
            ),

            // Product 10: Automatic Espresso Coffee Machine (Home & Kitchen)
            ProductEntity(
                id = 10,
                name = "BaristaPro 15-Bar High Pressure Espresso & Cappuccino Coffee Maker with Milk Frother",
                brand = "BaristaPro",
                category = "Home & Kitchen",
                subcategory = "Appliances",
                sku = "BP-CFE-ESP-10",
                price = 4499.0,
                mrp = 8999.0,
                discountPercent = 50,
                stock = 35,
                rating = 4.7,
                reviewCount = 340,
                description = "Craft cafe-quality espresso, creamy lattes, and frothy cappuccinos at home with 15-Bar Italian high-pressure pump, rapid thermoblock heating, and stainless steel steam wand.",
                imagesJson = """["https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=800&q=80","https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?w=800&q=80"]""",
                featuresJson = """["15-Bar professional Italian ULKA pump extracts rich crema","Dual temperature control for brewing and milk frothing simultaneously","Stainless steel high-power adjustable steam frothing wand","Detachable 1.5L transparent water tank and removable drip tray for easy cleaning"]""",
                specsJson = """{"Brand":"BaristaPro","Pump Pressure":"15 Bar","Water Tank":"1.5 Litres","Power":"1050 Watts","Material":"Brushed Stainless Steel"}""",
                includedJson = """["BaristaPro Espresso Machine","Portafilter","Single & Double Shot Filters","Coffee Measuring Tamper Spoon","Stainless Steel Frothing Pitcher"]""",
                deliveryEstimate = "FREE Delivery by Tomorrow",
                isDeal = true,
                dealDiscountText = "50% Off Special Home Festival",
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["1.5L Standard"]""",
                colorsJson = """["Brushed Stainless Steel","Matte Black"]""",
                keywords = "coffee maker, espresso, cappuccino, kitchen, appliance, baristapro, home, tea, coffee machine",
                frequentlyBoughtIdsJson = """[]"""
            ),

            // Product 11: Luxury Oud & Amber Eau De Parfum (Beauty & Grooming)
            ProductEntity(
                id = 11,
                name = "AromaLux Imperial Oud & Amber Royale Eau De Parfum (100ml)",
                brand = "AromaLux",
                category = "Beauty & Grooming",
                subcategory = "Perfumes",
                sku = "AL-OUD-100-11",
                price = 799.0,
                mrp = 1999.0,
                discountPercent = 60,
                stock = 80,
                rating = 4.6,
                reviewCount = 490,
                description = "Long-lasting luxury fragrance crafted with rare Cambodian Oud, warm golden Amber, Bergamot, and rich Madagascar Vanilla notes. Lasts up to 24 hours.",
                imagesJson = """["https://images.unsplash.com/photo-1594035910387-fea47794261f?w=800&q=80","https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=800&q=80"]""",
                featuresJson = """["Concentrated Eau De Parfum with 20% fragrance oil concentration","24-hour long-lasting scent projection","Top Notes: Bergamot & Cardamom | Heart: Cambodian Oud | Base: Amber & Vanilla","Elegantly packaged in heavy crystal glass bottle with magnetic gold cap"]""",
                specsJson = """{"Brand":"AromaLux","Volume":"100 ml","Fragrance Type":"Eau De Parfum (EDP)","Target Gender":"Unisex"}""",
                includedJson = """["100ml AromaLux Imperial Oud Perfume","Luxury Velvet Gift Pouch"]""",
                deliveryEstimate = "FREE Delivery in 2 Days",
                isDeal = false,
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["100 ml","50 ml"]""",
                colorsJson = """["Royal Gold"]""",
                keywords = "perfume, fragrance, oud, attar, scent, beauty, grooming, aroma, cologne, men perfume",
                frequentlyBoughtIdsJson = """[12]"""
            ),

            // Product 12: Men's All-in-One Trimmer & Grooming Kit
            ProductEntity(
                id = 12,
                name = "GroomMaster Multi-Blade Titanium Cordless Beard & Body Groomer",
                brand = "GroomMaster",
                category = "Beauty & Grooming",
                subcategory = "Shaving & Grooming",
                sku = "GM-TRM-TIT-12",
                price = 849.0,
                mrp = 1999.0,
                discountPercent = 57,
                stock = 110,
                rating = 4.5,
                reviewCount = 740,
                description = "Professional cordless beard trimmer with self-sharpening titanium-coated blades, 20 precision length settings, 120 minutes runtime, and fast USB-C charging.",
                imagesJson = """["https://images.unsplash.com/photo-1621607512214-68297480165e?w=800&q=80","https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800&q=80"]""",
                featuresJson = """["Self-sharpening titanium rounded blades ensure skin-friendly zero cuts","20 lock-in length settings from 0.5mm to 10mm with 0.5mm precision zoom wheel","120 minutes of cordless grooming on a single 1.5-hour quick charge","100% Waterproof IPX7 washable head for easy tap cleaning"]""",
                specsJson = """{"Brand":"GroomMaster","Run Time":"120 Minutes","Blade Material":"Titanium Stainless Steel","Charging":"USB Type-C","Waterproof":"IPX7 Washable"}""",
                includedJson = """["GroomMaster Cordless Trimmer","Adjustable Precision Comb (0.5-10mm)","Nose & Ear Trimmer Attachment","Cleaning Brush","Lubricating Oil","Type-C Charging Cable"]""",
                deliveryEstimate = "FREE Delivery by Tomorrow",
                isDeal = true,
                dealDiscountText = "Save ₹1150",
                isBestSeller = true,
                isRecommended = true,
                sizesJson = """["Standard"]""",
                colorsJson = """["Gunmetal Grey","Matte Black"]""",
                keywords = "trimmer, grooming, beard trimmer, shaver, razor, beauty, men care, groommaster, shaving",
                frequentlyBoughtIdsJson = """[11, 7]"""
            )
        )
    }
}
