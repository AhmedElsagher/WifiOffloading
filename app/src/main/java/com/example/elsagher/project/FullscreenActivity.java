package com.example.elsagher.project;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class FullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        ImageView image = (ImageView) dialog.findViewById(R.id.image_ads);
        Picasso.with(this).load("http://wifioffloading.hol.es/orange-tele.png").into(image);
        Button dialogButton = (Button) dialog.findViewById(R.id.skip_ads);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(FullscreenActivity.this, IntroActivity.class);
                        FullscreenActivity.this.startActivity(mainIntent);
                        FullscreenActivity.this.finish();
                    }
                }, 3000);
            }
        });

        dialog.show();


    }
}
