package night.mission.intimate

import kotlin.random.Random

object WeightCalculator {
    /**
     * Rating weighting rules:
     * 0 stars (unrated): normal weight 1.0
     * 1 star: reduced weight 0.2 (shows less)
     * 2 stars: weight 2.0
     * 3 stars: weight 3.0
     * 4 stars: weight 4.0
     * 5 stars: weight 5.0
     */
    fun getWeightForRating(rating: Int): Double {
        return when (rating) {
            1 -> 0.2
            2 -> 2.0
            3 -> 3.0
            4 -> 4.0
            5 -> 5.0
            else -> 1.0 // 0 stars or unrated
        }
    }

    /**
     * Selects an image filename using weighted random selection based on current ratings.
     */
    fun pickWeightedRandomImage(
        images: List<String>,
        ratings: Map<String, Int>,
        currentImage: String? = null,
        randomGenerator: Random = Random.Default
    ): String? {
        if (images.isEmpty()) return null
        if (images.size == 1) return images.first()

        // Filter candidates if possible to avoid picking the exact same image consecutively
        val candidates = if (currentImage != null && images.size > 1) {
            images.filter { it != currentImage }
        } else {
            images
        }

        val weights = candidates.map { image ->
            val rating = ratings[image] ?: 0
            getWeightForRating(rating)
        }

        val totalWeight = weights.sum()
        if (totalWeight <= 0) return candidates.random(randomGenerator)

        var randomValue = randomGenerator.nextDouble() * totalWeight
        for (i in candidates.indices) {
            randomValue -= weights[i]
            if (randomValue <= 0) {
                return candidates[i]
            }
        }

        return candidates.last()
    }
}
