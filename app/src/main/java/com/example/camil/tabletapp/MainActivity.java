package com.example.camil.tabletapp;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camil.tabletapp.Service.StickyService;
import com.example.camil.tabletapp.data.Data;
import com.example.camil.tabletapp.data.remote.APIService;
import com.example.camil.tabletapp.data.remote.ApiUtils;
import com.example.camil.tabletapp.model.Post;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //connection to the firebase
    private DatabaseReference databaseCode;
    private DatabaseReference databaseTotal;
    private DatabaseReference databaseListTimeStamps;
    private DatabaseReference databasePhoneLockStatus;
    private DatabaseReference databaseUnlockIdentifier;
    private DatabaseReference databasePhone;


    private String totalScoreString;
    private int totalScoreInt;
    private ImageView background;
    private int increasedTotalScoreInt;
    private String increasedTotalScoreString;
    private String totalScoreStringNow;
    private int totalScoreIntNow;
    private TextView codeTextView;
    private String newCodeString;
    private TextView unlockTextView;
    private MediaPlayer musicMP;
    private boolean cheater;
    private boolean isRunning;

    //------- FCM + Retrofit logic ---------
    private APIService mainAPIService;
    private String TAG = "MainActivity";
    String sendToFcmTopic = "/topics/dog";
    String fcmMessage = "Firebase Cloud Messaging";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.phoneLockButton);
        codeTextView = findViewById(R.id.passwordTextView);
        isRunning = false;

        mainAPIService = ApiUtils.getAPIService();
        startService(new Intent(this, StickyService.class));

        //initiating the database
        databaseCode = FirebaseDatabase.getInstance().getReference("Code");
        databaseTotal = FirebaseDatabase.getInstance().getReference("Total");
        databaseListTimeStamps = FirebaseDatabase.getInstance().getReference("ListTimeStamps");
        databasePhoneLockStatus = FirebaseDatabase.getInstance().getReference("PhoneLockStatus");
        databaseUnlockIdentifier = FirebaseDatabase.getInstance().getReference("UnlockIdentifier");
        databasePhone = FirebaseDatabase.getInstance().getReference("Phone");

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
                } else if (totalScoreInt >= 97) {
                    ImageView stageTwo = findViewById(R.id.dogStateImageView);
                    stageTwo.setImageResource(R.drawable.stagetwo);
                    AnimationDrawable stageTwoAnimation = (AnimationDrawable) stageTwo.getDrawable();
                    stageTwoAnimation.start();
                    background = findViewById(R.id.backgroundImageView);
                    background.setImageResource(R.drawable.secondstage);
                } else if (totalScoreInt >= 95) {
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
                if (!isRunning) {
                    lock();
                    sendPost(sendToFcmTopic, fcmMessage);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Phones are already locked!", Toast.LENGTH_SHORT);
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

        /*Query lockedPhones = databasePhone.orderByChild("Name").equalTo("Hmette");
        lockedPhones.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().child("Name").setValue("Himette");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/

        code();
        databasePhoneLockStatus.setValue(true);

        //storing all time stamps in the list
        String timestampListID = databaseListTimeStamps.push().getKey();
        databaseListTimeStamps.child(timestampListID).setValue(ServerValue.TIMESTAMP);

        //timer after the button is pushed to unlock the phones after pre-set time
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                databasePhoneLockStatus.setValue(false);
                Query unlockedPhones = databasePhone.orderByChild("Name").equalTo("Himette");
                unlockedPhones.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().child("Name").setValue("Hmette");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                isRunning = false;
                if (!cheater) {
                    databaseTotal.setValue(increasedTotalScoreString);
                }
            }
        }, 1800000); //1800000 = 30 minutes
    }


    public void code() {
        //generating the code
        CodeGenerator codeGenerator = new CodeGenerator(6, ThreadLocalRandom.current());
        codeGenerator.nextString();
        String unlockCode = codeGenerator.getString();

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


    //--------- Send FCM data message to phones ---------------------
    public void sendPost(String to, String fcmMessage) {
        mainAPIService.savePost(new Post(to, new Data(fcmMessage))).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (response.isSuccessful()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Phones locked!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), "Phones could not be locked!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
    }
}


