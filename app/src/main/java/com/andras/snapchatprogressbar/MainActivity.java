package com.andras.snapchatprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private SnapchatProgressBar one, two , three, four, five;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        one = (SnapchatProgressBar) findViewById(R.id.one);
        two = (SnapchatProgressBar) findViewById(R.id.two);
        three = (SnapchatProgressBar) findViewById(R.id.three);
        four = (SnapchatProgressBar) findViewById(R.id.four);
        five = (SnapchatProgressBar) findViewById(R.id.five);

        one.startSpinning();
        two.startSpinning();
        three.startSpinning();
        three.setBarColor(getResources().getColor(R.color.colorAccent));
        four.startSpinning();
        four.setInnerBarWidth(15);
        four.setOuterBarWidth(30);
        four.setBarColor(getResources().getColor(R.color.green));
        five.startSpinning();
        five.setBarColor(getResources().getColor(R.color.colorPrimary));
        five.setSpinSpeed(16);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnapchatProgressBar progressBar = (SnapchatProgressBar) view;
                if (progressBar.isSpinning) {
                    progressBar.stopSpinning();
                } else {
                    progressBar.startSpinning();
                }
            }
        };
        one.setOnClickListener(onClickListener);
        two.setOnClickListener(onClickListener);
        three.setOnClickListener(onClickListener);
        four.setOnClickListener(onClickListener);
        five.setOnClickListener(onClickListener);
    }
}
