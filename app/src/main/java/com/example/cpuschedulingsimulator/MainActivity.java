package com.example.cpuschedulingsimulator;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cpuschedulingsimulator.activity.ProcessInputActivity;
import com.google.android.material.card.MaterialCardView;

/**
 * The first screen of the application.
 *
 * It allows the user to select a CPU scheduling algorithm.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ALGORITHM =
            "com.example.cpuschedulingsimulator.EXTRA_ALGORITHM";

    public static final String ALGORITHM_FCFS = "FCFS";
    public static final String ALGORITHM_SJF = "SJF";
    public static final String ALGORITHM_PRIORITY = "PRIORITY";
    public static final String ALGORITHM_ROUND_ROBIN = "ROUND_ROBIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCardView cardFcfs =
                findViewById(R.id.cardFcfs);

        MaterialCardView cardSjf =
                findViewById(R.id.cardSjf);

        MaterialCardView cardPriority =
                findViewById(R.id.cardPriority);

        MaterialCardView cardRoundRobin =
                findViewById(R.id.cardRoundRobin);

        cardFcfs.setOnClickListener(
                view -> openProcessInput(ALGORITHM_FCFS)
        );

        cardSjf.setOnClickListener(
                view -> openProcessInput(ALGORITHM_SJF)
        );

        cardPriority.setOnClickListener(
                view -> openProcessInput(ALGORITHM_PRIORITY)
        );

        cardRoundRobin.setOnClickListener(
                view -> openProcessInput(ALGORITHM_ROUND_ROBIN)
        );
    }

    /**
     * Opens the process-input screen and sends the selected algorithm.
     */
    private void openProcessInput(String algorithm) {

        Intent intent = new Intent(
                MainActivity.this,
                ProcessInputActivity.class
        );

        intent.putExtra(EXTRA_ALGORITHM, algorithm);

        startActivity(intent);
    }
}