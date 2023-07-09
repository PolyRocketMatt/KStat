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
class DiscreteRange(vararg val values: Double) : AbstractRange(values.min(), values.max()) {

    private val typedValues = values.toTypedArray()

    override fun contains(value: Double): Boolean = value in typedValues

    override fun contains(range: AbstractRange): Boolean {
        if (range !is DiscreteRange) throw KStatException("Range must be of type DisjointRange")
        for (value in range.values)
            if (value !in typedValues) return false
        return true
    }

    override fun union(range: IRange): DiscreteRange {
        if (range !is DiscreteRange) throw KStatException("Range must be of type DisjointRange")
        return DiscreteRange(*values, *range.values)
    }

    override fun intersection(range: IRange): DiscreteRange {
        if (range !is DiscreteRange) throw KStatException("Range must be of type DisjointRange")
        val result = mutableListOf<Double>()
        for (value in values)
            if (range.contains(value)) result.add(value)
        return DiscreteRange(*result.toDoubleArray())
    }

    override fun difference(range: IRange): List<DiscreteRange> {
        if (range !is DiscreteRange) throw KStatException("Range must be of type DisjointRange")
        val result = mutableListOf<DiscreteRange>()
        for (value in values)
            if (!range.contains(value)) result.add(DiscreteRange(value))
        return result
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