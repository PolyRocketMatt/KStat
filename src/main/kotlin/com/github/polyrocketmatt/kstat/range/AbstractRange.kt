package com.github.polyrocketmatt.kstat.range

import com.github.polyrocketmatt.kstat.exception.KStatException

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