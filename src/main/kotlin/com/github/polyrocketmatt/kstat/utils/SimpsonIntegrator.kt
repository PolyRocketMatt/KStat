package com.github.polyrocketmatt.kstat.utils

object SimpsonIntegrator {

    private const val DEFAULT_PRECISION = 1000

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