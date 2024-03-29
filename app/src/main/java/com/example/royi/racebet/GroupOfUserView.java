package com.example.royi.racebet;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupOfUserView extends AppCompatActivity {

    TextView userName;
    ImageView userPhoto;
    private User user;
    JSONArray arrayUsers;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_of_user_view);

        userName = findViewById(R.id.userName);
        userPhoto = findViewById(R.id.userPhoto);

        user = getIntent().getParcelableExtra("user");

        userName.setText("Hello, "+user.getName());
        userPhoto.setImageURI(Uri.parse(user.getPhone()));

        initListGroup();


    }

    private void initListGroup() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.SERVERPATH + "groupofusers",
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
                        Toast.makeText(GroupOfUserView.this,error.toString(),Toast.LENGTH_LONG).show();
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
                headers.put("id",user.getUuid());
                headers.put("token",user.getToken());
                return headers;
            }

        };

        AppController.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void initListGroup1() {
        lv = (ListView) findViewById(R.id.listViewInGroup);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < arrayUsers.length(); i++) {
            try {
                String groupName = arrayUsers.getJSONObject(i).getString("gname");
                list.add(groupName);
            }catch (Exception e){

            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) lv.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
                try {//gid,token,id
                    final String gid = arrayUsers.getJSONObject(position).getString("gid");
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.SERVERPATH + "group",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //Toast.makeText(CreateGroupPage.this,response,Toast.LENGTH_LONG).show();
                                    try{
                                        JSONObject jsonObject = new JSONObject(response);
                                        Toast.makeText(getApplicationContext(),jsonObject.toString(),Toast.LENGTH_LONG).show();
                                        Group group = new Group(jsonObject.getString("gid"),
                                                jsonObject.getString("gname"),
                                                jsonObject.getString("duration"),
                                                jsonObject.getString("maxusers"),
                                                jsonObject.getString("betprice"),
                                                jsonObject.getString("adminid"),null);
                                        Intent intent = new Intent(getApplicationContext(),GroupView.class);
                                        intent.putExtra("group",group);
                                        intent.putExtra("user",user);
                                        startActivity(intent);
                                    }catch (Exception e){
                                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(GroupOfUserView.this,error.toString(),Toast.LENGTH_LONG).show();
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
                            headers.put("id",user.getUuid());
                            headers.put("token",user.getToken());
                            headers.put("gid",gid);
                            return headers;
                        }

                    };

                    AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

                }catch (Exception e){

                }
            }

        });
    }

    private ArrayList<ModelGroupView> getModel(){
        ArrayList<ModelGroupView> list = new ArrayList<>();
        try {
            for (int i = 0; i < arrayUsers.length(); i++) {
                ModelGroupView model = new ModelGroupView();
                model.setFullname(arrayUsers.getJSONObject(i).getString("gname"));
                model.setScore("");
                list.add(model);
            }
        }catch (Exception e){
            Toast.makeText(GroupOfUserView.this,e.toString(),Toast.LENGTH_LONG).show();
        }
        return list;
    }
}
