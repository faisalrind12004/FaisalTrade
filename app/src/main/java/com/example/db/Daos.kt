package com.example.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
  @Query("SELECT * FROM cart_items")
  fun getAllCartItems(): Flow<List<CartItemEntity>>

  @Query("SELECT * FROM cart_items WHERE productId = :productId AND packageId = :packageId LIMIT 1")
  suspend fun findItem(productId: String, packageId: String): CartItemEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(item: CartItemEntity): Long

  @Update
  suspend fun update(item: CartItemEntity)

  @Delete
  suspend fun delete(item: CartItemEntity)

  @Query("DELETE FROM cart_items WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM cart_items")
  suspend fun clearCart()
}

@Dao
interface OrderDao {
  @Query("SELECT * FROM orders ORDER BY createdAt DESC")
  fun getAllOrders(): Flow<List<OrderEntity>>

  @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
  fun getOrderById(id: Long): Flow<OrderEntity?>

  @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
  suspend fun getOrderByNumber(orderNumber: String): OrderEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: OrderEntity): Long

  @Update
  suspend fun updateOrder(order: OrderEntity)

  @Query("UPDATE orders SET status = :status WHERE id = :orderId")
  suspend fun updateOrderStatus(orderId: Long, status: String)

  @Query("UPDATE orders SET trxId = :trxId, status = 'VERIFYING' WHERE id = :orderId")
  suspend fun updateTransactionId(orderId: Long, trxId: String)
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
  fun getAllNotifications(): Flow<List<NotificationEntity>>

  @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
  fun getUnreadCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationEntity): Long

  @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
  suspend fun markAsRead(id: Long)

  @Query("UPDATE notifications SET isRead = 1")
  suspend fun markAllAsRead()

  @Query("DELETE FROM notifications")
  suspend fun clearAll()
}

@Dao
interface UserProfileDao {
  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  fun getProfile(): Flow<UserProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

  @Query("UPDATE user_profile SET loyaltyPoints = loyaltyPoints + :pointsToAdd WHERE id = 1")
  suspend fun addLoyaltyPoints(pointsToAdd: Int)

  @Query("UPDATE user_profile SET loyaltyPoints = :newPoints WHERE id = 1")
  suspend fun updateLoyaltyPoints(newPoints: Int)

  @Query("UPDATE user_profile SET name = :name, phone = :phone, defaultAddress = :address, city = :city WHERE id = 1")
  suspend fun updateContactInfo(name: String, phone: String, address: String, city: String)
}

@Dao
interface LoyaltyVoucherDao {
  @Query("SELECT * FROM loyalty_vouchers ORDER BY createdAt DESC")
  fun getAllVouchers(): Flow<List<LoyaltyVoucherEntity>>

  @Query("SELECT * FROM loyalty_vouchers WHERE isUsed = 0 ORDER BY createdAt DESC")
  fun getActiveVouchers(): Flow<List<LoyaltyVoucherEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVoucher(voucher: LoyaltyVoucherEntity): Long

  @Query("UPDATE loyalty_vouchers SET isUsed = 1 WHERE id = :id")
  suspend fun markVoucherUsed(id: Long)
}

@Dao
interface LoyaltyHistoryDao {
  @Query("SELECT * FROM loyalty_history ORDER BY timestamp DESC")
  fun getAllHistory(): Flow<List<LoyaltyHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistory(item: LoyaltyHistoryEntity): Long
}
