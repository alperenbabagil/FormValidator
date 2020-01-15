package com.sha.formvalidator.validator

import com.sha.formvalidator.core.validator.NumericRangeValidator
import com.sha.formvalidator.core.validator.TextValidator
import org.junit.Before
import org.junit.Test

class NumericLengthRangeValidatorTest {
    lateinit var validator: TextValidator

    @Before
    fun setup() {
        validator = NumericRangeValidator(1, 5, "Invalid!")
    }

    @Test
    fun validate_valid() {
        assert(validator.isValid("1"))
    }

    @Test
    fun validate_invalid() {
        assert(!validator.isValid("6"))
    }
}