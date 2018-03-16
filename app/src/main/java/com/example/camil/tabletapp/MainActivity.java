package com.example.camil.tabletapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    //connection to the firebase
    DatabaseReference databaseCode;
    DatabaseReference databaseTotal;
    DatabaseReference databasePhoneStatus;
    DatabaseReference databaseTimeStamp;
    DatabaseReference databaseTimeLocked;
    DatabaseReference databaseListTimeStamps;
    DatabaseReference databasePhone;
    DatabaseReference databaseCodeEntered;

    TextView totalCountTextView;
    String totalScoreString;
    int totalScoreInt;
    ImageView background;
    String timestampListID;
    int increasedTotalScoreInt;
    String increasedTotalScoreString;
    String totalScoreStringNow;
    int totalScoreIntNow;
    String checkCodeEnteredString;
    int checkCodeEnteredInt;
    boolean occured;
    TextView codeTextView;
    String newCodeString;
    TextView unlockTextView;

    //string containing code for unlocking
    String unlockCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totalCountTextView = findViewById(R.id.totalCount);
        codeTextView = findViewById(R.id.passwordTextView);

        //initiating the database
        databaseCode = FirebaseDatabase.getInstance().getReference("Code");
        databaseTotal = FirebaseDatabase.getInstance().getReference("Total");
        databasePhoneStatus = FirebaseDatabase.getInstance().getReference("PhoneLockStatus");
        databaseTimeStamp = FirebaseDatabase.getInstance().getReference("TimeStamp");
        databaseTimeLocked = FirebaseDatabase.getInstance().getReference("TimesLockActivated");
        databaseListTimeStamps = FirebaseDatabase.getInstance().getReference("ListTimeStamps");
        databasePhone = FirebaseDatabase.getInstance().getReference("Phone");
        databaseCodeEntered = FirebaseDatabase.getInstance().getReference("CodeEntered");


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
                lock();
            }
        });


        Timer time = new Timer();
        //Scheduler scheduledTask = new Scheduler();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 12);
        calendar.set(Calendar.SECOND, 0);
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                lock();
            }
        }, calendar.getTime(), 15000);

        //86400000 = miliseconds in one day
    }

    //method for locking the phones
    public void lock() {
        occured = false;
        code();
        //Code for storing timestamp of locking the phones
        databaseTimeStamp.setValue(ServerValue.TIMESTAMP);

        //CodeEntered field in database is set to "1"
        databaseCodeEntered.setValue("1");

        //storing all time stamps in the list
        timestampListID = databaseListTimeStamps.push().getKey();
        databaseListTimeStamps.child(timestampListID).setValue(ServerValue.TIMESTAMP);

        //locking all phones
        Query unlockedPhones = databasePhone.orderByChild("PhoneLockStatus").equalTo(false);
        unlockedPhones.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().child("PhoneLockStatus").setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //timer after the button is pushed to unlock the phones after pre-set time
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Query lockedPhones = databasePhone.orderByChild("PhoneLockStatus").equalTo(true);
                lockedPhones.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("PhoneLockStatus").setValue(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                //when nobody entered the code after set time, the total Score is increased by 1
                databaseCodeEntered.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        checkCodeEnteredString = dataSnapshot.getValue(String.class);
                        checkCodeEnteredInt = Integer.parseInt(checkCodeEnteredString);
                        if (checkCodeEnteredInt == 1 && occured == false) {
                            occured = true;
                            databaseTotal.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    totalScoreStringNow = dataSnapshot.getValue(String.class);
                                    totalScoreIntNow = Integer.parseInt(totalScoreStringNow);
                                    increasedTotalScoreInt = totalScoreIntNow + 1;
                                    increasedTotalScoreString = Integer.toString(increasedTotalScoreInt);
                                    databaseTotal.setValue(increasedTotalScoreString);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }, 8000); //8 seconds - need to change afterwards to 30 minutes
    }

    public void code() {
        //generating the code
        CodeGenerator codeGenerator = new CodeGenerator(6, ThreadLocalRandom.current());
        codeGenerator.nextString();
        unlockCode = codeGenerator.getString();

        //add generated code to the database
        addCode(unlockCode);

        databaseCode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newCodeString = dataSnapshot.getValue(String.class);
                codeTextView.setText(newCodeString);
                codeTextView.setVisibility(View.VISIBLE);

                //Display text Code to unlock the phone
                unlockTextView = findViewById(R.id.textAboveCodeTextView);
                unlockTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //method for storing generated Code in the Firebase
    public void addCode(String unlockcode) {
        databaseCode.setValue(unlockcode);
    }

}


