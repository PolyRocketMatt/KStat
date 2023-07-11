package com.github.polyrocketmatt.kstat.distributions.continuous

import com.github.polyrocketmatt.kstat.Constants.EULER_MASCHERONI
import com.github.polyrocketmatt.kstat.Functions.entropyLog
import com.github.polyrocketmatt.kstat.Gamma.gamma
import com.github.polyrocketmatt.kstat.distributions.Continuous
import com.github.polyrocketmatt.kstat.distributions.ContinuousDistribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import com.github.polyrocketmatt.kstat.exception.KStatUndefinedException
import com.github.polyrocketmatt.kstat.range.SingleRange
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the Weibull distribution.
 *
 * @param lambda The scale parameter (λ).
 * @param k The shape parameter (k).
 * @param seed The seed for the random number generator.
 * @constructor Creates a new normal distribution.
 * @throws KStatException If λ or k are not greater than 0.
 *
 * @see [Normal Distribution](https://en.wikipedia.org/wiki/Gamma_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Continuous
class WeibullDistribution(
    private val lambda: Double,
    private val k: Double,
    private val seed: Int = 0
) : ContinuousDistribution(0) {

    init {
        requireParam(lambda > 0) { "λ must be > 0" }
        requireParam(k > 0) { "k must be > 0" }
    }

    private val mean = lambda * gamma(1.0 + 1.0 / k)
    private val variance = lambda * lambda * (gamma(1.0 + 2.0 / k) - gamma(1.0 + 1.0 / k).pow(2))
    private val stddev = sqrt(variance)
    private val skewness = (gamma(1.0 + 3.0 / k) * lambda.pow(3) - 3 * mean * variance - mean.pow(3)) / stddev.pow(3)
    private val kurtosis = (gamma(1.0 + 4.0 / k) * lambda.pow(4) - 4 * skewness * mean * variance.pow(3) - 6 * mean.pow(2) * variance.pow(2) - mean.pow(4)) / stddev.pow(4)
    private val median = lambda * (ln(2.0)).pow(1.0 / k)

    /**
     * Returns a sample that is Weibull distributed (using the Inverse-transform).
     *
     * @return A random sample from the distribution.
     */
    override fun sample(vararg support: Double): Double {
        val u = prng.nextDouble()
        return lambda * (-ln(1 - u)).pow(1 / k)
    }

    /**
     * Returns n samples that are Weibull distributed.
     *
     * @param n The number of samples to return.
     * @return n random samples from the distribution.
     */
    override fun sample(n: Int, vararg support: Double): DoubleArray = DoubleArray(n) { sample(*support) }

    override fun pdf(x: Double): SingleRange = SingleRange(if (x < 0.0) 0.0 else (k / lambda) * (x / lambda).pow(k - 1) * exp(-(x / lambda).pow(k)))

    override fun cdf(x: Double): SingleRange = SingleRange(if (x < 0.0) 0.0 else 1.0 - exp(-(x / lambda).pow(k)))

    override fun quantile(x: Double): SingleRange {
        requireParam(x in 0.0..1.0) { "x must be in [0, 1]" }

        return SingleRange(lambda * (-ln(1.0 - x)).pow(1.0 / k))
    }

    override fun mean(): Double = mean

    override fun variance(): Double = variance

    override fun stddev(): Double = stddev

    override fun skewness(): Double = skewness

    override fun kurtosis(): Double = kurtosis

    override fun entropy(type: EntropyType): Double = EULER_MASCHERONI * (1 - 1 / k) + entropyLog(lambda / k, type) + 1

    override fun median(): SingleRange = SingleRange(median)

    override fun mode(): SingleRange = SingleRange(if (k <= 1) 0.0 else lambda * ((k - 1) / k).pow(1 / k))

    override fun mad(): Double = throw KStatUndefinedException("MAD is undefined for the Weibull distribution")

    override fun moment(n: Int): Double = mgf()(n)

    override fun mgf(): (Int) -> Double = throw KStatUndefinedException("MGF is undefined for the Weibull distribution")

    override fun fisherInformation(n: Int): DoubleArray = throw KStatUndefinedException("Fisher information is undefined for the Weibull distribution")
    override fun klDivergence(other: ContinuousDistribution): Double {
        requireParam(other is WeibullDistribution) { "other distribution must be a Weibull distribution" }

        val logA = log2(k / lambda.pow(k))
        val logB = log2(other.k / other.lambda.pow(other.k))
        val diff = (k - other.k) * (log2(lambda) - (EULER_MASCHERONI / k))
        val gamma = (lambda / other.lambda).pow(k) * gamma(other.k / k + 1)
        return logA - logB + diff + gamma - 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is WeibullDistribution)      return false
        if (seed != other.seed)                 return false
        if (k != other.k)                       return false
        if (lambda != other.lambda)             return false
        return true
    }

    override fun hashCode(): Int {
        var result = seed
        result = 31 * result + mean.hashCode()
        result = 31 * result + stddev.hashCode()
        return result
    }

    override fun toString(): String = "WeibullDistribution(lambda=$lambda, k=$k, seed=$seed)"
}