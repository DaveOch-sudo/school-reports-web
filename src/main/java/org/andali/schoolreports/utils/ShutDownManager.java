package org.andali.schoolreports.utils;

import org.andali.schoolreports.config.StageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ShutDownManager {

    @Autowired
    private StageManager stageManager;

    @Autowired
    private ApplicationContext applicationContext;

    public void requestExit() {

        if(!stageManager.canLeaveCurrentView()) {
            return; // user cancelled
        }

    }
}
