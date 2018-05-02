package com.example.leeicheng.dogbook.main;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeneralTask extends AsyncTask<String, Integer, String> {
    private final int CONNECT_SUCCESS = 200;
    private String url, outStream;

    public GeneralTask(String url, String outStream) {
        this.url = url;
        this.outStream = outStream;
    }

    @Override
    protected String doInBackground(String... strings) {
        return dataHelper();
    }

    String dataHelper() {
        String TAG = "資料服務";
        HttpURLConnection connection = null;
        StringBuilder InStream = new StringBuilder();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "utf-8");

            OutputStreamWriter outputWriter = new OutputStreamWriter(connection.getOutputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(outputWriter);
            bufferedWriter.write(outStream);
            Log.d(TAG, bufferedWriter.toString());
            bufferedWriter.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == CONNECT_SUCCESS) {
                InputStreamReader inputReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputReader);

                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    InStream.append(line);
                }

            } else {
                Log.d(TAG,"responseCode = "+responseCode);
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        Log.d(TAG,"input"+InStream);

        return InStream.toString();
    }
}
