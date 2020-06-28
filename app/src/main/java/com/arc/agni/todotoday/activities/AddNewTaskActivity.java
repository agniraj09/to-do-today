package com.arc.agni.todotoday.activities;

import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.helper.DateHelper;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;
import com.arc.agni.todotoday.reminder.ReminderBroadcast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import static com.arc.agni.todotoday.constants.AppConstants.NOTIFY_BEFORE_TIME_IDS;
import static com.arc.agni.todotoday.constants.AppConstants.NOTIFY_BEFORE_TIME_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.PATTERN_TIME;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_BACKGROUND;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_IDS;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LOW;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_MEDIUM;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_IDS;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_NONE;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.REDIRECTED_FROM_ADD_NEW_TASK;
import static com.arc.agni.todotoday.constants.AppConstants.REMINDER_TYPE_IDS;
import static com.arc.agni.todotoday.constants.AppConstants.REMINDER_TYPE_NOTIFICATION;
import static com.arc.agni.todotoday.constants.AppConstants.REMINDER_TYPE_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.SAVE;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ADDED;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_UPDATED;
import static com.arc.agni.todotoday.constants.AppConstants.TEST_DEVICE_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TITLE_UPDATE_TASK;

public class AddNewTaskActivity extends AppCompatActivity {

    TextInputLayout taskDescriptionValue;
    String priority = PRIORITY_LOW;
    int notifyBeforeMinutes;
    String recurrenceType = RECURRENCE_VALUES.get(0);
    TextView lowPriority;
    TextView mediumPriority;
    TextView highPriority;
    CheckBox isSetReminderChecked;
    boolean notifyBeforeEnabled;
    String reminderType = REMINDER_TYPE_NOTIFICATION;
    EditText taskTime;
    int taskTimeHour;
    int taskTimeMinute;
    CheckBox autoDeleteCheckBox;
    Button addOrSaveButton;
    boolean isItAnUpdateTask;
    long taskID;
    ConstraintLayout notifyBeforeOptionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        // Change the status bar color to white & make the icons and elements darker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(AddNewTaskActivity.this, R.color.pure_white));
        }

        taskDescriptionValue = findViewById(R.id.task_description_value);
        lowPriority = findViewById(R.id.low);
        mediumPriority = findViewById(R.id.medium);
        highPriority = findViewById(R.id.high);
        isSetReminderChecked = findViewById(R.id.set_reminder);
        taskTime = findViewById(R.id.task_time);
        notifyBeforeOptionLayout = findViewById(R.id.notify_before_layout);
        autoDeleteCheckBox = findViewById(R.id.auto_delete_value);
        addOrSaveButton = findViewById(R.id.add_save_button);

        // Disable next line when user clicks 'Enter' button in keyboard
        taskDescriptionValue.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskDescriptionValue.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);

        // This is applicable for edit task only. When user wants to edit a task, TASK_ID will be send in intent extra from HomeScreenActivity.
        taskID = getIntent().getLongExtra(TASK_ID, 0);
        if (taskID != 0) {
            isItAnUpdateTask = true;
            prePopulateDataForTaskUpdate(taskID);
            ((TextView) findViewById(R.id.ant_title)).setText(TITLE_UPDATE_TASK);
        }

        // Check/Uncheck listener for 'More options'(checkbox)
        isSetReminderChecked.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                findViewById(R.id.notification_items).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.notification_items).setVisibility(View.GONE);
                resetReminderOptionsLayout();
            }
        });

        // Initialize MobileAds & Request for ads
        AdView mAdView = findViewById(R.id.ant_adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(TEST_DEVICE_ID).build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /**
     * Priority Selection Method.
     * When a user selects a priority option,
     * 1. Selected layout will be set to specific priority color and text will be changed to white color.
     * 2. Other priority layouts will be set to default - light Gray background(no_priority) & black text.
     * 3. 'priority' value is set.
     */
    public void changePriority(View view) {
        for (int priorityID : PRIORITY_IDS) {
            if (priorityID == view.getId()) {
                findViewById(priorityID).setBackgroundResource(PRIORITY_BACKGROUND.get(PRIORITY_IDS.indexOf(priorityID)));
                ((TextView) findViewById(priorityID)).setTextColor(getResources().getColor(R.color.pure_white));
                setElevation(true, priorityID);
            } else {
                findViewById(priorityID).setBackgroundResource(R.drawable.ic_priority_no);
                ((TextView) findViewById(priorityID)).setTextColor(getResources().getColor(R.color.pure_black));
                setElevation(false, priorityID);
            }
        }
        priority = PRIORITY_VALUES.get(PRIORITY_IDS.indexOf(view.getId()));
    }

    /**
     * NotifyBeforeTime Selection Method.
     * When a user selects a time,
     * 1. Selected time layout will be set to blue background & white text.
     * 2. Other time layouts will be set to default - light Gray background & black text.
     * 3. 'notifyBeforeMinutes' value is set.
     */
    public void selectNotifyBeforeTime(View view) {
        for (int id : NOTIFY_BEFORE_TIME_IDS) {
            if (view.getId() == id) {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_low);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_white));
                setElevation(true, id);
            } else {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_no);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_black));
                setElevation(false, id);
            }
        }
        notifyBeforeMinutes = NOTIFY_BEFORE_TIME_VALUES.get(NOTIFY_BEFORE_TIME_IDS.indexOf(view.getId()));
    }

    /**
     * Recurrence Selection Method.
     * When a user selects a specific recurrence type,
     * 1. Selected recurrence layout will be set to blue background & white text.
     * 2. Other recurrence layouts will be set to default - light Gray background & black text.
     * 3. 'recurrenceType' value is set.
     * 4. If the selected recurrence type is 'None' - which means a one time task, make 'autoDeleteBYEOD' checkbox visible, else make it gone.
     * 5. Reset the 'Reminder option' layout.
     */
    public void selectRecurrence(View view) {
        for (int id : RECURRENCE_IDS) {
            if (view.getId() == id) {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_low);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_white));
                setElevation(true, id);
            } else {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_no);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_black));
                setElevation(false, id);
            }
        }
        recurrenceType = RECURRENCE_VALUES.get(RECURRENCE_IDS.indexOf(view.getId()));
        autoDeleteCheckBox.setVisibility(RECURRENCE_NONE.equalsIgnoreCase(recurrenceType) ? View.VISIBLE : View.GONE);
        resetReminderOptionsLayout();
    }

    /**
     * Reminder Tpe selection Method.
     * When a user selects a specific reminder type,
     * 1. Selected reminder type layout will be set to blue background & white text.
     * 2. Other reminder type layouts will be set to default - light Gray background & black text.
     * 3. 'reminderType' value is set.
     */
    public void selectReminderType(View view) {
        for (int id : REMINDER_TYPE_IDS) {
            if (view.getId() == id) {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_low);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_white));
                setElevation(true, id);
            } else {
                findViewById(id).setBackgroundResource(R.drawable.ic_priority_no);
                ((TextView) findViewById(id)).setTextColor(getResources().getColor(R.color.pure_black));
                setElevation(false, id);
            }
        }
        reminderType = REMINDER_TYPE_VALUES.get(REMINDER_TYPE_IDS.indexOf(view.getId()));
    }

    /**
     * This method is used to set the elevation for layouts programmatically.
     * Currently 1.Priority, 2. Recurrence Type 3. Notify before 4. Reminder Type layouts make use of this method
     */
    private void setElevation(boolean set, int viewID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(viewID).setElevation(set ? 5.0F : 0.0F);
        }
    }

    /**
     * Time picker method for task time. When a user selects a time,
     * 1. Selected time is validated against all checks.
     * 2. If validation is successful,
     * i. 'taskTimeHour' & 'taskTimeMinute' value is set.
     * ii. 'taskTime' text is set.
     * 3. If validation is not successful,
     * i. 'taskTime' text is set to null.
     */
    public void selectTaskTime(View view) {
        final Calendar currentTime = Calendar.getInstance();
        final int hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY);
        final int minuteOfHour = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hour, minute) -> {
            if (checkIfTaskTimeIsValid(hour, minute)) {
                resetReminderOptionsLayout();
                taskTimeHour = hour;
                taskTimeMinute = minute;
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(Calendar.HOUR_OF_DAY, hour);
                selectedTime.set(Calendar.MINUTE, minute);
                taskTime.setText(new SimpleDateFormat("h : mm a", Locale.ENGLISH).format(selectedTime.getTime()));
                showNotifyBeforeTimesIfApplicable(hour, minute);
            } else {
                taskTime.setText("");
            }
        }, hourOfDay, minuteOfHour, false);

        timePickerDialog.show();
    }

    /**
     * Validation method for task time. This method does below validations for a task time selected by user.
     * 1. Task time should not be null.
     * 2. If recurrence type is 'None', then task time should be greater than current time.
     */
    private boolean checkIfTaskTimeIsValid(int hour, int minute) {

        /*Check 1 - Not null check*/
        if (hour == 0 && minute == 0) {
            showError(getResources().getString(R.string.task_time_error_empty));
            resetTaskTimeToZero();
            return false;
        }

        /*Check 2 - Past time selection check - only if NoRecurrence is selected */
        if (RECURRENCE_NONE.equalsIgnoreCase(recurrenceType)) {
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedTime.set(Calendar.MINUTE, minute);
            if (Calendar.getInstance().getTime().after(selectedTime.getTime())) {
                showError(getResources().getString(R.string.task_time_error_invalid_time));
                resetTaskTimeToZero();
                return false;
            }
        }

        return true;
    }

    /**
     * This method resets the values of taskTimeHour & taskTimeMinute variables to 0.
     */
    private void resetTaskTimeToZero() {
        taskTimeHour = 0;
        taskTimeMinute = 0;
    }

    /**
     * Validation method for Notify before minutes. This method does below validations for remind before minutes.
     * 1. If recurrence type is 'None' &  User opted to set reminder,
     * Current time should be greater than (TaskTime - notifyBeforeMinutes)
     */
    private boolean checkIfNotifyBeforeMinuteIsValid(int taskHour, int taskMinute, int notifyMinutes) {

        /*Check 1*/
        if (RECURRENCE_NONE.equalsIgnoreCase(recurrenceType) && notifyBeforeEnabled) {
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, taskHour);
            selectedTime.set(Calendar.MINUTE, taskMinute);
            selectedTime.add(Calendar.MINUTE, -notifyMinutes);
            if (Calendar.getInstance().getTime().compareTo(selectedTime.getTime()) >= 0) {
                String error = "Reminder can't be set before " + notifyMinutes + " minutes.\n"
                        + (notifyMinutes == 5 ? "You can still save the task without reminder." : "Select lesser duration.");
                showError(error);
                if (notifyMinutes == 5) {
                    notifyBeforeOptionLayout.setVisibility(View.GONE);
                    notifyBeforeEnabled = false;
                    notifyBeforeMinutes = 0;
                }
                return false;
            }
        }

        return true;
    }

    /**
     * This method makes the  Notify Before layout visible if taskTime has passed all the validations.
     * 1. This enables & disables the selection for each notify before minutes layout.
     * 2. Shows / hides the notify before layout.
     */
    public void showNotifyBeforeTimesIfApplicable(int hour, int minute) {
        if (RECURRENCE_NONE.equalsIgnoreCase(recurrenceType)) {
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedTime.set(Calendar.MINUTE, minute);

            Calendar currentTime = Calendar.getInstance();
            // If duration between current time and task time is greater than 5 minutes, show the Notify before layout and set the default value as 5 minutes.
            currentTime.add(Calendar.MINUTE, 5);
            if (currentTime.getTime().compareTo(selectedTime.getTime()) < 0) {
                notifyBeforeOptionLayout.setVisibility(View.VISIBLE);
                notifyBeforeMinutes = NOTIFY_BEFORE_TIME_VALUES.get(0);
                notifyBeforeEnabled = true;

                // Check the duration for more ranges and disable selection if duration lesser than any of the value.
                disableSelectionIfDurationIsLessThan(30, selectedTime);
                disableSelectionIfDurationIsLessThan(15, selectedTime);
                disableSelectionIfDurationIsLessThan(10, selectedTime);
            } else {
                notifyBeforeEnabled = false;
            }
        } else {
            notifyBeforeOptionLayout.setVisibility(View.VISIBLE);
            notifyBeforeEnabled = true;
        }
    }

    /**
     * This method disables the selection for a specific notify before minutes layout if current time is greater than passed Time
     */
    private void disableSelectionIfDurationIsLessThan(int minutes, Calendar selectedTime) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.MINUTE, minutes);
        if (currentTime.getTime().compareTo(selectedTime.getTime()) >= 0) {
            findViewById(NOTIFY_BEFORE_TIME_IDS.get(NOTIFY_BEFORE_TIME_VALUES.indexOf(minutes))).setEnabled(false);
        }
    }

    /**
     * This method is used to pre render the details of a task when a user wants to edit task.
     * This method retrieves the details of a task using Task ID and renders its details in UI.
     */
    private void prePopulateDataForTaskUpdate(long taskID) {
        Task task = TaskHelper.getTask(this, taskID);
        taskDescriptionValue.getEditText().setText(task.getDescription());
        taskDescriptionValue.getEditText().setSelection(taskDescriptionValue.getEditText().length());
        changePriority(findViewById(PRIORITY_IDS.get(PRIORITY_VALUES.indexOf(task.getPriority()))));
        selectRecurrence(findViewById(RECURRENCE_IDS.get(RECURRENCE_VALUES.indexOf(task.getRecurrence()))));
        if (task.isReminderSet()) {
            isSetReminderChecked.setChecked(true);
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, task.getReminderHour());
            selectedTime.set(Calendar.MINUTE, task.getReminderMinute());
            taskTimeHour = task.getReminderHour();
            taskTimeMinute = task.getReminderMinute();
            taskTime.setText(DateHelper.formatDate(selectedTime, PATTERN_TIME));
            findViewById(R.id.notification_items).setVisibility(View.VISIBLE);
            if (task.getRemindBefore() != 0) {
                notifyBeforeOptionLayout.setVisibility(View.VISIBLE);
                selectNotifyBeforeTime(findViewById(NOTIFY_BEFORE_TIME_IDS.get(NOTIFY_BEFORE_TIME_VALUES.indexOf(task.getRemindBefore()))));
                selectReminderType(findViewById(REMINDER_TYPE_IDS.get(REMINDER_TYPE_VALUES.indexOf(task.getReminderType()))));
            }
        }

        if (RECURRENCE_NONE.equalsIgnoreCase(task.getRecurrence())) {
            autoDeleteCheckBox.setChecked(task.isAutoDeleteByEOD());
        }
        addOrSaveButton.setText(SAVE);
    }

    /**
     * Validation method for Task description. This method does below validations,
     * 1.Task description should not be empty
     */
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

    /**
     * This method is used to reset the Task time & Notify before before layouts together whenever user changes the task recurrence choice.
     */
    public void resetReminderOptionsLayout() {
        // 1. Make error layout invisible/gone
        findViewById(R.id.task_time_error).setVisibility(View.GONE);

        // 2. Reset task time layout
        autoDeleteCheckBox.setChecked(false);
        taskTime.setBackgroundResource(R.drawable.ic_edittext_box);
        taskTime.setTextColor(getResources().getColor(R.color.pure_black));
        taskTime.setText("");
        ((TextView) findViewById(R.id.task_time_label)).setTextColor(getResources().getColor(R.color.pure_black));

        // 3. Reset task time values
        resetTaskTimeToZero();

        // 4. Reset notifyBeforeOptionLayout - visibility and ability to select
        // layout - five is never disabled at any condition so not enabling it again.
        findViewById(R.id.ten).setEnabled(true);
        findViewById(R.id.fifteen).setEnabled(true);
        findViewById(R.id.thirty).setEnabled(true);
        selectNotifyBeforeTime(findViewById(NOTIFY_BEFORE_TIME_IDS.get(0)));
        notifyBeforeOptionLayout.setVisibility(View.GONE);
        notifyBeforeEnabled = false;

        // If recurrence type is none, by default, notify before time should be set as 0.
        if (RECURRENCE_NONE.equalsIgnoreCase(recurrenceType)) {
            notifyBeforeMinutes = 0;
        }
    }

    /**
     * This method is used to show the error message and design the error layout. This is currently used by task time selection alone
     */
    public void showError(String errorMessage) {
        TextView taskTimeError = findViewById(R.id.task_time_error);

        ((TextView) findViewById(R.id.task_time_label)).setTextColor(getResources().getColor(R.color.orange));
        taskTime.setBackgroundResource(R.drawable.ic_edittext_box_error);
        taskTime.setTextColor(getResources().getColor(R.color.orange));
        taskTimeError.setText(errorMessage);
        taskTimeError.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when a user wants to add a new task or wants to save the edited task. This reads all the values for a task and save/update in DB.
     * Once the task is saved, it navigates the user back to Home Screen.
     */
    public void addOrUpdateTaskToDatabase(View view) {
        String description = taskDescriptionValue.getEditText().getText().toString();
        if (isValidDescription(description) && (!isSetReminderChecked.isChecked() || (checkIfTaskTimeIsValid(taskTimeHour, taskTimeMinute)
                && checkIfNotifyBeforeMinuteIsValid(taskTimeHour, taskTimeMinute, notifyBeforeMinutes)))) {
            boolean isAutoDeleteChecked = autoDeleteCheckBox.isChecked();
            Calendar dateCreated = (isItAnUpdateTask ? TaskHelper.getTask(this, taskID).getDateCreated() : Calendar.getInstance());
            Task task = new Task(description, priority, recurrenceType, isAutoDeleteChecked, dateCreated, isSetReminderChecked.isChecked(), taskTimeHour, taskTimeMinute, notifyBeforeMinutes, reminderType, false);
            taskID = TaskHelper.addTaskToDatabase(this, taskID, task);

            Intent backToHome = new Intent(this, HomeScreenActivity.class);
            backToHome.putExtra(REDIRECTED_FROM_ADD_NEW_TASK, (isItAnUpdateTask ? TASK_UPDATED : TASK_ADDED));
            backToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToHome);

        }
    }

    /**
     * This method is called when a user clicks back button.
     * A popup will be triggered if it is for a task edit.
     */
    @Override
    public void onBackPressed() {
        if (isItAnUpdateTask) {
            new AlertDialog.Builder(this)
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
