package com.example.royi.racebet;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class PaypalPage extends AppCompatActivity {

    public static int PalPalResultCode = 7171;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)//use sandbox on test
            .clientId(Config.PAYPAL_KEY);


    Button btnPayNow;
    EditText edtAmount;

    String amount="";


    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_page);

        //start Paypal service
        Intent intent=new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        btnPayNow=(Button)findViewById(R.id.btnPayNow);
        edtAmount=(EditText) findViewById(R.id.edtAmount);

        btnPayNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                processPayment();

            }
        });
    }

    private void processPayment(){
        amount=edtAmount.getText().toString();
        PayPalPayment payPalPayment=new PayPalPayment(new BigDecimal(String.valueOf(amount)),"USD",
                "The Game started",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent=new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PalPalResultCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode == PalPalResultCode)
       {
           if(resultCode == RESULT_OK)
           {
               PaymentConfirmation confirmation=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
               if(confirmation !=null)
               {
                   try{
                       String PaymentDetails=confirmation.toJSONObject().toString(4);
                       startActivity(new Intent(this,PaymentDetails.class)
                               .putExtra("PaymentDetails",PaymentDetails)
                               .putExtra("PaymentAmount",amount)
                       );



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
}