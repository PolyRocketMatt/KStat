package com.github.polyrocketmatt.kstat.distributions

import com.github.polyrocketmatt.kstat.IRange
import com.github.polyrocketmatt.kstat.exception.KStatException
import kotlin.jvm.Throws
import kotlin.random.Random

/**
 * Represents a probability distribution.
 *
 * @since 1.0.0
 * @author Matthias Kovacic
 */
abstract class Distribution(private val seed: Int) {

    protected val prng = Random(seed)

    /**
     * Returns the seed used to generate the distribution.
     *
     * @return The seed used to generate the distribution
     */
    fun getSeed(): Int = seed

    /**
     * Returns true if the distribution is discrete.
     *
     * @return True if the distribution is discrete
     */
    fun isDiscrete(): Boolean = javaClass.annotations.any { it is Discrete }

    /**
     * Returns true if the distribution is continuous.
     *
     * @return True if the distribution is continuous
     */
    fun isContinuous(): Boolean = javaClass.annotations.any { it is Continuous }

    /**
     * Returns a random sample from the distribution.
     *
     * @return A random sample from the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun sample(vararg support: Double): Double

    /**
     * Returns count random samples from the distribution.
     *
     * @param n The number of samples to return
     * @return n random samples from the distribution
     * @throws KStatException If count is less than 1
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun sample(n: Int, vararg support: Double): DoubleArray

    /**
     * Returns the probability density function (PDF) of the distribution.
     *
     * @param x The value to evaluate the PDF at
     * @return The probability density function (PDF) of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun pdf(x: Double): IRange

    /**
     * Returns the cumulative distribution function (CDF) of the distribution.
     *
     * @param x The value to evaluate the CDF at
     * @return The cumulative distribution function (CDF) of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun cdf(x: Double): IRange

    /**
     * Returns the quantile of the distribution.
     *
     * @param x The value to evaluate the quantile at
     * @return The quantile of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun quantile(x: Double): IRange

    /**
     * Returns the mean of the distribution.
     *
     * @return The mean of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun mean(): Double

    /**
     * Returns the variance of the distribution.
     *
     * @return The variance of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun variance(): Double

    /**
     * Returns the standard deviation of the distribution.
     *
     * @return The standard deviation of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun stddev(): Double

    /**
     * Returns the skewness of the distribution.
     *
     * @return The skewness of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun skewness(): Double

    /**
     * Returns the kurtosis of the distribution.
     *
     * @return The kurtosis of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun kurtosis(): Double

    /**
     * Returns the entropy of the distribution.
     *
     * @param type The type of entropy to return
     * @return The entropy of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun entropy(type: EntropyType): Double

    /**
     * Returns the median of the distribution.
     *
     * @return The median of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun median(): IRange

    /**
     * Returns the mode of the distribution.
     *
     * @return The mode of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun mode(): IRange

    /**
     * Returns the mean absolute deviation (MAD) of the distribution.
     *
     * @return The mean absolute deviation (MAD) of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun mad(): Double

    /**
     * Returns the n-th moment of the distribution.
     *
     * @param n The moment to return
     * @return The n-th moment of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun moment(n: Int): Double

    /**
     * Returns the moment generating function (MGF) of the distribution.
     *
     * @return The moment generating function (MGF) of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun momentGeneratingFunction(): (Int) -> Double

    /**
     * Returns the Fisher information of the distribution.
     *
     * @return The Fisher information of the distribution
     * @throws KStatException If some error occurred
     */
    @Throws(KStatException::class)
    abstract fun fisherInformation(): Double

    /**
     * Types of entropy.
     *
     * @since 1.0.0
     * @author Matthias Kovacic
     * @see Distribution.entropy
     */
    enum class EntropyType {

        SHANNON,
        NATURAL

    }

}