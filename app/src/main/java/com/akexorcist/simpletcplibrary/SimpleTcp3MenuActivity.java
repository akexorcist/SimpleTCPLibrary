package com.akexorcist.simpletcplibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SimpleTcp3MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_tcp_3_menu);

        findViewById(R.id.buttonClient).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openActivity(SimpleTcp3ClientActivity.class);
            }
        });

        findViewById(R.id.buttonServer).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openActivity(SimpleTcp3ServerActivity.class);
            }
        });
    }

    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
