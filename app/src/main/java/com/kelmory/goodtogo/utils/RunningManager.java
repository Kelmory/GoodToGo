package com.kelmory.goodtogo.utils;

import com.kelmory.goodtogo.utils.persistence.RunTableItem;

public class RunningManager {
    private boolean started;
    private RunTableItem run;

    public static RunningManager getInstance(boolean start){
        RunningManager runningManager = new RunningManager();
        if(start){
            runningManager.startRunning();
        }
        return runningManager;
    }

    private RunningManager(){
        // private empty method
    }

    public void startRunning(){

    }

    public void stopRunning(){

    }

}
