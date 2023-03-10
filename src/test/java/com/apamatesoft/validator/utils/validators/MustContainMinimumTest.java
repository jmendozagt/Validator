package com.apamatesoft.validator.utils.validators;

import org.junit.jupiter.api.Test;

import static com.apamatesoft.validator.utils.RegularExpression.ALPHABET_LOWERCASE;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static com.apamatesoft.validator.utils.Validators.mustContainMinimum;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MustContainMinimumTest {

    private static final String[] NOT_PERMIT = { null, "", "ABC", "123", "abC" };
    private static final String[] PERMIT = { "abc", "abcd", "aBcDe", "abcABC123..." };
    private static final int MIN = 3;

    @Test
    void test() {
        boolean b = stream(NOT_PERMIT).anyMatch( it -> mustContainMinimum(it, ALPHABET_LOWERCASE, MIN) );
        assertFalse(b);
    }

    @Test
    void permit() {
        boolean b = stream(PERMIT).allMatch( it -> mustContainMinimum(it, ALPHABET_LOWERCASE, MIN) );
        assertTrue(b);
    }

}
