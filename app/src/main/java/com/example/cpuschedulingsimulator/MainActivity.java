package com.example.cpuschedulingsimulator;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cpuschedulingsimulator.activity.AlgorithmSelectionActivity;
import com.example.cpuschedulingsimulator.activity.ProcessInputActivity;
import com.google.android.material.card.MaterialCardView;

/**
 * The first screen of the application.
 *
 * It allows the user to choose between Preemptive, Non-Preemptive,
 * or Hybrid scheduling. Preemptive and Non-Preemptive lead to an
 * algorithm-selection screen; Hybrid has no further choice and
 * goes straight to process input.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ALGORITHM =
            "com.example.cpuschedulingsimulator.EXTRA_ALGORITHM";

    public static final String ALGORITHM_FCFS = "FCFS";
    public static final String ALGORITHM_SJF = "SJF";
    public static final String ALGORITHM_PRIORITY = "PRIORITY";
    public static final String ALGORITHM_ROUND_ROBIN = "ROUND_ROBIN";

    public static final String ALGORITHM_SJF_PREEMPTIVE = "SJF_PREEMPTIVE";
    public static final String ALGORITHM_PRIORITY_PREEMPTIVE = "PRIORITY_PREEMPTIVE";

    public static final String ALGORITHM_HYBRID = "HYBRID";

    public static final String EXTRA_CATEGORY =
            "com.example.cpuschedulingsimulator.EXTRA_CATEGORY";

    public static final String CATEGORY_PREEMPTIVE = "PREEMPTIVE";
    public static final String CATEGORY_NON_PREEMPTIVE = "NON_PREEMPTIVE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCardView cardPreemptive =
                findViewById(R.id.cardPreemptive);

        MaterialCardView cardNonPreemptive =
                findViewById(R.id.cardNonPreemptive);

        MaterialCardView cardHybrid =
                findViewById(R.id.cardHybrid);

        cardPreemptive.setOnClickListener(
                view -> openAlgorithmSelection(CATEGORY_PREEMPTIVE)
        );

        cardNonPreemptive.setOnClickListener(
                view -> openAlgorithmSelection(CATEGORY_NON_PREEMPTIVE)
        );

        cardHybrid.setOnClickListener(
                view -> openProcessInput(ALGORITHM_HYBRID)
        );
    }

    /**
     * Opens the algorithm-selection screen for the chosen category.
     */
    private void openAlgorithmSelection(String category) {

        Intent intent = new Intent(
                MainActivity.this,
                AlgorithmSelectionActivity.class
        );

        intent.putExtra(EXTRA_CATEGORY, category);

        startActivity(intent);
    }

    /**
     * Opens the process-input screen directly, skipping algorithm
     * selection. Used by Hybrid, which has no further choice.
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
