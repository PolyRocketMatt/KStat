package com.github.polyrocketmatt.kstat.distributions

import com.github.polyrocketmatt.kstat.DisjointRange
import com.github.polyrocketmatt.kstat.EntropyType
import com.github.polyrocketmatt.kstat.IRange
import com.github.polyrocketmatt.kstat.Range
import com.github.polyrocketmatt.kstat.SingleRange
import com.github.polyrocketmatt.kstat.exception.KStatException
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the bernoulli distribution.
 *
 * @property seed The seed to use for the random number generator.
 * @property p The probability of success.
 * @constructor Creates a new bernoulli distribution.
 * @throws KStatException if [p] is not between 0 and 1.
 *
 * @see [Bernoulli Distribution](https://en.wikipedia.org/wiki/Bernoulli_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Discrete
class BernoulliDistribution(
    private val seed: Int = 0,
    private val p: Double = 0.5
) : Distribution(seed) {

    init {
        if (p < 0.0 || p > 1.0)
            throw KStatException("p must be between 0 and 1")
    }

    private val q = 1.0 - p
    private val variance = p * q
    private val stddev = sqrt(variance)
    private val skewness = (1.0 - 2.0 * p) / stddev
    private val kurtosis = (1.0 - 6.0 * variance) / variance

    override fun sample(vararg support: Double): Double = if (prng.nextDouble() < p) 1.0 else 0.0

    override fun sample(n: Int, vararg support: Double): DoubleArray = DoubleArray(n) { sample() }

    override fun pdf(x: Double): SingleRange = when (x) {
        0.0 -> SingleRange(q)
        1.0 -> SingleRange(p)
        else -> throw KStatException("x must be 0 or 1")
    }

    override fun cdf(x: Double): SingleRange {
        return if (x < 0.0)
            SingleRange(0.0)
        else if (x >= 1.0)
            SingleRange(1.0)
        else
            SingleRange(q)
    }

    override fun quantile(x: Double): IRange {
        if (x < 0.0 || x > 1.0)
            throw KStatException("x must be between 0 and 1")
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

    override fun entropy(type: EntropyType): Double = when(type) {
        EntropyType.SHANNON     -> -q * log2(q) - p * log2(p)
        EntropyType.NATURAL     -> -q * ln(q) - p * ln(p)
    }

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
            DisjointRange(0.0, 1.0)
    }

    override fun mad(): Double = 0.5

    override fun moment(n: Int): Double = momentGeneratingFunction().invoke(n)

    override fun momentGeneratingFunction(): (Int) -> Double = { t -> q + p * Math.E.pow(t) }

    override fun fisherInformation(): Double = 1.0 / variance

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is BernoulliDistribution)    return false
        if (seed != other.seed)                 return false
        return true
    }

    override fun toString(): String = "BernoulliDistribution(seed=$seed)"
}