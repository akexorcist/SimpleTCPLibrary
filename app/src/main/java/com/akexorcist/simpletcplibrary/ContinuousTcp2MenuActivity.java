package com.akexorcist.simpletcplibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ContinuousTcp2MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continuous_tcp_2_menu);

        findViewById(R.id.buttonClient).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openActivity(ContinuousTcp2ClientActivity.class);
            }
        });

        findViewById(R.id.buttonServer).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openActivity(ContinuousTcp2ServerActivity.class);
            }
        });
    }

    private void openActivity(Class<?> cls) {
        Intent i = new Intent(this, cls);
        startActivity(i);
    }
}
