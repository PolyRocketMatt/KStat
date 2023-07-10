package com.github.polyrocketmatt.kstat.exception

class KStatConvergenceException(reason: String) : KStatException("Failed to converge: $reason")