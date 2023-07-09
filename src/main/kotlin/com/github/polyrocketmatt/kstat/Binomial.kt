package com.github.polyrocketmatt.kstat

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