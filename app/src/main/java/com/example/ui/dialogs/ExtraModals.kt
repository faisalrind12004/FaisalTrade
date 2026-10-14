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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.NotificationEntity
import com.example.model.PaymentDirectory
import com.example.ui.theme.FarmAmberDark
import com.example.ui.theme.FarmAmberLight
import com.example.ui.theme.FarmAmberPrimary
import com.example.ui.theme.FarmGreen
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.GoldenYolkSoft
import com.example.util.IntentHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsBottomSheet(
  notifications: List<NotificationEntity>,
  onMarkRead: (Long) -> Unit,
  onMarkAllRead: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .padding(bottom = 16.dp)
        .testTag("notifications_sheet")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = FarmAmberPrimary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Push Notifications & Updates",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
          )
        }

        if (notifications.isNotEmpty()) {
          TextButton(onClick = onMarkAllRead) {
            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Read All", fontSize = 12.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (notifications.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No notifications yet",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .weight(1f, fill = false)
            .padding(bottom = 16.dp)
        ) {
          items(notifications, key = { it.id }) { notif ->
            val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(notif.timestamp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (notif.isRead) MaterialTheme.colorScheme.surface else GoldenYolkSoft,
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onMarkRead(notif.id) }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.Top
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (notif.isRead) Color.Transparent else FarmAmberPrimary)
                    .padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = notif.title,
                      fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                      fontSize = 13.5.sp,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = dateStr,
                      fontSize = 10.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = notif.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactBottomSheet(
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .padding(bottom = 16.dp)
        .testTag("contact_sheet")
    ) {
      Text(
        text = "Contact Rozgar Egg Point",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
      )
      Text(
        text = "Official accounts for direct payment verification & inquiries",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(16.dp))

      // M Faisal Card
      ContactPersonTile(
        name = "M Faisal",
        phone = "03337803988",
        role = "Accounts & Order Verification",
        onCall = { IntentHelper.dialPhone(context, "03337803988") },
        onWhatsApp = {
          IntentHelper.openWhatsApp(
            context,
            "03337803988",
            "Assalam-o-Alaikum M Faisal Bhai, I am reaching out from Rozgar Egg Point app."
          )
        }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Nek Muhammad Card
      ContactPersonTile(
        name = "Nek Muhammad",
        phone = "03313177438",
        role = "Operations & Fresh Dispatch",
        onCall = { IntentHelper.dialPhone(context, "03313177438") },
        onWhatsApp = {
          IntentHelper.openWhatsApp(
            context,
            "03313177438",
            "Assalam-o-Alaikum Nek Muhammad Bhai, I am reaching out from Rozgar Egg Point app."
          )
        }
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Website Link
      Button(
        onClick = { IntentHelper.openWebUrl(context, PaymentDirectory.OFFICIAL_WEBSITE) },
        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenDark),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Visit rozgar-egg-point.pk", fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun ContactPersonTile(
  name: String,
  phone: String,
  role: String,
  onCall: () -> Unit,
  onWhatsApp: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = FarmAmberLight)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = FarmAmberDark
          )
          Text(text = role, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(
            text = phone,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            color = FarmAmberPrimary
          )
        }

        Row {
          Button(
            onClick = onWhatsApp,
            colors = ButtonDefaults.buttonColors(containerColor = FarmGreen),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("WhatsApp", fontSize = 11.5.sp)
          }
          Spacer(modifier = Modifier.width(6.dp))
          OutlinedButton(
            onClick = onCall,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Call", fontSize = 11.5.sp)
          }
        }
      }
    }
  }
}
