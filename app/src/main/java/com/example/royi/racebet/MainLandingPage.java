package com.example.royi.racebet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainLandingPage extends AppCompatActivity {
    FirebaseAuth mAuth;
    JSONArray array;
    User user;
    JSONArray jsonArray;
    TextView userName;
    ImageView userPhoto;
    Button createGroup,viewTable,viewPendingInvitation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_landing_page);


        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.userNameMainPage);
        createGroup = findViewById(R.id.createGroup);
        viewTable = findViewById(R.id.viewTable);
        viewPendingInvitation = findViewById(R.id.viewPendingInvitation);
        user = (User) getIntent().getParcelableExtra("user");

        MyWorker.token = user.getToken();
        MyWorker.id = user.getUuid();
        userName.setText(user.getName());

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLandingPage.this, CreateGroupPage.class);
                intent.putExtra("user", user);
                startActivity(intent);
                //startActivity(new Intent(getApplicationContext(),GroupView.class));
            }
        });

        viewTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLandingPage.this, GroupOfUserView.class);
                intent.putExtra("user", user);
                startActivity(intent);
                //startActivity(new Intent(getApplicationContext(),GroupView.class));
            }
        });

        viewPendingInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final String uniqueId = UUID.randomUUID().toString().replace("-", "");
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.SERVERPATH + "user",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(response);
                                        JSONArray jsonArray = jsonObject1.getJSONArray("inv_list");
                                        ShowAlertDialogOfPendongInvitation(jsonArray);
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainLandingPage.this, error.toString(), Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("token", user.getToken());
                            params.put("id", uniqueId);
                            params.put("name", user.getName());
                            params.put("email", getIntent().getStringExtra("userEmail"));
                            params.put("ppath", "1");
                            params.put("phone", "1");
                            return params;
                        }

                    };
                    AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                    // Signed in successfully, show authenticated UI.
                    //updateUI(account);
                    //user = new User(token,uniqueId,account.getDisplayName(),userPhone.getText().toString(),
                    //        account.getPhotoUrl().toString(),null);


                    ShowAlertDialogOfPendongInvitation(array);
                } catch (Exception e) {

                }
            }
        });
        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        });

        startWorker();
    }

    private void startWorker() {
        Toast.makeText(getApplicationContext(),"Worker Start Working",Toast.LENGTH_LONG).show();
        //MyWorker.id = null;
        //MyWorker.token = null;
        PeriodicWorkRequest.Builder fitWorkBuilder =
                new PeriodicWorkRequest.Builder(MyWorker.class, 16,
                        TimeUnit.MINUTES);
        // ...if you want, you can apply constraints to the builder here...
        PeriodicWorkRequest myWork = fitWorkBuilder.build();
        // Create the actual work object:
        // Then enqueue the recurring task:
        WorkManager.getInstance().enqueue(myWork);
    }



    private void ShowAlertDialogOfPendongInvitation(final JSONArray jsonArray) {
        if (jsonArray.length() == 0)
            Toast.makeText(getApplicationContext(),"You dont have pending invitation!",Toast.LENGTH_LONG).show();
        else {
            ArrayList<String> arrayList = new ArrayList<>();
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    arrayList.add("Group Name: " + jsonArray.getJSONObject(i).getJSONObject("group").getString("gname") +
                            " inviter: " + jsonArray.getJSONObject(i).getString("inviter"));
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainLandingPage.this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.custom_pendonginvitation_alertdialog, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Pendong Invitation");
            final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
            lv.setAdapter(adapter);
            alertDialog.show();

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {

                    // ListView Clicked item index
                    int itemPosition = position;

                    // ListView Clicked item value
                    String itemValue = (String) lv.getItemAtPosition(position);

                    // Show Alert
                    Toast.makeText(getApplicationContext(),
                            "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                            .show();
                    try {
                        Intent intent = new Intent(MainLandingPage.this, GroupInvitatoinDetails.class);
                        JSONObject jsonObject = jsonArray.getJSONObject(position).getJSONObject("group");
                        Group group = new Group(jsonObject.getString("gid"),
                                jsonObject.getString("gname"),
                                jsonObject.getString("duration"),
                                jsonObject.getString("maxusers"),
                                jsonObject.getString("betprice"),
                                jsonObject.getString("adminid"),null);
                        intent.putExtra("group",group);
                        intent.putExtra("user",user);
                        intent.putExtra("invitatoinName",jsonArray.getJSONObject(position).getString("inviter"));
                        startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    private void updateUI() {
        userName.setText("Hello, " + user.getName());
    }
}
