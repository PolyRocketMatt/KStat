package com.github.polyrocketmatt.kstat.range

import com.github.polyrocketmatt.kstat.exception.KStatException


/**
 * Represents a range of values between min (inclusive) and max (exclusive) of the given list of values.
 *
 * @property values The values of the range.
 *
 * @since 1.0.0
 * @see Range
 * @author Matthias Kovacic
 */
class ContinuousRange(val values: DoubleArray, val accuracy: Double = 0.01) : AbstractRange(values.min(), values.max()) {

    override fun union(range: IRange): ContinuousRange {
        throw KStatException("Cannot union continuous ranges")
    }

    override fun intersection(range: IRange): ContinuousRange {
        throw KStatException("Cannot intersect continuous ranges")
    }

    override fun difference(range: IRange): List<ContinuousRange> {
        throw KStatException("Cannot difference continuous ranges")
    }

    /**
     * Returns the values of this range as a double array.
     *
     * @return The values of this range as a double array
     */
    fun toDoubleArray(): DoubleArray = values

    override fun toString(): String {
        return values.joinToString(prefix = "[", postfix = "]")
    }
}
