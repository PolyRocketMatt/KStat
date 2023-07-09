package com.github.polyrocketmatt.kstat.distributions

import com.github.polyrocketmatt.kstat.DiscreteRange
import com.github.polyrocketmatt.kstat.EntropyType
import com.github.polyrocketmatt.kstat.IRange
import com.github.polyrocketmatt.kstat.SingleRange
import com.github.polyrocketmatt.kstat.binomial
import com.github.polyrocketmatt.kstat.exception.KStatException
import kotlin.jvm.Throws
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the bernoulli distribution.
 *
 * @property n The number of trials.
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
    private val n: Int,
    private val p: Double
) : Distribution(0) {

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
    private val fisher = n / (p * q)

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

    override fun cdf(x: Double): SingleRange {
        var sum = 0.0
        for (i in 0..floor(x).toInt())
            sum += sample(support = doubleArrayOf(i.toDouble()))
        return SingleRange(sum)
    }

    override fun quantile(x: Double): IRange {
        var cumulativeProbability = 0.0
        var k = 0
        while (cumulativeProbability <= p && k <= n) {
            cumulativeProbability += pdf(k.toDouble()).value
            k++
        }
        return SingleRange(k - 1.0)
    }

    override fun mean(): Double = mean

    override fun variance(): Double = variance

    override fun stddev(): Double = stddev

    override fun skewness(): Double = skewness

    override fun kurtosis(): Double = kurtosis

    override fun entropy(type: EntropyType): Double = when(type) {
        EntropyType.SHANNON     -> 0.5 * log2(2.0 * Math.PI * Math.E * variance)
        EntropyType.NATURAL     -> 0.5 * ln(2.0 * Math.PI * Math.E * variance)
    }

    override fun median(): SingleRange = SingleRange(floor(n * p))

    override fun mode(): SingleRange = SingleRange(floor((n + 1) * p))

    override fun mad(): Double = throw KStatException("MAD is not implemented for the binomial distribution")

    override fun moment(n: Int): Double = momentGeneratingFunction().invoke(n)

    override fun momentGeneratingFunction(): (Int) -> Double = { t -> (q + p * Math.E.pow(t)).pow(n) }

    override fun fisherInformation(): Double = fisher

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is BinomialDistribution)     return false
        if (n != other.n)                       return false
        if (p != other.p)                       return false
        return true
    }

    override fun toString(): String = "BinomialDistribution(n=$n, p=$p)"

}