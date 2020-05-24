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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LOW;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_MEDIUM;
import static com.arc.agni.todotoday.constants.AppConstants.SAVE;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TITLE_ADD_TASK;
import static com.arc.agni.todotoday.constants.AppConstants.TITLE_UPDATE_TASK;

public class AddNewTaskActivity extends AppCompatActivity {

    EditText taskDescriptionValue;
    RadioGroup priorityGroup;
    CheckBox autoDeleteCheckBox;
    CheckBox needReminderCheckBox;
    int reminderHour, reminderMinute;
    TextView reminderTimeText;
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
        priorityGroup = findViewById(R.id.priority_group);
        autoDeleteCheckBox = findViewById(R.id.auto_delete_value);
        needReminderCheckBox = findViewById(R.id.need_reminder_value);
        reminderTimeText = findViewById(R.id.reminder_time);
        addOrSaveButton = findViewById(R.id.add_save_button);

        taskDescriptionValue.setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskDescriptionValue.setRawInputType(InputType.TYPE_CLASS_TEXT);

        taskID = getIntent().getIntExtra(TASK_ID, 0);
        if (taskID != 0) {
            isItAnUpdateTask = true;
            prePopulateDataForTaskUpdate(taskID);
            ((TextView) findViewById(R.id.ant_title)).setText(TITLE_UPDATE_TASK);
        }

        taskDescriptionValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

/*        needReminderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog;

            timePickerDialog = new TimePickerDialog(AddNewTaskActivity.this, (view, hourOfDay, minute) -> {
                reminderHour = hourOfDay;
                reminderMinute = minute;
                reminderTimeText.setText(reminderHour + " " + reminderHour);
            }, currentHour, currentMinute, false);
            timePickerDialog.show();
        });*/
    }

    private void prePopulateDataForTaskUpdate(int taskID) {
        Task task = TaskHelper.getTask(this, taskID);
        taskDescriptionValue.setText(task.getDescription());
        RadioButton priority = findViewById(PRIORITY_LOW.equalsIgnoreCase(task.getPriority()) ? R.id.low : (PRIORITY_MEDIUM.equalsIgnoreCase(task.getPriority()) ? R.id.medium : R.id.high));
        priority.setChecked(true);
        autoDeleteCheckBox.setChecked(task.isAutoDeleteByEOD());
        needReminderCheckBox.setChecked(task.isReminderNeeded());
        addOrSaveButton.setText(SAVE);

    }

    public void addTaskToDatabase(View view) {
        String description = taskDescriptionValue.getText().toString();
        String priority = ((RadioButton) findViewById(priorityGroup.getCheckedRadioButtonId())).getText().toString();
        boolean isAutoDeleteChecked = autoDeleteCheckBox.isChecked();
        boolean isReminderNeeded = needReminderCheckBox.isChecked();
        Date dateCreated = (isAutoDeleteChecked ? Calendar.getInstance().getTime() : null);
        TaskHelper.addTaskToDatabase(this, taskID, description, priority, isAutoDeleteChecked, dateCreated, isReminderNeeded, reminderHour, reminderMinute);
        Intent backToHome = new Intent(this, HomeScreenActivity.class);
        backToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToHome);
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
                    .setTitle("Save Task")
                    .setMessage("Do you want to save the changes?")
                    .setNegativeButton("No", (arg0, arg1) -> startActivity(homeIntent))
                    .setPositiveButton("Yes", (arg0, arg1) -> {
                        addTaskToDatabase(null);
                        startActivity(homeIntent);
                    }).create().show();
        } else {
            startActivity(homeIntent);
        }
    }
}
