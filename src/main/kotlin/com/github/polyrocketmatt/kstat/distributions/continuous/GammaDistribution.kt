package com.github.polyrocketmatt.kstat.distributions.continuous

import com.github.polyrocketmatt.kstat.Constants.EPSILON
import com.github.polyrocketmatt.kstat.Gamma.diGamma
import com.github.polyrocketmatt.kstat.Gamma.gamma
import com.github.polyrocketmatt.kstat.Gamma.incompleteGamma
import com.github.polyrocketmatt.kstat.Gamma.lnGamma
import com.github.polyrocketmatt.kstat.distributions.Continuous
import com.github.polyrocketmatt.kstat.distributions.ContinuousDistribution
import com.github.polyrocketmatt.kstat.distributions.nextGaussian
import com.github.polyrocketmatt.kstat.exception.KStatException
import com.github.polyrocketmatt.kstat.exception.KStatUndefinedException
import com.github.polyrocketmatt.kstat.range.IRange
import com.github.polyrocketmatt.kstat.range.SingleRange
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the gamma distribution.
 *
 * @param alpha The shape parameter (α).
 * @param beta The scale parameter (β).
 * @param seed The seed for the random number generator.
 * @constructor Creates a new normal distribution.
 * @throws KStatException If α or β are not greater than 0.
 *
 * @see [Normal Distribution](https://en.wikipedia.org/wiki/Gamma_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Continuous
class GammaDistribution(
    private val alpha: Double,
    private val beta: Double,
    private val seed: Int = 0
) : ContinuousDistribution(seed) {

    init {
        requireParam(alpha > 0) { "α must be > 0" }
        requireParam(beta > 0) { "β must be > 0" }
    }

    private val mean = alpha / beta
    private val variance = alpha / (beta * beta)
    private val stddev = sqrt(variance)
    private val skewness = 2 / sqrt(alpha)
    private val kurtosis = 6 / alpha
    private val mode = (alpha - 1) / beta
    private val fisher = doubleArrayOf(diGamma(alpha), -1.0 / beta, -1.0 / beta, alpha / (beta * beta))

    /**
     * Returns a sample that is gamma distributed (using Cheng's algorithm).
     *
     * @return A random sample from the distribution.
     */
    override fun sample(vararg support: Double): Double {
        val d = alpha - 1.0 / 3.0
        val c = 1.0 / sqrt(9.0 * d)
        while (true) {
            val x = prng.nextGaussian()
            val v = 1.0 + c * x
            if (v > 0.0) {
                val xSquared = x * x
                val u = prng.nextDouble()
                val t = 0.9 * (1.0 + 0.5 * xSquared) * v.pow(3.0)
                if (u < 1.0 - 0.0331 * xSquared * xSquared || ln(u) < t)
                    return beta * d * v
            }
        }
    }

    /**
     * Returns n samples that are gamma distributed.
     *
     * @param n The number of samples to return.
     * @return n random samples from the distribution.
     */
    override fun sample(n: Int, vararg support: Double): DoubleArray = DoubleArray(n) { sample(*support) }

    override fun pdf(x: Double): SingleRange {
        val nominator = beta.pow(alpha) * x.pow(alpha - 1) * exp(-beta * x)
        val denominator = gamma(alpha)
        return SingleRange(nominator / denominator)
    }

    override fun cdf(x: Double): SingleRange {
        val nominator = incompleteGamma(alpha, beta * x)
        val denominator = gamma(alpha)
        return SingleRange(nominator / denominator)
    }

    override fun quantile(x: Double): SingleRange {
        requireParam(x in 0.0..1.0) { "Quantile 'x' must be between 0 and 1" }

        var lowerBound = 0.0
        var upperBound = alpha * beta * 100.0
        while (upperBound - lowerBound > EPSILON) {
            val p = (lowerBound + upperBound) / 2
            val cumulativeProbability = incompleteGamma(alpha, beta * p) / gamma(alpha)

            if (cumulativeProbability < x) {
                lowerBound = p
            } else {
                upperBound = p
            }
        }

        return SingleRange((lowerBound + upperBound) / 2)
    }

    override fun mean(): Double = mean

    override fun variance(): Double = variance

    override fun stddev(): Double = stddev

    override fun skewness(): Double = skewness

    override fun kurtosis(): Double = kurtosis

    override fun entropy(type: EntropyType): Double {
        TODO("Not yet implemented")
    }

    override fun median(): IRange = throw KStatUndefinedException("Median has no closed form for the gamma distribution")

    override fun mode(): SingleRange = SingleRange(mode)

    override fun mad(): Double = throw KStatUndefinedException("MAD is undefined for the gamma distribution")

    override fun moment(n: Int): Double = mgf()(n)

    override fun mgf(): (Int) -> Double = { t -> (1.0 - (t / beta)).pow(-alpha) }

    override fun fisherInformation(): DoubleArray = fisher

    override fun klDivergence(other: ContinuousDistribution): Double {
        requireParam(other is GammaDistribution) { "KL divergence is only defined for identical distributions" }

        val a = (alpha - other.alpha) * diGamma(alpha)
        val b = ln(other.beta / beta)
        val c = ln(lnGamma(alpha) / lnGamma(other.alpha))
        val d = (other.alpha - alpha) * diGamma(other.alpha)
        return a + b + c + d
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is GammaDistribution)        return false
        if (seed != other.seed)                 return false
        if (alpha != other.alpha)               return false
        if (beta != other.beta)                 return false
        return true
    }

    override fun hashCode(): Int {
        var result = seed
        result = 31 * result + seed.hashCode()
        result = 31 * result + alpha.hashCode()
        result = 31 * result + beta.hashCode()
        return result
    }

    override fun toString(): String = "GammaDistribution(alpha=$alpha, beta=$beta, seed=$seed)"

}