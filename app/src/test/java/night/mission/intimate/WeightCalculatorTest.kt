package night.mission.intimate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlin.random.Random

class WeightCalculatorTest {

    @Test
    fun testWeightForRatings() {
        assertEquals(1.0, WeightCalculator.getWeightForRating(0), 0.001)
        assertEquals(0.2, WeightCalculator.getWeightForRating(1), 0.001)
        assertEquals(2.0, WeightCalculator.getWeightForRating(2), 0.001)
        assertEquals(3.0, WeightCalculator.getWeightForRating(3), 0.001)
        assertEquals(4.0, WeightCalculator.getWeightForRating(4), 0.001)
        assertEquals(5.0, WeightCalculator.getWeightForRating(5), 0.001)
    }

    @Test
    fun testWeightedRandomSelection() {
        val images = listOf("img1.jpg", "img2.jpg")
        val ratings = mapOf(
            "img1.jpg" to 1, // weight 0.2
            "img2.jpg" to 5  // weight 5.0
        )

        var count1 = 0
        var count2 = 0
        val runs = 10000

        for (i in 0 until runs) {
            val picked = WeightCalculator.pickWeightedRandomImage(images, ratings, currentImage = null)
            if (picked == "img1.jpg") count1++
            if (picked == "img2.jpg") count2++
        }

        // img2 should be picked vastly more often than img1
        assert(count2 > count1 * 10)
    }

    @Test
    fun testAvoidConsecutiveDuplicatesWhenPossible() {
        val images = listOf("img1.jpg", "img2.jpg")
        val ratings = emptyMap<String, Int>()

        val picked = WeightCalculator.pickWeightedRandomImage(images, ratings, currentImage = "img1.jpg")
        assertEquals("img2.jpg", picked)
    }
}
