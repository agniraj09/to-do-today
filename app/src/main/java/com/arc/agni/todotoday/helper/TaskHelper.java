package com.arc.agni.todotoday.helper;

import android.content.Context;
import android.util.Log;

import com.arc.agni.todotoday.model.Task;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.arc.agni.todotoday.constants.AppConstants.ID_LIMIT;

public class TaskHelper {

    public static void addTaskToDatabase(Context context, int taskID, String description, String priority, boolean isAutoDeleteChecked, Date dateCreated, boolean isReminderNeeded, int reminderHour, int reminderMinute) {

        // New Task to be saved in DB
        if (taskID == 0) {
            // id column value- Generate Task ID using a random generator
            taskID = (new Random().nextInt(ID_LIMIT) + 1);

            // taskdetail column value - Json
            Task task = new Task(taskID, description, priority, isAutoDeleteChecked, dateCreated, isReminderNeeded, reminderHour, reminderMinute, false);
            String taskdetail = convertObjectToJson(task);
            new DBHelper(context).insertTask(taskID, taskdetail);
        }
        // Existing Task to be updated
        else {
            // taskdetail column value - Json
            Task task = new Task(taskID, description, priority, isAutoDeleteChecked, dateCreated, isReminderNeeded, reminderHour, reminderMinute, false);
            String taskdetail = convertObjectToJson(task);
            new DBHelper(context).updateTask(taskID, taskdetail);
        }
    }

    public static void markTaskCompleted(Context context, int taskID) {
        DBHelper dbHelper = new DBHelper(context);
        Task task = convertJsonToObject(dbHelper.getTask(taskID));
        task.setTaskCompleted(true);
        String taskdetail = convertObjectToJson(task);
        new DBHelper(context).updateTask(taskID, taskdetail);
    }

    public static void deleteTask(Context context, int taskID) {
        new DBHelper(context).deleteTask(taskID);
    }

    public static Task getTask(Context context, int taskID) {
        return convertJsonToObject(new DBHelper(context).getTask(taskID));
    }

    public static List<Task> getAllTasksFromDB(Context context) {
        List<Task> allTasks = new ArrayList<>();
        List<String> allTasksInJson = new DBHelper(context).getAllTasks();
        if (null != allTasksInJson) {
            for (String taskJson : allTasksInJson) {
                allTasks.add(convertJsonToObject(taskJson));
            }
        }

        return allTasks;
    }

    private static String convertObjectToJson(Task task) {
        return (new Gson().toJson(task));
    }

    private static Task convertJsonToObject(String json) {
        return (new Gson().fromJson(json, Task.class));
    }
}
