package com.nvizo.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel;



/**
 * This class echoes a string called from JavaScript.
 */

public class ReceiptOcr extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {


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