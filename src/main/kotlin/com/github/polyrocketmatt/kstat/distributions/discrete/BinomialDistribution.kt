package com.github.polyrocketmatt.kstat.distributions.discrete

import com.github.polyrocketmatt.kstat.Constants.E
import com.github.polyrocketmatt.kstat.Constants.TAU
import com.github.polyrocketmatt.kstat.Functions.binomial
import com.github.polyrocketmatt.kstat.Functions.entropyLog
import com.github.polyrocketmatt.kstat.distributions.Discrete
import com.github.polyrocketmatt.kstat.distributions.Distribution
import com.github.polyrocketmatt.kstat.exception.KStatException
import com.github.polyrocketmatt.kstat.range.SingleRange
import kotlin.jvm.Throws
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents the bernoulli distribution.
 *
 * @property n The number of trials.
 * @property p The probability of success.
 * @constructor Creates a new binomial distribution.
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
        requireParam(n > 0) { "n must be positive" }
        requireParam(p >= 0.0 && p < 1.0) { "p must be between 0 and 1" }
    }

    private val q = 1.0 - p
    private val mean = n * p
    private val variance = n * p * q
    private val stddev = sqrt(variance)
    private val skewness = (q - p) / sqrt(stddev)
    private val kurtosis = (1.0 - 6.0 * variance) / stddev
    private val fisher = doubleArrayOf(n / (p * q))

    /**
     * Returns a sample that is distributed binomially.
     *
     * @return a random sample from the distribution
     * @throws KStatException if the support is empty
     * @throws KStatException if k is not between 0 and n
     */
    @Throws(KStatException::class)
    override fun sample(vararg support: Double): Double {
        requireParam(support.isNotEmpty()) { "Support must not be empty, expected a value for k" }
        val k = support[0].toInt()
        requireParam(k in 0..n) { "k must be between 0 and n" }
        return binomial(n, k) * p.pow(k) * q.pow(n - k)
    }

    /**
     * Returns n samples that are distributed binomially.
     *
     * @param n the number of samples to return
     * @return n random samples from the distribution
     * @throws KStatException if the support is empty
     * @throws KStatException if k is not between 0 and n
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
        requireParam(x in 0.0..1.0) { "x must be between 0 and 1" }

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

    override fun entropy(type: EntropyType): Double = 0.5 * entropyLog(TAU * E * variance, type)

    override fun median(): SingleRange = SingleRange(floor(n * p))

    override fun mode(): SingleRange = SingleRange(floor((n + 1) * p))

    override fun mad(): Double = throw KStatException("MAD is not implemented for discrete distributions")

    override fun moment(n: Int): Double = mgf()(n)

    override fun mgf(): (Int) -> Double = { t -> (q + p * Math.E.pow(t)).pow(n) }

    override fun fisherInformation(): DoubleArray = fisher

    override fun equals(other: Any?): Boolean {
        if (this === other)                     return true
        if (other !is BinomialDistribution)     return false
        if (n != other.n)                       return false
        if (p != other.p)                       return false
        return true
    }

    override fun hashCode(): Int {
        var result = n
        result = 31 * result + p.hashCode()
        return result
    }

    override fun toString(): String = "BinomialDistribution(n=$n, p=$p)"

}