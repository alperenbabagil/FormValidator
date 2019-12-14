package com.sha.formvalidator.validator

import android.widget.TextView
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.sha.formvalidator.textview.validator.CreditCardValidator
import com.sha.formvalidator.textview.validator.RequiredValidator
import com.sha.formvalidator.textview.validator.TextViewValidator
import org.junit.Before
import org.junit.Test

class RequiredValidatorTest {
    lateinit var validator: TextViewValidator
    lateinit var tv: TextView

    @Before
    fun setup() {
        tv = mock()
        validator = RequiredValidator("Invalid!")
    }

    @Test
    fun validate_valid() {
        given(tv.text).will { "378734493671000" }
        assert(validator.isValid(tv))
    }

    @Test
    fun validate_invalid() {
        given(tv.text).will { "" }
        assert(!validator.isValid(tv))
    }
}