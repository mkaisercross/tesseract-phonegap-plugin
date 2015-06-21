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
import java.nio.ByteBuffer;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.content.res.AssetManager;
import java.io.FileOutputStream;


/**
 * This class echoes a string called from JavaScript.
 */

public class ReceiptOcr extends CordovaPlugin {
    private static final String DEFAULT_LANGUAGE = "eng";
    private static final String TessBasePath = "tesseract-ocr/";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("run") ) {
            SharedPreferences settings = this.getSharedPreferences("com.nvizo.receiptocr", 0);
            boolean firstrun = settings.getBoolean("firstrun", true);
            if (firstrun) { 
                //if (!this.isLanguageDataAvailable()) error
                String imageJpegBase64 = args.getString(0);
                //ByteBuffer imageBuffer = ByteBuffer.wrap();
                byte[] imageJpeg = Base64.decode(imageJpegBase64, Base64.DEFAULT);
                BitmapFactory factory = new BitmapFactory();
                Bitmap image = factory.decodeByteArray(imageJpeg, 0, imageJpeg.length);
                //Bitmap image = Bitmap.createBitmap(1024,768);
                //image.copyPixelsFromBuffer(imageBuffer);
                final TessBaseAPI baseApi = new TessBaseAPI();
                baseApi.init(TessBasePath, DEFAULT_LANGUAGE);
                baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
                baseApi.setImage(image);
                final String hOcr = baseApi.getHOCRText(0);
                callbackContext.success(hOcr);
                return true;
            } else {
                return false;
            }
        } else if (action.equals("copyLanguageFiles")) {
            boolean copySuccessful = CopyLanguageFiles();
            return true;
        } else if (action.equals("isExternalStorageWritable")) {
            boolean isWritable = isExternalStorageWritable();
            return true;
        } else {
            return false;
        }
    }

    private boolean CopyLanguageFiles() {
        SharedPreferences settings = this.getSharedPreferences("com.nvizo.receiptocr", 0);
        boolean firstrun = settings.getBoolean("firstrun", true);
        if (firstrun) { 
            if (CopyAssets()) {
                SharedPreferences.Editor e = settings.edit();
                e.putBoolean("firstrun", false);
                e.commit(); 
                return true;
            } else {
                return false;
            }
        }
        return true;
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File createExternalStorageDirectory() {
        if (isExternalStorageWritable()) {
            // Get the directory for the app's private pictures directory. 
            File file = new File(context.getFilesDir() + "/lang" );
            if (!file.mkdirs()) {
                Log.e(LOG_TAG, "Directory not created");
            }
            return file;

        }
    }

    /**
     * -- Copy the file from the assets folder to the sdCard
     * ===========================================================
     **/
    private boolean CopyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        File externalDirectory = createExternalStorageDirectory();
        for (int i = 0; i < files.length; i++) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(files[i]);
                out = new FileOutputStream(externalDirectory + "/ezfinancetracker/lang/" + files[i]);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
        return true;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

    }

}



