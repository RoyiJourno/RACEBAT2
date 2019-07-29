package com.example.royi.racebet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Subscription;

import org.json.JSONObject;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static android.accounts.AccountManager.get;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "BasicRecordingApi";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private static final int RC_SIGN_IN = 1001;//code for google sign in
    EditText userPhone;
    FirebaseAuth userAuth;
    User user;
    private GoogleSignInClient mGoogleSignInClient;
    //check

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FitnessOptions fitnessOptions =
                FitnessOptions.builder().addDataType(DataType.TYPE_ACTIVITY_SAMPLES).build();

        // Check if the user has permissions to talk to Fitness APIs, otherwise authenticate the
        // user and request required permissions.
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            subscribe();
        }

        jumpIfRegisterd(); //if the user already sign-in.

        //google signin
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);




        userPhone = findViewById(R.id.userPhoneInput);


        findViewById(R.id.sign_in_button).setOnClickListener(this);

        userAuth = FirebaseAuth.getInstance();

    }

    /*
    * method to check if user is logged in,
    * if yes jump to MainLendingPage.
    * */
    private void jumpIfRegisterd() {
        if(isFinishing())
            return;
        Boolean registered;
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        registered = sharedPref.getBoolean("Registered",false);

        if (!registered)
        {
            //Do Nothing!
        }else {
            startActivity(new Intent(this,MainLandingPage.class));
            finish();
        }
    }



    /**
     * Subscribes to an available {@link DataType}. Subscriptions can exist across application
     * instances (so data is recorded even after the application closes down).  When creating
     * a new subscription, it may already exist from a previous invocation of this app.  If
     * the subscription already exists, the method is a no-op.  However, you can check this with
     * a special success code.
     */
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        // [START subscribe_to_datatype]
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_ACTIVITY_SAMPLES)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                });
        // [END subscribe_to_datatype]
    }

    /**
     * Cancels the ACTIVITY_SAMPLE subscription by calling unsubscribe on that {@link DataType}.
     */
    private void cancelSubscription() {
        final String dataTypeStr = DataType.TYPE_ACTIVITY_SAMPLES.toString();

        // Invoke the Recording API to unsubscribe from the data type and specify a callback that
        // will check the result.
        // [START unsubscribe_from_datatype]
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .unsubscribe(DataType.TYPE_ACTIVITY_SAMPLES);
        // [END unsubscribe_from_datatype]
    }

    public int getStepsFromCertainDate(Date date){

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.setTime(date);
        long startTime = cal.getTimeInMillis();
        //TODO : DataReadRequest readRequest
        return 0;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                if(!userPhone.getText().toString().isEmpty())
                    signIn();
                else
                    Toast.makeText(getApplicationContext(),"insert phone",Toast.LENGTH_LONG).show();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        }
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            final String token = account.getIdToken();
            final String uniqueId = UUID.randomUUID().toString().replace("-","");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rcbetapi.ddns.net/user",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                user = new User(token,jsonObject.getString("id"),
                                        URLDecoder.decode(jsonObject.getString("name"),"UTF-8")
                                        ,jsonObject.getString("phone"),
                                        jsonObject.getString("ppath"),null);
                                goToMainLandingPage();
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("token",token);
                    params.put("id",uniqueId);
                    params.put("name", account.getDisplayName());
                    params.put("email", account.getEmail());
                    params.put("ppath", account.getPhotoUrl().toString());
                    params.put("phone",userPhone.getText().toString());
                    return params;
                }

            };
            AppController.getInstance(this).addToRequestQueue(stringRequest);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            //user = new User(token,uniqueId,account.getDisplayName(),userPhone.getText().toString(),
            //        account.getPhotoUrl().toString(),null);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void goToMainLandingPage() {
        Intent intent = new Intent(this,MainLandingPage.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }
}
