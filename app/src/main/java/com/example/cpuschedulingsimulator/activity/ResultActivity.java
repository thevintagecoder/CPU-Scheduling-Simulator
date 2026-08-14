package com.example.cpuschedulingsimulator.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cpuschedulingsimulator.MainActivity;
import com.example.cpuschedulingsimulator.R;
import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.GanttBlock;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.List;
import java.util.Locale;

/**
 * Displays the scheduling result returned by the backend.
 */
public class ResultActivity extends AppCompatActivity {

    private TableLayout resultTable;
    private LinearLayout ganttChartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        TextView textResultAlgorithm =
                findViewById(R.id.textResultAlgorithm);

        TextView textAverageWaiting =
                findViewById(R.id.textAverageWaiting);

        TextView textAverageTurnaround =
                findViewById(R.id.textAverageTurnaround);

        resultTable =
                findViewById(R.id.resultTable);

        ganttChartContainer =
                findViewById(R.id.ganttChartContainer);

        Button buttonBack =
                findViewById(R.id.buttonBack);

        String selectedAlgorithm =
                getIntent().getStringExtra(
                        MainActivity.EXTRA_ALGORITHM
                );

        ScheduleResult result =
                readScheduleResult();

        if (selectedAlgorithm == null || result == null) {
            Toast.makeText(
                    this,
                    "The scheduling result could not be loaded.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        textResultAlgorithm.setText(
                getReadableAlgorithmName(selectedAlgorithm)
        );

        textAverageWaiting.setText(
                String.format(
                        Locale.US,
                        "%.2f",
                        result.getAverageWaitingTime()
                )
        );

        textAverageTurnaround.setText(
                String.format(
                        Locale.US,
                        "%.2f",
                        result.getAverageTurnaroundTime()
                )
        );

        boolean showPriority =
                MainActivity.ALGORITHM_PRIORITY.equals(
                        selectedAlgorithm
                )
                || MainActivity.ALGORITHM_PREEMPTIVE_PRIORITY.equals(
                        selectedAlgorithm
                );

        displayResultTable(
                result.getProcesses(),
                showPriority
        );

        displayGanttChart(
                result.getGanttBlocks()
        );

        buttonBack.setOnClickListener(
                view -> finish()
        );
    }

    /**
     * Reads the ScheduleResult sent by ProcessInputActivity.
     */
    @SuppressWarnings("deprecation")
    private ScheduleResult readScheduleResult() {

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.TIRAMISU) {

            return getIntent().getSerializableExtra(
                    ProcessInputActivity.EXTRA_RESULT,
                    ScheduleResult.class
            );
        }

        return (ScheduleResult)
                getIntent().getSerializableExtra(
                        ProcessInputActivity.EXTRA_RESULT
                );
    }

    /**
     * Builds the process result table dynamically.
     */
    private void displayResultTable(
            List<CpuProcess> processes,
            boolean showPriority
    ) {

        resultTable.removeAllViews();

        if (showPriority) {
            addTableRow(
                    true,
                    "Process",
                    "AT",
                    "BT",
                    "Priority",
                    "ST",
                    "CT",
                    "TAT",
                    "WT"
            );
        } else {
            addTableRow(
                    true,
                    "Process",
                    "AT",
                    "BT",
                    "ST",
                    "CT",
                    "TAT",
                    "WT"
            );
        }

        for (CpuProcess process : processes) {

            if (showPriority) {

                addTableRow(
                        false,
                        process.getProcessId(),
                        String.valueOf(
                                process.getArrivalTime()
                        ),
                        String.valueOf(
                                process.getBurstTime()
                        ),
                        String.valueOf(
                                process.getPriority()
                        ),
                        String.valueOf(
                                process.getStartTime()
                        ),
                        String.valueOf(
                                process.getCompletionTime()
                        ),
                        String.valueOf(
                                process.getTurnaroundTime()
                        ),
                        String.valueOf(
                                process.getWaitingTime()
                        )
                );

            } else {

                addTableRow(
                        false,
                        process.getProcessId(),
                        String.valueOf(
                                process.getArrivalTime()
                        ),
                        String.valueOf(
                                process.getBurstTime()
                        ),
                        String.valueOf(
                                process.getStartTime()
                        ),
                        String.valueOf(
                                process.getCompletionTime()
                        ),
                        String.valueOf(
                                process.getTurnaroundTime()
                        ),
                        String.valueOf(
                                process.getWaitingTime()
                        )
                );
            }
        }
    }

    /**
     * Adds one row to the result table.
     */
    private void addTableRow(
            boolean isHeader,
            String... values
    ) {

        TableRow row = new TableRow(this);

        for (String value : values) {

            TextView cell = new TextView(this);

            cell.setText(value);
            cell.setGravity(Gravity.CENTER);
            cell.setTextSize(13);
            cell.setPadding(
                    dp(12),
                    dp(13),
                    dp(12),
                    dp(13)
            );

            if (isHeader) {

                cell.setBackgroundColor(
                        ContextCompat.getColor(
                                this,
                                R.color.accent_cyan
                        )
                );

                cell.setTextColor(
                        ContextCompat.getColor(
                                this,
                                R.color.accent_text
                        )
                );

                cell.setTypeface(
                        cell.getTypeface(),
                        android.graphics.Typeface.BOLD
                );

            } else {

                cell.setBackgroundColor(
                        ContextCompat.getColor(
                                this,
                                R.color.card_background
                        )
                );

                cell.setTextColor(
                        ContextCompat.getColor(
                                this,
                                R.color.text_primary
                        )
                );
            }

            TableRow.LayoutParams cellParameters =
                    new TableRow.LayoutParams(
                            dp(88),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            cellParameters.setMargins(
                    dp(1),
                    dp(1),
                    dp(1),
                    dp(1)
            );

            row.addView(
                    cell,
                    cellParameters
            );
        }

        resultTable.addView(row);
    }

    /**
     * Creates one visible Gantt block for every execution interval.
     */
    private void displayGanttChart(
            List<GanttBlock> ganttBlocks
    ) {

        ganttChartContainer.removeAllViews();

        for (GanttBlock block : ganttBlocks) {

            LinearLayout blockContainer =
                    new LinearLayout(this);

            blockContainer.setOrientation(
                    LinearLayout.VERTICAL
            );

            blockContainer.setGravity(
                    Gravity.CENTER
            );

            TextView processText =
                    new TextView(this);

            processText.setText(
                    block.getProcessId()
            );

            processText.setGravity(
                    Gravity.CENTER
            );

            processText.setTextSize(16);

            processText.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.accent_cyan
                    )
            );

            processText.setBackgroundResource(
                    R.drawable.gantt_block_background
            );

            processText.setPadding(
                    dp(10),
                    dp(20),
                    dp(10),
                    dp(20)
            );

            TextView timeText =
                    new TextView(this);

            timeText.setText(
                    block.getStartTime()
                            + " → "
                            + block.getEndTime()
            );

            timeText.setGravity(
                    Gravity.CENTER
            );

            timeText.setTextSize(12);

            timeText.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.text_secondary
                    )
            );

            timeText.setPadding(
                    dp(4),
                    dp(7),
                    dp(4),
                    dp(4)
            );

            blockContainer.addView(
                    processText,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            blockContainer.addView(
                    timeText,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            int duration =
                    Math.max(
                            1,
                            block.getEndTime()
                                    - block.getStartTime()
                    );

            int calculatedWidth =
                    dp(45) * duration;

            int blockWidth =
                    Math.max(
                            dp(90),
                            calculatedWidth
                    );

            LinearLayout.LayoutParams blockParameters =
                    new LinearLayout.LayoutParams(
                            blockWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            blockParameters.setMargins(
                    0,
                    0,
                    dp(6),
                    0
            );

            ganttChartContainer.addView(
                    blockContainer,
                    blockParameters
            );
        }
    }

    /**
     * Converts dp measurements to screen pixels.
     */
    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(
                value * density
        );
    }

    private String getReadableAlgorithmName(
            String algorithm
    ) {

        switch (algorithm) {

            case MainActivity.ALGORITHM_FCFS:
                return "First Come First Served";

            case MainActivity.ALGORITHM_SJF:
                return "Shortest Job First";

            case MainActivity.ALGORITHM_SRTF:
                return "Shortest Remaining Time First";

            case MainActivity.ALGORITHM_PRIORITY:
                return "Priority Scheduling";

            case MainActivity.ALGORITHM_PREEMPTIVE_PRIORITY:
                return "Preemptive Priority Scheduling";

            case MainActivity.ALGORITHM_ROUND_ROBIN:
                return "Round Robin";

            default:
                return algorithm;
        }
    }
}