package com.example.royi.racebet;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupView extends AppCompatActivity {

    private ListView lv;
    public static ArrayList<ModelGroupView> modelArrayList;
    private CustomAdapterGroupView customAdapter;
    //ArrayList<UserInGroup> listGroup = new ArrayList<>();
    private TextView groupName,groupDuration,groupBetPrice;
    private Group group;
    private User user;
    private JSONArray arrayUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        groupBetPrice = findViewById(R.id.betPriceText);
        groupDuration = findViewById(R.id.durationGroupText);
        groupName = findViewById(R.id.groupNameText);

        group = getIntent().getParcelableExtra("group");
        user = getIntent().getParcelableExtra("user");

        groupName.setText("Group Name\n"+group.getName());
        groupDuration.setText("End Time\n"+group.getDurtion());
        groupName.setText("Bet Price\n"+group.getBetPrice());

        initListGroup();



       /* myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child2: children) {
                    userList.add(child2.getValue(User.class));

                }

                modelArrayList = getModel();
                lv.setAdapter(customAdapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        userList.get(position).updateVote();
                        recreate();
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    private void initListGroup() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://rcbetapi.ddns.net/usersingroup",
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
                        Toast.makeText(GroupView.this,error.toString(),Toast.LENGTH_LONG).show();
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
        customAdapter = new CustomAdapterGroupView(this);
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
            Toast.makeText(GroupView.this,e.toString(),Toast.LENGTH_LONG).show();
        }
            return list;
    }
}

