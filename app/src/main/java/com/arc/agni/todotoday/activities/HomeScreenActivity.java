package com.arc.agni.todotoday.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.adapter.TaskAdapter;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.arc.agni.todotoday.constants.AppConstants.TITLE_TO_DO_LIST_TODAY;

public class HomeScreenActivity extends AppCompatActivity {

    public static List<Task> taskList = new ArrayList<>();
    public TaskAdapter taskAdapter;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setTitle(TITLE_TO_DO_LIST_TODAY);

        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(HomeScreenActivity.this, R.color.pure_white));
        }

        populateTaskList();
    }

    public void populateTaskList() {
        taskList = TaskHelper.getAllTasksFromDB(this);
        taskList = deleteOlderTasks();
        if (taskList.size() > 0) {
            taskAdapter = new TaskAdapter(taskList, this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeScreenActivity.this);
            recyclerView = findViewById(R.id.task_list_recyclerview);
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

}
