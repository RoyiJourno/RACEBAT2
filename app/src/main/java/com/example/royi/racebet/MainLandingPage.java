package com.example.royi.racebet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainLandingPage extends AppCompatActivity {
    FirebaseAuth mAuth;
    User user;

    TextView userName;
    ImageView userPhoto;
    Button createGroup,viewTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_landing_page);


         mAuth = FirebaseAuth.getInstance();
         userName = findViewById(R.id.userNameMainPage);
         createGroup = findViewById(R.id.createGroup);
         viewTable = findViewById(R.id.viewTable);
         user = (User)getIntent().getParcelableExtra("user");
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

        /*viewTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLandingPage.this,viewTable.class);
                intent.putExtra("User",user);
                startActivity(intent);
            }
        });*/





        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }
        });




    }

    private void updateUI() {
        userName.setText("Hello, " + user.getName());
    }
}
