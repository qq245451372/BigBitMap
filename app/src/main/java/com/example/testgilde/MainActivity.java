package com.example.testgilde;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BigView bigView = findViewById(R.id.bigView);
        try {
            InputStream is = getAssets().open("aa.png");
            bigView.setImage(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }

}