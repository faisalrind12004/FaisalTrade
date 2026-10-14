package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val productId: String,
  val productTitle: String,
  val packageId: String,
  val packageTitle: String,
  val eggCount: Int,
  val unitPrice: Double,
  val quantity: Int
)

enum class OrderStatus(val displayTitle: String, val stepIndex: Int, val description: String) {
  VERIFYING("Payment Verification", 1, "Verifying your payment transaction with M Faisal / Nek Muhammad"),
  CONFIRMED("Order Confirmed", 2, "Payment verified! Order sent to dispatch center"),
  PACKING("Inspecting & Packing", 3, "Candling check and packing in shock-resistant egg trays"),
  OUT_FOR_DELIVERY("Out for Delivery", 4, "Express rider dispatched to your doorstep"),
  DELIVERED("Delivered Fresh", 5, "Delivered safely with zero-crack guarantee"),
  CANCELLED("Cancelled", 0, "Order was cancelled")
}

@Entity(tableName = "orders")
data class OrderEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val orderNumber: String,
  val createdAt: Long = System.currentTimeMillis(),
  val itemsSummary: String,
  val totalItemCount: Int,
  val subtotal: Double,
  val deliveryFee: Double,
  val discount: Double,
  val totalAmount: Double,
  val status: String = OrderStatus.VERIFYING.name,
  val paymentMethod: String, // "M_FAISAL", "NEK_MUHAMMAD", "COD"
  val paymentRecipient: String,
  val trxId: String,
  val senderPhone: String,
  val customerName: String,
  val customerPhone: String,
  val deliveryAddress: String,
  val deliveryCity: String,
  val deliverySlot: String,
  val deliveryNotes: String,
  val riderName: String = "Tariq Khan",
  val riderPhone: String = "03001234567",
  val riderVehicle: String = "Honda CD70 (Egg Carrier Box #14)",
  val etaMinutes: Int = 35
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val message: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false,
  val type: String = "ORDER_UPDATE", // ORDER_UPDATE, LOYALTY, OFFER, VERIFICATION
  val relatedOrderNumber: String? = null
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey
  val id: Int = 1,
  val name: String = "Muhammad Faisal",
  val phone: String = "03337803988",
  val defaultAddress: String = "Shop #4, Main Commercial Wholesale Egg Market",
  val city: String = "Karachi",
  val loyaltyPoints: Int = 280,
  val loyaltyTier: String = "Gold Trader",
  val streakDays: Int = 5,
  val lastCheckInDate: String = "",
  val referralCode: String = "FAISAL-TRADE-99"
)

@Entity(tableName = "loyalty_vouchers")
data class LoyaltyVoucherEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val description: String,
  val discountAmount: Double,
  val pointsCost: Int,
  val isUsed: Boolean = false,
  val code: String,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "loyalty_history")
data class LoyaltyHistoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val reason: String,
  val pointsChange: Int,
  val balanceAfter: Int,
  val timestamp: Long = System.currentTimeMillis()
)
