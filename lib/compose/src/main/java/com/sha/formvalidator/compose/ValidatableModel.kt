package com.sha.formvalidator.compose

import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.ContextAmbient
import com.sha.formvalidator.core.validator.Validator

class ValidationModel<V>(validator: Validator<V>): AbsValidationModel<V>() {
    override val validator: Validator<V> by lazy { validator }
}

abstract class AbsValidationModel<V>: ValidatableModel<V> {
    override var value: V? = null
        set(value) {
            field = value
            validator.value = value
            validate(false)
        }
    override var isValid: Boolean = false
    override var ignoreInitialValidation: Boolean = true
    override var errorMessage: String = ""
        get() = validator.errorMessage
        set(value) {
            field = value
            validator.errorMessage = value
        }
    override var validateOnChange: Boolean = false
    override var overrideValidateOnChangeOnce: Boolean = false
    override var recompose: () -> Unit = {}
    override var errorTextRes: Int = -1
        set(value) {
            field = value
            val context = +ambient(ContextAmbient)
            errorMessage = context.getString(value)
        }

    override var tmpError: String = ""

    override var onValidate: ((Boolean) -> Unit)? = null

    override var isMandatory: Boolean = true

    override var isDisabled: Boolean = false

    override fun validate(overrideValidateOnChangeOnce: Boolean): Boolean {
        if (isDisabled) return true

        if (overrideValidateOnChangeOnce) this.overrideValidateOnChangeOnce = true
        // tmpError is only used when calling showError(), we should remove it here
        // to show the error provided with errorText
        tmpError = ""
        isValid = validator.isValid
        recompose()
        onValidate?.invoke(isValid)
        return isValid
    }

    override fun showError(error: String) {
        isValid = false
        overrideValidateOnChangeOnce = true
        tmpError = error
        recompose()
    }

    override fun matches(
            model: ValidatableModel<V>,
            compositeValidation: CompositeValidation<Validatable>,
            errorMessage: String
    ): ValidatableModel<V> {
        return matches(listOf(model), compositeValidation, errorMessage)
    }

    override fun matches(
            models: List<ValidatableModel<V>>,
                         compositeValidation: CompositeValidation<Validatable>,
                         errorMessage: String
    ): ValidatableModel<V> {
        val list = models.toMutableList().apply { add(0, this@AbsValidationModel) }
        val matchModel = Validation.valueMatch(list, errorMessage)
        compositeValidation + matchModel
        return this
    }
}

interface ValidatableModel<V>: Validatable {
    var value: V?
    val validator: Validator<V>
    var isDisabled: Boolean
    fun createErrorText(): String? {
        if (isDisabled) return null

        if (ignoreInitialValidation) {
            ignoreInitialValidation = false
            return null
        }
        val canValidate = overrideValidateOnChangeOnce || validateOnChange
        overrideValidateOnChangeOnce = false

        if (canValidate && !isValid) {
            // tmpError is only used when calling showError(), and it's removed 
            // in the first call of isValid after showError() is called.
            return if(tmpError.isNotEmpty()) tmpError else errorMessage
        }
        return null
    }
    fun matches(
            model: ValidatableModel<V>,
            compositeValidation: CompositeValidation<Validatable>,
            errorMessage: String): ValidatableModel<V>
    fun matches(
            models: List<ValidatableModel<V>>,
            compositeValidation: CompositeValidation<Validatable>,
            errorMessage: String): ValidatableModel<V>

    fun addTo(compositeValidation: CompositeValidation<Validatable>): ValidatableModel<V> {
        compositeValidation + this
        return this
    }
}

interface Validatable: Recomposable {
    var tmpError: String
    var errorMessage: String
    var errorTextRes: Int
    var isValid: Boolean
    var ignoreInitialValidation: Boolean
    var validateOnChange: Boolean
    var overrideValidateOnChangeOnce: Boolean
    var onValidate: ((Boolean) -> Unit)?
    var isMandatory: Boolean
    fun validate(overrideValidateOnChangeOnce: Boolean = true): Boolean
    fun showError(error: String)
}

interface Recomposable {
    var recompose: () -> Unit
}
