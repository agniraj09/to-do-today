package com.arc.agni.todotoday.constants;

public class AppConstants {

    // Screen Titles
    public static final String TITLE_TO_DO_LIST_TODAY = "To Do List Today";
    public static final String TITLE_ADD_TASK = "Add New Task";
    public static final String TITLE_UPDATE_TASK = "Update Task";

    // Priority Constants
    public static final String PRIORITY_LOW = "Low";
    public static final String PRIORITY_MEDIUM = "Medium";
    public static final String PRIORITY_HIGH = "High";

    // Intent Constants
    public static final String TASK_ID = "taskID";

    public static final String SAVE= "Save";

    // DB Constants
    public static final String DATABASE_NAME = "arc.db";
    public static final String TASKS_TABLE_NAME = "tasks";
    public static final String TASKS_COLUMN_ID = "id";
    public static final String TASKS_COLUMN_TASK_DETAIL = "taskdetail";
    public static final String QUERY_SELECT_DATA = "select * from " + TASKS_TABLE_NAME;
    public static final String QUERY_CREATE_TABLE = "create table " + TASKS_TABLE_NAME + " (" + TASKS_COLUMN_ID + " integer primary key," + TASKS_COLUMN_TASK_DETAIL + " text)";
    public static final String QUERY_DROP_TALE = "DROP TABLE IF EXISTS " + TASKS_TABLE_NAME;
    public static final String QUERY_GET_ITEM = "select * from " + TASKS_TABLE_NAME + " where " + TASKS_COLUMN_ID + "=";

    public static final int ID_LIMIT = 9999998;
}
