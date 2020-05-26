package com.arc.agni.todotoday.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.helper.DBHelper;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_HIGH;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LOW;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_MEDIUM;
import static com.arc.agni.todotoday.constants.AppConstants.SAVE;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TEST_DEVICE_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TITLE_ADD_TASK;
import static com.arc.agni.todotoday.constants.AppConstants.TITLE_UPDATE_TASK;

public class AddNewTaskActivity extends AppCompatActivity {

    TextInputLayout taskDescriptionValue;
    String priority = PRIORITY_LOW;
    TextView lowPriority;
    TextView mediumPriority;
    TextView highPriority;
    CheckBox autoDeleteCheckBox;
    Button addOrSaveButton;
    boolean isItAnUpdateTask;

    int taskID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(AddNewTaskActivity.this, R.color.pure_white));
        }

        taskDescriptionValue = findViewById(R.id.task_description_value);
        lowPriority = findViewById(R.id.low);
        mediumPriority = findViewById(R.id.medium);
        highPriority = findViewById(R.id.high);
        autoDeleteCheckBox = findViewById(R.id.auto_delete_value);
        addOrSaveButton = findViewById(R.id.add_save_button);

        taskDescriptionValue.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskDescriptionValue.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);

        taskID = getIntent().getIntExtra(TASK_ID, 0);
        if (taskID != 0) {
            isItAnUpdateTask = true;
            prePopulateDataForTaskUpdate(taskID);
            ((TextView) findViewById(R.id.ant_title)).setText(TITLE_UPDATE_TASK);
        }

        taskDescriptionValue.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        // Initialize MobileAds & Request for ads
        AdView mAdView = findViewById(R.id.ant_adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(TEST_DEVICE_ID).build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void changePriority(View view) {
        if (view.getId() == R.id.low) {
            findViewById(R.id.low).setBackgroundResource(R.drawable.ic_priority_low);

            setPriorityAndDehighlightOtheOptions(PRIORITY_LOW, R.id.low, R.id.medium, R.id.high);
        } else if (view.getId() == R.id.medium) {
            findViewById(R.id.medium).setBackgroundResource(R.drawable.ic_priority_medium);
            setPriorityAndDehighlightOtheOptions(PRIORITY_MEDIUM, R.id.medium, R.id.low, R.id.high);
        } else if (view.getId() == R.id.high) {
            findViewById(R.id.high).setBackgroundResource(R.drawable.ic_priority_high);
            setPriorityAndDehighlightOtheOptions(PRIORITY_HIGH, R.id.high, R.id.low, R.id.medium);
        }
    }

    public void setPriorityAndDehighlightOtheOptions(String priorityPassed, int selectedPriority, int priority1ToDeHighlight, int priority2ToDeHighlight) {
        priority = priorityPassed;
        ((TextView) findViewById(selectedPriority)).setTextColor(getResources().getColor(R.color.pure_white));

        findViewById(priority1ToDeHighlight).setBackgroundResource(R.drawable.ic_priority_no);
        ((TextView) findViewById(priority1ToDeHighlight)).setTextColor(getResources().getColor(R.color.pure_black));
        findViewById(priority2ToDeHighlight).setBackgroundResource(R.drawable.ic_priority_no);
        ((TextView) findViewById(priority2ToDeHighlight)).setTextColor(getResources().getColor(R.color.pure_black));
    }

    private void prePopulateDataForTaskUpdate(int taskID) {
        Task task = TaskHelper.getTask(this, taskID);
        taskDescriptionValue.getEditText().setText(task.getDescription());
        View priority = findViewById(PRIORITY_LOW.equalsIgnoreCase(task.getPriority()) ? R.id.low : (PRIORITY_MEDIUM.equalsIgnoreCase(task.getPriority()) ? R.id.medium : R.id.high));
        changePriority(priority);
        autoDeleteCheckBox.setChecked(task.isAutoDeleteByEOD());
        addOrSaveButton.setText(SAVE);
    }

    public boolean isValidDescription(String description) {
        boolean valid;
        if (null != description && !description.trim().isEmpty()) {
            valid = true;
            taskDescriptionValue.setError(null);
        } else {
            valid = false;
            taskDescriptionValue.setError("Description can't be empty");
        }
        return valid;
    }

    public void addOrUpdateTaskToDatabase(View view) {
        String description = taskDescriptionValue.getEditText().getText().toString();
        if (isValidDescription(description)) {
            boolean isAutoDeleteChecked = autoDeleteCheckBox.isChecked();
            Date dateCreated = (isAutoDeleteChecked ? Calendar.getInstance().getTime() : null);
            TaskHelper.addTaskToDatabase(this, taskID, description, priority, isAutoDeleteChecked, dateCreated, false, 0, 0, false, false);
            Intent backToHome = new Intent(this, HomeScreenActivity.class);
            backToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToHome);
        }
    }

    public void hideKeyboard(View view) {
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(this, HomeScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        if (isItAnUpdateTask) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard Edit")
                    .setMessage("Do you want to discard the changes?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", (arg0, arg1) -> {
                        startActivity(homeIntent);
                    }).create().show();
        } else {
            startActivity(homeIntent);
        }
    }
}
