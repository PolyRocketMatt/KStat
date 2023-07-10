package com.github.polyrocketmatt.kstat.distributions.continuous

import com.github.polyrocketmatt.kstat.Constants.SQRT_2
import com.github.polyrocketmatt.kstat.Constants.SQRT_TAU
import com.github.polyrocketmatt.kstat.Constants.TAU
import com.github.polyrocketmatt.kstat.Functions.entropyLog
import com.github.polyrocketmatt.kstat.Functions.erf
import com.github.polyrocketmatt.kstat.Functions.erfc
import com.github.polyrocketmatt.kstat.distributions.Continuous
import com.github.polyrocketmatt.kstat.distributions.ContinuousDistribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import com.github.polyrocketmatt.kstat.range.SingleRange
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the normal distribution.
 *
 * @property seed The seed to use for the random number generator.
 * @property mean The mean of the distribution.
 * @property stddev The standard deviation of the distribution.
 * @property approx Whether to use Abramowitz and Stegun's approximation for the error function.
 * @constructor Creates a new normal distribution.
 * @throws KStatException if [stddev] is not positive.
 *
 * @see [Normal Distribution](https://en.wikipedia.org/wiki/Normal_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Continuous
class NormalDistribution(
    private val seed: Int,
    private val mean: Double,
    private val stddev: Double,
    private val approx: Boolean = false
) : ContinuousDistribution(seed) {

    init {
        requireParam(stddev > 0) { "Standard deviation must be positive" }
    }

    private val mu = mean
    private val sigma = stddev
    private val variance = sigma.pow(2.0)
    private val factor = 1.0 / (sigma * SQRT_TAU)
    private val fisher = doubleArrayOf(1.0 / variance, 0.0, 0.0, 2.0 / variance)

    /**
     * Returns a sample that is normally distributed (using the Box-Muller transform).
     *
     * @return a random sample from the distribution
     */
    override fun sample(vararg support: Double): Double {
        val u1 = prng.nextDouble()
        val u2 = prng.nextDouble()
        val z0 = sqrt(-2.0 * ln(u1)) * cos(TAU * u2)
        return mu + sigma * z0
    }

    /**
     * Returns n samples that are normally distributed.
     *
     * @param n the number of samples to return
     * @return n random samples from the distribution
     */
    override fun sample(n: Int, vararg support: Double): DoubleArray = DoubleArray(n) { sample() }

    override fun pdf(x: Double): SingleRange = SingleRange(factor * exp(-0.5 * ((x - mu) / sigma).pow(2.0)))

    override fun cdf(x: Double): SingleRange {
        val z = (x - mu) / sigma
        return SingleRange(0.5 * (1.0 + erf(z / SQRT_2, approx)))
    }

    override fun quantile(x: Double): SingleRange {
        requireParam(x in 0.0..1.0) { "Quantile 'x' must be between 0 and 1" }

        return SingleRange(mu + sigma * SQRT_2 * erfc(2.0 * x - 1.0, approx))
    }

    override fun mean(): Double = mu

    override fun variance(): Double = variance

    override fun stddev(): Double = sigma

    override fun skewness(): Double = 0.0

    override fun kurtosis(): Double = 0.0

    override fun entropy(type: EntropyType): Double = 0.5 * entropyLog(TAU * variance, type) + 0.5

    override fun median(): SingleRange = SingleRange(mu)

    override fun mode(): SingleRange = SingleRange(mu)

    override fun mad(): Double = sigma * SQRT_2 * erfc(0.5, approx)

    override fun moment(n: Int): Double = mgf()(n)

    override fun mgf(): (Int) -> Double = { t -> exp(mu * t + variance * t * t / 2.0) }

    override fun fisherInformation(): DoubleArray = fisher

    override fun klDivergence(other: ContinuousDistribution): Double {
        val stddevFract = variance / other.variance()
        val meanDiff = (mean - other.mean()).pow(2.0) / other.variance()
        return 0.5 * (stddevFract + meanDiff - 1.0 + ln(other.variance() / variance))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is NormalDistribution)       return false
        if (seed != other.seed)                 return false
        if (mean != other.mean)                 return false
        if (stddev != other.stddev)             return false
        return true
    }

    override fun hashCode(): Int {
        var result = seed
        result = 31 * result + mean.hashCode()
        result = 31 * result + stddev.hashCode()
        return result
    }

    override fun toString(): String = "NormalDistribution(seed=$seed, mean=$mean, stddev=$stddev, approx=$approx)"

}