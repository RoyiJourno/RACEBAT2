package com.example.royi.racebet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignupPage extends AppCompatActivity {

    EditText userEmail,userPhone,userFullName,userPassword,userRePassword;

    Button btnSigup;

    FirebaseAuth mAuth;
    DatabaseReference dRef;

    User user;

    String userEmailText,userPhoneText,userFullNameText,userPasswordText,userRePasswordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userFullName = findViewById(R.id.userFullName);
        userPassword = findViewById(R.id.userPassword);
        userRePassword = findViewById(R.id.userRePassword);

        btnSigup = findViewById(R.id.btnSignup);

        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();

        btnSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmailText = userEmail.getText().toString();
                userFullNameText = userFullName.getText().toString();
                userPhoneText = userPhone.getText().toString();
                userPasswordText = userPassword.getText().toString();
                userRePasswordText = userRePassword.getText().toString();

                if(userPasswordText.equals(userRePasswordText)){
                    /*mAuth.createUserWithEmailAndPassword(userEmailText,userPasswordText)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                       user = new User(userFullNameText,userPhoneText,null,null);
                                       dRef.child("Users").child(mAuth.getUid()).setValue(user);
                                       startActivity(new Intent(SignupPage.this,MainLandingPage.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Sign Up Filed!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });*/
                    registerUser();
                }
                else
                    Toast.makeText(getApplicationContext(),"Password Doesn't Match!",Toast.LENGTH_LONG).show();

            }
        });

    }

    private void registerUser() {
        StringRequest jsonObjRequest = new StringRequest(

                Request.Method.POST,
                getResources().getString(R.string.base_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //MyFunctions.toastShort(LoginActivity.this, response);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("volley", "Error: " + error.getMessage());
                        error.printStackTrace();
                        /*MyFunctions.croutonAlert(LoginActivity.this,
                                MyFunctions.parseVolleyError(error));
                        loading.setVisibility(View.GONE);*/
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", userEmailText.trim());
                params.put("ppath", userPasswordText.trim());
                params.put("phone", userPhoneText.trim());
                params.put("name", userFullNameText.trim());

                return params;
            }

        };

        AppController.getInstance(this).addToRequestQueue(jsonObjRequest);
    }


}
