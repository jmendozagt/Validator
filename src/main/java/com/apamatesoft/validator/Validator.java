// TODO:
//  - Crear reglas para números > < =
//  - Regla de fecha
//  - Traducir
//  - RegEgx
package com.apamatesoft.validator;

import com.apamatesoft.validator.exceptions.InvalidEvaluationException;
import com.apamatesoft.validator.messages.Messages;
import com.apamatesoft.validator.messages.MessagesEn;
import com.apamatesoft.validator.functions.NotPass;
import com.apamatesoft.validator.functions.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import static com.apamatesoft.validator.constants.Constants.EMAIL_RE;
import static com.apamatesoft.validator.constants.Constants.NUMBER;

/**
 * <h1>Validator</h1>
 *
 * Validator is a library written in Java, which aims to simplify the validation of Strings by declaring a series of
 * rules.
 *
 * @author ApamateSoft
 * @version 1.1.0
 */
public class Validator implements Cloneable {

    private static Messages messages = new MessagesEn();

    private final List<Rule> rules = new ArrayList<>();
    private NotPass notPass;
    private String notMatchMessage = messages.getNotMatchMessage();

    // <editor-fold defaulted="collapsed" desc="CONSTRUCTORS">
    public Validator() { }

    private Validator(Builder builder) {
        rules.addAll(builder.rules);
        notPass = builder.notPass;
        notMatchMessage = builder.notMatchMessage;
    }
    //</editor-fold>

    /**
     * Sets the default error messages for each of the pre-established rules.
     * @param messages error messages.
     */
    public static void setMessages(Messages messages) {
        if (messages==null) return;
        Validator.messages = messages;
    }

    /**
     * Validate that the String to evaluate meets all the rules.<br>
     * <b>Note:</b> If the String does not meet any rule, the {@link #onNotPass(NotPass)} event will be invoked with the
     * corresponding error message.
     * @param evaluate String to evaluate.
     * @return true: if validation passes.
     */
    public boolean isValid(String evaluate) {
        if (evaluate==null) {
            if (notPass!=null) notPass.invoke(rules.get(0).getMessage());
            return false;
        }
        for (Rule rule: rules) {
            if (!rule.validate(evaluate)) {
                if (notPass!=null) notPass.invoke(rule.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Validate that the String to evaluate meets all the rules.<br>
     * @param evaluate String to evaluate.
     * @throws InvalidEvaluationException Exception thrown if the String to evaluate does not meet any rule.
     */
    public void isValidOrFail(String evaluate) throws InvalidEvaluationException {

        if (evaluate==null)
            throw new InvalidEvaluationException(rules.get(0).getMessage(), null);

        for (Rule rule: rules)
            if (!rule.validate(evaluate))
                throw new InvalidEvaluationException(rule.getMessage(), evaluate);

    }

    /**
     * Validate that both Strings match and that they meet all the rules.
     * <br/><br/>
     * <b>Note:</b>
     * <ul>
     *     <li>
     *         If the Strings do not comply with some of the rules, the onNotPass {@link #onNotPass(NotPass)} event will
     *         be invoked, with the corresponding error message.
     *     </li>
     *     <li>
     *        An error message can be set in case the comparison fails with the {@link #setNotMatchMessage(String)}
     *        method.
     *     </li>
     * <ul/>
     * @param evaluate String to evaluate.
     * @param compare String to compare.
     * @return true: if validation passes.
     */
    public boolean compare(String evaluate, String compare) {
        if (evaluate==null || compare==null) {
            if (notPass!=null) notPass.invoke(notMatchMessage);
            return false;
        }
        if (!evaluate.equals(compare)) {
            if (notPass!=null) notPass.invoke(notMatchMessage);
            return false;
        }
        return isValid(evaluate);
    }

    /**
     * Validate that both Strings match and that they meet all the rules.
     * <br/><br/>
     * <b>Note:</b>
     * <ul>
     *     <li>
     *         If the Strings do not comply with some of the rules, the onNotPass {@link #onNotPass(NotPass)} event will
     *         be invoked, with the corresponding error message.
     *     </li>
     *     <li>
     *        An error message can be set in case the comparison fails with the {@link #setNotMatchMessage(String)}
     *        method.
     *     </li>
     * <ul/>
     * @param evaluate String to evaluate.
     * @param compare String to compare.
     * @throws InvalidEvaluationException Exception thrown if the String to evaluate does not meet any rule.
     */
    public void compareOrFail(String evaluate, String compare) throws InvalidEvaluationException {
        if (evaluate==null || compare==null)
            throw new InvalidEvaluationException(notMatchMessage, evaluate);
        if (!evaluate.equals(compare))
            throw new InvalidEvaluationException(notMatchMessage, evaluate);
        isValidOrFail(evaluate);
    }

    /**
     * Sets the error message to display, in case the String comparison fails in the method
     * {@link #compare(String, String)}.
     * @param message Error message.
     */
    public void setNotMatchMessage(String message) {
        this.notMatchMessage = message;
    }

    //<editor-fold desc="RULES">

    /**
     * Create a validation rule.
     * <br/><br/>
     * <b>Ejemplo:<b/><br>
     * <code>
     * <pre>
     * new Validator().rule("The text is different from 'xxx'", evaluate -> {
     *     return evaluate.equals("xxx");
     * });
     * </pre>
     * </code>
     *
     * @param message Error message.
     * @param validate Function that returns true when the String to evaluate meets the conditions.
     */
    public void rule(String message, Validate validate) {
        rules.add(new Rule(message, validate));
    }

    //<editor-fold desc=" - LENGTH RULES">

    /**
     * Validate that the String to evaluate is different from empty and null.
     * @param message Error message.
     */
    public void required(String message) {
        rule(message, it -> it!=null && !it.isEmpty());
    }

    /**
     * Validate that the String to evaluate is different from empty and null.
     */
    public void required() {
        required(messages.getRequireMessage());
    }

    /**
     * Validate that the String to evaluate has the exact length of characters to the condition.
     * @param condition character length.
     * @param message Error message.
     */
    public void length(int condition, String message) {
        rule(String.format(message, condition), it -> it.length()==condition);
    }

    /**
     * Validate that the String to evaluate has the exact length of characters to the condition.
     * @param condition character length.
     */
    public void length(int condition) {
        length(condition, messages.getLengthMessage());
    }

    /**
     * Validate that the String to evaluate has a minimum character length to the condition.
     * @param condition Minimum character length.
     * @param message Error message.
     */
    public void minLength(int condition, String message) {
        rule(String.format(message, condition), it -> it.length()>=condition);
    }

    /**
     * Validate that the String to evaluate has a minimum character length to the condition.
     * @param condition Minimum character length.
     */
    public void minLength(int condition) {
        minLength(condition, messages.getMinLengthMessage());
    }

    /**
     * Validate that the String to evaluate has a maximum length of characters to the condition.
     * @param condition maximum character length.
     * @param message Error message.
     */
    public void maxLength(int condition, String message) {
        rule(String.format(message, condition), it -> it.length()<=condition);
    }

    /**
     * Validate that the String to evaluate has a maximum length of characters to the condition.
     * @param condition maximum character length.
     */
    public void maxLength(int condition) {
        maxLength(condition, messages.getMaxLengthMessage());
    }

    //</editor-fold>

    //<editor-fold desc=" - FORMAT RULES">

    /**
     * Validate that the String has an email format
     * @param message Error message.
     */
    public void email(String message) {
        rule(message, it -> Pattern.compile(EMAIL_RE).matcher(it).find());
    }

    /**
     * Validate that the String has an email format
     */
    public void email() {
        email(messages.getEmailMessage());
    }

    /**
     * Validate that the String is in numeric format.
     * @param message Error message.
     */
    public void numericFormat(String message) {
        rule(message, it -> {
            try {
                double number = Double.parseDouble(it);
                return !Double.isNaN(number);
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Validate that the String is in numeric format.
     */
    public void numericFormat() {
        numericFormat(messages.getNumericFormat());
    }

    //</editor-fold>

    //<editor-fold desc=" - CONTENT RULES">

    /**
     * Validate that the String only contains characters included in the condition String.
     * @param condition String with allowed characters.
     * @param message  Error message.
     */
    public void shouldOnlyContain(String condition, String message) {
        rule(String.format(message, condition), it -> {
            if (it.isEmpty()) return false;
            for (char a: it.toCharArray()) {
                if (!condition.contains(String.valueOf(a))) return false;
            }
            return true;
        });
    }

    /**
     * Validate that the String only contains characters included in the condition String.
     * @param condition String with allowed characters.
     */
    public void shouldOnlyContain(String condition) {
        shouldOnlyContain(condition, messages.getShouldOnlyContainMessage());
    }

    /**
     * Validate that the String contains only numeric characters.
     * @param message Error message.
     */
    public void onlyNumbers(String message) {
        shouldOnlyContain(NUMBER, message);
    }

    /**
     * Validate that the String contains only numeric characters.
     */
    public void onlyNumbers() {
        onlyNumbers(messages.getOnlyNumbersMessage());
    }

    /**
     * Validate that the String does not contain any character included in the String of the condition.
     * @param condition String with invalid characters.
     * @param message Error message.
     */
    public void notContain(String condition, String message) {
        rule(String.format(message, condition), it -> {
            if (it.isEmpty()) return false;
            for (char a: condition.toCharArray()) {
                if (it.contains(a+"")) return false;
            }
            return true;
        });
    }

    /**
     * Validate that the String does not contain any character included in the String of the condition.
     * @param condition String with invalid characters.
     */
    public void notContain(String condition) {
        notContain(condition, messages.getNotContainMessage());
    }

    /**
     * Validates that the String contains at least one character included in the String of the condition.
     * @param condition String with desired characters.
     * @param message Error message.
     */
    public void mustContainOne(String condition, String message) {
        rule(String.format(message, condition), it -> {
            for (char a: condition.toCharArray()) {
                if (it.contains(a+"")) return true;
            }
            return false;
        });
    }

    /**
     * Validates that the String contains at least one character included in the String of the condition.
     * @param condition String with desired characters.
     */
    public void mustContainOne(String condition) {
        mustContainOne(condition, messages.getMustContainOneMessage());
    }

    //</editor-fold>

    //</editor-fold>

    /**
     * Event that is invoked when some rule is not fulfilled.
     * @param notPass Function with the error message.
     */
    public void onNotPass(NotPass notPass) {
        this.notPass = notPass;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Create a copy of the Validator object.
     * @return copy of Validator.
     */
    public Validator copy() {
        try {
            return (Validator) this.clone();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Class that allows to build a Validator in a sequential and centralized way.
     */
    public static class Builder {

        private final List<Rule> rules = new ArrayList<>();
        private NotPass notPass;
        private String notMatchMessage = messages.getNotMatchMessage();

        /**
         * Sets the error message to display, in case the String comparison fails in the method
         * {@link #compare(String, String)}.
         * @param message Error message.
         * @return Builder
         */
        public Builder setNotMatchMessage(String message) {
            this.notMatchMessage = message;
            return this;
        }

        //<editor-fold desc="RULES">

        /**
         * Create a validation rule.
         * <br/><br/>
         * <b>Ejemplo:<b/><br>
         * <code>
         * <pre>
         * new Validator().rule("The text is different from 'xxx'", evaluate -> {
         *     return evaluate.equals("xxx");
         * });
         * </pre>
         * </code>
         *
         * @param message Error message.
         * @param validate Function that returns true when the String to evaluate meets the conditions.
         * @return Builder
         */
        public Builder rule(String message, Validate validate) {
            rules.add(new Rule(message, validate));
            return this;
        }

        //<editor-fold desc=" - LENGTH RULES">

        /**
         * Validate that the String to evaluate is different from empty and null.
         * @param message Error message.
         * @return Builder
         */
        public Builder required(String message) {
            return rule(message, it -> it!=null && !it.isEmpty());
        }

        /**
         * Validate that the String to evaluate is different from empty and null.
         * @return Builder
         */
        public Builder required() {
            return required(messages.getRequireMessage());
        }

        /**
         * Validate that the String to evaluate has the exact length of characters to the condition.
         * @param condition character length.
         * @param message Error message.
         * @return Builder
         */
        public Builder length(int condition, String message) {
            return rule(String.format(message, condition), it -> it.length()==condition);
        }

        /**
         * Validate that the String to evaluate has the exact length of characters to the condition.
         * @param condition character length.
         * @return Builder
         */
        public Builder length(int condition) {
            return length(condition, messages.getLengthMessage());
        }

        /**
         * Validate that the String to evaluate has a minimum character length to the condition.
         * @param condition Minimum character length.
         * @param message Error message.
         * @return Builder
         */
        public Builder minLength(int condition, String message) {
            return rule(String.format(message, condition), it -> it.length()>=condition);
        }

        /**
         * Validate that the String to evaluate has a minimum character length to the condition.
         * @param condition Minimum character length.
         * @return Builder
         */
        public Builder minLength(int condition) {
            return minLength(condition, messages.getMinLengthMessage());
        }

        /**
         * Validate that the String to evaluate has a maximum length of characters to the condition.
         * @param condition maximum character length.
         * @param message Error message.
         * @return Builder
         */
        public Builder maxLength(int condition, String message) {
            return rule(String.format(message, condition), it -> it.length()<=condition);
        }

        /**
         * Validate that the String to evaluate has a maximum length of characters to the condition.
         * @param condition maximum character length.
         * @return Builder
         */
        public Builder maxLength(int condition) {
            return maxLength(condition, messages.getMaxLengthMessage());
        }

        //</editor-fold>

        //<editor-fold desc=" - FORMAT RULES">

        /**
         * Validate that the String has an email format
         * @param message Error message.
         * @return Builder
         */
        public Builder email(String message) {
            return rule(message, it -> Pattern.compile(EMAIL_RE).matcher(it).find());
        }

        /**
         * Validate that the String has an email format
         * @return Builder
         */
        public Builder email() {
            return email(messages.getEmailMessage());
        }

        /**
         * Validate that the String is in numeric format.
         * @param message Error message.
         * @return Builder
         */
        public Builder numericFormat(String message) {
            return rule(message, it -> {
                try {
                    double number = Double.parseDouble(it);
                    return !Double.isNaN(number);
                } catch (Exception e) {
                    return false;
                }
            });
        }

        /**
         * Validate that the String is in numeric format.
         * @return Builder
         */
        public Builder numericFormat() {
            return numericFormat(messages.getNumericFormat());
        }

        //</editor-fold>

        //<editor-fold desc=" - CONTENT RULES">

        /**
         * Validate that the String only contains characters included in the condition String.
         * @param condition String with allowed characters.
         * @param message  Error message.
         * @return Builder
         */
        public Builder shouldOnlyContain(String condition, String message) {
            return rule(String.format(message, condition), it -> {
                if (it.isEmpty()) return false;
                for (char a: it.toCharArray()) {
                    if (!condition.contains(String.valueOf(a))) return false;
                }
                return true;
            });
        }

        /**
         * Validate that the String only contains characters included in the condition String.
         * @param condition String with allowed characters.
         * @return Builder
         */
        public Builder shouldOnlyContain(String condition) {
            return shouldOnlyContain(condition, messages.getShouldOnlyContainMessage());
        }

        /**
         * Validate that the String contains only numeric characters.
         * @param message Error message.
         * @return Builder
         */
        public Builder onlyNumbers(String message) {
            return shouldOnlyContain(NUMBER, message);
        }

        /**
         * Validate that the String contains only numeric characters.
         * @return Builder
         */
        public Builder onlyNumbers() {
            return onlyNumbers(messages.getOnlyNumbersMessage());
        }

        /**
         * Validate that the String does not contain any character included in the String of the condition.
         * @param condition String with invalid characters.
         * @param message Error message.
         * @return Builder
         */
        public Builder notContain(String condition, String message) {
            return rule(String.format(message, condition), it -> {
                if (it.isEmpty()) return false;
                for (char a: condition.toCharArray()) {
                    if (it.contains(a+"")) return false;
                }
                return true;
            });
        }

        /**
         * Validate that the String does not contain any character included in the String of the condition.
         * @param condition String with invalid characters.
         * @return Builder
         */
        public Builder notContain(String condition) {
            return notContain(condition, messages.getNotContainMessage());
        }

        /**
         * Validates that the String contains at least one character included in the String of the condition.
         * @param condition String with desired characters.
         * @param message Error message.
         * @return Builder
         */
        public Builder mustContainOne(String condition, String message) {
            return rule(String.format(message, condition), it -> {
                for (char a: condition.toCharArray()) {
                    if (it.contains(a+"")) return true;
                }
                return false;
            });
        }

        /**
         * Validates that the String contains at least one character included in the String of the condition.
         * @param condition String with desired characters.
         * @return Builder
         */
        public Builder mustContainOne(String condition) {
            return mustContainOne(condition, messages.getMustContainOneMessage());
        }

        //</editor-fold>

        //</editor-fold>

        /**
         * Event that is invoked when some rule is not fulfilled.
         * @param notPass Function with the error message.
         * @return Builder
         */
        public Builder onNotPass(NotPass notPass) {
            this.notPass = notPass;
            return this;
        }

        /**
         * Build the Validator
         * @return Validator
         */
        public Validator build() {
            return new Validator(this);
        }

    }

}