package com.example.cpuschedulingsimulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cpuschedulingsimulator.MainActivity;
import com.example.cpuschedulingsimulator.R;
import com.google.android.material.card.MaterialCardView;

/**
 * Shows the algorithms that belong to the category chosen in
 * MainActivity (Preemptive or Non-Preemptive), and lets the user
 * pick one to move on to process input.
 */
public class AlgorithmSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_algorithm_selection);

        TextView textSelectedCategory =
                findViewById(R.id.textSelectedCategory);

        TextView textCategoryDescription =
                findViewById(R.id.textCategoryDescription);

        MaterialCardView cardFcfs =
                findViewById(R.id.cardFcfs);

        MaterialCardView cardSjf =
                findViewById(R.id.cardSjf);

        MaterialCardView cardPriority =
                findViewById(R.id.cardPriority);

        MaterialCardView cardRoundRobin =
                findViewById(R.id.cardRoundRobin);

        MaterialCardView cardSjfPreemptive =
                findViewById(R.id.cardSjfPreemptive);

        MaterialCardView cardPriorityPreemptive =
                findViewById(R.id.cardPriorityPreemptive);

        String category = getIntent().getStringExtra(
                MainActivity.EXTRA_CATEGORY
        );

        if (category == null) {
            Toast.makeText(
                    this,
                    "No scheduling type was selected.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        boolean isPreemptive =
                MainActivity.CATEGORY_PREEMPTIVE.equals(category);

        /*
         * Non-Preemptive: FCFS, SJF, Priority.
         * Preemptive: SJF (SRTF), Priority, Round Robin.
         */
        cardFcfs.setVisibility(
                isPreemptive ? View.GONE : View.VISIBLE
        );

        cardSjf.setVisibility(
                isPreemptive ? View.GONE : View.VISIBLE
        );

        cardPriority.setVisibility(
                isPreemptive ? View.GONE : View.VISIBLE
        );

        cardRoundRobin.setVisibility(
                isPreemptive ? View.VISIBLE : View.GONE
        );

        cardSjfPreemptive.setVisibility(
                isPreemptive ? View.VISIBLE : View.GONE
        );

        cardPriorityPreemptive.setVisibility(
                isPreemptive ? View.VISIBLE : View.GONE
        );

        if (isPreemptive) {

            textSelectedCategory.setText("Preemptive");

            textCategoryDescription.setText(
                    "These algorithms can interrupt a running "
                            + "process. Select one to enter process "
                            + "information and generate its "
                            + "scheduling result."
            );

        } else {

            textSelectedCategory.setText("Non-Preemptive");

            textCategoryDescription.setText(
                    "These algorithms run a process to completion "
                            + "once started. Select one to enter "
                            + "process information and generate its "
                            + "scheduling result."
            );
        }

        cardFcfs.setOnClickListener(
                view -> openProcessInput(MainActivity.ALGORITHM_FCFS)
        );

        cardSjf.setOnClickListener(
                view -> openProcessInput(MainActivity.ALGORITHM_SJF)
        );

        cardPriority.setOnClickListener(
                view -> openProcessInput(MainActivity.ALGORITHM_PRIORITY)
        );

        cardRoundRobin.setOnClickListener(
                view -> openProcessInput(MainActivity.ALGORITHM_ROUND_ROBIN)
        );

        cardSjfPreemptive.setOnClickListener(
                view -> openProcessInput(MainActivity.ALGORITHM_SJF_PREEMPTIVE)
        );

        cardPriorityPreemptive.setOnClickListener(
                view -> openProcessInput(MainActivity.ALGORITHM_PRIORITY_PREEMPTIVE)
        );
    }

    /**
     * Opens the process-input screen and sends the selected algorithm.
     */
    private void openProcessInput(String algorithm) {

        Intent intent = new Intent(
                AlgorithmSelectionActivity.this,
                ProcessInputActivity.class
        );

        intent.putExtra(MainActivity.EXTRA_ALGORITHM, algorithm);

        startActivity(intent);
    }
}
