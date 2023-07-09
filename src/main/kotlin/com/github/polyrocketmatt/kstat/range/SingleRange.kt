package com.github.polyrocketmatt.kstat.range

import com.github.polyrocketmatt.kstat.exception.KStatException

/**
 * Represents a singular value as a range.
 *
 * @property value The minimum value of the range.
 *
 * @since 1.0.0
 * @see Range
 * @author Matthias Kovacic
 */
class SingleRange(val value: Double) : AbstractRange(value, Double.POSITIVE_INFINITY) {

    override fun contains(value: Double): Boolean = value == this.value

    override fun contains(range: AbstractRange): Boolean {
        if (range !is SingleRange) throw KStatException("Range must be of type SingleRange")
        return range.value == value
    }

    override fun union(range: IRange): DiscreteRange {
        if (range !is SingleRange) throw KStatException("Range must be of type SingleRange")
        return DiscreteRange(value, range.value)
    }

    override fun intersection(range: IRange): DiscreteRange {
        if (range !is SingleRange) throw KStatException("Range must be of type SingleRange")
        return if (range.value == value) DiscreteRange(value) else DiscreteRange()
    }

    override fun difference(range: IRange): List<DiscreteRange> {
        if (range !is SingleRange) throw KStatException("Range must be of type SingleRange")
        return if (range.value == value) listOf() else listOf(DiscreteRange(value))
    }

    /**
     * Returns the value of this range as a double.
     *
     * @return The value of this range as a double
     */
    fun toDouble(): Double = value

    override fun toString(): String {
        return "[$value]"
    }
}