package com.github.polyrocketmatt.kstat.distributions

import com.github.polyrocketmatt.kstat.EntropyType
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
     * @return the seed used to generate the distribution
     */
    fun getSeed(): Int = seed

    /**
     * Returns true if the distribution is discrete.
     *
     * @return true if the distribution is discrete
     */
    fun isDiscrete(): Boolean = javaClass.annotations.any { it is Discrete }

    /**
     * Returns true if the distribution is continuous.
     *
     * @return true if the distribution is continuous
     */
    fun isContinuous(): Boolean = javaClass.annotations.any { it is Continuous }

    /**
     * Returns a random sample from the distribution.
     *
     * @return a random sample from the distribution
     * @throws KStatException if some error occurred
     */
    @Throws(KStatException::class)
    abstract fun sample(vararg support: Double): Double

    /**
     * Returns count random samples from the distribution.
     *
     * @param n the number of samples to return
     * @return count random samples from the distribution
     * @throws KStatException if count is less than 1
     * @throws KStatException if some error occurred
     */
    @Throws(KStatException::class)
    abstract fun sample(n: Int, vararg support: Double): DoubleArray

    @Throws(KStatException::class)
    abstract fun pdf(x: Double): IRange

    @Throws(KStatException::class)
    abstract fun cdf(x: Double): IRange

    @Throws(KStatException::class)
    abstract fun quantile(x: Double): IRange

    @Throws(KStatException::class)
    abstract fun mean(): Double

    @Throws(KStatException::class)
    abstract fun variance(): Double

    @Throws(KStatException::class)
    abstract fun stddev(): Double

    @Throws(KStatException::class)
    abstract fun skewness(): Double

    @Throws(KStatException::class)
    abstract fun kurtosis(): Double

    @Throws(KStatException::class)
    abstract fun entropy(type: EntropyType): Double

    @Throws(KStatException::class)
    abstract fun median(): IRange

    @Throws(KStatException::class)
    abstract fun mode(): IRange

    @Throws(KStatException::class)
    abstract fun mad(): Double

    @Throws(KStatException::class)
    abstract fun moment(n: Int): Double

    @Throws(KStatException::class)
    abstract fun momentGeneratingFunction(): (Int) -> Double

    @Throws(KStatException::class)
    abstract fun fisherInformation(): Double

}