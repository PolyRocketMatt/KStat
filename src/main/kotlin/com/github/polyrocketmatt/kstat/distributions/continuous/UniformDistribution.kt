package com.github.polyrocketmatt.kstat.distributions.continuous

import com.github.polyrocketmatt.kstat.Functions.entropyLog
import com.github.polyrocketmatt.kstat.distributions.Continuous
import com.github.polyrocketmatt.kstat.distributions.ContinuousDistribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import com.github.polyrocketmatt.kstat.range.ContinuousRange
import com.github.polyrocketmatt.kstat.range.SingleRange
import com.github.polyrocketmatt.kstat.utils.SimpsonIntegrator
import kotlin.math.exp
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the uniform distribution.
 *
 * @param min The minimum value of the distribution.
 * @param max The maximum value of the distribution.
 * @param seed The seed to use for the random number generator.
 * @constructor Creates a new uniform distribution.
 * @throws KStatException If min is greater than max.
 *
 * @see [Uniform Distribution](https://en.wikipedia.org/wiki/Continuous_uniform_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Continuous
class UniformDistribution(
    private val min: Double,
    private val max: Double,
    private val seed: Int = 0
) : ContinuousDistribution(seed) {

    init {
        requireParam(min < max) { "Min must be less than max" }
    }

    private val mean = (min + max) / 2.0
    private val variance = (max - min).pow(2.0) / 12.0
    private val stddev = sqrt(variance)
    private val skewness = 0.0
    private val kurtosis = -6.0 / 5.0
    private val mad = (max - min) / 4.0
    private val fisher = doubleArrayOf(0.0)

    /**
     * Returns a sample that is uniformly distributed.
     *
     * @return A random sample from the distribution.
     */
    override fun sample(vararg support: Double): Double = min + (max - min) * prng.nextDouble()

    /**
     * Returns n samples that are uniformly distributed.
     *
     * @param n The number of samples to return.
     * @return n random samples from the distribution.
     */
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
        requireParam(x in 0.0..1.0) { "Quantile 'x' must be between 0 and 1" }

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

    override fun klDivergence(other: ContinuousDistribution): Double {
        requireParam(other is UniformDistribution) { "Other distribution must be uniform" }

        return SimpsonIntegrator.simpson({ _ -> log2((other.max - other.min) / (max - min)) }, min, max)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is UniformDistribution)      return false
        if (seed != other.seed)                 return false
        if (min != other.min)                   return false
        if (max != other.max)                   return false
        return true
    }

    override fun hashCode(): Int {
        var result = seed
        result = 31 * result + seed.hashCode()
        result = 31 * result + min.hashCode()
        result = 31 * result + max.hashCode()
        return result
    }

    override fun toString(): String = "UniformDistribution(seed=$seed, min=$min, max=$max)"

}