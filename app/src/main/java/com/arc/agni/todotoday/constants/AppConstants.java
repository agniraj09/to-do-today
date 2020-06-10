package com.arc.agni.todotoday.constants;

import com.arc.agni.todotoday.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppConstants {

    // Screen Titles
    public static final String TITLE_TO_DO_LIST_TODAY = "To Do List Today";
    public static final String TITLE_ADD_TASK = "Add New Task";
    public static final String TITLE_UPDATE_TASK = "Update Task";

    // Priority Constants
    public static final String PRIORITY_LOW = "Low";
    public static final String PRIORITY_MEDIUM = "Medium";
    public static final String PRIORITY_HIGH = "High";

    public static final String RECURRENCE_NONE = "none";
    public static final String RECURRENCE_DAILY = "daily";
    public static final String RECURRENCE_WEEKLY = "weekly";
    public static final String RECURRENCE_MONTHLY = "monthly";


    public static final List<String> RECURRENCE = Arrays.asList("Today Only", "Daily Task", "Weekly Task", "Monthly Task");
    public static final List<Integer> NOTIFY_BEFORE_TIME_IDS = Arrays.asList(R.id.five, R.id.ten, R.id.fifteen, R.id.thirty);
    public static final List<Integer> NOTIFY_BEFORE_TIME_VALUES = Arrays.asList(5, 10, 15, 30);
    public static final List<Integer> RECURRENCE_IDS = Arrays.asList(R.id.none, R.id.daily, R.id.weekly, R.id.monthly);
    public static final List<String> RECURRENCE_VALUES = Arrays.asList("none", "daily", "weekly", "monthly");

    // Intent Constants
    public static final String TASK_ID = "taskID";

    public static final String SAVE = "Save";

    // DB Constants
    public static final String DATABASE_NAME = "arc.db";
    public static final String TASKS_TABLE_NAME = "tasks";
    public static final String TASKS_COLUMN_ID = "id";
    public static final String TASKS_COLUMN_TASK_DETAIL = "taskdetail";
    public static final String QUERY_SELECT_DATA = "select * from " + TASKS_TABLE_NAME;
    public static final String QUERY_CREATE_TABLE = "create table " + TASKS_TABLE_NAME + " (" + TASKS_COLUMN_ID + " integer primary key," + TASKS_COLUMN_TASK_DETAIL + " text)";
    public static final String QUERY_DROP_TALE = "DROP TABLE IF EXISTS " + TASKS_TABLE_NAME;
    public static final String DELETE_ALL_DATA = "delete from " + TASKS_TABLE_NAME;
    public static final String QUERY_GET_ITEM = "select * from " + TASKS_TABLE_NAME + " where " + TASKS_COLUMN_ID + "=";

    public static final int ID_LIMIT = 9999998;

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

    public static final String TEST_DEVICE_ID = "0EC56B91253E874AAF286CEDC3945F6A";
    public static String ADMOB_APP_ID = "ca-app-pub-4587610802196055~5991767207";

}