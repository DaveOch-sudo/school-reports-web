package org.andali.schoolreports.utils;

import lombok.Getter;

public class IncompleteMarksheetException extends RuntimeException {
    @Getter
    private final long missingCount;

    public IncompleteMarksheetException(long missingCount) {
        super("Not all subject marksheets for the class have been submitted");
        this.missingCount = missingCount;
    }

}
