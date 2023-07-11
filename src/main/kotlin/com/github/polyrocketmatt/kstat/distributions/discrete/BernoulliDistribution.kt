package com.github.polyrocketmatt.kstat.distributions.discrete

import com.github.polyrocketmatt.kstat.Functions.entropyLog
import com.github.polyrocketmatt.kstat.distributions.Discrete
import com.github.polyrocketmatt.kstat.distributions.DiscreteDistribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import com.github.polyrocketmatt.kstat.range.DiscreteRange
import com.github.polyrocketmatt.kstat.range.IRange
import com.github.polyrocketmatt.kstat.range.Range
import com.github.polyrocketmatt.kstat.range.SingleRange
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the bernoulli distribution.
 *
 * @param p The probability of success.
 * @param seed The seed to use for the random number generator.
 * @constructor Creates a new bernoulli distribution.
 * @throws KStatException If p is not between 0 and 1.
 *
 * @see [Bernoulli Distribution](https://en.wikipedia.org/wiki/Bernoulli_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Discrete
class BernoulliDistribution(
    private val p: Double,
    private val seed: Int = 0,
) : DiscreteDistribution(seed) {

    init {
        requireParam(p >= 0.0 && p < 1.0) { "p must be between 0 and 1" }
    }

    private val q = 1.0 - p
    private val variance = p * q
    private val stddev = sqrt(variance)
    private val skewness = (1.0 - 2.0 * p) / stddev
    private val kurtosis = (1.0 - 6.0 * variance) / variance
    private val fisher = doubleArrayOf(1.0 / variance)

    /**
     * Returns a sample that is bernoulli distributed.
     *
     * @return A random sample from the distribution.
     */
    override fun sample(vararg support: Double): Double = if (prng.nextDouble() < p) 1.0 else 0.0

    /**
     * Returns n samples that are normally distributed.
     *
     * @param n The number of samples to return.
     * @return n random samples from the distribution.
     */
    override fun sample(n: Int, vararg support: Double): DoubleArray = DoubleArray(n) { sample() }

    override fun pdf(x: Double): SingleRange {
        requireParam(x in 0.0..1.0) { "x must be between 0 and 1" }
        return if (x == 0.0)
            SingleRange(q)
        else
            SingleRange(p)
    }

    override fun cdf(x: Double): SingleRange {
        return if (x < 0.0)
            SingleRange(0.0)
        else if (x >= 1.0)
            SingleRange(1.0)
        else
            SingleRange(q)
    }

    override fun quantile(x: Double): SingleRange {
        requireParam(x in 0.0..1.0) { "x must be between 0 and 1" }

        return if (x < q)
            SingleRange(0.0)
        else
            SingleRange(1.0)
    }

    override fun mean(): Double = p

    override fun variance(): Double = variance

    override fun stddev(): Double = stddev

    override fun skewness(): Double = skewness

    override fun kurtosis(): Double = kurtosis

    override fun entropy(type: EntropyType): Double = -q * entropyLog(q, type) - p * entropyLog(p, type)

    override fun median(): IRange {
        return if (p < 0.5)
            SingleRange(0.0)
        else if (p > 0.5)
            SingleRange(1.0)
        else
            Range(0.0, 1.0)
    }

    override fun mode(): IRange {
        return if (p < 0.5)
            SingleRange(0.0)
        else if (p > 0.5)
            SingleRange(1.0)
        else
            DiscreteRange(0.0, 1.0)
    }

    override fun mad(): Double = 0.5

    override fun moment(n: Int): Double = mgf()(n)

    override fun mgf(): (Int) -> Double = { t -> q + p * Math.E.pow(t) }

    override fun fisherInformation(n: Int): DoubleArray = fisher

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is BernoulliDistribution)    return false
        if (seed != other.seed)                 return false
        if (p != other.p)                       return false
        return true
    }

    override fun hashCode(): Int {
        var result = seed
        result = 31 * result + p.hashCode()
        return result
    }

    override fun toString(): String = "BernoulliDistribution(seed=$seed, p=$p)"
}