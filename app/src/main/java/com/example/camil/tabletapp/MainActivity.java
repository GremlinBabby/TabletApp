package com.example.camil.tabletapp;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    //connection to the firebase
    DatabaseReference databaseCode;
    DatabaseReference databaseTotal;

    TextView totalCountTextView;

    //string containing code for unlocking
    String unlockcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totalCountTextView = findViewById(R.id.totalCount);

        //initiating the database
        databaseCode = FirebaseDatabase.getInstance().getReference("Code");
        databaseTotal = FirebaseDatabase.getInstance().getReference("Total");

        //animation stage One
        ImageView stageOne = (ImageView) findViewById(R.id.stageOneImageView);
        stageOne.setImageResource(R.drawable.stageone);
        AnimationDrawable stageOneAnimation = (AnimationDrawable) stageOne.getDrawable();
        stageOneAnimation.start();

        final Button button = findViewById(R.id.phoneLockButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Code for locking phones

                //To generate code - targets API 21
                CodeGenerator codeGenerator = new CodeGenerator(6, ThreadLocalRandom.current());
                codeGenerator.nextString();
                unlockcode = codeGenerator.getString();

                //add generated code to the database
                addCode(unlockcode);

                //Display generated code
                TextView codeTextView = findViewById(R.id.passwordTextView);
                codeTextView.setText(unlockcode);
                codeTextView.setVisibility(View.VISIBLE);

                //Display text Code to unlock the phone
                TextView unlockTextView = findViewById(R.id.textAboveCodeTextView);
                unlockTextView.setVisibility(View.VISIBLE);

            }
        });
    }

    //method for storing data in the Firebase
    public void addCode(String unlockcode) {
        databaseCode.setValue(unlockcode);
    }

    //method for checking the total count of codes entered
    @Override
    public void onStart() {
        super.onStart();
        databaseTotal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String totalCountOfCodesEntered = dataSnapshot.getValue(String.class);
                int ourInt = Integer.parseInt(totalCountOfCodesEntered);
                totalCountTextView.setText(String.valueOf(ourInt));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
