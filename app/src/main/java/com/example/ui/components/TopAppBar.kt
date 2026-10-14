package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.FarmAmberPrimary
import com.example.ui.theme.GoldenYolk
import com.example.util.IntentHelper

@Composable
fun RozgarTopBar(
  unreadCount: Int,
  cartItemCount: Int,
  onOpenNotifications: () -> Unit,
  onOpenCart: () -> Unit,
  onOpenContact: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 3.dp,
    shadowElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Brand logo & title
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onOpenContact() }
            .padding(vertical = 4.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_egg_logo),
            contentDescription = "Rozgar Egg Point Logo",
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Rozgar Egg Point",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 17.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              )
              Spacer(modifier = Modifier.width(4.dp))
              Box(
                modifier = Modifier
                  .background(GoldenYolk.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "PK",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = FarmAmberPrimary
                )
              }
            }
            Text(
              text = "Fresh Farm & Organic Eggs • Home Delivery",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        // Action icons (Web, Contact, Notifications, Cart)
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = {
              IntentHelper.openWebUrl(context, "https://rozgar-egg-point.pk")
            },
            modifier = Modifier
              .size(38.dp)
              .testTag("website_link_button")
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = "Visit rozgar-egg-point.pk",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = onOpenContact,
            modifier = Modifier
              .size(38.dp)
              .testTag("quick_contact_button")
          ) {
            Icon(
              imageVector = Icons.Default.Call,
              contentDescription = "Contact Faisal & Nek",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = onOpenNotifications,
            modifier = Modifier
              .size(38.dp)
              .testTag("notifications_button")
          ) {
            BadgedBox(
              badge = {
                if (unreadCount > 0) {
                  Badge(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                  ) {
                    Text(text = if (unreadCount > 9) "9+" else unreadCount.toString())
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
              )
            }
          }

          IconButton(
            onClick = onOpenCart,
            modifier = Modifier
              .size(38.dp)
              .testTag("cart_button")
          ) {
            BadgedBox(
              badge = {
                if (cartItemCount > 0) {
                  Badge(
                    containerColor = FarmAmberPrimary,
                    contentColor = Color.White
                  ) {
                    Text(text = cartItemCount.toString())
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = "Shopping Cart",
                tint = FarmAmberPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }
      }
    }
  }
}
