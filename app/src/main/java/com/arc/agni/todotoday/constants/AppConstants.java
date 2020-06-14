package com.arc.agni.todotoday.constants;

import com.arc.agni.todotoday.R;

import java.util.Arrays;
import java.util.List;

public class AppConstants {

    // Screen Titles
    public static final String TITLE_TO_DO_LIST_TODAY = "To Do List Today";
    public static final String TITLE_UPDATE_TASK = "Update Task";

    public static final String RECURRENCE_NONE = "none";
    public static final String RECURRENCE_DAILY = "daily";
    public static final String RECURRENCE_WEEKLY = "weekly";
    public static final String RECURRENCE_MONTHLY = "monthly";

    public static final List<Integer> NOTIFY_BEFORE_TIME_IDS = Arrays.asList(R.id.five, R.id.ten, R.id.fifteen, R.id.thirty);
    public static final List<Integer> NOTIFY_BEFORE_TIME_VALUES = Arrays.asList(5, 10, 15, 30);
    public static final List<Integer> RECURRENCE_IDS = Arrays.asList(R.id.none, R.id.daily, R.id.weekly, R.id.monthly);
    public static final List<String> RECURRENCE_VALUES = Arrays.asList(RECURRENCE_NONE, RECURRENCE_DAILY, RECURRENCE_WEEKLY, RECURRENCE_MONTHLY);
    public static final List<String> RECURRENCE_NOTIFICATION_TEXT = Arrays.asList("One time task", "Daily Task", "Weekly Task", "Monthly Task");

    public static final String PRIORITY_LOW = "Low", PRIORITY_MEDIUM = "Medium", PRIORITY_HIGH = "High";
    public static final List<Integer> PRIORITY_IDS = Arrays.asList(R.id.low, R.id.medium, R.id.high);
    public static final List<Integer> PRIORITY_COLORS = Arrays.asList(R.color.low, R.color.medium, R.color.high);
    public static final List<Integer> PRIORITY_BACKGROUND = Arrays.asList(R.drawable.ic_priority_low, R.drawable.ic_priority_medium, R.drawable.ic_priority_high);
    public static final List<String> PRIORITY_VALUES = Arrays.asList(PRIORITY_LOW, PRIORITY_MEDIUM, PRIORITY_HIGH);


    // Intent Constants
    public static final String TASK_ID = "taskID";

    public static final String SAVE = "Save";

    // DB Constants
    public static final String DATABASE_NAME = "arc.db";
    public static final String TASKS_TABLE_NAME = "tasks";
    public static final String TASKS_COLUMN_ID = "id";
    public static final String TASKS_COLUMN_TASK_DETAIL = "taskdetail";
    public static final String QUERY_SELECT_DATA = "select * from " + TASKS_TABLE_NAME;
    public static final String QUERY_CREATE_TABLE = "create table " + TASKS_TABLE_NAME + " (" + TASKS_COLUMN_ID + " integer primary key AUTOINCREMENT," + TASKS_COLUMN_TASK_DETAIL + " text)";
    public static final String QUERY_DROP_TALE = "DROP TABLE IF EXISTS " + TASKS_TABLE_NAME;
    public static final String DELETE_ALL_DATA = "delete from " + TASKS_TABLE_NAME;
    public static final String QUERY_GET_ITEM = "select * from " + TASKS_TABLE_NAME + " where " + TASKS_COLUMN_ID + "=";

    public static final String TASK_OVERVIEW = "Task Overview";
    public static final String DELETE_ALL = "Delete All";

    public static final String TASK_DELETED = "Task has been deleted successfully";
    public static final String UNDO = "Undo";
    public static final String TASK_COMPLETED = "Task has been marked completed";

    public static final int RESULT_CODE_ADD = 11;
    public static final int RESULT_CODE_UPDATE = 22;
    public static final String RESULT_MESSAGE = "Task list updated";

    public static final String REDIRECTED_FROM_ADD_NEW_TASK = "addnewtask";
    public static final String TASK_ADDED = "Task has been added successfully";
    public static final String TASK_UPDATED = "Task has been updated successfully";

    // Push Notification Constants
    public static final String CHANNEL_ID = "ToDoListToday";
    public static final String CHANNEL_NAME = "ToDoListToday Reminder Channel";
    public static final String CHANNEL_DESCRIPTION = "This is a channel to shoot notifications for ToDoListToday App";
    public static final String NOTIFICATION_TEXT = "Task is scheduled";
    public static final String INTENT_EXTRA_NOTIFICATION = "notification";
    public static final String INTENT_EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String INTENT_EXTRA_TASK_RECURRENCE = "task_recurrence";
    public static final String INTENT_EXTRA_TASK = "task";

    public static final String TEST_DEVICE_ID = "0EC56B91253E874AAF286CEDC3945F6A";
    public static String ADMOB_APP_ID = "ca-app-pub-4587610802196055~5991767207";

}