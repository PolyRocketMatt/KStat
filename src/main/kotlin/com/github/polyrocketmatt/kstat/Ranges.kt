package com.github.polyrocketmatt.kstat

import com.github.polyrocketmatt.kstat.exception.KStatException

/**
 * Represents a range.
 *
 * @since 1.0.0
 * @author Matthias Kovacic
 */
interface IRange

/**
 * Represents a range of values between min (inclusive) and max (exclusive).
 *
 * @property minVal The minimum value of the range.
 * @property maxVal The maximum value of the range.
 * @throws KStatException If minVal is greater than or equal to maxVal
 * @throws KStatException If minVal or maxVal is NaN
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
     * @param value The value to check
     * @return true If the range contains the value
     */
    open operator fun contains(value: Double): Boolean = value >= minVal && value < maxVal

    /**
     * Returns true if the range contains the range.
     *
     * @param range The range to check
     * @return true If the range contains the range
     */
    open operator fun contains(range: AbstractRange): Boolean = range.minVal >= minVal && range.maxVal <= maxVal

    /**
     * Returns true if this range overlaps the given range in terms of min and max values.
     *
     * @param range The range to check
     * @return True if this range overlaps the given range in terms of min and max values
     */
    fun overlaps(range: AbstractRange): Boolean = range.minVal < maxVal && range.maxVal > minVal

    /**
     * Returns true if this range overlaps the given value.
     *
     * @param value The value to check
     * @return True if this range overlaps the given value
     */
    fun overlaps(value: Double): Boolean = value < maxVal && value > minVal

    /**
     * Returns true if this range is a subset of the given range in terms of min and max values.
     *
     * @param range The range to check
     * @return True if this range is a subset of the given range
     */
    fun isSubsetOf(range: AbstractRange): Boolean = range.minVal <= minVal && range.maxVal >= maxVal

    /**
     * Returns true if this range is a superset of the given range in terms of min and max values.
     *
     * @param range The range to check
     * @return True if this range is a superset of the given range
     */
    fun isSupersetOf(range: AbstractRange): Boolean = range.minVal >= minVal && range.maxVal <= maxVal

    /**
     * Get the union of this range and the given range.
     *
     * @param range The range to union with
     * @return The union of this range and the given range
     */
    abstract fun union(range: IRange): IRange

    /**
     * Get the intersection of this range and the given range.
     *
     * @param range The range to intersect with
     * @return The intersection of this range and the given range
     */
    abstract fun intersection(range: IRange): IRange

    /**
     * Get the difference of this range and the given range.
     *
     * @param range The range to difference with
     * @return The difference of this range and the given range
     */
    abstract fun difference(range: IRange): List<IRange>

    override fun toString(): String {
        return "[$minVal, $maxVal)"
    }

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

    override fun toString(): String {
        return "[$min, $max)"
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