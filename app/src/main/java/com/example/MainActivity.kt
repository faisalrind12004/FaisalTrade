package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.RozgarBottomBar
import com.example.ui.components.RozgarTopBar
import com.example.ui.dialogs.CartBottomSheet
import com.example.ui.dialogs.CheckoutBottomSheet
import com.example.ui.dialogs.ContactBottomSheet
import com.example.ui.dialogs.NotificationsBottomSheet
import com.example.ui.screens.LoyaltyScreen
import com.example.ui.screens.PaymentHubScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppTab
import com.example.viewmodel.EggViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        RozgarEggApp()
      }
    }
  }
}

@Composable
fun RozgarEggApp(viewModel: EggViewModel = viewModel()) {
  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
  val orders by viewModel.orders.collectAsStateWithLifecycle()
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()
  val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
  val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
  val selectedOrder by viewModel.selectedOrder.collectAsStateWithLifecycle()
  val activeCategory by viewModel.activeCategoryFilter.collectAsStateWithLifecycle()
  val checkoutForm by viewModel.checkoutForm.collectAsStateWithLifecycle()

  val showCartSheet by viewModel.showCartSheet.collectAsStateWithLifecycle()
  val showCheckoutSheet by viewModel.showCheckoutSheet.collectAsStateWithLifecycle()
  val showNotificationsSheet by viewModel.showNotificationsSheet.collectAsStateWithLifecycle()
  val showContactSheet by viewModel.showContactSheet.collectAsStateWithLifecycle()
  val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  // Handle snackbar messages
  LaunchedEffect(userMessage) {
    userMessage?.let { msg ->
      snackbarHostState.showSnackbar(
        message = msg,
        duration = SnackbarDuration.Short
      )
      viewModel.clearUserMessage()
    }
  }

  // Back handling
  BackHandler(
    enabled = showCheckoutSheet || showCartSheet || showNotificationsSheet || showContactSheet || currentTab != AppTab.SHOP
  ) {
    when {
      showCheckoutSheet -> viewModel.setCheckoutSheetVisible(false)
      showCartSheet -> viewModel.setCartSheetVisible(false)
      showNotificationsSheet -> viewModel.setNotificationsSheetVisible(false)
      showContactSheet -> viewModel.setContactSheetVisible(false)
      currentTab != AppTab.SHOP -> viewModel.setTab(AppTab.SHOP)
    }
  }

  val activeOrdersCount = orders.count {
    it.status != "DELIVERED" && it.status != "CANCELLED"
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("main_scaffold"),
    topBar = {
      RozgarTopBar(
        unreadCount = unreadCount,
        cartItemCount = cartItems.sumOf { it.quantity },
        onOpenNotifications = { viewModel.setNotificationsSheetVisible(true) },
        onOpenCart = { viewModel.setCartSheetVisible(true) },
        onOpenContact = { viewModel.setContactSheetVisible(true) }
      )
    },
    bottomBar = {
      RozgarBottomBar(
        currentTab = currentTab,
        onTabSelected = { viewModel.setTab(it) },
        activeOrdersCount = activeOrdersCount
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        AppTab.SHOP -> {
          ShopScreen(
            cartItems = cartItems,
            activeCategory = activeCategory,
            onCategorySelected = { viewModel.setCategoryFilter(it) },
            onAddToCart = { product, pkg, qty -> viewModel.addToCart(product, pkg, qty) },
            onOpenCart = { viewModel.setCartSheetVisible(true) },
            onOpenContact = { viewModel.setContactSheetVisible(true) }
          )
        }
        AppTab.ORDERS -> {
          TrackingScreen(
            orders = orders,
            selectedOrder = selectedOrder,
            onSelectOrder = { viewModel.selectOrderForTracking(it) },
            onAdvanceOrderStatus = { id, next, num -> viewModel.advanceOrderStatus(id, next, num) },
            onUpdateTrxId = { id, trx, num -> viewModel.updateTransactionId(id, trx, num) },
            onNavigateToShop = { viewModel.setTab(AppTab.SHOP) }
          )
        }
        AppTab.LOYALTY -> {
          LoyaltyScreen(
            profile = userProfile,
            onDailyCheckIn = { viewModel.claimDailyCheckIn() },
            onRedeemPerk = { pts, label -> viewModel.redeemPoints(pts, label) }
          )
        }
        AppTab.PAYMENT_HUB -> {
          PaymentHubScreen()
        }
      }
    }
  }

  // Sheets
  if (showCartSheet) {
    CartBottomSheet(
      cartItems = cartItems,
      onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
      onRemoveItem = { id -> viewModel.removeCartItem(id) },
      onClearCart = { viewModel.clearCart() },
      onProceedToCheckout = {
        viewModel.setCartSheetVisible(false)
        viewModel.setCheckoutSheetVisible(true)
      },
      onDismiss = { viewModel.setCartSheetVisible(false) }
    )
  }

  if (showCheckoutSheet) {
    CheckoutBottomSheet(
      cartItems = cartItems,
      formState = checkoutForm,
      userProfile = userProfile,
      onFormUpdate = { update -> viewModel.updateCheckoutForm(update) },
      onSubmitOrder = { viewModel.submitOrder() },
      onDismiss = { viewModel.setCheckoutSheetVisible(false) }
    )
  }

  if (showNotificationsSheet) {
    NotificationsBottomSheet(
      notifications = notifications,
      onMarkRead = { viewModel.markNotificationAsRead(it) },
      onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
      onDismiss = { viewModel.setNotificationsSheetVisible(false) }
    )
  }

  if (showContactSheet) {
    ContactBottomSheet(
      onDismiss = { viewModel.setContactSheetVisible(false) }
    )
  }
}
