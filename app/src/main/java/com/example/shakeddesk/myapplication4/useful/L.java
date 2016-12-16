package com.example.shakeddesk.myapplication4.useful;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * L is a class that helps you log your application better.
 * By using static method: "log" the displayed message will be in the format:
 * [CLASS_NAME] : [METHOD_NAME] : [YOUR_MESSAGE]
 * L also provide a clean way to use a toast, just pass the context
 * <br><br>
 * You can turn the logs off by calling the static method 'setActive(false)'
 * If 'active' is false - no logs will appear
 * Default value is true
 * */
public final class L {
    private static final String APPLICATION_TAG = "MyApplication"; // Change this in accordance to your app's name
    private static boolean active = true;


    /**
     * Prints to log with the format [CLASS] : [METHOD](): msg
     * @param msg The message to be printed to the log
     * */
    public static void log(String msg) {
        print(msg);
    }

    /**
     * Prints to log with the format [CLASS] : [METHOD]
     * */
    public static void log() {
        print(null);
    }

    /**
     * Checks if active is true or false.<br>
     * If true - logs will appear
     * if false - logs will NOT appear
     * */
    public static boolean isActive() {
        return active;
    }

    /**
     * Sets the active value to true/false
     * @param active true - logs will appear
     *               false - logs will not appear<br>
     *
     * */
    public static void setActive(boolean active) {
        L.active = active;
    }

    /**
     * Pop a toast message to current context
     * @param c [@NonNull] Context given from activity/service/etc...
     * @param msg The String message to be displayed
     * */
    public static void toast(@NonNull Context c, String msg) {
        try {
            Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Log.e(APPLICATION_TAG, "Could not use Toast(Context, String). Context given was probably null");
            e.printStackTrace();
        }
    }

    private static void print(String str) {
        int depth = 4; // 4 = Takes the method which called this class's log function, change this if you know what you're doing
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        StackTraceElement current = ste[depth];

        String className = keepEndToFirstDot(current.getClassName());
        String methodName = current.getMethodName();

        Class<?> callingClass;
        String parameters = "";
        try {
            callingClass = Class.forName(current.getClassName());

            Method[] methods = callingClass.getMethods();
            Method calledMethod = null;
            for (Method i : methods) {
                if (i.getName().equals(methodName)){
                    calledMethod = i;
                    break;
                }
            }
            if (calledMethod != null) {
                Class<?> [] parameterTypes = calledMethod.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    parameters = parameters + parameterTypes[i].getSimpleName();
                    if (i == parameterTypes.length-1){
                        parameters = parameters + "";
                    } else {
                        parameters = parameters + ", ";
                    }
                }

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        if (isActive()) { // Will only print if active is set to true
            if (str != null) {
                Log.d(APPLICATION_TAG, className + ": " + methodName + "(" + parameters + "): " + str);
            } else {
                Log.d(APPLICATION_TAG, className + ": " + methodName + "(" + parameters + ") ");
            }
        }
    }

    private static String keepEndToFirstDot(String str) {
        for (int i = str.length()-1; i >= 0; i--) {
            if (str.charAt(i) == '.') {
                str = str.substring(i+1);
                break;
            }
        }
        return str;
    }
}