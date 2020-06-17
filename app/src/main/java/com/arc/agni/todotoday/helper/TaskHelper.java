package com.arc.agni.todotoday.helper;

import android.content.Context;

import com.arc.agni.todotoday.activities.HomeScreenActivity;
import com.arc.agni.todotoday.model.Task;
import com.arc.agni.todotoday.reminder.ReminderBroadcast;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TaskHelper {

    public static long addTaskToDatabase(Context context, long taskID, Task task) {

        String taskdetail = convertObjectToJson(task);
        long returnTaskID;


        // New Task to be saved in DB
        if (taskID == 0) {
            returnTaskID = (new DBHelper(context).insertNewTask(taskdetail));
            task.setId(returnTaskID);
            scheduleNotificationIfApplicable(context, task);
        }
        // Existing Task to be updated
        else {
            new DBHelper(context).updateTask(taskID, taskdetail);
            returnTaskID = taskID;
            task.setId(returnTaskID);
            scheduleNotificationIfApplicable(context, task);
        }

        return returnTaskID;
    }

    private static void scheduleNotificationIfApplicable(Context context, Task task) {

        // Remove any notification if exists
        ReminderBroadcast.cancelNotification(task.getId(), context);

        //Schedule or Cancel Reminder/Notification
        if (!task.isTaskCompleted() && task.isReminderSet()) {
            ReminderBroadcast.buildAndScheduleNotification(context, task.getId(), task);
        } else {
            ReminderBroadcast.cancelNotification(task.getId(), context);
        }

    }

    public static void addRestoredTaskToDataBase(Context context, Task task) {
        String taskDetail = convertObjectToJson(task);
        new DBHelper(context).insertRestoredTask(task.getId(), taskDetail);
        scheduleNotificationIfApplicable(context, task);

    }

    public static void markTaskCompletionStatus(Context context, long taskID, boolean status) {
        DBHelper dbHelper = new DBHelper(context);
        Task task = convertJsonToObject(dbHelper.getTask(taskID));
        task.setTaskCompleted(status);
        task.setCompletedDate(status ? Calendar.getInstance() : null);
        String taskDetail = convertObjectToJson(task);
        new DBHelper(context).updateTask(taskID, taskDetail);

        if (status) {
            ReminderBroadcast.cancelNotification(taskID, context);
        } else {
            scheduleNotificationIfApplicable(context, task);
        }
    }

    public static void deleteTask(Context context, long taskID) {
        new DBHelper(context).deleteTask(taskID);
        ReminderBroadcast.cancelNotification(taskID, context);
    }

    public static Task getTask(Context context, long taskID) {
        return convertJsonToObject(new DBHelper(context).getTask(taskID));
    }

    public static List<Task> getAllTasksFromDB(Context context) {
        List<Task> allTasks = new ArrayList<>();
        Map<Long, String> taskList = new DBHelper(context).getAllTasks();
        if (null != taskList) {
            for (Map.Entry<Long, String> taskKeyValue : taskList.entrySet()) {
                Task task = new Task();
                task = convertJsonToObject(taskKeyValue.getValue());
                task.setId(taskKeyValue.getKey());
                allTasks.add(task);
            }
        }
        //Comparator<Task> dateComparator = (taskOne, taskTwo) -> taskOne.getDateCreated().compareTo(taskTwo.getDateCreated());
        //Collections.sort(allTasks, dateComparator);
        return allTasks;
    }

    public static void deleteAllTasks(Context context) {
        new DBHelper(context).deleteAllTasks();
    }

    private static String convertObjectToJson(Task task) {
        return (new Gson().toJson(task));
    }

    private static Task convertJsonToObject(String json) {
        return (new Gson().fromJson(json, Task.class));
    }
}
