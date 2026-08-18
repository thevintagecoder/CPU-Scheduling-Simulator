package com.example.cpuschedulingsimulator.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
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
 *
 * Every algorithm except Hybrid produces a single ScheduleResult,
 * shown using the static views declared in activity_result.xml.
 * Hybrid produces two results (SJF-primary and Priority-primary);
 * the first uses those same static views, and the second is built
 * dynamically, in the same visual style, and appended above the
 * Back button.
 */
public class ResultActivity extends AppCompatActivity {

    private LinearLayout rootContainer;

    private TextView textAverageWaiting;
    private TextView textAverageTurnaround;
    private TableLayout resultTable;
    private LinearLayout ganttChartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        TextView textResultAlgorithm =
                findViewById(R.id.textResultAlgorithm);

        textAverageWaiting =
                findViewById(R.id.textAverageWaiting);

        textAverageTurnaround =
                findViewById(R.id.textAverageTurnaround);

        resultTable =
                findViewById(R.id.resultTable);

        ganttChartContainer =
                findViewById(R.id.ganttChartContainer);

        Button buttonBack =
                findViewById(R.id.buttonBack);

        rootContainer =
                (LinearLayout) buttonBack.getParent();

        String selectedAlgorithm =
                getIntent().getStringExtra(
                        MainActivity.EXTRA_ALGORITHM
                );

        if (selectedAlgorithm == null) {
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

        buttonBack.setOnClickListener(
                view -> finish()
        );

        if (MainActivity.ALGORITHM_HYBRID.equals(selectedAlgorithm)) {
            displayHybridResults();
        } else {
            displaySingleResult(selectedAlgorithm);
        }
    }

    /**
     * Renders the normal, single-scheduler result using the
     * static views declared in activity_result.xml.
     */
    private void displaySingleResult(String selectedAlgorithm) {

        ScheduleResult result =
                readScheduleResult(ProcessInputActivity.EXTRA_RESULT);

        if (result == null) {
            Toast.makeText(
                    this,
                    "The scheduling result could not be loaded.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        boolean showPriority =
                requiresPriorityColumn(selectedAlgorithm);

        populateResultSection(
                textAverageWaiting,
                textAverageTurnaround,
                resultTable,
                ganttChartContainer,
                result,
                showPriority
        );
    }

    /**
     * Renders both Hybrid results: the first reuses the static
     * views (same as any other algorithm), the second is built
     * dynamically and inserted right above the Back button.
     */
    private void displayHybridResults() {

        ScheduleResult hybridSjfResult =
                readScheduleResult(
                        ProcessInputActivity.EXTRA_RESULT_HYBRID_SJF
                );

        ScheduleResult hybridPriorityResult =
                readScheduleResult(
                        ProcessInputActivity.EXTRA_RESULT_HYBRID_PRIORITY
                );

        if (hybridSjfResult == null || hybridPriorityResult == null) {
            Toast.makeText(
                    this,
                    "The scheduling result could not be loaded.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        /*
         * Hybrid always needs the Priority column, since priority
         * is used as a tie-breaker for both results.
         */
        insertSectionLabel(
                "Hybrid — SJF primary (ties broken by Priority)",
                indexOfStatRow()
        );

        populateResultSection(
                textAverageWaiting,
                textAverageTurnaround,
                resultTable,
                ganttChartContainer,
                hybridSjfResult,
                true
        );

        appendHybridPrioritySection(hybridPriorityResult);
    }

    /**
     * Finds the index, within rootContainer, of the horizontal
     * stat-row LinearLayout that holds the average-waiting and
     * average-turnaround tiles.
     */
    private int indexOfStatRow() {

        View statRow =
                (View) textAverageWaiting.getParent().getParent();

        return rootContainer.indexOfChild(statRow);
    }

    /**
     * Inserts a small eyebrow-style label at the given position
     * in rootContainer, matching the app's existing eyebrow text
     * style (accent_cyan, bold, 15sp).
     */
    private void insertSectionLabel(String text, int atIndex) {

        TextView label = new TextView(this);

        label.setText(text);

        label.setTextColor(
                ContextCompat.getColor(this, R.color.accent_cyan)
        );

        label.setTextSize(15);

        label.setTypeface(
                label.getTypeface(),
                android.graphics.Typeface.BOLD
        );

        LinearLayout.LayoutParams labelParameters =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        labelParameters.topMargin = dp(20);

        rootContainer.addView(label, atIndex, labelParameters);
    }

    /**
     * Builds a second, fully self-contained result section
     * (in the same visual style as the first) for the
     * Priority-primary Hybrid result, and inserts it right
     * before the Back button.
     */
    private void appendHybridPrioritySection(ScheduleResult result) {

        int insertIndex =
                rootContainer.indexOfChild(
                        findViewById(R.id.buttonBack)
                );

        insertSectionLabel(
                "Hybrid — Priority primary (ties broken by SJF)",
                insertIndex
        );

        insertIndex++;

        StatRow statRow = buildStatRow();

        rootContainer.addView(statRow.view, insertIndex++);

        TextView tableHeading =
                buildHeading("Process Results", 30);

        rootContainer.addView(tableHeading, insertIndex++);

        TextView tableHint =
                buildHint("Swipe horizontally to view every column.");

        rootContainer.addView(tableHint, insertIndex++);

        TableLayout sectionTable = new TableLayout(this);

        HorizontalScrollView tableScroll =
                wrapInHorizontalScroll(sectionTable, 12);

        rootContainer.addView(tableScroll, insertIndex++);

        TextView ganttHeading =
                buildHeading("Gantt Chart", 30);

        rootContainer.addView(ganttHeading, insertIndex++);

        TextView ganttHint =
                buildHint(
                        "Swipe horizontally when the timeline "
                                + "is wider than the screen."
                );

        rootContainer.addView(ganttHint, insertIndex++);

        LinearLayout sectionGanttContainer = new LinearLayout(this);
        sectionGanttContainer.setOrientation(LinearLayout.HORIZONTAL);

        HorizontalScrollView ganttScroll =
                wrapInHorizontalScroll(sectionGanttContainer, 14);

        rootContainer.addView(ganttScroll, insertIndex);

        populateResultSection(
                statRow.averageWaiting,
                statRow.averageTurnaround,
                sectionTable,
                sectionGanttContainer,
                result,
                true
        );
    }

    /**
     * Holds the row view together with direct references to its
     * two value TextViews, avoiding duplicate view ids.
     */
    private static final class StatRow {
        final LinearLayout view;
        final TextView averageWaiting;
        final TextView averageTurnaround;

        StatRow(
                LinearLayout view,
                TextView averageWaiting,
                TextView averageTurnaround
        ) {
            this.view = view;
            this.averageWaiting = averageWaiting;
            this.averageTurnaround = averageTurnaround;
        }
    }

    /**
     * Builds a stat row identical in style to the one declared in
     * activity_result.xml.
     */
    private StatRow buildStatRow() {

        LinearLayout statRow = new LinearLayout(this);

        statRow.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams rowParameters =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        rowParameters.topMargin = dp(20);

        statRow.setLayoutParams(rowParameters);

        TextView[] waitingTileValue = new TextView[1];
        TextView[] turnaroundTileValue = new TextView[1];

        LinearLayout waitingTile = buildStatTile(
                R.color.accent_cyan,
                "Average Waiting Time",
                true,
                waitingTileValue
        );

        LinearLayout turnaroundTile = buildStatTile(
                R.color.process_3,
                "Average Turnaround Time",
                false,
                turnaroundTileValue
        );

        statRow.addView(waitingTile);
        statRow.addView(turnaroundTile);

        return new StatRow(
                statRow,
                waitingTileValue[0],
                turnaroundTileValue[0]
        );
    }

    private LinearLayout buildStatTile(
            int valueColorRes,
            String label,
            boolean isFirstTile,
            TextView[] valueViewOut
    ) {

        LinearLayout tile = new LinearLayout(this);

        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);

        tile.setBackgroundColor(
                ContextCompat.getColor(this, R.color.card_background)
        );

        tile.setPadding(dp(12), dp(12), dp(12), dp(12));

        LinearLayout.LayoutParams tileParameters =
                new LinearLayout.LayoutParams(
                        0,
                        dp(110),
                        1f
                );

        if (isFirstTile) {
            tileParameters.rightMargin = dp(7);
        } else {
            tileParameters.leftMargin = dp(7);
        }

        tile.setLayoutParams(tileParameters);

        TextView valueText = new TextView(this);

        valueText.setText("0.00");
        valueText.setTextColor(
                ContextCompat.getColor(this, valueColorRes)
        );
        valueText.setTextSize(26);
        valueText.setTypeface(
                valueText.getTypeface(),
                android.graphics.Typeface.BOLD
        );

        valueViewOut[0] = valueText;

        TextView labelText = new TextView(this);

        labelText.setText(label);
        labelText.setGravity(Gravity.CENTER);
        labelText.setTextColor(
                ContextCompat.getColor(this, R.color.text_secondary)
        );
        labelText.setTextSize(12);

        LinearLayout.LayoutParams labelParameters =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        labelParameters.topMargin = dp(7);

        tile.addView(valueText);
        tile.addView(labelText, labelParameters);

        return tile;
    }

    private TextView buildHeading(String text, int topMarginDp) {

        TextView heading = new TextView(this);

        heading.setText(text);
        heading.setTextColor(
                ContextCompat.getColor(this, R.color.text_primary)
        );
        heading.setTextSize(19);
        heading.setTypeface(
                heading.getTypeface(),
                android.graphics.Typeface.BOLD
        );

        LinearLayout.LayoutParams headingParameters =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        headingParameters.topMargin = dp(topMarginDp);

        heading.setLayoutParams(headingParameters);

        return heading;
    }

    private TextView buildHint(String text) {

        TextView hint = new TextView(this);

        hint.setText(text);
        hint.setTextColor(
                ContextCompat.getColor(this, R.color.text_secondary)
        );
        hint.setTextSize(13);

        LinearLayout.LayoutParams hintParameters =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        hintParameters.topMargin = dp(5);

        hint.setLayoutParams(hintParameters);

        return hint;
    }

    private HorizontalScrollView wrapInHorizontalScroll(
            View child,
            int topMarginDp
    ) {

        HorizontalScrollView scrollView =
                new HorizontalScrollView(this);

        LinearLayout.LayoutParams scrollParameters =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        scrollParameters.topMargin = dp(topMarginDp);

        scrollView.setLayoutParams(scrollParameters);
        scrollView.setFillViewport(false);

        scrollView.addView(
                child,
                new HorizontalScrollView.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        return scrollView;
    }

    /**
     * Formats the averages and builds the table and Gantt chart
     * for one ScheduleResult into the given target views.
     */
    private void populateResultSection(
            TextView averageWaitingView,
            TextView averageTurnaroundView,
            TableLayout table,
            LinearLayout ganttContainer,
            ScheduleResult result,
            boolean showPriority
    ) {

        averageWaitingView.setText(
                String.format(
                        Locale.US,
                        "%.2f",
                        result.getAverageWaitingTime()
                )
        );

        averageTurnaroundView.setText(
                String.format(
                        Locale.US,
                        "%.2f",
                        result.getAverageTurnaroundTime()
                )
        );

        displayResultTable(
                table,
                result.getProcesses(),
                showPriority
        );

        displayGanttChart(
                ganttContainer,
                result.getGanttBlocks()
        );
    }

    /**
     * True for every algorithm whose result table should include
     * the Priority column, either because priority is the main
     * rule or because it is used as a tie-breaker.
     */
    private boolean requiresPriorityColumn(String algorithm) {

        return MainActivity.ALGORITHM_PRIORITY.equals(algorithm)
                || MainActivity.ALGORITHM_PRIORITY_PREEMPTIVE.equals(algorithm);
    }

    /**
     * Reads the ScheduleResult sent by ProcessInputActivity under
     * the given intent extra key.
     */
    @SuppressWarnings("deprecation")
    private ScheduleResult readScheduleResult(String extraKey) {

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.TIRAMISU) {

            return getIntent().getSerializableExtra(
                    extraKey,
                    ScheduleResult.class
            );
        }

        return (ScheduleResult)
                getIntent().getSerializableExtra(extraKey);
    }

    /**
     * Builds the process result table dynamically.
     */
    private void displayResultTable(
            TableLayout table,
            List<CpuProcess> processes,
            boolean showPriority
    ) {

        table.removeAllViews();

        if (showPriority) {
            addTableRow(
                    table,
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
                    table,
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
                        table,
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
                        table,
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
     * Adds one row to the given result table.
     */
    private void addTableRow(
            TableLayout table,
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

        table.addView(row);
    }

    /**
     * Creates one visible Gantt block for every execution interval
     * inside the given target container.
     */
    private void displayGanttChart(
            LinearLayout container,
            List<GanttBlock> ganttBlocks
    ) {

        container.removeAllViews();

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

            container.addView(
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
