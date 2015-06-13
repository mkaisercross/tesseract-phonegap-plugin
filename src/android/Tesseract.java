package com.nvizo.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class Tesseract extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        String libName = "tess"; 
        System.loadLibrary( libName );
        if (action.equals("run")) {
            String image = args.getString(0);
            String message = "Hello from tesseract";
            callbackContext.success(message);
            return true;
        } else {
            return false;
        }
    }

}