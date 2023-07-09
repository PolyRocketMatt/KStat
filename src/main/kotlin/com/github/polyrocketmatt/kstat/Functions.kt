package com.github.polyrocketmatt.kstat

import com.github.polyrocketmatt.kstat.distributions.Distribution
import kotlin.math.ln
import kotlin.math.log2

object Functions {

    fun binomial(n: Int, k: Int): Int {
        if (k < 0 || k > n)
            return 0
        if (k == 0 || k == n)
            return 1
        if (k > n / 2)
            return binomial(n, n - k)
        var b = 1
        for (i in 1..k)
            b = b * (n - i + 1) / i
        return b
    }

    fun factorial(n: Int): Int {
        if (n < 0)
            return 0
        var f = 1
        for (i in 1..n)
            f *= i
        return f
    }

    fun entropyLog(x: Double, type: Distribution.EntropyType): Double = when(type) {
        Distribution.EntropyType.SHANNON -> log2(x)
        Distribution.EntropyType.NATURAL -> ln(x)
    }

}