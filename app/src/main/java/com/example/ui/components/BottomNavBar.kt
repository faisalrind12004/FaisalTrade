package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FarmAmberPrimary
import com.example.ui.theme.GoldenYolkSoft
import com.example.viewmodel.AppTab

data class NavItem(
  val tab: AppTab,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val label: String
)

@Composable
fun RozgarBottomBar(
  currentTab: AppTab,
  onTabSelected: (AppTab) -> Unit,
  activeOrdersCount: Int = 0,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavItem(AppTab.SHOP, Icons.Filled.Egg, Icons.Outlined.Egg, "Eggs"),
    NavItem(AppTab.ORDERS, Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping, "Tracking"),
    NavItem(AppTab.LOYALTY, Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard, "Egg Club"),
    NavItem(AppTab.PAYMENT_HUB, Icons.Filled.Payment, Icons.Outlined.Payment, "Payment")
  )

  NavigationBar(
    modifier = modifier
      .navigationBarsPadding()
      .testTag("bottom_nav_bar"),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp
  ) {
    items.forEach { item ->
      val selected = currentTab == item.tab
      NavigationBarItem(
        selected = selected,
        onClick = { onTabSelected(item.tab) },
        icon = {
          if (item.tab == AppTab.ORDERS && activeOrdersCount > 0) {
            BadgedBox(
              badge = {
                Badge(
                  containerColor = FarmAmberPrimary,
                  contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                  Text(text = activeOrdersCount.toString())
                }
              }
            ) {
              Icon(
                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label
              )
            }
          } else {
            Icon(
              imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
              contentDescription = item.label
            )
          }
        },
        label = {
          Text(
            text = item.label,
            fontSize = 11.sp
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = FarmAmberPrimary,
          selectedTextColor = FarmAmberPrimary,
          indicatorColor = GoldenYolkSoft,
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.testTag("nav_item_${item.tab.name.lowercase()}")
      )
    }
  }
}
