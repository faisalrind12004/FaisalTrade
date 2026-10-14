package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.UserProfileEntity
import com.example.ui.theme.FarmAmberDark
import com.example.ui.theme.FarmAmberLight
import com.example.ui.theme.FarmAmberPrimary
import com.example.ui.theme.FarmGreen
import com.example.ui.theme.FarmGreenSoft
import com.example.ui.theme.GoldenYolk
import com.example.ui.theme.GoldenYolkSoft
import com.example.util.IntentHelper

data class LoyaltyPerk(
  val id: String,
  val title: String,
  val description: String,
  val pointsCost: Int,
  val discountValue: String
)

@Composable
fun LoyaltyScreen(
  profile: UserProfileEntity?,
  onDailyCheckIn: () -> Unit,
  onRedeemPerk: (Int, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val user = profile ?: UserProfileEntity()

  val perks = listOf(
    LoyaltyPerk("perk_100_off", "Rs. 100 Instant Discount", "Save Rs. 100 on your next fresh egg order", 100, "Rs. 100 OFF"),
    LoyaltyPerk("perk_free_del", "Free Express Delivery Pass", "Zero delivery charge on any order value", 150, "FREE DELIVERY"),
    LoyaltyPerk("perk_half_desi", "Free 6-Pack Desi Eggs", "Voucher for 6 free pure organic desi eggs", 250, "FREE DESI EGGS"),
    LoyaltyPerk("perk_full_tray", "Free 1 Tray (30 Farm Eggs)", "Full tray of fresh white or brown eggs", 600, "FREE 30 EGGS TRAY")
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("loyalty_screen_column"),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    // 1. Golden Yolk Club Membership Card
    item {
      GoldenClubMemberCard(profile = user)
    }

    // 2. Daily Check-in Streak
    item {
      Spacer(modifier = Modifier.height(14.dp))
      DailyStreakCard(
        streakDays = user.streakDays,
        onClaim = onDailyCheckIn
      )
    }

    // 3. How to Earn Points Section
    item {
      Spacer(modifier = Modifier.height(14.dp))
      EarnPointsGuideCard()
    }

    // 4. Redeem Perks Header
    item {
      Spacer(modifier = Modifier.height(18.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Redeem Rewards & Vouchers",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${user.loyaltyPoints} Pts Available",
          fontWeight = FontWeight.Bold,
          color = FarmAmberPrimary,
          fontSize = 12.sp
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    // 5. Perks List
    items(perks) { perk ->
      PerkCard(
        perk = perk,
        currentPoints = user.loyaltyPoints,
        onRedeem = { onRedeemPerk(perk.pointsCost, perk.title) }
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    // 6. Referral Card
    item {
      Spacer(modifier = Modifier.height(14.dp))
      ReferralCard(
        referralCode = user.referralCode,
        onCopy = {
          IntentHelper.copyToClipboard(context, "Referral Code", user.referralCode, "Referral code copied!")
        },
        onShare = {
          val shareText = "Order farm fresh & organic desi eggs from Rozgar Egg Point (https://rozgar-egg-point.pk) using my code ${user.referralCode} and get Rs. 100 OFF your first order!"
          IntentHelper.openWhatsApp(context, "", shareText)
        }
      )
    }
  }
}

@Composable
fun GoldenClubMemberCard(profile: UserProfileEntity) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("golden_club_member_card"),
    shape = RoundedCornerShape(20.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(
            colors = listOf(
              Color(0xFFD35400),
              Color(0xFFE67E22),
              Color(0xFFF39C12)
            )
          )
        )
        .padding(20.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Egg,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "ROZGAR EGG CLUB",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
              )
              Text(
                text = "Loyalty & Rewards Program",
                color = GoldenYolkSoft,
                fontSize = 10.sp
              )
            }
          }

          Box(
            modifier = Modifier
              .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = profile.loyaltyTier,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Text(
              text = "GOLDEN YOLK BALANCE",
              color = GoldenYolkSoft,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "${profile.loyaltyPoints}",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Points (= Rs. ${profile.loyaltyPoints})",
                color = Color(0xFFFFF9E6),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 6.dp)
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = profile.name,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Text(
              text = "Member Since 2026",
              color = GoldenYolkSoft,
              fontSize = 10.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun DailyStreakCard(
  streakDays: Int,
  onClaim: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(GoldenYolkSoft),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = FarmAmberPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "Daily Egg Check-In",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Current Streak: $streakDays Days (+15 Pts)",
            fontSize = 12.sp,
            color = FarmAmberPrimary,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Button(
        onClick = onClaim,
        colors = ButtonDefaults.buttonColors(containerColor = FarmAmberPrimary),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
        modifier = Modifier.testTag("claim_daily_checkin_button")
      ) {
        Text("Claim", fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }
  }
}

@Composable
fun EarnPointsGuideCard() {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = FarmGreenSoft)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.LocalOffer,
        contentDescription = null,
        tint = FarmGreen,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "Earn 1 Golden Yolk Point per Rs. 50 Spent",
          fontWeight = FontWeight.Bold,
          color = FarmGreen,
          fontSize = 13.sp
        )
        Text(
          text = "Points are automatically credited as soon as your order delivery is completed. 1 Point = Rs. 1 off your next breakfast delivery!",
          fontSize = 11.5.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 15.sp
        )
      }
    }
  }
}

@Composable
fun PerkCard(
  perk: LoyaltyPerk,
  currentPoints: Int,
  onRedeem: () -> Unit
) {
  val canRedeem = currentPoints >= perk.pointsCost

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .background(FarmAmberLight, RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = perk.discountValue,
              color = FarmAmberPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${perk.pointsCost} Points",
            fontWeight = FontWeight.Bold,
            color = if (canRedeem) FarmAmberPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = perk.title,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = perk.description,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Button(
        onClick = onRedeem,
        enabled = canRedeem,
        colors = ButtonDefaults.buttonColors(
          containerColor = FarmGreen,
          disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.testTag("redeem_${perk.id}")
      ) {
        Text(
          text = if (canRedeem) "Redeem" else "Locked",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun ReferralCard(
  referralCode: String,
  onCopy: () -> Unit,
  onShare: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = FarmAmberPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Refer Friends & Get +50 Points",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Give your friend Rs. 100 off their first fresh egg crate. You receive 50 bonus points upon their first order delivery!",
        fontSize = 11.5.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GoldenYolkSoft,
          modifier = Modifier.weight(1f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = referralCode,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = FarmAmberDark
            )
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Code",
              tint = FarmAmberPrimary,
              modifier = Modifier
                .size(18.dp)
                .clickable { onCopy() }
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
          onClick = onShare,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("share_referral_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Share", fontSize = 12.sp)
        }
      }
    }
  }
}
