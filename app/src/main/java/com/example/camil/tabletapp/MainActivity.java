package com.example.camil.tabletapp;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                CodeGenerator cg = new CodeGenerator(6, ThreadLocalRandom.current());
                cg.nextString();
                String unlockcode = cg.getString();


                //Display generated code
                TextView txtv = findViewById(R.id.password);
                txtv.setText(unlockcode);

                //Display text Code to unlock the phone
                TextView textCode = findViewById(R.id.code);
                textCode.setVisibility(View.VISIBLE);
            }
        });



    }


}
