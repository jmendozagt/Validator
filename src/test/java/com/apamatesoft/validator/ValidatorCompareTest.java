package com.apamatesoft.validator;

import com.apamatesoft.validator.exceptions.InvalidEvaluationException;
import com.apamatesoft.validator.functions.NotPass;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ValidatorCompareTest {

    private final static Validator validator = new Validator.Builder()
            .required()
            .build();

    @Test
    void exceptionIsExpectedIfTextDoesNotMatch() {
        assertThrows(InvalidEvaluationException.class, () -> validator.compareOrFail("abc", "xyz") );
    }

    @Test
    void ExceptionIsExpectedIfTextIsEmpty() {
        assertThrows(InvalidEvaluationException.class, () -> validator.compareOrFail("", "") );
    }

    @Test
    void noExceptionExpectedIfTextMatches() {
        assertDoesNotThrow(() -> validator.compareOrFail("abc", "abc") );
    }

    @Test
    void returnsFalseForStringThatDoesNotMatch() {
        assertFalse(validator.compare("abc", "xyz"));
    }

    @Test
    void returnsTrueForMatchingText() {
        assertTrue(validator.compare("abc", "abc"));
    }

    @Test
    void verifyCallback() {
        final NotPass notPass = mock(NotPass.class);
        validator.onNotPass(notPass);
        validator.compare("abc", "xyz");
        verify(notPass).invoke("Not match");
    }

}
