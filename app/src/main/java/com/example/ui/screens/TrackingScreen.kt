package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.OrderEntity
import com.example.db.OrderStatus
import com.example.ui.theme.FarmAmberDark
import com.example.ui.theme.FarmAmberLight
import com.example.ui.theme.FarmAmberPrimary
import com.example.ui.theme.FarmGreen
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenSoft
import com.example.ui.theme.GoldenYolk
import com.example.ui.theme.GoldenYolkSoft
import com.example.util.IntentHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrackingScreen(
  orders: List<OrderEntity>,
  selectedOrder: OrderEntity?,
  onSelectOrder: (OrderEntity) -> Unit,
  onAdvanceOrderStatus: (Long, OrderStatus, String) -> Unit,
  onUpdateTrxId: (Long, String, String) -> Unit,
  onNavigateToShop: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activeOrder = selectedOrder ?: orders.firstOrNull()

  var showTrxDialog by remember { mutableStateOf(false) }
  var editedTrxId by remember { mutableStateOf("") }

  if (orders.isEmpty()) {
    EmptyTrackingView(onNavigateToShop = onNavigateToShop, modifier = modifier)
    return
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("tracking_screen_column"),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    // Orders selector chips if user has multiple orders
    if (orders.size > 1) {
      item {
        Text(
          text = "Select Order to Track:",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.padding(bottom = 12.dp)
        ) {
          items(orders) { ord ->
            val isSelected = ord.id == (activeOrder?.id ?: 0L)
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSelectOrder(ord) }
                .border(
                  width = 1.dp,
                  color = if (isSelected) FarmAmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                  shape = RoundedCornerShape(8.dp)
                ),
              color = if (isSelected) FarmAmberLight else MaterialTheme.colorScheme.surface
            ) {
              Text(
                text = "${ord.orderNumber} (Rs. ${ord.totalAmount.toInt()})",
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) FarmAmberPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    if (activeOrder != null) {
      val currentStatus = try {
        OrderStatus.valueOf(activeOrder.status)
      } catch (e: Exception) {
        OrderStatus.VERIFYING
      }

      // Live Status Hero Card
      item {
        LiveStatusHeaderCard(
          order = activeOrder,
          currentStatus = currentStatus,
          onAdvanceNext = {
            val next = getNextStatus(currentStatus)
            if (next != null) {
              onAdvanceOrderStatus(activeOrder.id, next, activeOrder.orderNumber)
            }
          }
        )
      }

      // Stepper of all 5 stages
      item {
        Spacer(modifier = Modifier.height(16.dp))
        OrderTrackingStepper(currentStatus = currentStatus)
      }

      // Payment Verification Box
      item {
        Spacer(modifier = Modifier.height(16.dp))
        PaymentVerificationCard(
          order = activeOrder,
          onOpenTrxDialog = {
            editedTrxId = if (activeOrder.trxId.startsWith("Pending")) "" else activeOrder.trxId
            showTrxDialog = true
          },
          onSendWhatsAppProof = {
            val phone = if (activeOrder.paymentMethod == "NEK_MUHAMMAD") "03313177438" else "03337803988"
            val contactName = if (activeOrder.paymentMethod == "NEK_MUHAMMAD") "Nek Muhammad" else "M Faisal"
            val message = "Assalam-o-Alaikum $contactName, I placed Order #${activeOrder.orderNumber} for Rs. ${activeOrder.totalAmount.toInt()} at Rozgar Egg Point.\nPayment Sent via EasyPaisa/JazzCash.\nTRX ID: ${activeOrder.trxId}\nCustomer: ${activeOrder.customerName}\nAddress: ${activeOrder.deliveryAddress}, ${activeOrder.deliveryCity}\nPlease confirm verification!"
            IntentHelper.openWhatsApp(context, phone, message)
          }
        )
      }

      // Rider details card if out for delivery or packing
      if (currentStatus == OrderStatus.OUT_FOR_DELIVERY || currentStatus == OrderStatus.PACKING || currentStatus == OrderStatus.DELIVERED) {
        item {
          Spacer(modifier = Modifier.height(16.dp))
          RiderCard(order = activeOrder, isDelivered = currentStatus == OrderStatus.DELIVERED)
        }
      }

      // Order Summary Receipt
      item {
        Spacer(modifier = Modifier.height(16.dp))
        OrderReceiptCard(order = activeOrder)
      }
    }
  }

  // Dialog to enter/update TRX ID
  if (showTrxDialog && activeOrder != null) {
    AlertDialog(
      onDismissRequest = { showTrxDialog = false },
      title = { Text("Update Transaction ID") },
      text = {
        Column {
          Text(
            text = "Enter the Transaction ID (TRX ID) from your EasyPaisa, JazzCash, or Bank app after sending money to ${activeOrder.paymentRecipient}:",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = editedTrxId,
            onValueChange = { editedTrxId = it },
            label = { Text("Transaction ID (TRX ID)") },
            placeholder = { Text("e.g. 28947192837") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_trx_dialog")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (editedTrxId.isNotBlank()) {
              onUpdateTrxId(activeOrder.id, editedTrxId.trim(), activeOrder.orderNumber)
            }
            showTrxDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = FarmAmberPrimary)
        ) {
          Text("Submit for Verification")
        }
      },
      dismissButton = {
        TextButton(onClick = { showTrxDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun LiveStatusHeaderCard(
  order: OrderEntity,
  currentStatus: OrderStatus,
  onAdvanceNext: () -> Unit
) {
  val dateStr = remember(order.createdAt) {
    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("live_status_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Order #${order.orderNumber}",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              color = FarmAmberPrimary,
              fontSize = 20.sp
            )
          )
          Text(
            text = "Placed on $dateStr",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Box(
          modifier = Modifier
            .background(
              when (currentStatus) {
                OrderStatus.DELIVERED -> FarmGreen
                OrderStatus.OUT_FOR_DELIVERY -> GoldenYolk
                OrderStatus.CONFIRMED, OrderStatus.PACKING -> FarmAmberPrimary
                else -> Color(0xFFE67E22)
              },
              RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = currentStatus.displayTitle,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = currentStatus.description,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.Medium,
          fontSize = 13.sp
        )
      )

      Spacer(modifier = Modifier.height(12.dp))
      LinearProgressIndicator(
        progress = { (currentStatus.stepIndex / 5f).coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = FarmAmberPrimary,
        trackColor = FarmAmberLight,
      )

      // Demo advancement button for user evaluation
      if (currentStatus != OrderStatus.DELIVERED && currentStatus != OrderStatus.CANCELLED) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(
            onClick = onAdvanceNext,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("advance_order_status_button")
          ) {
            Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Simulate Next Step", fontSize = 11.sp, color = FarmAmberPrimary)
          }
        }
      }
    }
  }
}

@Composable
fun OrderTrackingStepper(currentStatus: OrderStatus) {
  val steps = listOf(
    Triple(OrderStatus.VERIFYING, "Payment Verification", Icons.Default.Payment),
    Triple(OrderStatus.CONFIRMED, "Order Confirmed", Icons.Default.Verified),
    Triple(OrderStatus.PACKING, "Candling & Cushion Packing", Icons.Default.Inventory2),
    Triple(OrderStatus.OUT_FOR_DELIVERY, "Out for Delivery", Icons.Default.DirectionsBike),
    Triple(OrderStatus.DELIVERED, "Delivered Fresh", Icons.Default.Check)
  )

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Real-Time Tracking Stages",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(12.dp))

      steps.forEachIndexed { index, (status, title, icon) ->
        val isCompleted = currentStatus.stepIndex > status.stepIndex
        val isCurrent = currentStatus == status

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          // Circle icon
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(
                when {
                  isCompleted -> FarmGreen
                  isCurrent -> FarmAmberPrimary
                  else -> MaterialTheme.colorScheme.surfaceVariant
                }
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isCompleted) Icons.Default.Check else icon,
              contentDescription = null,
              tint = if (isCompleted || isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = title,
              fontSize = 13.sp,
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
              color = if (isCurrent) FarmAmberPrimary else if (isCompleted) FarmGreenDark else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isCurrent) {
              Text(
                text = "Currently in progress",
                fontSize = 11.sp,
                color = FarmAmberPrimary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          if (isCompleted) {
            Icon(
              imageVector = Icons.Outlined.CheckCircle,
              contentDescription = "Completed",
              tint = FarmGreen,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        if (index < steps.size - 1) {
          Box(
            modifier = Modifier
              .padding(start = 16.dp)
              .width(2.dp)
              .height(18.dp)
              .background(
                if (currentStatus.stepIndex > status.stepIndex) FarmGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
              )
          )
        }
      }
    }
  }
}

@Composable
fun PaymentVerificationCard(
  order: OrderEntity,
  onOpenTrxDialog: () -> Unit,
  onSendWhatsAppProof: () -> Unit
) {
  val context = LocalContext.current
  val isCod = order.paymentMethod == "COD"

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = FarmAmberLight)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Payment,
            contentDescription = null,
            tint = FarmAmberPrimary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Payment & Verification Info",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = FarmAmberDark
          )
        }

        if (!isCod) {
          IconButton(onClick = onOpenTrxDialog, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit TRX", tint = FarmAmberPrimary, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Method: ${order.paymentRecipient}",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )

      if (!isCod) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "TRX ID: ",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = order.trxId,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (order.trxId.startsWith("Pending")) Color.Red else FarmGreenDark
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy TRX",
            modifier = Modifier
              .size(16.dp)
              .clickable { IntentHelper.copyToClipboard(context, "TRX ID", order.trxId) },
            tint = FarmAmberPrimary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = onSendWhatsAppProof,
          colors = ButtonDefaults.buttonColors(containerColor = FarmGreen),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("whatsapp_confirm_button"),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Send Payment Proof on WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
        }
      } else {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Pay Rs. ${order.totalAmount.toInt()} in cash to the delivery rider upon inspecting the eggs.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun RiderCard(order: OrderEntity, isDelivered: Boolean) {
  val context = LocalContext.current

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(GoldenYolkSoft),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.DirectionsBike,
            contentDescription = null,
            tint = FarmAmberPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = order.riderName,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = order.riderVehicle,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = if (isDelivered) "Delivered safely!" else "ETA: ~${order.etaMinutes} mins to destination",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDelivered) FarmGreen else FarmAmberPrimary
          )
        }
      }

      Button(
        onClick = { IntentHelper.dialPhone(context, order.riderPhone) },
        shape = CircleShape,
        contentPadding = PaddingValues(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FarmGreen)
      ) {
        Icon(Icons.Default.Call, contentDescription = "Call Rider", tint = Color.White, modifier = Modifier.size(18.dp))
      }
    }
  }
}

@Composable
fun OrderReceiptCard(order: OrderEntity) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Order Details & Receipt",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Items: ${order.itemsSummary}",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "Total Eggs: ${order.totalItemCount} pieces",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "Deliver to: ${order.customerName} (${order.customerPhone})",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "Address: ${order.deliveryAddress}, ${order.deliveryCity}",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "Time Slot: ${order.deliverySlot}",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Divider(modifier = Modifier.padding(vertical = 10.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Subtotal", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Rs. ${order.subtotal.toInt()}", fontSize = 12.sp)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Delivery Fee", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(if (order.deliveryFee == 0.0) "FREE" else "Rs. ${order.deliveryFee.toInt()}", fontSize = 12.sp, color = if (order.deliveryFee == 0.0) FarmGreen else MaterialTheme.colorScheme.onSurface)
      }
      if (order.discount > 0.0) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Egg Club Discount", fontSize = 12.sp, color = FarmGreen)
          Text("-Rs. ${order.discount.toInt()}", fontSize = 12.sp, color = FarmGreen)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Total Paid / Payable", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(
          "Rs. ${order.totalAmount.toInt()}",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 16.sp,
          color = FarmAmberPrimary
        )
      }
    }
  }
}

fun getNextStatus(current: OrderStatus): OrderStatus? {
  return when (current) {
    OrderStatus.VERIFYING -> OrderStatus.CONFIRMED
    OrderStatus.CONFIRMED -> OrderStatus.PACKING
    OrderStatus.PACKING -> OrderStatus.OUT_FOR_DELIVERY
    OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
    else -> null
  }
}

@Composable
fun EmptyTrackingView(onNavigateToShop: () -> Unit, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(GoldenYolkSoft),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.LocalShipping,
        contentDescription = null,
        tint = FarmAmberPrimary,
        modifier = Modifier.size(42.dp)
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = "No Orders in Transit Yet",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "Once you place an order for fresh eggs, you can track payment verification, packing, and live rider delivery right here.",
      fontSize = 12.5.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
    Spacer(modifier = Modifier.height(18.dp))
    Button(
      onClick = onNavigateToShop,
      colors = ButtonDefaults.buttonColors(containerColor = FarmAmberPrimary),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.testTag("browse_eggs_button")
    ) {
      Icon(Icons.Default.Egg, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Order Fresh Eggs Now")
    }
  }
}
