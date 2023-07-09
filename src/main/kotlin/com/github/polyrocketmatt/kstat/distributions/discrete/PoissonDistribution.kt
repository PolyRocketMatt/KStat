package com.github.polyrocketmatt.kstat.distributions.discrete

import com.github.polyrocketmatt.kstat.Functions.entropyLog
import com.github.polyrocketmatt.kstat.Functions.factorial
import com.github.polyrocketmatt.kstat.IRange
import com.github.polyrocketmatt.kstat.SingleRange
import com.github.polyrocketmatt.kstat.distributions.Discrete
import com.github.polyrocketmatt.kstat.distributions.Distribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the Poisson distribution.
 *
 * @property rate The rate of the distribution.
 * @constructor Creates a new bernoulli distribution.
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
    private val fisher = 1.0 / variance

    override fun sample(vararg support: Double): Double {
        TODO("Not yet implemented")
    }

    override fun sample(n: Int, vararg support: Double): DoubleArray {
        TODO("Not yet implemented")
    }

    override fun pdf(x: Double): IRange {
        TODO("Not yet implemented")
    }

    override fun cdf(x: Double): IRange {
        TODO("Not yet implemented")
    }

    override fun quantile(x: Double): IRange {
        TODO("Not yet implemented")
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
            val primary = 0.5 * entropyLog(2.0 * Math.PI * Math.E * lambda, type)
            val secondary = 1.0 / (12.0 * lambda)
            val tertiary = 1.0 / (24.0 * lambda.pow(2))
            val quaternary = 19.0 / (360.0 * lambda.pow(3))
            return primary + secondary - tertiary - quaternary
        }
    }

    override fun median(): SingleRange = SingleRange(floor(lambda + 1.0 / 3.0 - 0.02 / lambda))

    override fun mode(): SingleRange = SingleRange(floor(lambda))

    override fun mad(): Double = throw KStatException("MAD is not implemented for discrete distributions")

    override fun moment(n: Int): Double = momentGeneratingFunction().invoke(n)

    override fun momentGeneratingFunction(): (Int) -> Double = { t -> exp(lambda * (Math.E.pow(t) - 1.0)) }

    override fun fisherInformation(): Double = fisher
}