package com.github.polyrocketmatt.kstat.range

import com.github.polyrocketmatt.kstat.exception.KStatException

/**
 * Represents a range of values between min (inclusive) and max (exclusive).
 *
 * @property minVal The minimum value of the range.
 * @property maxVal The maximum value of the range.
 *
 * @since 1.0.0
 * @author Matthias Kovacic
 */
class Range(val min: Double, val max: Double) : AbstractRange(min, max) {

    override fun union(range: IRange): Range {
        if (range !is Range) throw KStatException("Range must be of type Range")
        return Range(minOf(min, range.min), maxOf(max, range.max))
    }

    override fun intersection(range: IRange): Range {
        if (range !is Range) throw KStatException("Range must be of type Range")
        return Range(maxOf(min, range.min), minOf(max, range.max))
    }

    override fun difference(range: IRange): List<Range> {
        if (range !is Range) throw KStatException("Range must be of type Range")
        val result = mutableListOf<Range>()
        if (range.min <= min && range.max >= max)
            return result
        if (range.min > min)
            result.add(Range(min, range.min))
        if (range.max < max)
            result.add(Range(range.max, max))
        return result
    }

    override fun toString(): String {
        return "[$min, $max)"
    }

}