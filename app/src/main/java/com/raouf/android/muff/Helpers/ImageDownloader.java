package com.raouf.android.muff.Helpers;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

/**
 * helper to download image from the server and store it.
 * to use construct an object passing the parameters as expected then call {@link #download()}
 * 
 * @author Raouf
 */
class ImageDownloader extends AsyncTask<String, Integer, String>
{

    /**
     * url of image
     */
    private String url;
    /**
     * file to store image to
     */
    private File file;
    /**
     * listener for operation callbacks
     */
    private OnImageDownloaderListener onImageDownloaderListener;

    /**
     * @param url image url
     * @param file file to store image to
     * @param onImageDownloaderListener interface callback.
     */
    ImageDownloader(@NonNull String url, @NonNull File file , @NonNull OnImageDownloaderListener onImageDownloaderListener) {
        this.url = url;
        this.file = file;
        this.onImageDownloaderListener = onImageDownloaderListener;
    }

    /**
     * called when {@link #executeOnExecutor(Executor, Object[])} is called, runs in the background thread.
     * download the image from {@link #url} then store it.
     * returns null if success , else returns error message.
     */
    @Override
    protected String doInBackground(String... data)
    {
        try
        {
            // download image
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.connect();
            Bitmap bitmap = BitmapFactory.decodeStream((connection).getInputStream(),null, new BitmapFactory.Options());
            // store image
            saveBitmapFile(bitmap);
            // success
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // fail
            return e.getMessage();
        }
    }

    /**
     * called when the background process is finished. uses UI thread.
     * if operation was success it will call onSuccess with file.
     * if fails calls onFail with error message.
     */
    @Override
    protected void onPostExecute(String error)
    {
        // if proccess canceled set bitmap to null
	    if (isCancelled() || error != null){
    	    onImageDownloaderListener.onFail(error);
	    } else {
            onImageDownloaderListener.onSuccess(file);
        }
    }

    /**
     * executes download operation.
     */
    public void download(){
        executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    /**
     * method to compress then save a bitmap into to a {@link #file} in storage..
     * @param bitmap to be stored.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveBitmapFile(Bitmap bitmap) throws Exception {
        file.getParentFile().mkdirs();
        // create outputstream array
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        // set bitmap properties and resultion and convert it to the bytes array
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        // create the file path of the cover image
        file.createNewFile();
        // file output object
        FileOutputStream fos = new FileOutputStream(file);
        // write the bytes array of image to the file
        fos.write(array.toByteArray());
        // close the file
        fos.close();
    }

    /**
     * interface to implement the callbacks of the  operation of {@link ImageDownloader}
     */
    interface OnImageDownloaderListener{
        /**
         * called when operation is a success.
         * @param file file the image stored to.
         */
        void onSuccess(File file);

        /**
         * called when operation fails
         * @param error error message
         */
        void onFail(String error);
    }
}