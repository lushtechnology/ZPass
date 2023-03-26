package com.lushtechnology.zpass.store;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.lushtechnology.zpass.AppsFragment;
import com.lushtechnology.zpass.MainActivity;
import com.lushtechnology.zpass.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadWorker extends Worker {
    private static final String TAG = "DownloadWorker";
    private static final String URL_KEY = "url";
    private static final String FILENAME_KEY = "filename";

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String url = getInputData().getString(URL_KEY);

        try {
            URL downloadUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Server returned HTTP " + responseCode);
                return Result.failure();
            }

            int fileLength = connection.getContentLength();
            String fileName = url.substring(url.lastIndexOf('/') + 1);

            String outputFile = getApplicationContext().getFilesDir() + "/" + fileName;
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(outputFile);

            byte[] data = new byte[100 * 1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);

                int progress = (int) ((total * 100L) / fileLength);

                setProgressAsync(new Data.Builder().putInt("progress", progress).build());
            }

            output.flush();
            output.close();
            input.close();

            Log.i(TAG, "File downloaded successfully");
            return Result.success(new Data.Builder().putString("filePath", outputFile).build());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid URL");
            return Result.failure();
        } catch (IOException e) {
            Log.e(TAG, "IO error while downloading");
            return Result.failure();
        }
    }
}
