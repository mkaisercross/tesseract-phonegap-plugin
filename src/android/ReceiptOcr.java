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

import android.graphics.Bitmap;
import android.util.Base64;


/**
 * This class echoes a string called from JavaScript.
 */

public class ReceiptOcr extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {


        if (action.equals("run")) {
            String imageStringBase64 = args.getString(0);
            byte[] imageString = decode(imageStringBase64, DEFAULT);
            Bitmap image;
            image.copyPixelsFromBuffer(imageString);

            final TessBaseAPI baseApi = new TessBaseAPI();
            baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
            baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
            baseApi.setImage(image);
            final String hOcr = baseApi.getHOCRText(0);


            callbackContext.success(hOcr);
            return true;
        } else {
            return false;
        }
    }

}