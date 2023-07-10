package com.github.polyrocketmatt.kstat.distributions.continuous

import com.github.polyrocketmatt.kstat.Functions.entropyLog
import com.github.polyrocketmatt.kstat.distributions.Continuous
import com.github.polyrocketmatt.kstat.distributions.ContinuousDistribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import com.github.polyrocketmatt.kstat.exception.KStatUndefinedException
import com.github.polyrocketmatt.kstat.range.SingleRange
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Represents the exponential distribution.
 *
 * @param lambda The rate parameter.
 * @param seed The seed for the random number generator.
 * @constructor Creates a new exponential distribution.
 * @throws KStatException If lambda is not positive.
 *
 * @see [Exponential Distribution](https://en.wikipedia.org/wiki/Exponential_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Continuous
class ExponentialDistribution(
    private val lambda: Double,
    private val seed: Int = 0
) : ContinuousDistribution(seed) {

    init {
        requireParam(lambda > 0.0) { "Lambda must be positive" }
    }

    private val mean = 1.0 / lambda
    private val variance = 1.0 / (lambda * lambda)
    private val stddev = sqrt(variance)
    private val skewness = 2.0
    private val kurtosis = 6.0
    private val median = ln(2.0) / lambda
    private val mode = 0.0
    private val fisher = doubleArrayOf(1.0 / variance)

    /**
     * Returns a sample that is exponentially distributed (using the Inverse transform).
     *
     * @return A random sample from the distribution.
     */
    override fun sample(vararg support: Double): Double {
        val u = prng.nextDouble()
        return -ln(u) / lambda
    }

    /**
     * Returns n samples that are exponentially distributed.
     *
     * @param n The number of samples to return.
     * @return n random samples from the distribution.
     */
    override fun sample(n: Int, vararg support: Double): DoubleArray = DoubleArray(n) { sample() }

    override fun pdf(x: Double): SingleRange = SingleRange(lambda * exp(-lambda * x))

    override fun cdf(x: Double): SingleRange = SingleRange(1.0 - exp(-lambda * x))

    override fun quantile(x: Double): SingleRange {
        requireParam(x in 0.0..1.0) { "Quantile 'x' must be between 0 and 1" }

        return SingleRange(-ln(1.0 - x) / lambda)
    }

    override fun mean(): Double = mean

    override fun variance(): Double = variance

    override fun stddev(): Double = stddev

    override fun skewness(): Double = skewness

    override fun kurtosis(): Double = kurtosis

    override fun entropy(type: EntropyType): Double = 1.0 - entropyLog(lambda, type)

    override fun median(): SingleRange = SingleRange(median)

    override fun mode(): SingleRange = SingleRange(mode)

    override fun mad(): Double = throw KStatUndefinedException("MAD is undefined for the exponential distribution")

    override fun moment(n: Int): Double = mgf()(n)

    override fun mgf(): (Int) -> Double = { t -> lambda / (lambda - t) }

    override fun fisherInformation(): DoubleArray = fisher

    override fun klDivergence(other: ContinuousDistribution): Double {
        requireParam(other is ExponentialDistribution) { "Other distribution must be exponential" }

        return ln(other.lambda / lambda) + (lambda / other.lambda) - 1.0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is ExponentialDistribution)  return false
        if (seed != other.seed)                 return false
        if (lambda != other.lambda)             return false
        return true
    }

    override fun hashCode(): Int {
        var result = seed
        result = 31 * result + mean.hashCode()
        result = 31 * result + stddev.hashCode()
        return result
    }

    override fun toString(): String = "ExponentialDistribution(seed=$seed, lambda=$lambda)"

}