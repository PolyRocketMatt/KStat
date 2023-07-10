package com.github.polyrocketmatt.kstat.exception

class KStatUndefinedException(reason: String) : KStatException("Undefined: $reason")