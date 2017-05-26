package com.example.popbackinculsive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FrameLayout fm = (FrameLayout) findViewById(R.id.myframe);

        Button b = (Button) findViewById(R.id.bt_multiple_trans);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().findFragmentById(R.id.myframe) == null) {
                    getSupportFragmentManager().beginTransaction().add(R.id.myframe, new AFragment()).
                            addToBackStack("first")
                            .commit();
                }
            }
        });
        Button b2 = (Button) findViewById(R.id.bt_multiple_trans2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().add(R.id.myframe, new AFragment()).
                        addToBackStack("first")
                        .setAllowOptimization(true)
                        .commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.myframe, new BFragment()).
                        addToBackStack("second")
                        .setAllowOptimization(true)
                        .commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.myframe, new CFragment()).
                        addToBackStack("third")
                        .setAllowOptimization(true)
                        .commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.myframe, new DFragment()).
                        addToBackStack("fourth")
                        .setAllowOptimization(true)
                        .commit();
            }
        });
    }
}
