package com.example.cpuschedulingsimulator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cpuschedulingsimulator.MainActivity;
import com.example.cpuschedulingsimulator.R;
import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.ScheduleResult;
import com.example.cpuschedulingsimulator.scheduler.FcfsScheduler;
import com.example.cpuschedulingsimulator.scheduler.HybridPriorityScheduler;
import com.example.cpuschedulingsimulator.scheduler.HybridSjfScheduler;
import com.example.cpuschedulingsimulator.scheduler.PreemptivePriorityScheduler;
import com.example.cpuschedulingsimulator.scheduler.PreemptiveSjfScheduler;
import com.example.cpuschedulingsimulator.scheduler.PriorityScheduler;
import com.example.cpuschedulingsimulator.scheduler.RoundRobinScheduler;
import com.example.cpuschedulingsimulator.scheduler.Scheduler;
import com.example.cpuschedulingsimulator.scheduler.SjfScheduler;

import java.util.ArrayList;

/**
 * Collects process information from the user.
 *
 * This Activity converts the entered values into CpuProcess objects,
 * sends them to the selected scheduler, and opens the result screen.
 */
public class ProcessInputActivity extends AppCompatActivity {

    /**
     * Intent key used to send ScheduleResult to ResultActivity.
     */
    public static final String EXTRA_RESULT =
            "com.example.cpuschedulingsimulator.EXTRA_RESULT";

    /**
     * Intent keys used to send the two Hybrid results to
     * ResultActivity (Hybrid has no single scheduler; both the
     * SJF-primary and Priority-primary results are shown together).
     */
    public static final String EXTRA_RESULT_HYBRID_SJF =
            "com.example.cpuschedulingsimulator.EXTRA_RESULT_HYBRID_SJF";

    public static final String EXTRA_RESULT_HYBRID_PRIORITY =
            "com.example.cpuschedulingsimulator.EXTRA_RESULT_HYBRID_PRIORITY";

    private String selectedAlgorithm;

    private TextView textSelectedAlgorithm;
    private EditText editProcessCount;
    private EditText editTimeQuantum;

    private LinearLayout processRowsContainer;
    private LinearLayout quantumContainer;

    /*
     * Stores references to every dynamically created process row.
     */
    private final ArrayList<View> processRowViews =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_process_input);

        textSelectedAlgorithm =
                findViewById(R.id.textSelectedAlgorithm);

        editProcessCount =
                findViewById(R.id.editProcessCount);

        editTimeQuantum =
                findViewById(R.id.editTimeQuantum);

        processRowsContainer =
                findViewById(R.id.processRowsContainer);

        quantumContainer =
                findViewById(R.id.quantumContainer);

        Button buttonGenerateRows =
                findViewById(R.id.buttonGenerateRows);

        Button buttonCalculate =
                findViewById(R.id.buttonCalculate);

        /*
         * Read the algorithm selected in MainActivity.
         */
        selectedAlgorithm = getIntent().getStringExtra(
                MainActivity.EXTRA_ALGORITHM
        );

        if (selectedAlgorithm == null) {
            Toast.makeText(
                    this,
                    "No scheduling algorithm was selected.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        textSelectedAlgorithm.setText(
                getReadableAlgorithmName(selectedAlgorithm)
        );

        /*
         * Only Round Robin requires a time quantum.
         */
        if (MainActivity.ALGORITHM_ROUND_ROBIN.equals(
                selectedAlgorithm
        )) {
            quantumContainer.setVisibility(View.VISIBLE);
        } else {
            quantumContainer.setVisibility(View.GONE);
        }

        buttonGenerateRows.setOnClickListener(
                view -> generateProcessRows()
        );

        buttonCalculate.setOnClickListener(
                view -> calculateSchedule()
        );

        /*
         * Start with three process rows automatically.
         */
        editProcessCount.setText("3");
        generateProcessRows();
    }

    /**
     * Generates one process card for every requested process.
     */
    private void generateProcessRows() {

        Integer processCount = readPositiveInteger(
                editProcessCount,
                "Number of processes"
        );

        if (processCount == null) {
            return;
        }

        if (processCount > 10) {
            editProcessCount.setError(
                    "Enter between 1 and 10 processes."
            );

            editProcessCount.requestFocus();
            return;
        }

        processRowsContainer.removeAllViews();
        processRowViews.clear();

        for (int i = 1; i <= processCount; i++) {

            View processRow = getLayoutInflater().inflate(
                    R.layout.item_process_input,
                    processRowsContainer,
                    false
            );

            TextView textProcessName =
                    processRow.findViewById(
                            R.id.textProcessName
                    );

            LinearLayout priorityContainer =
                    processRow.findViewById(
                            R.id.priorityContainer
                    );

            textProcessName.setText("P" + i);

            /*
             * Priority Scheduling and every algorithm that uses
             * priority as a tie-breaker need priority input.
             */
            if (requiresPriorityInput(selectedAlgorithm)) {
                priorityContainer.setVisibility(View.VISIBLE);
            } else {
                priorityContainer.setVisibility(View.GONE);
            }

            processRowsContainer.addView(processRow);
            processRowViews.add(processRow);
        }
    }

    /**
     * Reads the fields, creates CpuProcess objects,
     * selects the scheduler, runs the calculation,
     * and opens ResultActivity.
     */
    private void calculateSchedule() {

        if (processRowViews.isEmpty()) {
            Toast.makeText(
                    this,
                    "Generate process rows first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ArrayList<CpuProcess> processes =
                readProcessesFromRows();

        if (processes == null) {
            return;
        }

        if (MainActivity.ALGORITHM_HYBRID.equals(selectedAlgorithm)) {
            calculateHybridSchedule(processes);
            return;
        }

        Scheduler scheduler;

        switch (selectedAlgorithm) {

            case MainActivity.ALGORITHM_FCFS:
                scheduler = new FcfsScheduler();
                break;

            case MainActivity.ALGORITHM_SJF:
                scheduler = new SjfScheduler();
                break;

            case MainActivity.ALGORITHM_PRIORITY:
                scheduler = new PriorityScheduler();
                break;

            case MainActivity.ALGORITHM_SJF_PREEMPTIVE:
                scheduler = new PreemptiveSjfScheduler();
                break;

            case MainActivity.ALGORITHM_PRIORITY_PREEMPTIVE:
                scheduler = new PreemptivePriorityScheduler();
                break;

            case MainActivity.ALGORITHM_ROUND_ROBIN:

                Integer timeQuantum = readPositiveInteger(
                        editTimeQuantum,
                        "Time quantum"
                );

                if (timeQuantum == null) {
                    return;
                }

                scheduler =
                        new RoundRobinScheduler(timeQuantum);

                break;

            default:
                Toast.makeText(
                        this,
                        "Unknown scheduling algorithm.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
        }

        try {

            /*
             * This line sends the entered processes
             * to your tested scheduling backend.
             */
            ScheduleResult result =
                    scheduler.schedule(processes);

            /*
             * Open the result screen.
             */
            Intent intent = new Intent(
                    ProcessInputActivity.this,
                    ResultActivity.class
            );

            /*
             * Send the selected algorithm so ResultActivity
             * knows which algorithm produced the result.
             */
            intent.putExtra(
                    MainActivity.EXTRA_ALGORITHM,
                    selectedAlgorithm
            );

            /*
             * Send the complete ScheduleResult.
             */
            intent.putExtra(
                    EXTRA_RESULT,
                    result
            );

            startActivity(intent);

        } catch (IllegalArgumentException exception) {

            Toast.makeText(
                    this,
                    exception.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Hybrid has no single scheduler: it runs both the SJF-primary
     * and the Priority-primary hybrid rules on the same processes
     * and sends both results to ResultActivity.
     */
    private void calculateHybridSchedule(
            ArrayList<CpuProcess> processes
    ) {

        try {

            ScheduleResult hybridSjfResult =
                    new HybridSjfScheduler().schedule(processes);

            ScheduleResult hybridPriorityResult =
                    new HybridPriorityScheduler().schedule(processes);

            Intent intent = new Intent(
                    ProcessInputActivity.this,
                    ResultActivity.class
            );

            intent.putExtra(
                    MainActivity.EXTRA_ALGORITHM,
                    selectedAlgorithm
            );

            intent.putExtra(
                    EXTRA_RESULT_HYBRID_SJF,
                    hybridSjfResult
            );

            intent.putExtra(
                    EXTRA_RESULT_HYBRID_PRIORITY,
                    hybridPriorityResult
            );

            startActivity(intent);

        } catch (IllegalArgumentException exception) {

            Toast.makeText(
                    this,
                    exception.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Converts every visible input row into one CpuProcess object.
     */
    private ArrayList<CpuProcess> readProcessesFromRows() {

        ArrayList<CpuProcess> processes =
                new ArrayList<>();

        for (View processRow : processRowViews) {

            TextView textProcessName =
                    processRow.findViewById(
                            R.id.textProcessName
                    );

            EditText editArrivalTime =
                    processRow.findViewById(
                            R.id.editArrivalTime
                    );

            EditText editBurstTime =
                    processRow.findViewById(
                            R.id.editBurstTime
                    );

            EditText editPriority =
                    processRow.findViewById(
                            R.id.editPriority
                    );

            Integer arrivalTime =
                    readNonNegativeInteger(
                            editArrivalTime,
                            textProcessName.getText()
                                    + " arrival time"
                    );

            if (arrivalTime == null) {
                return null;
            }

            Integer burstTime =
                    readPositiveInteger(
                            editBurstTime,
                            textProcessName.getText()
                                    + " burst time"
                    );

            if (burstTime == null) {
                return null;
            }

            int priority = 0;

            if (requiresPriorityInput(selectedAlgorithm)) {

                Integer enteredPriority =
                        readNonNegativeInteger(
                                editPriority,
                                textProcessName.getText()
                                        + " priority"
                        );

                if (enteredPriority == null) {
                    return null;
                }

                priority = enteredPriority;
            }

            CpuProcess process = new CpuProcess(
                    textProcessName.getText().toString(),
                    arrivalTime,
                    burstTime,
                    priority
            );

            processes.add(process);
        }

        return processes;
    }

    /**
     * True for every algorithm whose calculation depends on a
     * priority value, either as the main rule or as a tie-breaker.
     */
    private boolean requiresPriorityInput(String algorithm) {

        return MainActivity.ALGORITHM_PRIORITY.equals(algorithm)
                || MainActivity.ALGORITHM_PRIORITY_PREEMPTIVE.equals(algorithm)
                || MainActivity.ALGORITHM_HYBRID.equals(algorithm);
    }

    /**
     * Reads a number that may be zero but cannot be negative.
     */
    private Integer readNonNegativeInteger(
            EditText input,
            String fieldName
    ) {

        String text =
                input.getText().toString().trim();

        if (text.isEmpty()) {
            input.setError(
                    fieldName + " is required."
            );

            input.requestFocus();
            return null;
        }

        try {

            int value = Integer.parseInt(text);

            if (value < 0) {
                input.setError(
                        fieldName + " cannot be negative."
                );

                input.requestFocus();
                return null;
            }

            input.setError(null);
            return value;

        } catch (NumberFormatException exception) {

            input.setError(
                    fieldName + " must be a whole number."
            );

            input.requestFocus();
            return null;
        }
    }

    /**
     * Reads a number that must be greater than zero.
     */
    private Integer readPositiveInteger(
            EditText input,
            String fieldName
    ) {

        String text =
                input.getText().toString().trim();

        if (text.isEmpty()) {
            input.setError(
                    fieldName + " is required."
            );

            input.requestFocus();
            return null;
        }

        try {

            int value = Integer.parseInt(text);

            if (value <= 0) {
                input.setError(
                        fieldName
                                + " must be greater than zero."
                );

                input.requestFocus();
                return null;
            }

            input.setError(null);
            return value;

        } catch (NumberFormatException exception) {

            input.setError(
                    fieldName + " must be a whole number."
            );

            input.requestFocus();
            return null;
        }
    }

    /**
     * Converts the internal algorithm key into a readable screen title.
     */
    private String getReadableAlgorithmName(
            String algorithm
    ) {

        switch (algorithm) {

            case MainActivity.ALGORITHM_FCFS:
                return "First Come First Served";

            case MainActivity.ALGORITHM_SJF:
                return "Shortest Job First";

            case MainActivity.ALGORITHM_PRIORITY:
                return "Priority Scheduling";

            case MainActivity.ALGORITHM_ROUND_ROBIN:
                return "Round Robin";

            case MainActivity.ALGORITHM_SJF_PREEMPTIVE:
                return "Shortest Job First (Preemptive)";

            case MainActivity.ALGORITHM_PRIORITY_PREEMPTIVE:
                return "Priority Scheduling (Preemptive)";

            case MainActivity.ALGORITHM_HYBRID:
                return "Hybrid (SJF + Priority)";

            default:
                return algorithm;
        }
    }
}
