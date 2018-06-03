package com.example.dima.smarttool.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.dima.smarttool.R;

public class InfoActivity extends AppCompatActivity {
    ImageButton goVK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        goVK = findViewById(R.id.infoVkImageButton);
        goVK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Uri uriUrl = Uri.parse("https://vk.com/dimadomrachev2002");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
