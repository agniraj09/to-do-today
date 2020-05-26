package com.arc.agni.todotoday.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.adapter.TaskAdapter;
import com.arc.agni.todotoday.helper.SwipeToDeleteCallback;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.arc.agni.todotoday.constants.AppConstants.TASK_COMPLETED;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_DELETED;
import static com.arc.agni.todotoday.constants.AppConstants.TEST_DEVICE_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TITLE_TO_DO_LIST_TODAY;
import static com.arc.agni.todotoday.constants.AppConstants.UNDO;

public class HomeScreenActivity extends AppCompatActivity {

    public static List<Task> taskList = new ArrayList<>();
    public TaskAdapter taskAdapter;
    public RecyclerView recyclerView;
    FloatingActionButton addNewButton;
    RelativeLayout homeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setTitle(TITLE_TO_DO_LIST_TODAY);

        Objects.requireNonNull(getSupportActionBar()).hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.pure_white));
        }

        addNewButton = findViewById(R.id.addnewtask);
        homeScreen = findViewById(R.id.homescreen_layout);
        recyclerView = findViewById(R.id.task_list_recyclerview);
        populateTaskList();
        enableSwipeToDeleteAndUndo();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: {
                        addNewButton.setVisibility(View.INVISIBLE);
                        break;
                    }
                    default: {
                        addNewButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        // Initialize MobileAds & Request for ads
        AdView mAdView = findViewById(R.id.hs_adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(TEST_DEVICE_ID).build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void populateTaskList() {
        taskList = TaskHelper.getAllTasksFromDB(this);
        taskList = deleteOlderTasks();
        if (taskList.size() > 0) {
            taskAdapter = new TaskAdapter(taskList, this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeScreenActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(taskAdapter);

        }
    }

    // Delete tasks which are created with 'Auto delete by EOD'.This checks if today comes after dateCreated.
    public List<Task> deleteOlderTasks() {
        List<Task> validTasks = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        Calendar dateCreated = Calendar.getInstance(); // Will be changed to correct value in loop below
        for (Task task : taskList) {
            if (task.isAutoDeleteByEOD() && null != task.getDateCreated()) {
                dateCreated.setTime(task.getDateCreated());
                if (today.get(Calendar.YEAR) == dateCreated.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == dateCreated.get(Calendar.DAY_OF_YEAR)) {
                    validTasks.add(task);
                } else {
                    TaskHelper.deleteTask(this, task.getId());
                }
            } else {
                validTasks.add(task);
            }
        }
        return validTasks;
    }

    public void createNewTask(View view) {
        Intent addNewTask = new Intent(HomeScreenActivity.this, AddNewTaskActivity.class);
        startActivity(addNewTask);
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Task currentTask = taskAdapter.getData().get(position);

                // Delete Task
                if (direction == ItemTouchHelper.LEFT) {
                    taskAdapter.deleteTask(position, currentTask.getId());
                    Snackbar snackbar = Snackbar.make(homeScreen, TASK_DELETED, Snackbar.LENGTH_LONG).setActionTextColor(Color.YELLOW);
                    snackbar.setAction(UNDO, view -> {
                        taskAdapter.restoreItem(HomeScreenActivity.this, currentTask, position);
                        recyclerView.scrollToPosition(position);
                    });
                    snackbar.show();
                }
                // Mark Task Completed
                else if (direction == ItemTouchHelper.RIGHT) {

                    new AlertDialog.Builder(HomeScreenActivity.this)
                            .setTitle("Mark Completed")
                            .setMessage("Nice, Have you completed the task?")
                            .setNegativeButton("No", (arg0, arg1) -> taskAdapter.refreshItem(position))
                            .setPositiveButton("Yes", (arg0, arg1) -> {
                                // Mark completed logic
                                currentTask.setTaskCompleted(true);
                                taskAdapter.markTaskCompletionStatus(position, currentTask.getId(), true);
                                Snackbar.make(homeScreen, TASK_COMPLETED, Snackbar.LENGTH_LONG).setActionTextColor(Color.YELLOW).show();

                                // Undo mark logic
                                Snackbar snackbar = Snackbar.make(homeScreen, TASK_COMPLETED, Snackbar.LENGTH_LONG).setActionTextColor(Color.YELLOW);
                                snackbar.setAction(UNDO, view -> {
                                    currentTask.setTaskCompleted(false);
                                    taskAdapter.markTaskCompletionStatus(position, currentTask.getId(), false);
                                });
                                snackbar.show();
                            }).create().show();
                }
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
}
