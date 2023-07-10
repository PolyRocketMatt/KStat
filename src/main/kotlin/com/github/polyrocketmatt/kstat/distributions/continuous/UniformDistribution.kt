package com.github.polyrocketmatt.kstat.distributions.continuous

import com.github.polyrocketmatt.kstat.Functions.entropyLog
import com.github.polyrocketmatt.kstat.distributions.Continuous
import com.github.polyrocketmatt.kstat.distributions.Distribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import com.github.polyrocketmatt.kstat.range.ContinuousRange
import com.github.polyrocketmatt.kstat.range.SingleRange
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the uniform distribution.
 *
 * @property min The minimum value of the distribution.
 * @property max The maximum value of the distribution.
 * @constructor Creates a new bernoulli distribution.
 * @throws KStatException if [min] is greater than [max].
 *
 * @see [Uniform Distribution](https://en.wikipedia.org/wiki/Continuous_uniform_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Continuous
class UniformDistribution(
    private val seed: Int,
    private val min: Double,
    private val max: Double
) : Distribution(seed) {

    init {
        requireParam(min < max) { "min must be less than max" }
    }

    private val mean = (min + max) / 2.0
    private val variance = (max - min).pow(2.0) / 12.0
    private val stddev = sqrt(variance)
    private val skewness = 0.0
    private val kurtosis = -6.0 / 5.0
    private val mad = (max - min) / 4.0
    private val fisher = doubleArrayOf(0.0)

    override fun sample(vararg support: Double): Double = min + (max - min) * prng.nextDouble()

    override fun sample(n: Int, vararg support: Double): DoubleArray = DoubleArray(n) { sample() }

    override fun pdf(x: Double): SingleRange = if (x in min..max) SingleRange(1.0 / (max - min)) else SingleRange(0.0)

    override fun cdf(x: Double): SingleRange =
        if (x < min)
            SingleRange(0.0)
        else if (x > max)
            SingleRange(1.0)
        else
            SingleRange((x - min) / (max - min))

    override fun quantile(x: Double): SingleRange {
        requireParam(x in 0.0..1.0) { "x must be between 0 and 1" }

        return SingleRange(min + x * (max - min))
    }

    override fun mean(): Double = mean

    override fun variance(): Double = variance

    override fun stddev(): Double = stddev

    override fun skewness(): Double = skewness

    override fun kurtosis(): Double = kurtosis

    override fun entropy(type: EntropyType): Double = entropyLog(max - min, type)

    override fun median(): SingleRange = SingleRange(mean)

    override fun mode(): ContinuousRange = ContinuousRange(doubleArrayOf(min, max))

    override fun mad(): Double = mad

    override fun moment(n: Int): Double = mgf()(n)

    override fun mgf(): (Int) -> Double = { t -> if (t == 0) 1.0 else (exp(t * max) - exp(t * min)) / (t * (max - min)) }

    override fun fisherInformation(): DoubleArray = fisher
}