package org.baggle.domain.sample.exception;

import org.baggle.global.error.exception.ConflictException;
import org.baggle.global.error.exception.ErrorCode;

public class SampleDuplicateException extends ConflictException {
    public SampleDuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
