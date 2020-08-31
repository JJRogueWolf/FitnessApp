package org.perfit.fitness.utilities;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadImage extends AsyncTask<ImageUploadData, Void, String> {

    @Override
    protected String doInBackground(ImageUploadData... imageUploadData) {
        uploadGivenImage(imageUploadData[0].userId,imageUploadData[0].exerciseId,imageUploadData[0].imagePath, imageUploadData[0].upLoadServerUri, imageUploadData[0].volleyCallback);
        return "Executed";
    }

    // Think this was directly lifted from https://stackoverflow.com/questions/40260243/how-to-upload-an-image-from-android-to-php-using-asynctask
    public void uploadGivenImage(String userId, String exerciseId, String imagePath, String upLoadServerUri, final VolleyCallback volleyCallback) {
        try {
            String sourceFileUri = imagePath;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            if (sourceFile.isFile()) {
                try {
                    // upLoadServerUri = APIManager.UploadImageFront;

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(
                            sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE",
                            "multipart/form-data");
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("debug", sourceFileUri);
                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; userId=\"" + userId + "\";exerciseId=\"" + exerciseId + "\";name=\"img_name\";filename=\""
                            + sourceFileUri + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,
                                bufferSize);

                    }

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);

                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    // Responses from the server (code and message)
                    int serverResponseCode = conn.getResponseCode();
                    // String serverResponseMessage = conn.getResponseMessage();
                    // Delete file

                    if (serverResponseCode == 200) {
                        JSONObject dummy = new JSONObject();
                        volleyCallback.onSuccessResponse(dummy);
                    }
                    else {
                        volleyCallback.onFailureResponse("");
                    }

                    // close the streams //




                } catch (Exception e) {

                    // dialog.dismiss();
                    e.printStackTrace();

                } finally {
                    File deleteFileAfterUpload = new File(imagePath);
                    deleteFileAfterUpload.delete();
                }
                // dialog.dismiss();

            } // End else block


        } catch (Exception ex) {
            // dialog.dismiss();

            ex.printStackTrace();
        }
    }
}
