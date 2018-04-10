package com.example.camil.tabletapp;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    //connection to the firebase
    private DatabaseReference databaseCode;
    private DatabaseReference databaseTotal;
    private DatabaseReference databaseTimeStamp;
    private DatabaseReference databaseListTimeStamps;
    private DatabaseReference databasePhoneLockStatus;
    private DatabaseReference databaseUnlockIdentifier;


    private String totalScoreString;
    private int totalScoreInt;
    private ImageView background;
    private String timestampListID;
    private int increasedTotalScoreInt;
    private String increasedTotalScoreString;
    private String totalScoreStringNow;
    private int totalScoreIntNow;
    private TextView codeTextView;
    private String newCodeString;
    private TextView unlockTextView;
    private Button button;
    private MediaPlayer musicMP;
    private String unlockCode;
    private boolean cheater;
    private boolean isRunning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.phoneLockButton);
        codeTextView = findViewById(R.id.passwordTextView);
        isRunning = false;

        //initiating the database
        databaseCode = FirebaseDatabase.getInstance().getReference("Code");
        databaseTotal = FirebaseDatabase.getInstance().getReference("Total");
        databaseTimeStamp = FirebaseDatabase.getInstance().getReference("TimeStamp");
        databaseListTimeStamps = FirebaseDatabase.getInstance().getReference("ListTimeStamps");
        databasePhoneLockStatus = FirebaseDatabase.getInstance().getReference("PhoneLockStatus");
        databaseUnlockIdentifier = FirebaseDatabase.getInstance().getReference("UnlockIdentifier");

        databasePhoneLockStatus.setValue(false);

        databaseUnlockIdentifier.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                cheater = true;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                cheater = true;
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseTotal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalScoreStringNow = dataSnapshot.getValue(String.class);
                totalScoreIntNow = Integer.parseInt(totalScoreStringNow);
                increasedTotalScoreInt = totalScoreIntNow + 1;
                increasedTotalScoreString = Integer.toString(increasedTotalScoreInt);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //method for checking the total count of codes entered
        super.onStart();
        databaseTotal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalScoreString = dataSnapshot.getValue(String.class);
                totalScoreInt = Integer.parseInt(totalScoreString);
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
        musicMP = MediaPlayer.create(this, R.raw.sound);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning == false) {
                    lock();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Phones are locked!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }

            }
        });
    }


    //method for locking the phones
    public void lock() {
        isRunning = true;
        cheater = false;
        musicMP.start();
        code();
        //Code for storing timestamp of locking the phones
        databaseTimeStamp.setValue(ServerValue.TIMESTAMP);

        //storing all time stamps in the list
        timestampListID = databaseListTimeStamps.push().getKey();
        databaseListTimeStamps.child(timestampListID).setValue(ServerValue.TIMESTAMP);

        databasePhoneLockStatus.setValue(true);


        //timer after the button is pushed to unlock the phones after pre-set time
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                databasePhoneLockStatus.setValue(false);
                isRunning = false;
                if (cheater == false) {
                    databaseTotal.setValue(increasedTotalScoreString);
                }
            }
        }, 40000); //1800000 = 30 minutes
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


