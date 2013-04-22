package com.peter.simplecontact;

import android.app.Application;
import android.content.Context;

import com.peter.simplecontact.access.db.PersonService;

public class SimpleContactApplication extends Application {

    private static Context context;
    private static PersonService personService;
    
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        System.out.println("SimpleContactApplication.onCreate()");
    }
    
    public static Context getAppContext(){
        return context;
    }
    
    public static PersonService getDBController(){
        if(null == personService){
            personService = new PersonService(context);
        }
        System.out.println("SimpleContactApplication.getDBController():"+context);
        return personService;
    }
}
