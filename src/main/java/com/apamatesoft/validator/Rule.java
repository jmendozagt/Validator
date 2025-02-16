package com.apamatesoft.validator;

import com.apamatesoft.validator.functions.Validate;

public class Rule {

    private final String message;
    private final Validate validate;

    public Rule(String message, Validate validate) {
        this.validate = validate;
        this.message = message;
    }

    public boolean validate(String evaluate) {
        return validate.invoke(evaluate);
    }

    public String getMessage() {
        return message;
    }

}