package com.github.polyrocketmatt.kstat

import com.github.polyrocketmatt.kstat.distributions.BinomialDistribution

fun main() {
    val dist = BinomialDistribution(1, 10, 0.65)

    println(dist.pdf(9))
}
