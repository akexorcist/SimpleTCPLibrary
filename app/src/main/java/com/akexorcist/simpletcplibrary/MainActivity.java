package com.akexorcist.simpletcplibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonSimpleTcp1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(SimpleTcp1Activity.class);
            }
        });

        findViewById(R.id.buttonSimpleTcp2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(SimpleTcp2Activity.class);
            }
        });

        findViewById(R.id.buttonSimpleTcp3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(SimpleTcp3MenuActivity.class);
            }
        });

        findViewById(R.id.buttonContinuous1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(ContinuousTcp1Activity.class);
            }
        });

        findViewById(R.id.buttonContinuous2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(ContinuousTcp2MenuActivity.class);
            }
        });
    }

    private void openActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
