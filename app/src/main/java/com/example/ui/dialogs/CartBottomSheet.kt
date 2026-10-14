package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.CartItemEntity
import com.example.ui.theme.FarmAmberDark
import com.example.ui.theme.FarmAmberLight
import com.example.ui.theme.FarmAmberPrimary
import com.example.ui.theme.FarmGreen
import com.example.ui.theme.FarmGreenSoft
import com.example.ui.theme.GoldenYolkSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBottomSheet(
  cartItems: List<CartItemEntity>,
  onUpdateQuantity: (Long, Int) -> Unit,
  onRemoveItem: (Long) -> Unit,
  onClearCart: () -> Unit,
  onProceedToCheckout: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
  val totalEggs = cartItems.sumOf { it.eggCount * it.quantity }
  val deliveryFee = if (subtotal >= 1500.0 || cartItems.isEmpty()) 0.0 else 120.0
  val totalAmount = subtotal + deliveryFee

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .testTag("cart_bottom_sheet")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            tint = FarmAmberPrimary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Your Egg Basket",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 19.sp
            )
          )
        }

        if (cartItems.isNotEmpty()) {
          TextButton(onClick = onClearCart) {
            Text("Clear", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
          }
        }
      }

      if (cartItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Your egg basket is empty",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Add some fresh organic or farm eggs to get started!",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        // Delivery progress indicator
        if (subtotal < 1500.0) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = GoldenYolkSoft,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp)
          ) {
            Text(
              text = "Add Rs. ${(1500.0 - subtotal).toInt()} more to qualify for FREE Home Delivery!",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = FarmAmberDark,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        } else {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = FarmGreenSoft,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp)
          ) {
            Text(
              text = "🎉 You unlocked FREE Express Home Delivery!",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = FarmGreen,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }

        // Cart items list
        LazyColumn(
          modifier = Modifier
            .weight(1f, fill = false)
            .padding(vertical = 8.dp)
        ) {
          items(cartItems, key = { it.id }) { item ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.productTitle,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text(
                  text = "${item.packageTitle} • Rs. ${item.unitPrice.toInt()} each",
                  fontSize = 11.5.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "Subtotal: Rs. ${(item.unitPrice * item.quantity).toInt()}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = FarmAmberPrimary
                )
              }

              // Counter
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(30.dp)
                        .clickable { onUpdateQuantity(item.id, item.quantity - 1) },
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                    }
                    Text(
                      text = item.quantity.toString(),
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    Box(
                      modifier = Modifier
                        .size(30.dp)
                        .clickable { onUpdateQuantity(item.id, item.quantity + 1) },
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                    }
                  }
                }

                IconButton(
                  onClick = { onRemoveItem(item.id) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
          }
        }

        // Summary Breakdown
        Column(modifier = Modifier.padding(vertical = 10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Total Eggs:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$totalEggs eggs", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Subtotal:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Rs. ${subtotal.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Delivery Fee:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              text = if (deliveryFee == 0.0) "FREE" else "Rs. ${deliveryFee.toInt()}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (deliveryFee == 0.0) FarmGreen else MaterialTheme.colorScheme.onSurface
            )
          }

          Divider(modifier = Modifier.padding(vertical = 8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Total Payable:",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "Rs. ${totalAmount.toInt()}",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 18.sp,
              color = FarmAmberPrimary
            )
          }
        }

        Button(
          onClick = onProceedToCheckout,
          colors = ButtonDefaults.buttonColors(containerColor = FarmAmberPrimary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .testTag("proceed_checkout_button")
        ) {
          Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Proceed to Checkout (Rs. ${totalAmount.toInt()})",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }
  }
}
