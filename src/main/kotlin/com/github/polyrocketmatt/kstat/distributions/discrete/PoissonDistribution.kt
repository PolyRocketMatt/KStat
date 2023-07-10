package com.github.polyrocketmatt.kstat.distributions.discrete

import com.github.polyrocketmatt.kstat.Constants.E
import com.github.polyrocketmatt.kstat.Constants.TAU
import com.github.polyrocketmatt.kstat.Functions.entropyLog
import com.github.polyrocketmatt.kstat.Functions.factorial
import com.github.polyrocketmatt.kstat.distributions.Discrete
import com.github.polyrocketmatt.kstat.distributions.Distribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import com.github.polyrocketmatt.kstat.range.SingleRange
import kotlin.jvm.Throws
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the Poisson distribution.
 *
 * @property rate The rate of the distribution.
 * @constructor Creates a new Poisson distribution.
 * @throws KStatException if [rate] is not positive.
 *
 * @see [Poisson Distribution](https://en.wikipedia.org/wiki/Poisson_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Discrete
class PoissonDistribution(
    private val rate: Int = 0
) : Distribution(0) {

    init {
        if (rate < 0)
            throw KStatException("rate must be greater than 0")
    }

    private val lambda = rate.toDouble()
    private val mean = lambda
    private val variance = lambda
    private val stddev = sqrt(variance)
    private val skewness = 1.0 / stddev
    private val kurtosis = 1.0 / variance
    private val fisher = doubleArrayOf(1.0 / variance)

    /**
     * Returns a sample that is Poisson distributed.
     *
     * @return a random sample from the distribution
     * @throws KStatException if the support is empty
     * @throws KStatException if k is negative
     */
    @Throws(KStatException::class)
    override fun sample(vararg support: Double): Double {
        requireParam(support.isNotEmpty()) { "Support must not be empty, expected a value for k" }
        val k = support[0].toInt()
        requireParam(k >= 0) { "k must be positive" }
        return exp(-lambda) * lambda.pow(k) / factorial(k)
    }

    /**
     * Returns n samples that are Poisson distributed.
     *
     * @param n the number of samples to return
     * @return n random samples from the distribution
     * @throws KStatException if the support is empty
     * @throws KStatException if k is negative
     */
    @Throws(KStatException::class)
    override fun sample(n: Int, vararg support: Double): DoubleArray = DoubleArray(n) { sample(*support) }

    override fun pdf(x: Double): SingleRange = SingleRange(sample(x))

    fun pdf(x: Int): SingleRange = SingleRange(sample(x.toDouble()))

    override fun cdf(x: Double): SingleRange {
        var sum = 0.0
        for (i in 0..floor(x).toInt())
            sum += sample(i.toDouble())
        return SingleRange(sum)
    }

    override fun quantile(x: Double): SingleRange {
        var sum = 0.0
        var k = 0
        while (sum < x) {
            sum += pdf(k.toDouble()).value
            k++
        }
        return SingleRange(k - 1.0)
    }

    override fun mean(): Double = mean

    override fun variance(): Double = variance

    override fun stddev(): Double = stddev

    override fun skewness(): Double = skewness

    override fun kurtosis(): Double = kurtosis

    override fun entropy(type: EntropyType): Double {
        if (rate < 10) {
            val factor = Math.E.pow(-lambda)
            var sum = 0.0
            for (i in 0..rate) {
                val factorial = factorial(i).toDouble()
                sum += (lambda.pow(i) * entropyLog(factorial, type)) / factorial
            }

            return lambda * (1.0 - entropyLog(lambda, type)) + factor * sum
        } else {
            val primary = 0.5 * entropyLog(TAU * E * lambda, type)
            val secondary = 1.0 / (12.0 * lambda)
            val tertiary = 1.0 / (24.0 * lambda.pow(2))
            val quaternary = 19.0 / (360.0 * lambda.pow(3))
            return primary + secondary - tertiary - quaternary
        }
    }

    override fun median(): SingleRange = SingleRange(floor(lambda + 1.0 / 3.0 - 0.02 / lambda))

    override fun mode(): SingleRange = SingleRange(floor(lambda))

    override fun mad(): Double = throw KStatException("MAD is not implemented for discrete distributions")

    override fun moment(n: Int): Double = mgf()(n)

    override fun mgf(): (Int) -> Double = { t -> exp(lambda * (E.pow(t) - 1.0)) }

    override fun fisherInformation(): DoubleArray = fisher

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is PoissonDistribution)      return false
        if (rate != other.rate)                 return false
        return true
    }

    override fun hashCode(): Int = 31 * lambda.toInt()

    override fun toString(): String = "PoissonDistribution(rate=$rate)"

}