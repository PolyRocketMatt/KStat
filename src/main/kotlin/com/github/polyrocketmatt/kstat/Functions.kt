package com.github.polyrocketmatt.kstat

import com.github.polyrocketmatt.kstat.distributions.Distribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

object Functions {

    /**
     * Returns the binomial coefficient of n choose k.
     *
     * @param n The number of items
     * @param k The number of items to choose
     * @return The number of ways to choose k items from n items
     * @throws KStatException If k is negative or k is greater than n
     */
    fun binomial(n: Int, k: Int): Int {
        if (k < 0 || k > n)
            throw KStatException("Cannot choose $k items from $n items")
        if (k == 0 || k == n)
            return 1
        if (k > n / 2)
            return binomial(n, n - k)
        var b = 1
        for (i in 1..k)
            b = b * (n - i + 1) / i
        return b
    }

    /**
     * Returns the factorial of n.
     *
     * @param n The number to find the factorial of
     * @return The factorial of n
     * @throws KStatException If n is negative
     */
    fun factorial(n: Int): Int {
        if (n < 0)
            throw KStatException("Cannot find factorial of negative number")
        var f = 1
        for (i in 1..n)
            f *= i
        return f
    }

    /**
     * Returns the error function of x using the Abramowitz and Stegun approximation with 6 terms.
     *
     * @param x The number to find the error function of
     * @return The error function of x using the Abramowitz and Stegun approximation with 6 terms
     */
    fun erfApprox(x: Double): Double {
        val t = 1.0 / (1.0 + 0.5 * abs(x))
        val term1 = 0.3275911
        val term2 = 0.254829592
        val term3 = -0.284496736
        val term4 = 1.421413741
        val term5 = -1.453152027
        val term6 = 1.061405429
        val poly = term1 * t +
                term2 * t.pow(2) +
                term3 * t.pow(3) +
                term4 * t.pow(4) +
                term5 * t.pow(5) +
                term6 * t.pow(6)
        val result = poly * exp(-x * x)
        return if (x >= 0.0) 1.0 - result else result - 1.0
    }

    /**
     * Returns the error function of x using Taylor series approximation with 10 terms.
     *
     * @param x The number to find the error function of
     * @return The error function of x using Taylor series approximation with 10 terms
     * TODO: Improve accuracy by looking at how Boost (C++) does it
     */
    fun erfExact(x: Double): Double {
        val terms = 50
        var result = 0.0
        var numerator = x
        var denominator = 1.0
        var sign = 1
        for (n in 0 until terms) {
            val term = sign * (numerator / denominator)

            result += term
            numerator *= x * x
            denominator *= (2 * n + 3)
            sign *= -1
        }

        return 2.0 / sqrt(Math.PI) * result
    }

    /**
     * Returns the error function of x using either the Abramowitz and Stegun approximation or the Taylor series
     * approximation.
     *
     * @param x The number to find the error function of
     * @param approx Whether to use the Abramowitz and Stegun approximation or the Taylor series approximation
     * @return The error function of x using either the Abramowitz and Stegun approximation or the Taylor series
     */
    fun erf(x: Double, approx: Boolean): Double = if (approx) erfApprox(x) else erfExact(x)

    /**
     * Returns the complementary error function of x using either the Abramowitz and Stegun approximation or the
     * Taylor series approximation.
     *
     * @param x The number to find the complementary error function of
     * @param approx Whether to use the Abramowitz and Stegun approximation or the Taylor series approximation
     * @return The complementary error function of x using either the Abramowitz and Stegun approximation or the Taylor
     */
    fun erfc(x: Double, approx: Boolean): Double = 1.0 - erf(x, approx)

    /**
     * Computes the logarithm of x with base defined by the entropy type.
     *
     * @param x The number to find the logarithm of
     * @param type The entropy type to use
     * @return The logarithm of x with base defined by the entropy type
     */
    fun entropyLog(x: Double, type: Distribution.EntropyType): Double = when(type) {
        Distribution.EntropyType.SHANNON -> log2(x)
        Distribution.EntropyType.NATURAL -> ln(x)
    }

}