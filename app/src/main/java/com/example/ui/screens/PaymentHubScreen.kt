package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.model.PaymentContact
import com.example.model.PaymentDirectory
import com.example.ui.theme.FarmAmberDark
import com.example.ui.theme.FarmAmberLight
import com.example.ui.theme.FarmAmberPrimary
import com.example.ui.theme.FarmGreen
import com.example.ui.theme.FarmGreenDark
import com.example.ui.theme.FarmGreenSoft
import com.example.ui.theme.GoldenYolk
import com.example.ui.theme.GoldenYolkSoft
import com.example.util.IntentHelper

@Composable
fun PaymentHubScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("payment_hub_column"),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    // Header
    item {
      PaymentHubHeader()
    }

    // Account 1: M Faisal
    item {
      Spacer(modifier = Modifier.height(14.dp))
      OfficialAccountCard(
        contact = PaymentDirectory.primaryContacts[0], // M Faisal (03337803988)
        onCopy = {
          IntentHelper.copyToClipboard(context, "M Faisal Number", "03337803988", "M Faisal's account number copied!")
        },
        onCall = {
          IntentHelper.dialPhone(context, "03337803988")
        },
        onWhatsApp = {
          IntentHelper.openWhatsApp(
            context,
            "03337803988",
            "Assalam-o-Alaikum M Faisal Bhai, I am contacting you from the Rozgar Egg Point app regarding payment verification."
          )
        }
      )
    }

    // Account 2: Nek Muhammad
    item {
      Spacer(modifier = Modifier.height(14.dp))
      OfficialAccountCard(
        contact = PaymentDirectory.primaryContacts[1], // Nek Muhammad (03313177438)
        onCopy = {
          IntentHelper.copyToClipboard(context, "Nek Muhammad Number", "03313177438", "Nek Muhammad's account number copied!")
        },
        onCall = {
          IntentHelper.dialPhone(context, "03313177438")
        },
        onWhatsApp = {
          IntentHelper.openWhatsApp(
            context,
            "03313177438",
            "Assalam-o-Alaikum Nek Muhammad Bhai, I am contacting you from the Rozgar Egg Point app regarding payment verification."
          )
        }
      )
    }

    // Cash On Delivery Card
    item {
      Spacer(modifier = Modifier.height(14.dp))
      CodOptionCard()
    }

    // 4-Step Verification Guide
    item {
      Spacer(modifier = Modifier.height(16.dp))
      VerificationProcessGuide()
    }

    // Official Website & Farm Guarantee
    item {
      Spacer(modifier = Modifier.height(14.dp))
      WebsiteAndFarmCard(
        onVisitWeb = { IntentHelper.openWebUrl(context, PaymentDirectory.OFFICIAL_WEBSITE) }
      )
    }
  }
}

@Composable
fun PaymentHubHeader() {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = GoldenYolkSoft)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(FarmAmberPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Official Payment & Verification Hub",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = FarmAmberDark
          )
          Text(
            text = "Direct, secure transfer & instant manual verification",
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "To guarantee fast dispatch, transfer to either M Faisal or Nek Muhammad using EasyPaisa or JazzCash. Your order is confirmed immediately upon transaction matching.",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 16.sp
      )
    }
  }
}

@Composable
fun OfficialAccountCard(
  contact: PaymentContact,
  onCopy: () -> Unit,
  onCall: () -> Unit,
  onWhatsApp: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("account_card_${contact.name.lowercase().replace(" ", "_")}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = contact.name,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = FarmAmberPrimary
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .background(FarmGreenSoft, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "VERIFIED ACCOUNT",
                color = FarmGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }
          }
          Text(
            text = contact.title,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Number Box with 1-tap copy
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = FarmAmberLight,
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onCopy() }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Account / Mobile Number:",
              fontSize = 10.5.sp,
              color = FarmAmberDark
            )
            Text(
              text = contact.phoneNumber,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 18.sp,
              color = FarmAmberDark,
              letterSpacing = 1.sp
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy number",
              tint = FarmAmberPrimary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmAmberPrimary)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Supported Apps: " + contact.accountsAvailable.joinToString(" • "),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Direct Action Buttons (Call & WhatsApp)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onWhatsApp,
          colors = ButtonDefaults.buttonColors(containerColor = FarmGreen),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onCall,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call Directly", fontSize = 12.sp)
        }
      }
    }
  }
}

@Composable
fun CodOptionCard() {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Payment, contentDescription = null, tint = FarmAmberPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Cash on Delivery (COD)",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Prefer inspecting your eggs first? Choose Cash on Delivery during checkout. Inspect the fresh batch, count the eggs, and pay the exact amount to the courier rider upon delivery.",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 16.sp
      )
    }
  }
}

@Composable
fun VerificationProcessGuide() {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "How Verification & Confirmation Works",
        fontWeight = FontWeight.Bold,
        fontSize = 14.5.sp
      )
      Spacer(modifier = Modifier.height(10.dp))

      val steps = listOf(
        "1. Copy M Faisal (03337803988) or Nek Muhammad (03313177438) number.",
        "2. Open EasyPaisa, JazzCash, or Bank app and transfer the order total.",
        "3. Copy the Transaction ID (TRX ID) from your SMS or payment screen.",
        "4. Paste the TRX ID during checkout or share screenshot on WhatsApp for instant confirmation!"
      )

      steps.forEach { step ->
        Row(modifier = Modifier.padding(vertical = 3.dp)) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = FarmGreen,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = step, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
  }
}

@Composable
fun WebsiteAndFarmCard(onVisitWeb: () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = FarmGreenSoft)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Official Website & Farm Portal",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = FarmGreenDark
          )
          Text(
            text = "https://rozgar-egg-point.pk",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = FarmGreen
          )
        }

        Button(
          onClick = onVisitWeb,
          colors = ButtonDefaults.buttonColors(containerColor = FarmGreenDark),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Visit Web", fontSize = 11.5.sp)
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Support Hours: 7:00 AM – 10:00 PM Everyday • Bio-secure packaging and fresh harvest dispatch.",
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
