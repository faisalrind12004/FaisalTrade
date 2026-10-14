package com.example.repository

import android.content.Context
import com.example.db.AppDatabase
import com.example.db.CartItemEntity
import com.example.db.NotificationEntity
import com.example.db.OrderEntity
import com.example.db.OrderStatus
import com.example.db.UserProfileEntity
import com.example.model.EggProduct
import com.example.model.PackagingOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class EggRepository(private val context: Context) {
  private val database = AppDatabase.getDatabase(context)
  private val cartDao = database.cartDao()
  private val orderDao = database.orderDao()
  private val notificationDao = database.notificationDao()
  private val userProfileDao = database.userProfileDao()
  private val repositoryScope = CoroutineScope(Dispatchers.IO)

  val allCartItems: Flow<List<CartItemEntity>> = cartDao.getAllCartItems()
  val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
  val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
  val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()
  val userProfile: Flow<UserProfileEntity?> = userProfileDao.getProfile()

  init {
    // Seed initial profile and welcome notification if database is empty
    repositoryScope.launch {
      val existingProfile = userProfileDao.getProfile().firstOrNull()
      if (existingProfile == null) {
        userProfileDao.insertOrUpdateProfile(
          UserProfileEntity(
            id = 1,
            name = "Muhammad",
            phone = "03301234567",
            defaultAddress = "House 42, Street 8, Block 4, Clifton",
            city = "Karachi",
            loyaltyPoints = 150,
            loyaltyTier = "Silver Roost",
            streakDays = 3,
            referralCode = "REP-GOLD-55"
          )
        )
        notificationDao.insertNotification(
          NotificationEntity(
            title = "🥚 Welcome to Rozgar Egg Point!",
            message = "Enjoy pure farm-fresh & organic desi eggs delivered direct to your doorstep. Free delivery above Rs. 1,500!",
            type = "OFFER"
          )
        )
      }
    }
  }

  suspend fun addToCart(product: EggProduct, packageOption: PackagingOption, quantity: Int = 1) {
    val existing = cartDao.findItem(product.id, packageOption.id)
    if (existing != null) {
      cartDao.update(existing.copy(quantity = existing.quantity + quantity))
    } else {
      cartDao.insertOrUpdate(
        CartItemEntity(
          productId = product.id,
          productTitle = product.title,
          packageId = packageOption.id,
          packageTitle = packageOption.title,
          eggCount = packageOption.eggCount,
          unitPrice = packageOption.pricePkr,
          quantity = quantity
        )
      )
    }
  }

  suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
    if (newQuantity <= 0) {
      cartDao.deleteById(cartItemId)
    } else {
      // Find and update
      val items = cartDao.getAllCartItems().firstOrNull() ?: return
      val target = items.find { it.id == cartItemId } ?: return
      cartDao.update(target.copy(quantity = newQuantity))
    }
  }

  suspend fun removeCartItem(cartItemId: Long) {
    cartDao.deleteById(cartItemId)
  }

  suspend fun clearCart() {
    cartDao.clearCart()
  }

  suspend fun placeOrder(
    customerName: String,
    customerPhone: String,
    address: String,
    city: String,
    deliverySlot: String,
    deliveryNotes: String,
    paymentMethod: String,
    trxId: String,
    senderPhone: String,
    appliedDiscount: Double = 0.0
  ): Long {
    val cartItems = cartDao.getAllCartItems().firstOrNull().orEmpty()
    if (cartItems.isEmpty()) return -1L

    val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
    val totalCount = cartItems.sumOf { it.eggCount * it.quantity }
    val deliveryFee = if (subtotal >= 1500.0) 0.0 else 120.0
    val totalAmount = (subtotal + deliveryFee - appliedDiscount).coerceAtLeast(0.0)

    val itemsSummary = cartItems.joinToString(", ") { "${it.quantity}x ${it.packageTitle}" }
    val randomId = Random.nextInt(1000, 9999)
    val orderNumber = "REP-$randomId"

    val recipientName = when (paymentMethod) {
      "M_FAISAL" -> "M Faisal (03337803988)"
      "NEK_MUHAMMAD" -> "Nek Muhammad (03313177438)"
      else -> "Cash on Delivery (COD)"
    }

    val initialStatus = if (paymentMethod == "COD") {
      OrderStatus.CONFIRMED.name
    } else {
      OrderStatus.VERIFYING.name
    }

    val order = OrderEntity(
      orderNumber = orderNumber,
      createdAt = System.currentTimeMillis(),
      itemsSummary = itemsSummary,
      totalItemCount = totalCount,
      subtotal = subtotal,
      deliveryFee = deliveryFee,
      discount = appliedDiscount,
      totalAmount = totalAmount,
      status = initialStatus,
      paymentMethod = paymentMethod,
      paymentRecipient = recipientName,
      trxId = trxId.ifBlank { if (paymentMethod == "COD") "N/A (COD)" else "Pending TRX ID" },
      senderPhone = senderPhone,
      customerName = customerName,
      customerPhone = customerPhone,
      deliveryAddress = address,
      deliveryCity = city,
      deliverySlot = deliverySlot,
      deliveryNotes = deliveryNotes,
      riderName = listOf("Tariq Khan", "Kashif Ali", "Zubair Ahmed", "Farhan Rasheed").random(),
      riderPhone = "03${Random.nextInt(10, 49)}${Random.nextInt(1000000, 9999999)}",
      riderVehicle = "Honda CD70 (Egg Safe-Carrier #${Random.nextInt(10, 99)})",
      etaMinutes = if (deliverySlot.contains("Express", ignoreCase = true)) 45 else 75
    )

    val orderId = orderDao.insertOrder(order)
    cartDao.clearCart()

    // Add loyalty points (1 point per Rs. 50 spent)
    val earnedPoints = (totalAmount / 50).toInt().coerceAtLeast(5)
    userProfileDao.addLoyaltyPoints(earnedPoints)

    // Save notification
    notificationDao.insertNotification(
      NotificationEntity(
        title = "📦 Order #$orderNumber Received!",
        message = if (paymentMethod == "COD") {
          "Order confirmed! Packaging your fresh egg order for express delivery."
        } else {
          "Order submitted! Verifying TRX $trxId with $recipientName."
        },
        type = "ORDER_UPDATE",
        relatedOrderNumber = orderNumber
      )
    )

    // Simulate progressive automated updates in background
    simulateOrderStatusProgression(orderId, orderNumber, paymentMethod)

    return orderId
  }

  private fun simulateOrderStatusProgression(orderId: Long, orderNumber: String, paymentMethod: String) {
    repositoryScope.launch {
      if (paymentMethod != "COD") {
        // Step 1 -> Step 2: Verification after 12 seconds
        delay(12000)
        orderDao.updateOrderStatus(orderId, OrderStatus.CONFIRMED.name)
        notificationDao.insertNotification(
          NotificationEntity(
            title = "✅ Payment Verified (#$orderNumber)",
            message = "Transaction confirmed by Accounts Team. Safe packaging initiated.",
            type = "VERIFICATION",
            relatedOrderNumber = orderNumber
          )
        )
      }

      // Step 2 -> Step 3: Packing
      delay(16000)
      orderDao.updateOrderStatus(orderId, OrderStatus.PACKING.name)
      notificationDao.insertNotification(
        NotificationEntity(
          title = "🥚 Egg Candling & Shock-Proof Packing (#$orderNumber)",
          message = "Your fresh eggs have passed quality candling and are being packed in cushioned trays.",
          type = "ORDER_UPDATE",
          relatedOrderNumber = orderNumber
        )
      )

      // Step 3 -> Step 4: Out for Delivery
      delay(20000)
      orderDao.updateOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY.name)
      notificationDao.insertNotification(
        NotificationEntity(
          title = "🚴 Rider Out For Delivery (#$orderNumber)",
          message = "Rider is heading to your address with zero-crack guaranteed delivery.",
          type = "ORDER_UPDATE",
          relatedOrderNumber = orderNumber
        )
      )
    }
  }

  suspend fun manualAdvanceOrderStatus(orderId: Long, nextStatus: OrderStatus, orderNumber: String) {
    orderDao.updateOrderStatus(orderId, nextStatus.name)
    val msg = when (nextStatus) {
      OrderStatus.VERIFYING -> "Verifying payment with M Faisal / Nek Muhammad."
      OrderStatus.CONFIRMED -> "Payment manually confirmed! Preparing dispatch."
      OrderStatus.PACKING -> "Carefully packing inspected unbroken eggs."
      OrderStatus.OUT_FOR_DELIVERY -> "Rider dispatched with insulated crate."
      OrderStatus.DELIVERED -> "Order delivered successfully! Thank you for choosing Rozgar Egg Point."
      OrderStatus.CANCELLED -> "Order was marked cancelled."
    }
    notificationDao.insertNotification(
      NotificationEntity(
        title = "Status Updated: ${nextStatus.displayTitle}",
        message = msg,
        type = "ORDER_UPDATE",
        relatedOrderNumber = orderNumber
      )
    )
  }

  suspend fun updateTransactionId(orderId: Long, trxId: String, orderNumber: String) {
    orderDao.updateTransactionId(orderId, trxId)
    notificationDao.insertNotification(
      NotificationEntity(
        title = "🔄 TRX ID Submitted (#$orderNumber)",
        message = "Transaction ID $trxId submitted for verification to M Faisal / Nek Muhammad.",
        type = "VERIFICATION",
        relatedOrderNumber = orderNumber
      )
    )
  }

  suspend fun dailyCheckIn(): Boolean {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val profile = userProfileDao.getProfile().firstOrNull() ?: return false
    if (profile.lastCheckInDate == today) {
      return false
    }

    val newStreak = profile.streakDays + 1
    val bonusPoints = 10 + (newStreak * 2).coerceAtMost(20)
    userProfileDao.insertOrUpdateProfile(
      profile.copy(
        loyaltyPoints = profile.loyaltyPoints + bonusPoints,
        streakDays = newStreak,
        lastCheckInDate = today
      )
    )

    notificationDao.insertNotification(
      NotificationEntity(
        title = "🎉 Daily Egg Streak Claimed! (Day $newStreak)",
        message = "You earned +$bonusPoints Golden Yolk Loyalty Points today!",
        type = "LOYALTY"
      )
    )
    return true
  }

  suspend fun redeemLoyaltyDiscount(points: Int): Boolean {
    val profile = userProfileDao.getProfile().firstOrNull() ?: return false
    if (profile.loyaltyPoints < points) return false
    userProfileDao.updateLoyaltyPoints(profile.loyaltyPoints - points)
    return true
  }

  suspend fun markNotificationAsRead(id: Long) {
    notificationDao.markAsRead(id)
  }

  suspend fun markAllNotificationsAsRead() {
    notificationDao.markAllAsRead()
  }
}
