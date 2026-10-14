package com.example

import com.example.model.EggCatalog
import com.example.model.PaymentDirectory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testEggCatalogHasItems() {
    assertTrue(EggCatalog.products.isNotEmpty())
    val desi = EggCatalog.products.find { it.id == "desi_organic" }
    assertNotNull(desi)
    assertTrue(desi!!.packagingOptions.isNotEmpty())
  }

  @Test
  fun testPaymentContactsExist() {
    val contacts = PaymentDirectory.primaryContacts
    assertEquals(2, contacts.size)
    val faisal = contacts.find { it.name == "M Faisal" }
    val nek = contacts.find { it.name == "Nek Muhammad" }

    assertNotNull(faisal)
    assertNotNull(nek)
    assertEquals("03337803988", faisal?.phoneNumber)
    assertEquals("03313177438", nek?.phoneNumber)
  }

  @Test
  fun testOfficialWebsiteUrl() {
    assertEquals("https://rozgar-egg-point.pk", PaymentDirectory.OFFICIAL_WEBSITE)
  }
}
