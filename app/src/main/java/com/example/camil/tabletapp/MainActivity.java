package com.example.camil.tabletapp;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    //connection to the firebase
    DatabaseReference databaseReference;

    //string containing code for unlocking
    String unlockcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initiating the database
        databaseReference = FirebaseDatabase.getInstance().getReference("Code");

        //animation stage One
        ImageView stageOne = (ImageView) findViewById(R.id.stageOne);
        stageOne.setImageResource(R.drawable.stageone);
        AnimationDrawable stageOneAnimation = (AnimationDrawable) stageOne.getDrawable();
        stageOneAnimation.start();

        final Button button = findViewById(R.id.phoneLock);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Code for locking phones

                //To generate code - targets API 21, might need to reconsider if tablets are old
                CodeGenerator codeGenerator = new CodeGenerator(6, ThreadLocalRandom.current());
                codeGenerator.nextString();
                unlockcode = codeGenerator.getString();

                //add generated code to the database
                addCode(unlockcode);

                //Display generated code
                TextView codeTextView = findViewById(R.id.password);
                codeTextView.setText(unlockcode);

                //Display text Code to unlock the phone
                TextView unlockTextView = findViewById(R.id.code);
                unlockTextView.setVisibility(View.VISIBLE);

            }
        });
    }

    //method for storing data in the Firebase
    public void addCode(String unlockcode) {
       databaseReference.setValue(unlockcode);

    }
}
