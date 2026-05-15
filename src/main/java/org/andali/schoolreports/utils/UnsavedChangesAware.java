package org.andali.schoolreports.utils;

public interface UnsavedChangesAware {
    boolean hasUnsavedChanges();
    String getUnsavedChangesMessage();
    void saveChanges();
}
