package com.github.polyrocketmatt.kstat

import com.github.polyrocketmatt.kstat.Constants.EPSILON
import com.github.polyrocketmatt.kstat.Constants.EULER_MASCHERONI
import com.github.polyrocketmatt.kstat.Constants.PI
import com.github.polyrocketmatt.kstat.Constants.SQRT_TAU
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The Gamma class contains functions related to the gamma function.
 *
 * @see [Gamma Function](https://en.wikipedia.org/wiki/Gamma_function)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
object Gamma {

    /**
     * Returns the gamma function of a real number using the Lanczos approximation.
     *
     * @param a The real number to find the gamma function of.
     * @return The gamma function of the real number.
     */
    fun gamma(a: Double): Double {
        var z = a
        val g = 7.0
        val p = arrayOf(
            0.9999999999998099, 676.5203681218851, -1259.1392167224028,
            771.3234287776531, -176.6150291621406, 12.507343278686905,
            -0.13857109526572012, 9.984369578019572E-6, 1.5056327351493116e-7
        )

        return if (z < 0.5) {
            PI / (sin(PI * z) * gamma(1.0 - z)) // Reflection formula
        } else {
            z -= 1.0
            var x = p[0]
            for (i in 1 until p.size)
                x += p[i] / (z + i)
            val t = z + g + 0.5
            sqrt(2 * PI) * t.pow(z + 0.5) * exp(-t) * x
        }
    }

    /**
     * Returns the incomplete gamma function of a real number.
     *
     * @param a The shape parameter.
     * @param x The real number to find the incomplete gamma function of.
     * @return The incomplete gamma function of the real number.
     */
    fun incompleteGamma(a: Double, x: Double): Double {
        val gammaApprox = gamma(a)
        var term = exp(-x) * x.pow(a) / a
        var sum = term
        var k = 1
        while (term / sum > 1e-15) {
            term *= x / (a + k)
            sum += term
            k++
        }

        return gammaApprox * sum
    }

    /**
     * Returns the digamma function of a real number.
     *
     * @param x The real number to find the digamma function of.
     * @return The digamma function of the real number.
     */
    fun diGamma(x: Double): Double {
        var result = -EULER_MASCHERONI - 1 / x
        var n = 1
        var term = 1 / (x * x)
        while (term / result > EPSILON) {
            result += term
            term = n * (n + 1) / (n + x) / (n + x)
            n++
        }
        return result
    }

    /**
     * Return the log gamma function of a real number using Stirling's approximation.
     *
     * @param x The real number to find the log gamma function of.
     * @return The log gamma function of the real number.
     */
    fun lnGamma(x: Double): Double {
        val c = arrayOf(
            1.000000000190015, 76.18009172947146, -86.50532032941678,
            24.01409824083091, -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5
        )

        var y = x
        var tmp = x + 5.5
        tmp -= (x + 0.5) * ln(tmp)
        var ser = 1.000000000190015
        for (element in c) {
            y++
            ser += element / y
        }
        return -tmp + ln(SQRT_TAU * ser / x)
    }

}