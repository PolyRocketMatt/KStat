package com.github.polyrocketmatt.kstat.distributions

import com.github.polyrocketmatt.kstat.DisjointRange
import com.github.polyrocketmatt.kstat.EntropyType
import com.github.polyrocketmatt.kstat.IRange
import com.github.polyrocketmatt.kstat.SingleRange
import com.github.polyrocketmatt.kstat.binomial
import com.github.polyrocketmatt.kstat.exception.KStatException
import kotlin.jvm.Throws
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the bernoulli distribution.
 *
 * @property seed The seed to use for the random number generator.
 * @property p The probability of success.
 * @constructor Creates a new bernoulli distribution.
 * @throws KStatException if [n] is not positive.
 * @throws KStatException if [p] is not between 0 and 1.
 *
 * @see [Bernoulli Distribution](https://en.wikipedia.org/wiki/Bernoulli_distribution)
 * @since 1.0.0
 * @author Matthias Kovacic
 */
@Discrete
class BinomialDistribution(
    private val seed: Int,
    private val n: Int,
    private val p: Double
) : Distribution(seed) {

    init {
        if (n <= 0)
            throw KStatException("n must be positive")
        if (p < 0.0 || p > 1.0)
            throw KStatException("p must be between 0 and 1")
    }

    private val q = 1.0 - p
    private val mean = n * p
    private val variance = n * p * q
    private val stddev = sqrt(variance)
    private val skewness = (q - p) / sqrt(stddev)
    private val kurtosis = (1.0 - 6.0 * variance) / stddev

    /**
     * Returns a random sample from the distribution.
     *
     * @return a random sample from the distribution
     * @throws KStatException if the support is empty
     * @throws KStatException if k is not between 0 and n
     */
    @Throws(KStatException::class)
    override fun sample(vararg support: Double): Double {
        if (support.isEmpty())
            throw KStatException("support must not be empty, expected a value for k")
        val k = support[0].toInt()
        if (k < 0 || k > n)
            throw KStatException("k must be between 0 and n")
        return binomial(n, k) * p.pow(k) * q.pow(n - k)
    }

    /**
     * Returns count random samples from the distribution.
     *
     * @param n the number of samples to return
     * @return count random samples from the distribution
     * @throws KStatException if count is less than 1
     * @throws KStatException if the support is empty
     * @throws KStatException if k is not between 0 and n
     */
    @Throws(KStatException::class)
    override fun sample(n: Int, vararg support: Double): DoubleArray = DoubleArray(n) { sample(support = support) }

    override fun pdf(x: Double): SingleRange = SingleRange(sample(x))

    fun pdf(x: Int): SingleRange = SingleRange(sample(x.toDouble()))

    override fun cdf(x: Double): DisjointRange {
        TODO("Not yet implemented")
    }

    override fun quantile(x: Double): IRange {
        TODO("Not yet implemented")
    }

    override fun mean(): Double {
        TODO("Not yet implemented")
    }

    override fun variance(): Double {
        TODO("Not yet implemented")
    }

    override fun stddev(): Double {
        TODO("Not yet implemented")
    }

    override fun skewness(): Double {
        TODO("Not yet implemented")
    }

    override fun kurtosis(): Double {
        TODO("Not yet implemented")
    }

    override fun entropy(type: EntropyType): Double {
        TODO("Not yet implemented")
    }

    override fun median(): IRange {
        TODO("Not yet implemented")
    }

    override fun mode(): IRange {
        TODO("Not yet implemented")
    }

    override fun mad(): Double {
        TODO("Not yet implemented")
    }

    override fun moment(n: Int): Double {
        TODO("Not yet implemented")
    }

    override fun momentGeneratingFunction(): (Int) -> Double {
        TODO("Not yet implemented")
    }

    override fun fisherInformation(): Double {
        TODO("Not yet implemented")
    }
}