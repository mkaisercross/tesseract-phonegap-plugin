package com.nvizo.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.os.Environment;

import android.content.res.Resources;
import android.content.res.AssetManager;
import android.content.SharedPreferences;
import android.content.Context;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;

import android.util.Log;
import android.util.Base64;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;
import android.graphics.BitmapFactory;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel;




/**
 * This class echoes a string called from JavaScript.
 */

public class ReceiptOcr extends CordovaPlugin {
    private static final String DEFAULT_LANGUAGE = "eng";
    private static final String inputBasePath = "tesseract-ocr";
    private static String outputBasePath = "/Documents";
    private static final String LOG_TAG = "ReceiptOcrActivity";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("run") ) {
            Log.e(LOG_TAG, "ReceiptOcr::run()");
            //if (!this.isLanguageDataAvailable()) error
            String imageJpegBase64 = args.getString(0);
            //ByteBuffer imageBuffer = ByteBuffer.wrap();
            byte[] imageJpeg = Base64.decode(imageJpegBase64, Base64.DEFAULT);
            BitmapFactory factory = new BitmapFactory();
            Bitmap image = factory.decodeByteArray(imageJpeg, 0, imageJpeg.length);
            Log.d(LOG_TAG, String.valueOf(imageJpeg.length));
            //Bitmap image = Bitmap.createBitmap(1024,768);
            //image.copyPixelsFromBuffer(imageBuffer);
            final TessBaseAPI baseApi = new TessBaseAPI();
            Log.d(LOG_TAG, "Opening language file: " + outputBasePath + "/tessdata/eng.traineddata");
            baseApi.init(outputBasePath/* + "/tessdata/eng.traineddata"*/, DEFAULT_LANGUAGE);
            baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
            Log.d(LOG_TAG, "Setting image");
            baseApi.setImage(image);
            Log.d(LOG_TAG, "Getting HOCR Text");
            String hocr = baseApi.getHOCRText(0);
            Log.d(LOG_TAG, hocr);
            String escapedHocr = hocr.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"");
            String returnData = "{\n    \"hocr\": \"" + escapedHocr + "\",\n    \"imageData\": \"" + imageJpegBase64 + "\"\n}";

            JSONObject parameter = new JSONObject();
            parameter.put("hocr", hocr);
            parameter.put("imageData", imageJpegBase64);
            PluginResult result = new PluginResult(PluginResult.Status.OK, parameter);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);

            //callbackContext.success(returnData);
            return true;
        } else if (action.equals("copyLanguageFiles")) {
            Log.e(LOG_TAG, "ReceiptOcr::copyLanguagesFiles()");
            boolean copySuccessful = copyLanguageFiles();
            if (copyLanguageFiles()) callbackContext.success("true");
            else callbackContext.success("false");
            return true;
        } else {
            return false;
        }
    }

    public boolean copyLanguageFiles() {
        Context context=this.cordova.getActivity().getApplicationContext();         
        outputBasePath = context.getExternalFilesDir(null).toString();
        copy_r(inputBasePath, outputBasePath);
        Log.e(LOG_TAG, "Language files copied to external memory");
        return true;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * -- Copy the file from the assets folder to the sdCard
     * ===========================================================
     **/


    public void copy_r(String path, String outputPath) {
        Context context=this.cordova.getActivity().getApplicationContext();         
        AssetManager assetManager = context.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copy(path, outputPath);
            } else {
                File dir = new File(outputPath);
                if (!dir.exists())
                    dir.mkdir();
                for (int i = 0; i < assets.length; ++i) {
                    copy_r(path + "/" + assets[i], outputPath + "/" + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e(LOG_TAG, "I/O Exception", ex);
        }
    }

    private void copy(String filename, String outputFilename) {
        Context context=this.cordova.getActivity().getApplicationContext();         
        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            out = new FileOutputStream(outputFilename);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }


    /*
    private boolean CopyAssets() {
        Context context=this.cordova.getActivity().getApplicationContext();         
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(TessBasePath);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            Log.e(LOG_TAG, filename.toString());
        }

        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(context.getExternalFilesDir(null), filename);
                Log.e(LOG_TAG, outFile.toString());
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e(LOG_TAG, "Failed to copy asset file: " + filename, e);
                return false;
            }     
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    } 
                }
            }  
        }
        return true;

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    */


}



