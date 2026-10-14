package com.example.model

data class PackagingOption(
  val id: String,
  val title: String,
  val eggCount: Int,
  val pricePkr: Double,
  val originalPricePkr: Double? = null,
  val savingText: String? = null
)

data class EggProduct(
  val id: String,
  val title: String,
  val urduTitle: String,
  val subtitle: String,
  val category: String,
  val description: String,
  val packagingOptions: List<PackagingOption>,
  val proteinPerEgg: String,
  val caloriesPerEgg: String,
  val farmOrigin: String,
  val badge: String,
  val isBestSeller: Boolean = false,
  val isDesiOrganic: Boolean = false
)

object EggCatalog {
  val products = listOf(
    EggProduct(
      id = "desi_organic",
      title = "Desi Farm Organic Eggs",
      urduTitle = "اصلی دیسی انڈے",
      subtitle = "100% Free-Range Organic Golden Yolk",
      category = "Desi Organic",
      description = "Gathered daily from healthy, grain-fed, naturally roaming desi hens. Rich deep amber yolks packed with natural Vitamin A, D, and Omega-3. Cleaned, inspected, and crack-free guaranteed.",
      packagingOptions = listOf(
        PackagingOption("desi_dozen", "1 Dozen (12 Eggs)", 12, 420.0, 460.0, "Save Rs. 40"),
        PackagingOption("desi_tray", "1 Tray (30 Eggs)", 30, 990.0, 1150.0, "Save Rs. 160"),
        PackagingOption("desi_crate", "Quarter Crate (90 Eggs)", 90, 2850.0, 3450.0, "Save Rs. 600")
      ),
      proteinPerEgg = "6.5g",
      caloriesPerEgg = "74 kcal",
      farmOrigin = "Rozgar Organic Poultry Farm, Punjab",
      badge = "100% PURE DESI",
      isBestSeller = true,
      isDesiOrganic = true
    ),
    EggProduct(
      id = "farm_white_grade_a",
      title = "Farm Fresh White Eggs",
      urduTitle = "فارم کے تازہ سفید انڈے",
      subtitle = "Grade-A Extra Large Daily Morning Harvest",
      category = "Farm Fresh",
      description = "Classic daily breakfast eggs produced in sanitized, bio-secure, temperature-regulated facilities. Candled and weight-sorted for uniform size and pristine shells.",
      packagingOptions = listOf(
        PackagingOption("white_dozen", "1 Dozen (12 Eggs)", 12, 330.0, 360.0, "Save Rs. 30"),
        PackagingOption("white_tray", "1 Tray (30 Eggs)", 30, 780.0, 900.0, "Save Rs. 120"),
        PackagingOption("white_crate", "Full Crate (360 Eggs)", 360, 8900.0, 10800.0, "Wholesale Rate")
      ),
      proteinPerEgg = "6.3g",
      caloriesPerEgg = "70 kcal",
      farmOrigin = "Rozgar Modern Layers Farm",
      badge = "DAILY HARVEST",
      isBestSeller = true
    ),
    EggProduct(
      id = "premium_brown_layers",
      title = "Golden Brown Farm Eggs",
      urduTitle = "گولڈن براؤن فارمی انڈے",
      subtitle = "High Protein Thick-Shelled Brown Eggs",
      category = "Farm Fresh",
      description = "Naturally brown-shelled eggs from Rhode Island Red layers. Thicker shell protects inner yolk freshness longer, offering a delicious rich taste for cooking and baking.",
      packagingOptions = listOf(
        PackagingOption("brown_dozen", "1 Dozen (12 Eggs)", 12, 360.0, 400.0, "Save Rs. 40"),
        PackagingOption("brown_tray", "1 Tray (30 Eggs)", 30, 850.0, 980.0, "Save Rs. 130"),
        PackagingOption("brown_crate", "Master Crate (360 Eggs)", 360, 9800.0, 12000.0, "Bakery Favorite")
      ),
      proteinPerEgg = "6.7g",
      caloriesPerEgg = "72 kcal",
      farmOrigin = "Rozgar Certified Breeders Hub",
      badge = "THICK SHELL"
    ),
    EggProduct(
      id = "omega3_enriched",
      title = "Omega-3 Enriched Golden Eggs",
      urduTitle = "اومیگا 3 افزودہ انڈے",
      subtitle = "Fortified with Flaxseed & Natural DHA",
      category = "Omega-3 & Special",
      description = "Hens fed on organic flaxseed, marine algae, and natural greens yielding 5x more Omega-3 fatty acids for heart, brain, and eye health. Perfect for fitness and kids.",
      packagingOptions = listOf(
        PackagingOption("omega_dozen", "1 Dozen (12 Eggs)", 12, 490.0, 560.0, "Save Rs. 70"),
        PackagingOption("omega_tray", "1 Tray (30 Eggs)", 30, 1180.0, 1400.0, "Save Rs. 220")
      ),
      proteinPerEgg = "6.8g",
      caloriesPerEgg = "73 kcal",
      farmOrigin = "Rozgar Bio-Nutrient Poultry Research",
      badge = "HEALTH & FITNESS"
    ),
    EggProduct(
      id = "quail_eggs_bater",
      title = "Fresh Quail Eggs (Bater Kay Anday)",
      urduTitle = "بٹیر کے انڈے",
      subtitle = "Nutrient-Dense Traditional Delicacy",
      category = "Omega-3 & Special",
      description = "Exotic, bite-sized spotted quail eggs prized for high iron, B-vitamins, and delicate rich flavor. Popular for traditional immunity boosting and gourmet dishes.",
      packagingOptions = listOf(
        PackagingOption("quail_pack18", "Carton Pack (18 Eggs)", 18, 380.0, 420.0, "Save Rs. 40"),
        PackagingOption("quail_pack36", "Double Carton (36 Eggs)", 36, 720.0, 840.0, "Save Rs. 120")
      ),
      proteinPerEgg = "1.2g",
      caloriesPerEgg = "14 kcal",
      farmOrigin = "Rozgar Quail Sanctuary, Rawalpindi",
      badge = "IMMUNITY BOOST"
    ),
    EggProduct(
      id = "bulk_wholesale_crates",
      title = "Commercial Restaurant Wholesale Crates",
      urduTitle = "ہول سیل انڈے برائے ریسٹورنٹ",
      subtitle = "Bulk Trays with Direct Delivery for Cafes & Bakeries",
      category = "Wholesale Crates",
      description = "Commercial grade daily trays supplied to restaurants, hotels, cafes, and bakeries at official wholesale rates. Includes shock-proof carton protection and morning express delivery.",
      packagingOptions = listOf(
        PackagingOption("bulk_5_trays", "5 Trays Bundle (150 Eggs)", 150, 3650.0, 4100.0, "Save Rs. 450"),
        PackagingOption("bulk_10_trays", "10 Trays Master Crate (300 Eggs)", 300, 7100.0, 8200.0, "Wholesale Discount"),
        PackagingOption("bulk_20_trays", "20 Trays Mega Lot (600 Eggs)", 600, 13900.0, 16400.0, "Best Margin")
      ),
      proteinPerEgg = "6.4g",
      caloriesPerEgg = "71 kcal",
      farmOrigin = "Rozgar Direct Logistics Hub",
      badge = "WHOLESALE DEALS"
    )
  )
}

data class PaymentContact(
  val name: String,
  val phoneNumber: String,
  val title: String,
  val accountsAvailable: List<String>,
  val description: String
)

object PaymentDirectory {
  val primaryContacts = listOf(
    PaymentContact(
      name = "M Faisal",
      phoneNumber = "03337803988",
      title = "Managing Partner & Accounts",
      accountsAvailable = listOf("EasyPaisa", "JazzCash", "Meezan / HBL Bank"),
      description = "Transfer payment via EasyPaisa or JazzCash to this mobile number for quick verification."
    ),
    PaymentContact(
      name = "Nek Muhammad",
      phoneNumber = "03313177438",
      title = "Operations & Verification Head",
      accountsAvailable = listOf("EasyPaisa", "JazzCash", "Bank Alfalah"),
      description = "Alternative official account for 24/7 transaction confirmation and dispatch queries."
    )
  )

  const val OFFICIAL_WEBSITE = "https://rozgar-egg-point.pk"
  const val SUPPORT_HOURS = "7:00 AM – 10:00 PM (Everyday)"
}
