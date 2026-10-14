package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.CartItemEntity
import com.example.db.UserProfileEntity
import com.example.ui.theme.FarmAmberDark
import com.example.ui.theme.FarmAmberLight
import com.example.ui.theme.FarmAmberPrimary
import com.example.ui.theme.FarmGreen
import com.example.ui.theme.GoldenYolk
import com.example.ui.theme.GoldenYolkSoft
import com.example.util.IntentHelper
import com.example.viewmodel.CheckoutFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutBottomSheet(
  cartItems: List<CartItemEntity>,
  formState: CheckoutFormState,
  userProfile: UserProfileEntity?,
  onFormUpdate: (CheckoutFormState.() -> CheckoutFormState) -> Unit,
  onSubmitOrder: () -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
  val deliveryFee = if (subtotal >= 1500.0) 0.0 else 120.0
  val discount = if (formState.redeemPoints) 100.0 else 0.0
  val finalTotal = (subtotal + deliveryFee - discount).coerceAtLeast(0.0)

  val deliverySlots = listOf(
    "Express Delivery (45-60 mins)",
    "Morning Slot (8:00 AM - 11:00 AM)",
    "Afternoon Slot (1:00 PM - 4:00 PM)",
    "Evening Slot (5:00 PM - 8:00 PM)"
  )

  val cities = listOf("Karachi", "Lahore", "Islamabad", "Rawalpindi", "Faisalabad", "Multan", "Peshawar", "Hyderabad", "Quetta")

  var isCityDropdownOpen by remember { mutableStateOf(false) }
  var isSlotDropdownOpen by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .testTag("checkout_bottom_sheet"),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Express Checkout",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 19.sp
            )
          )
          Text(
            text = "Rs. ${finalTotal.toInt()}",
            fontWeight = FontWeight.ExtraBold,
            color = FarmAmberPrimary,
            fontSize = 18.sp
          )
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Customer Info
      item {
        Text("1. Contact & Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = formState.name,
          onValueChange = { onFormUpdate { copy(name = it) } },
          label = { Text("Full Name") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_checkout_name")
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = formState.phone,
          onValueChange = { onFormUpdate { copy(phone = it) } },
          label = { Text("Mobile Phone Number") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_checkout_phone")
        )
        Spacer(modifier = Modifier.height(8.dp))

        // City selection
        ExposedDropdownMenuBox(
          expanded = isCityDropdownOpen,
          onExpandedChange = { isCityDropdownOpen = it },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = formState.city,
            onValueChange = {},
            readOnly = true,
            label = { Text("City") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCityDropdownOpen) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("select_city")
          )
          ExposedDropdownMenu(
            expanded = isCityDropdownOpen,
            onDismissRequest = { isCityDropdownOpen = false }
          ) {
            cities.forEach { city ->
              DropdownMenuItem(
                text = { Text(city) },
                onClick = {
                  onFormUpdate { copy(city = city) }
                  isCityDropdownOpen = false
                }
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = formState.address,
          onValueChange = { onFormUpdate { copy(address = it) } },
          label = { Text("Complete Street Address & Flat/House #") },
          minLines = 2,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_checkout_address")
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Delivery Slot
        ExposedDropdownMenuBox(
          expanded = isSlotDropdownOpen,
          onExpandedChange = { isSlotDropdownOpen = it },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = formState.deliverySlot,
            onValueChange = {},
            readOnly = true,
            label = { Text("Delivery Slot") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSlotDropdownOpen) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
              .testTag("select_slot")
          )
          ExposedDropdownMenu(
            expanded = isSlotDropdownOpen,
            onDismissRequest = { isSlotDropdownOpen = false }
          ) {
            deliverySlots.forEach { slot ->
              DropdownMenuItem(
                text = { Text(slot) },
                onClick = {
                  onFormUpdate { copy(deliverySlot = slot) }
                  isSlotDropdownOpen = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
      }

      // Payment Method Section
      item {
        Text("2. Secure Payment Options", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        // Option 1: M Faisal
        PaymentMethodOptionTile(
          title = "Transfer to M Faisal (03337803988)",
          subtitle = "EasyPaisa / JazzCash / Bank Transfer",
          isSelected = formState.paymentMethod == "M_FAISAL",
          onSelect = { onFormUpdate { copy(paymentMethod = "M_FAISAL") } },
          onCopyNumber = {
            IntentHelper.copyToClipboard(context, "M Faisal Number", "03337803988", "M Faisal number copied!")
          }
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Option 2: Nek Muhammad
        PaymentMethodOptionTile(
          title = "Transfer to Nek Muhammad (03313177438)",
          subtitle = "EasyPaisa / JazzCash / Bank Transfer",
          isSelected = formState.paymentMethod == "NEK_MUHAMMAD",
          onSelect = { onFormUpdate { copy(paymentMethod = "NEK_MUHAMMAD") } },
          onCopyNumber = {
            IntentHelper.copyToClipboard(context, "Nek Muhammad Number", "03313177438", "Nek Muhammad number copied!")
          }
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Option 3: COD
        PaymentMethodOptionTile(
          title = "Cash on Delivery (COD)",
          subtitle = "Pay upon inspection of fresh unbroken eggs",
          isSelected = formState.paymentMethod == "COD",
          onSelect = { onFormUpdate { copy(paymentMethod = "COD") } },
          onCopyNumber = null
        )

        Spacer(modifier = Modifier.height(10.dp))
      }

      // If transfer selected: TRX ID input
      if (formState.paymentMethod != "COD") {
        item {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = FarmAmberLight,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "Manual Transaction Verification:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = FarmAmberDark
              )
              Text(
                text = "Transfer Rs. ${finalTotal.toInt()} via EasyPaisa/JazzCash to ${if (formState.paymentMethod == "M_FAISAL") "M Faisal (03337803988)" else "Nek Muhammad (03313177438)"}, then enter the TRX ID below:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = formState.trxId,
                onValueChange = { onFormUpdate { copy(trxId = it) } },
                label = { Text("Transaction ID (TRX ID)*") },
                placeholder = { Text("e.g. 2891827419") },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_checkout_trx")
              )
              Spacer(modifier = Modifier.height(6.dp))

              OutlinedTextField(
                value = formState.senderPhone,
                onValueChange = { onFormUpdate { copy(senderPhone = it) } },
                label = { Text("Sender Mobile Number (Optional)") },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_checkout_sender_phone")
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
        }
      }

      // Loyalty Points Redemption
      if (userProfile != null && userProfile.loyaltyPoints >= 100) {
        item {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = GoldenYolkSoft,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = formState.redeemPoints,
                onCheckedChange = { onFormUpdate { copy(redeemPoints = it) } },
                colors = CheckboxDefaults.colors(checkedColor = FarmAmberPrimary)
              )
              Column {
                Text(
                  text = "Redeem 100 Golden Yolk Points for Rs. 100 OFF",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = FarmAmberDark
                )
                Text(
                  text = "Available balance: ${userProfile.loyaltyPoints} points",
                  fontSize = 10.5.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
        }
      }

      // Delivery Notes
      item {
        OutlinedTextField(
          value = formState.notes,
          onValueChange = { onFormUpdate { copy(notes = it) } },
          label = { Text("Delivery Instructions / Notes") },
          placeholder = { Text("e.g. Leave with security, call when outside") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Final Confirm Button
      item {
        Button(
          onClick = onSubmitOrder,
          colors = ButtonDefaults.buttonColors(containerColor = FarmAmberPrimary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("confirm_order_button")
        ) {
          Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Confirm Order (Rs. ${finalTotal.toInt()})",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }
      }
    }
  }
}

@Composable
fun PaymentMethodOptionTile(
  title: String,
  subtitle: String,
  isSelected: Boolean,
  onSelect: () -> Unit,
  onCopyNumber: (() -> Unit)?
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .clickable { onSelect() }
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        color = if (isSelected) FarmAmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        shape = RoundedCornerShape(10.dp)
      ),
    color = if (isSelected) FarmAmberLight else MaterialTheme.colorScheme.surface
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(18.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .border(2.dp, if (isSelected) FarmAmberPrimary else Color.Gray, androidx.compose.foundation.shape.CircleShape)
            .background(if (isSelected) FarmAmberPrimary else Color.Transparent)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = title,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            color = if (isSelected) FarmAmberDark else MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = subtitle,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      if (onCopyNumber != null) {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onCopyNumber() }
            .padding(horizontal = 6.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = FarmAmberPrimary, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FarmAmberPrimary)
        }
      }
    }
  }
}
