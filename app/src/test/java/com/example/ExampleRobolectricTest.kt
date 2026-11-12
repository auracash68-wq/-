package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import com.example.data.ExpenseCategory
import com.example.data.ExpenseRepository
import com.example.utils.PdfReportGenerator
import com.example.utils.VoiceExpenseParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `launch MainActivity and verify UI renders without crashing`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("splash_screen").assertExists()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Hisab Khata", appName)
    }

    @Test
    fun `verify reference expense seed calculations match exact specifications`() {
        val seed = ExpenseRepository.defaultSeedExpenses()
        val thisMonth = seed.filter { it.periodTag == "THIS_MONTH" }
        val totalThisMonth = thisMonth.sumOf { it.amount }
        assertEquals(5400, totalThisMonth)
        assertEquals("₹5,400", PdfReportGenerator.formatIndianRupee(totalThisMonth))

        val todayItems = seed.filter { it.isToday }.sortedBy { it.sortOrder }
        assertEquals(3, todayItems.size)
        assertEquals("Food", todayItems[0].title)
        assertEquals("Lunch at Restaurant", todayItems[0].description)
        assertEquals(250, todayItems[0].amount)
        assertEquals("8:15 PM", todayItems[0].timeFormatted)

        assertEquals("Travel", todayItems[1].title)
        assertEquals("Bus Ticket", todayItems[1].description)
        assertEquals(120, todayItems[1].amount)
        assertEquals("5:42 PM", todayItems[1].timeFormatted)

        assertEquals("Groceries", todayItems[2].title)
        assertEquals("Vegetables & Fruits", todayItems[2].description)
        assertEquals(180, todayItems[2].amount)
        assertEquals("1:20 PM", todayItems[2].timeFormatted)

        val foodTotal = thisMonth.filter { it.category == "Food" }.sumOf { it.amount }
        val travelTotal = thisMonth.filter { it.category == "Travel" }.sumOf { it.amount }
        val groceriesTotal = thisMonth.filter { it.category == "Groceries" }.sumOf { it.amount }
        val othersTotal = thisMonth.filter { it.category == "Others" }.sumOf { it.amount }

        assertEquals(2268, foodTotal)
        assertEquals(1512, travelTotal)
        assertEquals(972, groceriesTotal)
        assertEquals(648, othersTotal)
    }

    @Test
    fun `verify voice expense parser extracts category description and amount`() {
        val parsedFood = VoiceExpenseParser.parseSpokenText("Lunch at Restaurant 250")
        assertNotNull(parsedFood)
        assertEquals(ExpenseCategory.FOOD, parsedFood?.category)
        assertEquals(250, parsedFood?.amount)

        val parsedTravel = VoiceExpenseParser.parseSpokenText("Bus Ticket 120")
        assertNotNull(parsedTravel)
        assertEquals(ExpenseCategory.TRAVEL, parsedTravel?.category)
        assertEquals(120, parsedTravel?.amount)
    }
}
