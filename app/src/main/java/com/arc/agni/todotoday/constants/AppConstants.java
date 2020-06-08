package com.arc.agni.todotoday.constants;

import java.util.Arrays;
import java.util.List;

public class AppConstants {

    // Screen Titles
    public static final String TITLE_TO_DO_LIST_TODAY = "To Do List Today";
    public static final String TITLE_ADD_TASK = "Add New Task";
    public static final String TITLE_UPDATE_TASK = "Update Task";

    // Priority Constants
    public static final String PRIORITY_LOW = "Low";
    public static final String PRIORITY_MEDIUM = "Medium";
    public static final String PRIORITY_HIGH = "High";

    public static final List<String> RECURRENCE = Arrays.asList("One Time Task", "Daily Task", "Weekly Task", "Monthly Task");

    // Intent Constants
    public static final String TASK_ID = "taskID";

    public static final String SAVE = "Save";

    // DB Constants
    public static final String DATABASE_NAME = "arc.db";
    private static final String TASKS_TABLE_NAME = "tasks";
    private static final String TASKS_COLUMN_ID = "id";
    private static final String TASKS_COLUMN_TASK_DETAIL = "taskdetail";
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