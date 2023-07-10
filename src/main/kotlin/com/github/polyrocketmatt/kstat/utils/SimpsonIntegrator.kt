package com.github.polyrocketmatt.kstat.utils

/**
 * Utility class to perform Simpson's rule to integrate a function.
 *
 * @since 1.0.0
 * @author Matthias Kovacic
 */
object SimpsonIntegrator {

    private const val DEFAULT_PRECISION = 1000

    /**
     * Performs Simpson's rule to integrate a function.
     *
     * @param function The function to integrate
     * @param min The lower bound of the integral
     * @param max The upper bound of the integral
     * @param n The number of intervals to use
     * @return The integral of the function
     */
    fun simpson(function: (Double) -> Double, min: Double, max: Double, n: Int = DEFAULT_PRECISION): Double {
        val h = (max - min) / n
        var sum = function(min) + function(max)
        for (i in 1 until n) {
            val x = min + i * h
            sum += if (i % 2 == 0) 2 * function(x) else 4 * function(x)
        }

        return sum * h / 3.0
    }

}