package com.github.polyrocketmatt.kstat

import com.github.polyrocketmatt.kstat.exception.KStatException

interface IRange

/**
 * Represents a range of values between min (inclusive) and max (exclusive).
 *
 * @property minVal The minimum value of the range.
 * @property maxVal The maximum value of the range.
 * @throws KStatException if minVal is greater than or equal to maxVal
 * @throws KStatException if minVal or maxVal is NaN
 *
 * @since 1.0.0
 * @author Matthias Kovacic
 */
abstract class AbstractRange(val minVal: Double, val maxVal: Double) : IRange {

    init {
        if (minVal >= maxVal) throw KStatException("minVal must be less than maxVal")
        if (minVal.isNaN() || maxVal.isNaN()) throw KStatException("minVal and maxVal must not be NaN")
    }

    /**
     * Returns true if the range contains the value.
     *
     * @param value The value to check.
     */
    open operator fun contains(value: Double): Boolean = value >= minVal && value < maxVal

    open operator fun contains(range: AbstractRange): Boolean = range.minVal >= minVal && range.maxVal <= maxVal

    fun overlaps(range: AbstractRange): Boolean = range.minVal < maxVal && range.maxVal > minVal

    fun overlaps(value: Double): Boolean = value < maxVal && value > minVal

    fun isSubsetOf(range: AbstractRange): Boolean = range.minVal <= minVal && range.maxVal >= maxVal

    fun isSupersetOf(range: AbstractRange): Boolean = range.minVal >= minVal && range.maxVal <= maxVal

    abstract fun union(range: IRange): IRange

    abstract fun intersection(range: IRange): IRange

    abstract fun difference(range: IRange): List<IRange>

}

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

}

/**
 * Represents a range of values between min (inclusive) and max (exclusive) of the given list of values.
 *
 * @property values The values of the range.
 *
 * @since 1.0.0
 * @see Range
 * @author Matthias Kovacic
 */
class DisjointRange(vararg val values: Double) : AbstractRange(values.min(), values.max()) {

    private val typedValues = values.toTypedArray()

    override fun contains(value: Double): Boolean = value in typedValues

    override fun contains(range: AbstractRange): Boolean {
        if (range !is DisjointRange) throw KStatException("Range must be of type DisjointRange")
        for (value in range.values)
            if (value !in typedValues) return false
        return true
    }

    override fun union(range: IRange): DisjointRange {
        if (range !is DisjointRange) throw KStatException("Range must be of type DisjointRange")
        return DisjointRange(*values, *range.values)
    }

    override fun intersection(range: IRange): DisjointRange {
        if (range !is DisjointRange) throw KStatException("Range must be of type DisjointRange")
        val result = mutableListOf<Double>()
        for (value in values)
            if (range.contains(value)) result.add(value)
        return DisjointRange(*result.toDoubleArray())
    }

    override fun difference(range: IRange): List<DisjointRange> {
        if (range !is DisjointRange) throw KStatException("Range must be of type DisjointRange")
        val result = mutableListOf<DisjointRange>()
        for (value in values)
            if (!range.contains(value)) result.add(DisjointRange(value))
        return result
    }
}

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

    override fun union(range: IRange): DisjointRange {
        if (range !is SingleRange) throw KStatException("Range must be of type SingleRange")
        return DisjointRange(value, range.value)
    }

    override fun intersection(range: IRange): DisjointRange {
        if (range !is SingleRange) throw KStatException("Range must be of type SingleRange")
        return if (range.value == value) DisjointRange(value) else DisjointRange()
    }

    override fun difference(range: IRange): List<DisjointRange> {
        if (range !is SingleRange) throw KStatException("Range must be of type SingleRange")
        return if (range.value == value) listOf() else listOf(DisjointRange(value))
    }
}