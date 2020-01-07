package com.sha.formvalidator.core.validator.composite

import com.sha.formvalidator.core.validator.TextValidator

/**
 * The AND validator checks if all of the passed validators is returning true.<br></br>
 * Note: the message that will be shown is the one of the first failing validator
 *
 */
class AndValidator: CompositeValidator {

    constructor(vararg validators: TextValidator): super("", *validators)
    constructor(validators: List<TextValidator>): super("", validators)

    override fun isValid(value: String): Boolean {
       val anyFails = validators.firstOrNull { !it.isValid(value) }
        anyFails?.let {
            // error message equals the first failing validator
            this.errorMessage = it.errorMessage
            return false // Remember :) We're acting like an && operator.
        }
        // true if no one fails
        return true
    }
}
