package org.andali.schoolreports.event;

import org.andali.schoolreports.model.Student;
import org.springframework.context.ApplicationEvent;

public record StudentSavedToClassEvent (Student student) {

}
