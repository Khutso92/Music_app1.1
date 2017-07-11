package com.example.khutsomatlala.music_app11;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final ViewFlipper MyViewFlipper =   findViewById(R.id.viewflipper);

        final Button buttonAutoFlip =  findViewById(R.id.buttonautoflip);

        MyViewFlipper.setFlipInterval(800);
        buttonAutoFlip.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (MyViewFlipper.isFlipping()) {
                    MyViewFlipper.stopFlipping();
                    buttonAutoFlip.setText("Start Auto Flip");
                } else {
                    MyViewFlipper.startFlipping();
                    buttonAutoFlip.setText("Stop Auto Flip");
                }

            }
        });

    }


}
