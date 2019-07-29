package com.example.royi.racebet;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class CreateGroupPage extends AppCompatActivity {


    public static int PalPalResultCode = 7171;

    private String amount="";
    private Group group;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)//use sandbox on test
            .clientId(Config.PAYPAL_KEY);

    private Button btnCreateGroup, btnAddFriend;
    private EditText txtGroupName,txtBet,txtDuration,txtPhoneNumber,txtMaxUser;
    User user;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_page);

        //start Paypal service
        Intent intent=new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);


        user = (User)getIntent().getParcelableExtra("user");

        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnAddFriend=findViewById(R.id.btnAddFriend);
        txtGroupName=findViewById(R.id.txtGroupName);
        txtBet=findViewById(R.id.txtBetAmount);
        txtDuration=findViewById(R.id.txtDuration);
        txtPhoneNumber=findViewById(R.id.txtPhoneNumber);
        txtMaxUser=findViewById(R.id.txtMaxUser);



        btnCreateGroup.setOnClickListener(new View.OnClickListener() {//יוצר קבוצה ומכניס לדאטה בייס
            @Override
            public void onClick(View v) {
                /*Group group = new Group(dRef.push().getKey(),txtGroupName.getText().toString(),txtDuration.getText().toString(),null,mAuth.getUid(),null);
                dRef.child(group.getGruopID()).setValue(group);
                Intent intent=new Intent(CreateGroupPage.this,PaypalPage.class);
                startActivity(intent);*/
                amount = txtBet.getText().toString();
                PayPalPayment payPalPayment=new PayPalPayment(new BigDecimal(String.valueOf(amount)),"USD",
                        "The Game started",PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent=new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
                intent.putExtra("user",user);
                startActivityForResult(intent,PalPalResultCode);
            }
        });




        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPhoneNumber.length() != 10) // check if valid phone number
                {
                    Toast.makeText(CreateGroupPage.this,"NOT VALID phone number",Toast.LENGTH_LONG).show();
                }
                // ** neet to:insert data to sql + declares successful

                Toast.makeText(CreateGroupPage.this,"Member joined!:)",Toast.LENGTH_LONG).show();

            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PalPalResultCode)
        {
            //user = (User)data.getParcelableExtra("user");
            if(resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation !=null)
                {
                    try{
                        User usertest = user;
                        String PaymentDetails=confirmation.toJSONObject().toString(4);
                        creatGroup(usertest);
                        /*startActivity(new Intent(this,PaymentDetails.class)
                                .putExtra("PaymentDetails",PaymentDetails)
                                .putExtra("PaymentAmount",amount)
                        );*/
                        /*Intent intent = new Intent(getApplicationContext(),CreateGroupPage.class);
                        intent.putExtra("group",group);*/
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
            else if(resultCode == RESULT_CANCELED)
                Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this,"Invalid",Toast.LENGTH_SHORT).show();
    }

    private void creatGroup(final User user1) {
        final String uniqueId = UUID.randomUUID().toString().replace("-","");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rcbetapi.ddns.net/group",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(CreateGroupPage.this,response,Toast.LENGTH_LONG).show();
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            group = new Group(jsonObject.getString("gid"),jsonObject.getString("gname"),
                                    jsonObject.getString("duration"),jsonObject.getString("maxusers")
                                    ,jsonObject.getString("betprice"),jsonObject.getString("adminid"),null);
                            goToShowGroupPage();
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreateGroupPage.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Calendar c = Calendar.getInstance();
                Date d = c.getTime();
                c.setTime(d);
                c.add(Calendar.DATE,Integer.valueOf(txtDuration.getText().toString()));
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String finalDate = df.format(c.getTime());
                Map<String,String> params = new HashMap<String, String>();
                params.put("token",user1.getToken());
                params.put("id",uniqueId);
                params.put("name", txtGroupName.getText().toString());
                params.put("duration",finalDate);
                params.put("max_users",txtMaxUser.getText().toString());
                params.put("admin_id",user.getUuid());
                params.put("bet_price",txtBet.getText().toString());
                return params;
            }

        };

        AppController.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void goToShowGroupPage() {
        Intent intent = new Intent(this,GroupView.class);
        intent.putExtra("group",group);
        intent.putExtra("user",user);
        startActivity(intent);
    }
}


