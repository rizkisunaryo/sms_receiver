package com.rizkisunaryo.smsreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DELL on 2/5/2016.
 */
public class SmsReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;

        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++)


            {
                String messageReceived = "";
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                messageReceived += msgs[i].getOriginatingAddress ();
                messageReceived += "\n";
                messageReceived += msgs[i].getMessageBody().toString();
                messageReceived += "\n";

                Toast.makeText(context, messageReceived, Toast.LENGTH_SHORT).show();
                JSONObject post_dict = new JSONObject();

                try {
                    post_dict.put("address" , msgs[i].getOriginatingAddress ());
                    post_dict.put("body", msgs[i].getMessageBody().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (post_dict.length() > 0) {
                    new AsyncT().execute(String.valueOf(post_dict));
//            #call to async class
                }
            }





            //---display the new SMS message---
            System.out.println("\n\n\nmasuk pertama\n" +
                    "\n" +
                    "\n");



            // Get the Sender Phone Number


            String senderPhoneNumber=msgs[0].getOriginatingAddress ();


        }
    }

    class AsyncT extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://192.168.169.9:2904/api/topup/request");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();
//response data
//                Log.i(TAG,JsonResponse);
                try {
//send to post execute
                    return JsonResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;



            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
//                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}