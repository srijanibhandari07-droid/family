package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.SystemsThinkingEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches Between Us`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Between Us", appName)
    }

    @Test
    fun `systems thinking engine detects mind reading accurately`() {
        val phrase = "They think I am lazy and don't care about my college."
        val result = SystemsThinkingEngine.detectMindReading(phrase)
        assertNotNull(result)
        assertTrue(result!!.contains("That's a conclusion"))
    }

    @Test
    fun `systems thinking engine identifies safety risks`() {
        val safePhrase = "We had an argument about washing the clothes."
        val unsafePhrase = "They threatened to hit me if I don't listen."
        assertEquals(false, SystemsThinkingEngine.checkSafetyRisk(safePhrase))
        assertEquals(true, SystemsThinkingEngine.checkSafetyRisk(unsafePhrase))
    }

    @Test
    fun `systems thinking engine calculates heat level and deconstructs`() = runBlocking {
        val analysis = SystemsThinkingEngine.analyzeHeatQuick(
            whatHappened = "Mom said I only know how to sit with my phone.",
            whatFeeling = "Angry and guilty about my Barasat PG life.",
            whatWant = "For them to understand that I am working hard."
        )
        assertTrue(analysis.heatScore > 50)
        assertNotNull(analysis.visibleWords)
        assertNotNull(analysis.possiblePressure)
    }
}
