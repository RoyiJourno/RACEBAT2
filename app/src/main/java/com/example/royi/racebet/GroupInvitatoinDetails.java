package com.example.royi.racebet;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupInvitatoinDetails extends AppCompatActivity {
    public static int PalPalResultCode = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)//use sandbox on test
            .clientId(Config.PAYPAL_KEY);

    private ListView lv;
    public static ArrayList<ModelGroupView> modelArrayList;
    private CustomGroupsOfUsers customAdapter;
    private TextView groupName,groupDuration,groupBetPrice,infoOfUser;
    private Group group;
    private User user;
    private JSONArray arrayUsers;
    private String invitationName;
    private Button btnApprove,btnDismiss;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invitatoin_details);


        //start Paypal service
        Intent intent=new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        groupBetPrice = findViewById(R.id.betPriceText);
        groupDuration = findViewById(R.id.durationGroupText);
        groupName = findViewById(R.id.groupNameText);
        infoOfUser = findViewById(R.id.InfoOfUser);
        btnApprove = findViewById(R.id.approveGroup);
        btnDismiss = findViewById(R.id.dismissGroup);

        group = getIntent().getParcelableExtra("group");
        user = getIntent().getParcelableExtra("user");
        invitationName = getIntent().getStringExtra("invitatoinName");

        infoOfUser.setText("Hello, "+user.getName()+"\n\nbelow is your invitation details from "+
                invitationName);

        groupName.setText("Group Name\n"+group.getName());
        groupDuration.setText("End Date\n"+group.getDurtion().substring(0,10));
        groupBetPrice.setText("Bet Price\n"+group.getBetPrice());

        initListGroup();

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Config.SERVERPATH + "usersingroup",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Toast.makeText(CreateGroupPage.this,response,Toast.LENGTH_LONG).show();
                                try{
                                    finish();
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(GroupInvitatoinDetails.this,error.toString(),Toast.LENGTH_LONG).show();
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
                    public Map<String, String> getParams() {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("gid",group.getGruopID());
                        params.put("id",user.getUuid());
                        params.put("token",user.getToken());
                        return params;
                    }

                };

                AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }
        });

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //paypal
                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(group.getBetPrice()), "USD",
                        "The Game started", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                intent.putExtra("user", user);
                startActivityForResult(intent, PalPalResultCode);

            }
        });

    }

    private void initListGroup() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.SERVERPATH + "usersingroup",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(CreateGroupPage.this,response,Toast.LENGTH_LONG).show();
                        try{
                            JSONArray jsonObject = new JSONArray(response);
                            Toast.makeText(getApplicationContext(),jsonObject.toString(),Toast.LENGTH_LONG).show();
                            arrayUsers = jsonObject;
                            initListGroup1();
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GroupInvitatoinDetails.this,error.toString(),Toast.LENGTH_LONG).show();
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
                headers.put("gid",group.getGruopID());
                headers.put("id",user.getUuid());
                headers.put("token",user.getToken());
                return headers;
            }

        };

        AppController.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void initListGroup1() {
        lv = (ListView) findViewById(R.id.listView);
        customAdapter = new CustomGroupsOfUsers(this);
        //listGroup = group.getGroupUser();
        modelArrayList = getModel();
        lv.setAdapter(customAdapter);
    }

    private ArrayList<ModelGroupView> getModel(){
        ArrayList<ModelGroupView> list = new ArrayList<>();
        try {
            for (int i = 0; i < arrayUsers.length(); i++) {
                ModelGroupView model = new ModelGroupView();
                model.setFullname(arrayUsers.getJSONObject(i).getString("name"));
                model.setScore(arrayUsers.getJSONObject(i).getString("count"));
                list.add(model);
            }
        }catch (Exception e){
            Toast.makeText(GroupInvitatoinDetails.this,e.toString(),Toast.LENGTH_LONG).show();
        }
        return list;
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
                        if(confirmation.getProofOfPayment().getState().equals("approved")){
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.SERVERPATH + "usersingroup",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            //Toast.makeText(CreateGroupPage.this,response,Toast.LENGTH_LONG).show();
                                            try{


                                            }catch (Exception e){
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(GroupInvitatoinDetails.this,error.toString(),Toast.LENGTH_LONG).show();
                                        }
                                    }){
                                @Override
                                public Map<String, String> getParams() {
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("gid",group.getGruopID());
                                    params.put("id",user.getUuid());
                                    params.put("token",user.getToken());
                                    return params;
                                }

                            };

                            AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"There was an Error",Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e){
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
}
