package com.github.polyrocketmatt.kstat.distributions

import com.github.polyrocketmatt.kstat.exception.KStatException

annotation class Discrete
annotation class Continuous

abstract class DiscreteDistribution(seed: Int) : Distribution(seed)

abstract class ContinuousDistribution(seed: Int): Distribution(seed) {

    /**
     * Calculates the Kullback-Leibler divergence between this distribution and the other one.
     *
     * @param other The other distribution.
     * @return The Kullback-Leibler divergence.
     * @throws KStatException If the Kullback-Leibler divergence cannot be calculated.
     * @throws KStatException If the other distribution is not the same type as this one.
     */
    @Throws(KStatException::class)
    abstract fun klDivergence(other: ContinuousDistribution): Double

}
