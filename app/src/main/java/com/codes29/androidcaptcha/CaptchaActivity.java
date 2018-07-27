package com.codes29.androidcaptcha;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CaptchaActivity extends AppCompatActivity {

    final String SITE_KEY = "6Le-v2YUAAAAAEuwPmHtqZTcceOra8tvk5NEcGcw";
    final String SECRET_KEY = "6Le-v2YUAAAAADSZ5V3iI7PUAdLwGyChSWW7DCXq";
    public String TAG = "Main Activity";
    public String userResponseToken;

    Button btnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);
        connect();
    }

    public void connect() {
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(this,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was
                                // successful.
                                userResponseToken = response.getTokenResult();
                                Log.d(TAG, "response " + userResponseToken);
                                if (!userResponseToken.isEmpty()) {
                                    new Check().execute();
                                }
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.d(TAG, "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                        } else {
                            // A different, unknown type of error occurred.
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                    }
                });
    }

    public class Check extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CaptchaActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Wait ");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String isSuccess="";
            InputStream is = null;
            String API="https://www.google.com/recaptcha/api/siteverify?";
            String newAPI=API+"secret="+SECRET_KEY+"&response="+userResponseToken;
            Log.d(TAG," API  " +newAPI);
            try {
                URL url = new URL(newAPI);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(8000 /* milliseconds */);
                httpURLConnection.setConnectTimeout(4000 /* milliseconds */);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                // Starts the query
                httpURLConnection.connect();
                int response = httpURLConnection.getResponseCode();
                progressDialog.dismiss();
                System.out.println(response);
                is = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                String result = stringBuilder.toString();
                Log.d("Api", result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    System.out.println("Result Object :  " + jsonObject);
                    isSuccess = jsonObject.getString("success");
                    System.out.println("obj "+isSuccess);

                } catch (Exception e) {
                    Log.d("Exception: ", e.getMessage());
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
            return isSuccess;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s != null) {
                    switch (s) {
                        case "true":
                            return ;
                        case "socketexception":
                            return;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
