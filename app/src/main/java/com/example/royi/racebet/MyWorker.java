package com.example.royi.racebet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyWorker extends Worker implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "History";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TEXT = "text";
    public static final String EXTRA_OUTPUT_MESSAGE = "output_message";
    public static GoogleApiClient googleApiClient;
    private String stepCount = "0";
    public static String id;
    JSONArray arrayUsers;
    public static String token;
    String startTime = "None";



    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    @NonNull
    @Override
    public Result doWork() {

        // Getting Data from MainActivity.
        String title = getInputData().getString(EXTRA_TITLE);
        String text = getInputData().getString(EXTRA_TEXT);

        sendNotification(title, text);

        Data output = new Data.Builder()
                .putString(EXTRA_OUTPUT_MESSAGE, "I have come from MyWorkWithData Class!")
                .build();

        // Sending Data to MainActivity.
        //setOutputData(output);
        updateDate();

        if(token!=null)
            return Result.success();
        else
            return Result.failure();
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayLastWeeksData() {
        if(!startTime.equals("None")) {
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            long startTime1 = 0;
            long endTime = cal.getTimeInMillis();
            try {
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime));
                startTime1 = cal.getTimeInMillis();//date group start
            }
            catch (Exception e) {

            }
            java.text.DateFormat dateFormat = DateFormat.getDateInstance();
            Log.e("History", "Range Start: " + dateFormat.format(startTime1));
            Log.e("History", "Range End: " + dateFormat.format(endTime));

            //Check how many steps were walked and recorded in the last 7 days
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime1, endTime, TimeUnit.MILLISECONDS)
                    .build();

            DataReadResult dataReadResult = Fitness.HistoryApi.readData(googleApiClient, readRequest).await(0, TimeUnit.SECONDS);

            //Used for aggregated data
            if (dataReadResult.getBuckets().size() > 0) {
                Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        showDataSet(dataSet);
                    }
                }
            }
            //Used for non-aggregated data
            else if (dataReadResult.getDataSets().size() > 0) {
                Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    showDataSet(dataSet);
                }
            }
        }
    }

    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.e("Value", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                stepCount = dp.getValue(field).toString();
            }
        }
    }

    public void updateDate(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.SERVERPATH + "groupofusers",
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(CreateGroupPage.this,response,Toast.LENGTH_LONG).show();
                        try{
                            JSONArray jsonObject = new JSONArray(response);
                            arrayUsers = jsonObject;
                            try {
                                for (int i = 0; i < arrayUsers.length(); i++) {
                                    final String gid = arrayUsers.getJSONObject(i).getString("gid");
                                    startTime = arrayUsers.getJSONObject(i).getString("start");
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            //TODO your background code
                                            displayLastWeeksData();
                                            updateUserStepCount(gid);
                                        }
                                    });


                                }
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(),startTime,Toast.LENGTH_LONG).show();

                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){/*
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("gid",group.getGruopID());
                params.put("id",user.getUuid());
                params.put("token",user.getToken());

                return params;
            }*/
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("id",id);
                headers.put("token",token);
                return headers;
            }

        };

        AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void updateUserStepCount(final String gid) {
        if (!startTime.equals("None")) {
            StringRequest stringRequest = new StringRequest(Request.Method.PUT, Config.SERVERPATH + "groupofusers",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(CreateGroupPage.this,response,Toast.LENGTH_LONG).show();
                            try {


                            } catch (Exception e) {
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {/*
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("gid",group.getGruopID());
                params.put("id",user.getUuid());
                params.put("token",user.getToken());

                return params;
            }*/
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("gid", gid);
                    params.put("id", id);
                    params.put("token", token);
                    params.put("steps", stepCount);
                    return params;
                }

            };

            AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}

