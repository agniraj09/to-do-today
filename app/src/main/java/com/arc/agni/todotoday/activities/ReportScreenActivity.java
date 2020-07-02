package com.arc.agni.todotoday.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.model.Task;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_HIGH;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LOW;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_MEDIUM;
import static com.arc.agni.todotoday.constants.AppConstants.TEST_DEVICE_ID;

public class ReportScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(ReportScreenActivity.this, R.color.pure_white));
        }

        populateReport();

        // Initialize MobileAds & Request for ads
        AdView mAdView = findViewById(R.id.rs_adView);
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice(TEST_DEVICE_ID).build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /** This method is used to populate the below details in Report screen.
     *  1. Tasks created -> Total, Low P., Medium P., High P.
     *  2. Tasks Pending -> Total, Low P., Medium P., High P.
     *  3. Tasks Completed -> Total, Low P., Medium P., High P.
     *  */
    private void populateReport() {
        List<Task> totalTasks = new ArrayList<>(HomeScreenActivity.taskList);
        List<Task> completedTasks = calculateCompletedTasks(totalTasks);
        List<Task> pendingTasks = new ArrayList<>(totalTasks);
        pendingTasks.removeAll(completedTasks);

        Map<String, Integer> priorityDistributionTotal = calculatePriorityTasks(totalTasks);
        Map<String, Integer> priorityDistributionCompleted = calculatePriorityTasks(completedTasks);
        Map<String, Integer> priorityDistributionPending = calculatePriorityTasks(pendingTasks);

        ((TextView) findViewById(R.id.tasks_created_total)).setText(String.valueOf(totalTasks.size()));
        ((TextView) findViewById(R.id.tasks_completed_total)).setText(String.valueOf(completedTasks.size()));
        ((TextView) findViewById(R.id.tasks_pending_total)).setText(String.valueOf(pendingTasks.size()));

        ((TextView) findViewById(R.id.tasks_created_high_count)).setText(String.valueOf(priorityDistributionTotal.get(PRIORITY_HIGH)));
        ((TextView) findViewById(R.id.tasks_created_medium_count)).setText(String.valueOf(priorityDistributionTotal.get(PRIORITY_MEDIUM)));
        ((TextView) findViewById(R.id.tasks_created_low_count)).setText(String.valueOf(priorityDistributionTotal.get(PRIORITY_LOW)));

        ((TextView) findViewById(R.id.tasks_completed_high_count)).setText(String.valueOf(priorityDistributionCompleted.get(PRIORITY_HIGH)));
        ((TextView) findViewById(R.id.tasks_completed_medium_count)).setText(String.valueOf(priorityDistributionCompleted.get(PRIORITY_MEDIUM)));
        ((TextView) findViewById(R.id.tasks_completed_low_count)).setText(String.valueOf(priorityDistributionCompleted.get(PRIORITY_LOW)));

        ((TextView) findViewById(R.id.tasks_pending_high_count)).setText(String.valueOf(priorityDistributionPending.get(PRIORITY_HIGH)));
        ((TextView) findViewById(R.id.tasks_pending_medium_count)).setText(String.valueOf(priorityDistributionPending.get(PRIORITY_MEDIUM)));
        ((TextView) findViewById(R.id.tasks_pending_low_count)).setText(String.valueOf(priorityDistributionPending.get(PRIORITY_LOW)));
    }

    private List<Task> calculateCompletedTasks(List<Task> tasks) {
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (null != task && task.isTaskCompleted()) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    private Map<String, Integer> calculatePriorityTasks(List<Task> tasks) {
        Map<String, Integer> priorityDistribution = new HashMap<>();
        int highPriority = 0;
        int mediumPriority = 0;
        int lowPriority = 0;
        for (Task task : tasks) {
            if (null != task) {
                if (PRIORITY_HIGH.equalsIgnoreCase(task.getPriority())) {
                    highPriority++;
                } else if (PRIORITY_MEDIUM.equalsIgnoreCase(task.getPriority())) {
                    mediumPriority++;
                } else {
                    lowPriority++;
                }
            }
        }
        priorityDistribution.put(PRIORITY_HIGH, highPriority);
        priorityDistribution.put(PRIORITY_MEDIUM, mediumPriority);
        priorityDistribution.put(PRIORITY_LOW, lowPriority);
        return priorityDistribution;
    }

}
