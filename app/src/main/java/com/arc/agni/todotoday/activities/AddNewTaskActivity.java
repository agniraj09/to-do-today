package com.arc.agni.todotoday.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static com.arc.agni.todotoday.constants.AppConstants.NOTIFY_BEFORE_TIME_IDS;
import static com.arc.agni.todotoday.constants.AppConstants.NOTIFY_BEFORE_TIME_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_HIGH;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LOW;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_MEDIUM;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_IDS;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_NONE;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.REDIRECTED_FROM_ADD_NEW_TASK;
import static com.arc.agni.todotoday.constants.AppConstants.SAVE;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ADDED;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_UPDATED;
import static com.arc.agni.todotoday.constants.AppConstants.TEST_DEVICE_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TITLE_UPDATE_TASK;

public class AddNewTaskActivity extends AppCompatActivity {

    TextInputLayout taskDescriptionValue;
    String priority = PRIORITY_LOW;
    int notifyBeforeMinutes = NOTIFY_BEFORE_TIME_VALUES.get(0);
    String recurrenceType = RECURRENCE_VALUES.get(0);
    TextView lowPriority;
    TextView mediumPriority;
    TextView highPriority;
    CheckBox isReminderNeeded;
    EditText taskTime;
    int taskTimeHour;
    int taskTimeMinute;
    CheckBox autoDeleteCheckBox;
    Button addOrSaveButton;
    boolean isItAnUpdateTask;

    int taskID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(AddNewTaskActivity.this, R.color.pure_white));
        }

        taskDescriptionValue = findViewById(R.id.task_description_value);
        lowPriority = findViewById(R.id.low);
        mediumPriority = findViewById(R.id.medium);
        highPriority = findViewById(R.id.high);
        isReminderNeeded = findViewById(R.id.need_reminder);
        taskTime = findViewById(R.id.task_time);
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

        isReminderNeeded.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                findViewById(R.id.notification_items).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.notification_items).setVisibility(View.GONE);
                resetTaskTimeLayout();
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

    public void selectNotifyBeforeTime(View view) {
        for (int id : NOTIFY_BEFORE_TIME_IDS) {
            if (view.getId() == id) {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_low);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_white));
            } else {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_no);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_black));
            }
        }
        notifyBeforeMinutes = NOTIFY_BEFORE_TIME_VALUES.get(NOTIFY_BEFORE_TIME_IDS.indexOf(view.getId()));
    }

    public void selectRecurrence(View view) {
        for (int id : RECURRENCE_IDS) {
            if (view.getId() == id) {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_low);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_white));
            } else {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_no);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_black));
            }
        }
        recurrenceType = RECURRENCE_VALUES.get(RECURRENCE_IDS.indexOf(view.getId()));
        autoDeleteCheckBox.setVisibility(RECURRENCE_NONE.equalsIgnoreCase(recurrenceType) ? View.VISIBLE : View.GONE);
        resetTaskTimeLayout();
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

    public void resetTaskTimeLayout() {
        findViewById(R.id.task_time_error).setVisibility(View.GONE);
        findViewById(R.id.task_time).setBackgroundResource(R.drawable.ic_edittext_box);
        ((TextView) findViewById(R.id.task_time_label)).setTextColor(getResources().getColor(R.color.pure_black));
        ((TextView) findViewById(R.id.task_time_error)).setText(getResources().getString(R.string.task_time_error_empty));
        taskTime.setTextColor(getResources().getColor(R.color.pure_black));
        taskTime.setText("");
    }

    public boolean checkIfValidTime(int hour, int minute) {

        /*Check 1 - Not null check*/
        if (hour == 0 && minute == 0) {
            findViewById(R.id.task_time_error).setVisibility(View.VISIBLE);
            findViewById(R.id.task_time).setBackgroundResource(R.drawable.ic_edittext_box_error);
            ((TextView) findViewById(R.id.task_time_label)).setTextColor(getResources().getColor(R.color.orange));
            ((TextView) findViewById(R.id.task_time_error)).setText(getResources().getString(R.string.task_time_error_empty));
            //taskTime.setError("Please select valid time for task");
            //Toast.makeText(AddNewTaskActivity.this, "Please select valid time for task", Toast.LENGTH_SHORT).show();
            return false;
        }

        /*Check 2 - only if NoRecurrence is selected */
        Calendar selectedTime = Calendar.getInstance();
        selectedTime.set(Calendar.HOUR_OF_DAY, hour);
        selectedTime.set(Calendar.MINUTE, minute);
        if (RECURRENCE_NONE.equalsIgnoreCase(recurrenceType) && selectedTime.getTime().before(Calendar.getInstance().getTime())) {
            findViewById(R.id.task_time_error).setVisibility(View.VISIBLE);
            findViewById(R.id.task_time).setBackgroundResource(R.drawable.ic_edittext_box_error);
            ((TextView) findViewById(R.id.task_time_label)).setTextColor(getResources().getColor(R.color.orange));
            ((TextView) findViewById(R.id.task_time_error)).setText(getResources().getString(R.string.task_time_error_invalid_time));
            taskTime.setTextColor(getResources().getColor(R.color.orange));
            //taskTime.setError("Please select future time");
            //Toast.makeText(AddNewTaskActivity.this, "Please select future time", Toast.LENGTH_SHORT).show();
            return false;
        }

        /* All validations are over - time is valid - set everything to default*/
        resetTaskTimeLayout();
        return true;
    }

    public void addOrUpdateTaskToDatabase(View view) {
        String description = taskDescriptionValue.getEditText().getText().toString();
        if (isValidDescription(description) && checkIfValidTime(taskTimeHour, taskTimeMinute)) {
            boolean isAutoDeleteChecked = autoDeleteCheckBox.isChecked();
            Date dateCreated = Calendar.getInstance().getTime();
            TaskHelper.addTaskToDatabase(this, taskID, description, priority, isAutoDeleteChecked, dateCreated, false, 0, 0, false, false);
            Intent backToHome = new Intent(this, HomeScreenActivity.class);
            backToHome.putExtra(REDIRECTED_FROM_ADD_NEW_TASK, (isItAnUpdateTask ? TASK_UPDATED : TASK_ADDED));
            backToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToHome);
        }
    }

/*    public void hideKeyboard(View view) {
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }*/

    public void timePicker(View view) {
        final Calendar currentTime = Calendar.getInstance();
        final int hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY);
        final int minuteOfHour = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hour, minute) -> {
            taskTimeHour = 0;
            taskTimeMinute = 0;

            if (checkIfValidTime(hour, minute)) {
                taskTimeHour = hour;
                taskTimeMinute = minute;
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(Calendar.HOUR_OF_DAY, hour);
                selectedTime.set(Calendar.MINUTE, minute);
                taskTime.setText(new SimpleDateFormat("h : mm a", Locale.ENGLISH).format(selectedTime.getTime()));
            } else {
                taskTime.setText("");
            }
        }, hourOfDay, minuteOfHour, false);

        timePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (isItAnUpdateTask) {
            new AlertDialog.Builder(this)
                    //.setTitle("Discard Edit")
                    .setMessage("Discard changes?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", (arg0, arg1) -> {
                        Intent homeIntent = new Intent(this, HomeScreenActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(homeIntent);
                    }).create().show();
        } else {
            this.finish();
        }
    }
}
