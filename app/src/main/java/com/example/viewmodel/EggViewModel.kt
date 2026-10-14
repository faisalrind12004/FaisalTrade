package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.CartItemEntity
import com.example.db.NotificationEntity
import com.example.db.OrderEntity
import com.example.db.OrderStatus
import com.example.db.UserProfileEntity
import com.example.model.EggCatalog
import com.example.model.EggProduct
import com.example.model.PackagingOption
import com.example.repository.EggRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val iconLabel: String) {
  SHOP("Shop Fresh", "Eggs"),
  ORDERS("Live Tracking", "Track"),
  LOYALTY("Egg Club", "Club"),
  PAYMENT_HUB("Payment Hub", "Pay")
}

data class CheckoutFormState(
  val name: String = "Muhammad Ali",
  val phone: String = "03331234567",
  val address: String = "Apartment 4B, Gulshan-e-Iqbal, Block 6",
  val city: String = "Karachi",
  val deliverySlot: String = "Express Delivery (45-60 mins)",
  val notes: String = "Handle with care, call when at gate",
  val paymentMethod: String = "M_FAISAL", // M_FAISAL, NEK_MUHAMMAD, COD
  val trxId: String = "",
  val senderPhone: String = "",
  val redeemPoints: Boolean = false
)

class EggViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = EggRepository(application)

  val cartItems: StateFlow<List<CartItemEntity>> = repository.allCartItems
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val orders: StateFlow<List<OrderEntity>> = repository.allOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val unreadCount: StateFlow<Int> = repository.unreadNotificationsCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  private val _currentTab = MutableStateFlow(AppTab.SHOP)
  val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

  private val _selectedProduct = MutableStateFlow<EggProduct?>(null)
  val selectedProduct: StateFlow<EggProduct?> = _selectedProduct.asStateFlow()

  private val _selectedOrder = MutableStateFlow<OrderEntity?>(null)
  val selectedOrder: StateFlow<OrderEntity?> = _selectedOrder.asStateFlow()

  private val _showCartSheet = MutableStateFlow(false)
  val showCartSheet: StateFlow<Boolean> = _showCartSheet.asStateFlow()

  private val _showCheckoutSheet = MutableStateFlow(false)
  val showCheckoutSheet: StateFlow<Boolean> = _showCheckoutSheet.asStateFlow()

  private val _showNotificationsSheet = MutableStateFlow(false)
  val showNotificationsSheet: StateFlow<Boolean> = _showNotificationsSheet.asStateFlow()

  private val _showContactSheet = MutableStateFlow(false)
  val showContactSheet: StateFlow<Boolean> = _showContactSheet.asStateFlow()

  private val _checkoutForm = MutableStateFlow(CheckoutFormState())
  val checkoutForm: StateFlow<CheckoutFormState> = _checkoutForm.asStateFlow()

  private val _activeCategoryFilter = MutableStateFlow("All")
  val activeCategoryFilter: StateFlow<String> = _activeCategoryFilter.asStateFlow()

  private val _userMessage = MutableStateFlow<String?>(null)
  val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

  fun setTab(tab: AppTab) {
    _currentTab.value = tab
  }

  fun setCategoryFilter(category: String) {
    _activeCategoryFilter.value = category
  }

  fun selectProduct(product: EggProduct?) {
    _selectedProduct.value = product
  }

  fun selectOrderForTracking(order: OrderEntity?) {
    _selectedOrder.value = order
    if (order != null) {
      _currentTab.value = AppTab.ORDERS
    }
  }

  fun setCartSheetVisible(visible: Boolean) {
    _showCartSheet.value = visible
  }

  fun setCheckoutSheetVisible(visible: Boolean) {
    _showCheckoutSheet.value = visible
  }

  fun setNotificationsSheetVisible(visible: Boolean) {
    _showNotificationsSheet.value = visible
  }

  fun setContactSheetVisible(visible: Boolean) {
    _showContactSheet.value = visible
  }

  fun updateCheckoutForm(update: CheckoutFormState.() -> CheckoutFormState) {
    _checkoutForm.value = _checkoutForm.value.update()
  }

  fun clearUserMessage() {
    _userMessage.value = null
  }

  fun addToCart(product: EggProduct, packageOption: PackagingOption, quantity: Int = 1) {
    viewModelScope.launch {
      repository.addToCart(product, packageOption, quantity)
      _userMessage.value = "Added ${packageOption.title} to cart!"
    }
  }

  fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
    viewModelScope.launch {
      repository.updateCartQuantity(cartItemId, newQuantity)
    }
  }

  fun removeCartItem(cartItemId: Long) {
    viewModelScope.launch {
      repository.removeCartItem(cartItemId)
    }
  }

  fun clearCart() {
    viewModelScope.launch {
      repository.clearCart()
    }
  }

  fun submitOrder() {
    val form = _checkoutForm.value
    if (form.name.isBlank() || form.phone.isBlank() || form.address.isBlank()) {
      _userMessage.value = "Please complete your delivery name, phone, and address."
      return
    }

    if (form.paymentMethod != "COD" && form.trxId.isBlank()) {
      _userMessage.value = "Please enter the Transaction ID (TRX ID) from your EasyPaisa/JazzCash app."
      return
    }

    viewModelScope.launch {
      val discountAmount = if (form.redeemPoints) 100.0 else 0.0
      val orderId = repository.placeOrder(
        customerName = form.name,
        customerPhone = form.phone,
        address = form.address,
        city = form.city,
        deliverySlot = form.deliverySlot,
        deliveryNotes = form.notes,
        paymentMethod = form.paymentMethod,
        trxId = form.trxId,
        senderPhone = form.senderPhone,
        appliedDiscount = discountAmount
      )

      if (orderId > 0) {
        _showCheckoutSheet.value = false
        _showCartSheet.value = false
        _userMessage.value = "🎉 Order placed successfully! Tracking initiated."
        _currentTab.value = AppTab.ORDERS
      }
    }
  }

  fun advanceOrderStatus(orderId: Long, nextStatus: OrderStatus, orderNumber: String) {
    viewModelScope.launch {
      repository.manualAdvanceOrderStatus(orderId, nextStatus, orderNumber)
    }
  }

  fun updateTransactionId(orderId: Long, trxId: String, orderNumber: String) {
    viewModelScope.launch {
      repository.updateTransactionId(orderId, trxId, orderNumber)
      _userMessage.value = "TRX ID updated! Verification in progress."
    }
  }

  fun claimDailyCheckIn() {
    viewModelScope.launch {
      val success = repository.dailyCheckIn()
      if (success) {
        _userMessage.value = "⭐ Daily egg check-in claimed! Bonus points added."
      } else {
        _userMessage.value = "Already checked in today! Come back tomorrow."
      }
    }
  }

  fun redeemPoints(points: Int, discountLabel: String) {
    viewModelScope.launch {
      val success = repository.redeemLoyaltyDiscount(points)
      if (success) {
        _userMessage.value = "Unlocked $discountLabel! Added to your account."
      } else {
        _userMessage.value = "Not enough Golden Yolk points."
      }
    }
  }

  fun markNotificationAsRead(id: Long) {
    viewModelScope.launch {
      repository.markNotificationAsRead(id)
    }
  }

  fun markAllNotificationsAsRead() {
    viewModelScope.launch {
      repository.markAllNotificationsAsRead()
    }
  }
}
