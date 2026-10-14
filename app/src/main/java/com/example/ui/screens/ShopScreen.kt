package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.db.CartItemEntity
import com.example.model.EggCatalog
import com.example.model.EggProduct
import com.example.model.PackagingOption
import com.example.ui.theme.FarmAmberDark
import com.example.ui.theme.FarmAmberLight
import com.example.ui.theme.FarmAmberPrimary
import com.example.ui.theme.FarmGreen
import com.example.ui.theme.FarmGreenSoft
import com.example.ui.theme.GoldenYolk
import com.example.ui.theme.GoldenYolkSoft
import com.example.util.IntentHelper

@Composable
fun ShopScreen(
  cartItems: List<CartItemEntity>,
  activeCategory: String,
  onCategorySelected: (String) -> Unit,
  onAddToCart: (EggProduct, PackagingOption, Int) -> Unit,
  onOpenCart: () -> Unit,
  onOpenContact: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val categories = listOf("All", "Desi Organic", "Farm Fresh", "Omega-3 & Special", "Wholesale Crates")

  val filteredProducts = remember(activeCategory) {
    if (activeCategory == "All") {
      EggCatalog.products
    } else {
      EggCatalog.products.filter { it.category == activeCategory }
    }
  }

  val totalCartCount = cartItems.sumOf { it.quantity }
  val totalCartAmount = cartItems.sumOf { it.unitPrice * it.quantity }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("shop_product_list"),
      contentPadding = PaddingValues(bottom = if (totalCartCount > 0) 90.dp else 24.dp)
    ) {
      // 1. Fresh harvest ticker
      item {
        FreshHarvestTicker()
      }

      // 2. Hero Farm Banner
      item {
        FarmHeroBanner(onOpenContact = onOpenContact)
      }

      // 3. Quick Payment & Verification contacts strip
      item {
        QuickVerificationStrip(
          onCallFaisal = { IntentHelper.dialPhone(context, "03337803988") },
          onCallNek = { IntentHelper.dialPhone(context, "03313177438") }
        )
      }

      // 4. Category Filter Chips
      item {
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(categories) { category ->
            val isSelected = activeCategory == category
            FilterChip(
              selected = isSelected,
              onClick = { onCategorySelected(category) },
              label = {
                Text(
                  text = category,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = FarmAmberPrimary,
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.testTag("category_chip_${category.lowercase().replace(" ", "_")}")
            )
          }
        }
      }

      // 5. Products List
      items(filteredProducts, key = { it.id }) { product ->
        ProductCard(
          product = product,
          onAddToCart = { packageOption, qty ->
            onAddToCart(product, packageOption, qty)
          }
        )
      }

      // 6. Guarantee & Info Banner
      item {
        ZeroBreakageGuaranteeCard()
      }
    }

    // Floating Cart Summary Bar
    AnimatedVisibility(
      visible = totalCartCount > 0,
      enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
      exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(16.dp)
    ) {
      FloatingCartBar(
        itemCount = totalCartCount,
        totalAmount = totalCartAmount,
        onViewCart = onOpenCart
      )
    }
  }
}

@Composable
fun FreshHarvestTicker() {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = GoldenYolkSoft
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(FarmGreen)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "TODAY'S HARVEST: Farm-fresh Desi & White eggs ready for instant home delivery! Free shipping above Rs. 1,500.",
        style = MaterialTheme.typography.bodySmall.copy(
          fontSize = 11.5.sp,
          fontWeight = FontWeight.SemiBold,
          color = FarmAmberDark
        )
      )
    }
  }
}

@Composable
fun FarmHeroBanner(onOpenContact: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 10.dp),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_hero_eggs),
        contentDescription = "Fresh Farm Eggs Crates",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, Color(0xDD120C07)),
              startY = 30f
            )
          )
      )
      Column(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .background(FarmGreen, RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "100% FARM FRESH",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .background(GoldenYolk, RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "HOME DELIVERY",
              color = Color.Black,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Fresh Eggs Delivered to Your Doorstep",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
        Text(
          text = "Candled, weight-sorted & shock-proof packed for zero breakage",
          color = Color(0xFFF3ECE1),
          fontSize = 12.sp
        )
      }
    }
  }
}

@Composable
fun QuickVerificationStrip(
  onCallFaisal: () -> Unit,
  onCallNek: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    shape = RoundedCornerShape(12.dp),
    color = FarmAmberLight,
    border = androidx.compose.foundation.BorderStroke(1.dp, FarmAmberPrimary.copy(alpha = 0.3f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = FarmAmberPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "EasyPaisa / JazzCash Verification",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = FarmAmberDark
            )
          )
        }
        Text(
          text = "M Faisal: 03337803988 • Nek Muhammad: 03313177438",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Row {
        OutlinedButton(
          onClick = onCallFaisal,
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.testTag("call_faisal_button")
        ) {
          Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Faisal", fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.width(6.dp))
        OutlinedButton(
          onClick = onCallNek,
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.testTag("call_nek_button")
        ) {
          Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Nek", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
fun ProductCard(
  product: EggProduct,
  onAddToCart: (PackagingOption, Int) -> Unit
) {
  var selectedPackageIndex by remember { mutableIntStateOf(0) }
  var quantity by remember { mutableIntStateOf(1) }

  val currentPackage = product.packagingOptions.getOrElse(selectedPackageIndex) {
    product.packagingOptions.first()
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header with badge and urdu title
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .background(
                if (product.isDesiOrganic) GoldenYolk else FarmAmberPrimary,
                RoundedCornerShape(4.dp)
              )
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = product.badge,
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = product.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
          )
          Text(
            text = product.urduTitle,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              color = FarmAmberDark,
              fontSize = 14.sp
            )
          )
          Text(
            text = product.subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = product.description,
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )
      )

      // Nutrition and farm badges
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = FarmGreenSoft
        ) {
          Text(
            text = "⚡ Protein: ${product.proteinPerEgg}",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 11.sp,
            color = FarmGreen,
            fontWeight = FontWeight.SemiBold
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = GoldenYolkSoft
        ) {
          Text(
            text = "🔥 ${product.caloriesPerEgg}",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 11.sp,
            color = FarmAmberDark,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Select Quantity & Packing:",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))

      // Packaging option tabs
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        product.packagingOptions.forEachIndexed { index, option ->
          val isSelected = index == selectedPackageIndex
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .clickable { selectedPackageIndex = index }
              .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) FarmAmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
              ),
            color = if (isSelected) FarmAmberLight else MaterialTheme.colorScheme.surface
          ) {
            Column(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = option.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) FarmAmberPrimary else MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Rs. ${option.pricePkr.toInt()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              if (option.savingText != null) {
                Text(
                  text = option.savingText,
                  fontSize = 9.sp,
                  color = FarmGreen,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Price and Add to Cart Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "Rs. ${(currentPackage.pricePkr * quantity).toInt()}",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = FarmAmberPrimary,
                fontSize = 20.sp
              )
            )
            if (currentPackage.originalPricePkr != null) {
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Rs. ${(currentPackage.originalPricePkr * quantity).toInt()}",
                style = MaterialTheme.typography.bodySmall.copy(
                  textDecoration = TextDecoration.LineThrough,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }
          Text(
            text = "${currentPackage.eggCount * quantity} Fresh Eggs in total",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Counter
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clickable { if (quantity > 1) quantity-- },
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
              }
              Text(
                text = quantity.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 6.dp)
              )
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clickable { quantity++ },
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
              }
            }
          }

          Button(
            onClick = {
              onAddToCart(currentPackage, quantity)
              quantity = 1
            },
            colors = ButtonDefaults.buttonColors(containerColor = FarmAmberPrimary),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            modifier = Modifier.testTag("add_to_cart_${product.id}")
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }
    }
  }
}

@Composable
fun ZeroBreakageGuaranteeCard() {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = FarmGreenSoft)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Security,
        contentDescription = "Zero Breakage Guarantee",
        tint = FarmGreen,
        modifier = Modifier.size(32.dp)
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "Zero Breakage Egg Guarantee",
          fontWeight = FontWeight.Bold,
          color = FarmGreen,
          fontSize = 14.sp
        )
        Text(
          text = "Every egg tray is inspected, cushioned, and delivered safely. If you find any damaged egg upon arrival, report it immediately for 100% free instant replacement!",
          fontSize = 11.5.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 15.sp
        )
      }
    }
  }
}

@Composable
fun FloatingCartBar(
  itemCount: Int,
  totalAmount: Double,
  onViewCart: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onViewCart() }
      .testTag("floating_cart_bar"),
    color = FarmAmberPrimary,
    shadowElevation = 8.dp,
    shape = RoundedCornerShape(14.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .background(Color.White.copy(alpha = 0.25f), CircleShape)
            .padding(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "$itemCount Items Selected",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = "Total: Rs. ${totalAmount.toInt()}",
            color = GoldenYolkSoft,
            fontSize = 12.sp
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Checkout",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
          imageVector = Icons.Default.LocalShipping,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
