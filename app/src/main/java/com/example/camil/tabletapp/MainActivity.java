package com.example.camil.tabletapp;

import android.graphics.drawable.AnimationDrawable;
import android.nfc.FormatException;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    //connection to the firebase
    DatabaseReference databaseCode;
    DatabaseReference databaseTotal;
    DatabaseReference databasePhoneStatus;
    DatabaseReference databaseTimeStamp;
    DatabaseReference databaseTimeLocked;
    DatabaseReference databaseListTimeStamps;

    TextView totalCountTextView;
    String totalScoreString;
    int totalScoreInt;
    ImageView background;
    String timestampListID;


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
        databasePhoneStatus = FirebaseDatabase.getInstance().getReference("PhoneLockStatus");
        databaseTimeStamp = FirebaseDatabase.getInstance().getReference("TimeStamp");
        databaseTimeLocked = FirebaseDatabase.getInstance().getReference("TimesLockActivated");
        databaseListTimeStamps = FirebaseDatabase.getInstance().getReference("ListTimeStamps");



        //method for checking the total count of codes entered
        super.onStart();
        databaseTotal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalScoreString = dataSnapshot.getValue(String.class);
                totalScoreInt = Integer.parseInt(totalScoreString);
                totalCountTextView.setText(String.valueOf(totalScoreInt));
                //changing the animation
                if (totalScoreInt >= 100) {
                    ImageView stageOne = findViewById(R.id.dogStateImageView);
                    stageOne.setImageResource(R.drawable.stageone);
                    AnimationDrawable stageOneAnimation = (AnimationDrawable) stageOne.getDrawable();
                    stageOneAnimation.start();
                    background = findViewById(R.id.backgroundImageView);
                    background.setImageResource(R.drawable.firststage);
                } else if (totalScoreInt < 100 && totalScoreInt >= 97) {
                    ImageView stageTwo = findViewById(R.id.dogStateImageView);
                    stageTwo.setImageResource(R.drawable.stagetwo);
                    AnimationDrawable stageTwoAnimation = (AnimationDrawable) stageTwo.getDrawable();
                    stageTwoAnimation.start();
                    background = findViewById(R.id.backgroundImageView);
                    background.setImageResource(R.drawable.secondstage);
                } else if (totalScoreInt < 97 && totalScoreInt >= 95) {
                    ImageView stageThree = findViewById(R.id.dogStateImageView);
                    stageThree.setImageResource(R.drawable.stagethree);
                    AnimationDrawable stageThreeAnimation = (AnimationDrawable) stageThree.getDrawable();
                    stageThreeAnimation.start();
                    background = findViewById(R.id.backgroundImageView);
                    background.setImageResource(R.drawable.thirdstage);
                } else {
                    ImageView stageFour = findViewById(R.id.dogStateImageView);
                    stageFour.setImageResource(R.drawable.last);
                    background = findViewById(R.id.backgroundImageView);
                    background.setImageResource(R.drawable.fourthstage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //pushing the button LockPhones
        final Button button = findViewById(R.id.phoneLockButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Code for locking phones and storing timestamp of locking the phones
                changeToTrue();
                databaseTimeStamp.setValue(ServerValue.TIMESTAMP);
                timestampListID = databaseListTimeStamps.push().getKey();
                databaseListTimeStamps.child(timestampListID).setValue(ServerValue.TIMESTAMP);


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

    //changing phoneLockStatus to TRUE
    public void changeToTrue() {
        databasePhoneStatus.setValue(true);
    }

}

