package com.example.royi.racebet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {

    TextView txtID,txtAmount,txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        txtID=(TextView)findViewById(R.id.txtID);
        txtAmount=(TextView)findViewById(R.id.txtAmount);
        txtStatus=(TextView)findViewById(R.id.txtStatus);

        //get intent
        Intent intent=getIntent();
        try {
            JSONObject jsonObject=new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"),intent.getStringExtra("PaymentAmount"));
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

   private void showDetails(JSONObject response,String paymentAmount){
        try {
            txtID.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            txtAmount.setText("$"+paymentAmount);

        }catch (JSONException e){
            e.printStackTrace();
        }
   }
}
