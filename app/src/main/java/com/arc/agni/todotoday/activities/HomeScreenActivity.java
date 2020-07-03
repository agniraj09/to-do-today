package com.arc.agni.todotoday.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.adapter.SliderPagerAdapter;
import com.arc.agni.todotoday.adapter.TaskAdapter;
import com.arc.agni.todotoday.helper.PrefManager;
import com.arc.agni.todotoday.helper.SwipeToDeleteCallback;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static com.arc.agni.todotoday.constants.AppConstants.REDIRECTED_FROM_ADD_NEW_TASK;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_COMPLETED;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_DELETED;
import static com.arc.agni.todotoday.constants.AppConstants.TEST_DEVICE_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TITLE_TO_DO_LIST_TODAY;
import static com.arc.agni.todotoday.constants.AppConstants.UNDO;

public class HomeScreenActivity extends AppCompatActivity {

    // First Time activity
    private ViewPager viewPager;
    private Button button;
    private SliderPagerAdapter adapter;
    private PrefManager prefManager;

    public static List<Task> taskList = new ArrayList<>();
    public TaskAdapter taskAdapter;
    LinearLayoutManager layoutManager;
    public RecyclerView recyclerView;
    FloatingActionButton addNewButton;
    ConstraintLayout homeScreen;
    CardView titleCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            // making activity full screen
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().getDecorView()
                        .setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            setContentView(R.layout.activity_intro_slide);

            // bind views
            viewPager = findViewById(R.id.pagerIntroSlider);
            TabLayout tabLayout = findViewById(R.id.tabs);
            button = findViewById(R.id.button);
            // init slider pager adapter
            adapter = new SliderPagerAdapter(getSupportFragmentManager(),
                    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            // set adapter
            viewPager.setAdapter(adapter);
            // set dot indicators
            tabLayout.setupWithViewPager(viewPager);
            // make status bar transparent
            changeStatusBarColor();
            button.setOnClickListener(view -> {
                if (viewPager.getCurrentItem() < adapter.getCount() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    launchHomeScreen();
                }
            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (position == adapter.getCount() - 1) {
                        button.setText(R.string.get_started);
                    } else {
                        button.setText(R.string.next);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        } else {
            setContentView(R.layout.activity_home_screen);
            setTitle(TITLE_TO_DO_LIST_TODAY);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.pure_white));
            }

            addNewButton = findViewById(R.id.addnewtask);
            homeScreen = findViewById(R.id.homescreen_layout);
            titleCard = findViewById(R.id.hs_title_card);
            recyclerView = findViewById(R.id.task_list_recyclerview);
            layoutManager = new LinearLayoutManager(HomeScreenActivity.this);
            populateTaskList();
            enableSwipeToDeleteAndUndo();

            if (taskList.size() > 0) {
                //placing toolbar in place of actionbar
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            }

            showOrHideChilloutLayout();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (layoutManager.findFirstVisibleItemPosition() > 0) {
                        titleCard.setCardElevation(10.0F);
                    } else {
                        titleCard.setCardElevation(0.0F);
                    }
                }
            });

            // INTENT EXTRA
            if (null != getIntent().getStringExtra(REDIRECTED_FROM_ADD_NEW_TASK)) {
                showSnackBar(getIntent().getStringExtra(REDIRECTED_FROM_ADD_NEW_TASK));
            }

            // Initialize MobileAds & Request for ads
            AdView mAdView = findViewById(R.id.hs_adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(TEST_DEVICE_ID).build();
            // AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    /**
     * This method is called when a launches the app & has already created tasks. This method is responsible for populatingg the tasks using recyclerview.
     */
    public void populateTaskList() {
        taskList = TaskHelper.getAllTasksFromDB(this);
        taskList = deleteOlderTasks();
        if (taskList.size() > 0) {
            taskAdapter = new TaskAdapter(taskList, this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(taskAdapter);
        }
    }

    /**
     * This method deletes tasks which are created with 'Auto delete by EOD'.This checks if today comes after dateCreated.
     */
    public List<Task> deleteOlderTasks() {
        List<Task> validTasks = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        Calendar dateCreated = Calendar.getInstance(); // Will be changed to correct value in loop below
        for (Task task : taskList) {
            if (task.isAutoDeleteByEOD() && null != task.getDateCreated()) {
                dateCreated.setTime(task.getDateCreated().getTime());
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

    /**
     * This method is responsible for *Deleting a task when a user swipes right to left & *Mark tasks completed when a user swipes left to right.
     */
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
                        showOrHideChilloutLayout();
                    });
                    snackbar.show();
                    showOrHideChilloutLayout();
                }
                // Mark Task Completed
                else if (direction == ItemTouchHelper.RIGHT) {

                    // Mark completed logic
                    currentTask.setTaskCompleted(true);
                    currentTask.setCompletedDate(Calendar.getInstance());
                    taskAdapter.markTaskCompletionStatus(position, currentTask.getId(), true);

                    // Undo mark logic
                    Snackbar snackbar = Snackbar.make(homeScreen, TASK_COMPLETED, Snackbar.LENGTH_LONG).setActionTextColor(Color.YELLOW);
                    snackbar.setAction(UNDO, view -> {
                        currentTask.setTaskCompleted(false);
                        currentTask.setCompletedDate(null);
                        taskAdapter.markTaskCompletionStatus(position, currentTask.getId(), false);
                    });
                    snackbar.show();
                }
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    /**
     * This method navigates user to Reports screen.
     */
    public void gotoReportsScreen() {
        Intent reportsIntent = new Intent(HomeScreenActivity.this, ReportScreenActivity.class);
        startActivity(reportsIntent);
    }

    /**
     * This method deletes all the tasks in one shot.
     */
    public void deleteAllTasks() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this)
                .setMessage("Are you sure want to delete all tasks ?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (arg0, arg1) -> {
                    taskAdapter.deleteAllTasks();
                    Objects.requireNonNull(getSupportActionBar()).hide();
                    showOrHideChilloutLayout();
                });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * This is used to show Snackbar whenever a user creates or edits a task.
     */
    public void showSnackBar(String message) {
        Snackbar.make(homeScreen, message, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    /**
     * This method is used to show the 'chill layout' which will be visible when there is no task created.
     */
    public void showOrHideChilloutLayout() {
        if (taskList.size() > 0) {
            findViewById(R.id.no_tasks_view).setVisibility(View.GONE);
        } else {
            findViewById(R.id.no_tasks_view).setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method is called when user launched the app first time and viewed all the slides and clicked 'Get Started'. Basically a refresh.
     */
    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        finish();
        startActivity(getIntent());
    }

    /**
     * This method is invoked only when the app is launched for the first time. This changes the status bar color to transparent.
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tasks_overview:
                gotoReportsScreen();
                break;

            case R.id.delete_all:
                deleteAllTasks();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.homescreen_menu, menu);
        return true;
    }
}