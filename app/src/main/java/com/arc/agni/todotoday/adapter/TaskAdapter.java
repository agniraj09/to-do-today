package com.arc.agni.todotoday.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.activities.AddNewTaskActivity;
import com.arc.agni.todotoday.activities.HomeScreenActivity;
import com.arc.agni.todotoday.helper.DateHelper;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import static com.arc.agni.todotoday.constants.AppConstants.LAST_OCCURRENCE_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.NEXT_OCCURRENCE_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.PATTERN_FULL_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_BACKGROUND;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LABELS;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LOW;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_MEDIUM;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_DAILY;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_NONE;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_WEEKLY;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ID;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Task> tasks;
    private Context context;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout taskDetailLayout;
        ImageView priorityView;
        TextView taskDescriptionView;
        ImageView permanentDoneIcon;

        MyViewHolder(View view) {
            super(view);
            taskDetailLayout = view.findViewById(R.id.task_detail_layout);
            priorityView = view.findViewById(R.id.priority_view);
            taskDescriptionView = view.findViewById(R.id.task_description);
            permanentDoneIcon = view.findViewById(R.id.perm_done_mark);
        }

    }

    public TaskAdapter(List<Task> items, Context context) {
        this.tasks = items;
        this.context = context;
        setHasStableIds(true);
    }

    public void restoreItem(Context context, Task item, int position) {
        TaskHelper.addRestoredTaskToDataBase(context, item);
        tasks.add(position, item);
        HomeScreenActivity.taskList = tasks;
        notifyItemInserted(position);
        notifyItemRangeChanged(position, tasks.size());
    }

    public List<Task> getData() {
        return tasks;
    }

    public void deleteTask(int position, long taskID) {
        TaskHelper.deleteTask(context, taskID);
        tasks.remove(position);
        HomeScreenActivity.taskList = tasks;
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());
    }

    public void deleteAllTasks() {
        TaskHelper.deleteAllTasks(context);
        tasks.clear();
        HomeScreenActivity.taskList.clear();
        notifyDataSetChanged();
    }

    public void markTaskCompletionStatus(int position, long taskID, boolean status) {
        TaskHelper.markTaskCompletionStatus(context, taskID, status);
        tasks.get(position).setTaskCompleted(status);
        HomeScreenActivity.taskList = tasks;
        notifyItemChanged(position);
    }

    public void refreshItem(int position) {
        notifyItemChanged(position);
    }

    public void refreshAllItems() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_schema, parent, false);
        return new TaskAdapter.MyViewHolder(itemView);
    }

    @Override
    public long getItemId(int position) {
        Task task = tasks.get(position);
        return task.getId();
    }

    @Override
    public int getItemViewType(int position) {
        Task task = tasks.get(position);
        return (int) task.getId();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Task currentTask = tasks.get(position);

        if (null != currentTask) {
            String taskDescription = currentTask.getDescription();
            String priority = currentTask.getPriority();
            boolean isTaskCompleted = currentTask.isTaskCompleted();

            holder.taskDescriptionView.setText(taskDescription);

            // Mark Task related logic
            // If task is already completed, show done_mark icon and hide edit & priority icon
            if (isTaskCompleted) {
                holder.permanentDoneIcon.setVisibility(View.VISIBLE);
                holder.priorityView.setVisibility(View.GONE);
            } else {
                holder.permanentDoneIcon.setVisibility(View.GONE);
                holder.priorityView.setVisibility(View.VISIBLE);

                Drawable priorityLabel = context.getResources().getDrawable(PRIORITY_LABELS.get(PRIORITY_VALUES.indexOf(currentTask.getPriority())));
                holder.priorityView.setImageDrawable(priorityLabel);
            }

            holder.taskDetailLayout.setOnClickListener(v -> {
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
                View sheetView = ((Activity) context).getLayoutInflater().inflate(R.layout.task_options_bottom_sheet, null);
                // 0. Icon
                ((ImageView) sheetView.findViewById(R.id.priority_icon)).setImageDrawable(context.getResources().getDrawable(PRIORITY_LABELS.get(PRIORITY_VALUES.indexOf(currentTask.getPriority()))));
                // 1. Date Created
                ((TextView) sheetView.findViewById(R.id.date_created_value)).setText(DateHelper.formatDate(currentTask.getDateCreated(), PATTERN_FULL_DATE));
                // 2. Date Completed
                if (currentTask.isTaskCompleted()) {
                    ((TextView) sheetView.findViewById(R.id.date_completed_value)).setText(DateHelper.formatDate(currentTask.getCompletedDate(), PATTERN_FULL_DATE));
                    sheetView.findViewById(R.id.date_completed_value).setVisibility(View.VISIBLE);
                } else {
                    sheetView.findViewById(R.id.date_completed_value).setVisibility(View.GONE);
                }
                // 3. Recurrence
                ((TextView) sheetView.findViewById(R.id.recurrence_value)).setText(currentTask.getRecurrence());
                // 4. Occurrence Dates
                if (!currentTask.isTaskCompleted() && !RECURRENCE_NONE.equalsIgnoreCase(currentTask.getRecurrence())) {
                    Map<String, String> occurrences = DateHelper.calculateOccurrenceDatesFromDateCreated(currentTask);
                    ((TextView) sheetView.findViewById(R.id.last_occurrence_value)).setText(occurrences.get(LAST_OCCURRENCE_DATE));
                    ((TextView) sheetView.findViewById(R.id.next_occurrence_value)).setText(occurrences.get(NEXT_OCCURRENCE_DATE));
                    sheetView.findViewById(R.id.occurence_group).setVisibility(View.VISIBLE);
                } else {
                    sheetView.findViewById(R.id.occurence_group).setVisibility(View.GONE);
                }

                if (currentTask.isTaskCompleted()) {
                    sheetView.findViewById(R.id.date_completed_group).setVisibility(View.VISIBLE);
                    sheetView.findViewById(R.id.edit_task).setVisibility(View.GONE);
                } else {
                    sheetView.findViewById(R.id.date_completed_group).setVisibility(View.GONE);
                    sheetView.findViewById(R.id.edit_task).setVisibility(View.VISIBLE);
                }

                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();

                sheetView.findViewById(R.id.edit_task).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editIntent = new Intent(context, AddNewTaskActivity.class);
                        editIntent.putExtra(TASK_ID, currentTask.getId());
                        context.startActivity(editIntent);
                    }
                });
            });
        }

    }

    /*public String formatTaskTime(Task task) {
        String formattedTaskTime = "";

        Calendar currentDay = Calendar.getInstance();
        int currentDayDate = currentDay.get(Calendar.DAY_OF_YEAR);
        int currentDayMonth = currentDay.get(Calendar.MONTH);

        Calendar dateCreated = Calendar.getInstance();
        dateCreated.setTime(task.getDateCreated());
        if (RECURRENCE_NONE.equalsIgnoreCase(task.getRecurrence())) {
            if (dateCreated.get(Calendar.DAY_OF_YEAR) < currentDayDate && dateCreated.get(Calendar.MONTH) < currentDayMonth) {
                formattedTaskTime = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(dateCreated.getTime());
            } else {
                Calendar reminderTime = Calendar.getInstance();
                reminderTime.set(Calendar.HOUR_OF_DAY, task.getReminderHour());
                reminderTime.set(Calendar.MINUTE, task.getReminderMinute());
                reminderTime.add(Calendar.MINUTE, -task.getRemindBefore());
                formattedTaskTime = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(reminderTime.getTime());
            }
        } else {
            if (task.isTaskCompleted()) {
                if (null != task.getLastCycleDate()) {
                    formattedTaskTime = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(task.getLastCycleDate().getTime());
                } else {
                    formattedTaskTime = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(dateCreated.getTime());
                }
            } else {
                Date timeToBeConsidered = (null != task.getLastCycleDate() ? task.getLastCycleDate() : task.getDateCreated());
                Log.e("inside non-comp", "inside not completed");
                if (RECURRENCE_DAILY.equalsIgnoreCase(task.getRecurrence())) {
                    Calendar nextCycle = Calendar.getInstance();
                    nextCycle.setTime(timeToBeConsidered);
                    nextCycle.add(Calendar.DAY_OF_YEAR, 1);

                    Log.e("inside daily", "next cycle" + nextCycle.get(Calendar.DAY_OF_YEAR) + "/" + nextCycle.get(Calendar.MONTH) + " | " + "currntDay" + currentDayDate + "/" + currentDayMonth);
                    if (nextCycle.get(Calendar.DAY_OF_YEAR) > currentDayDate && nextCycle.get(Calendar.MONTH) >= currentDayMonth) {
                        Log.e("inside ***", "");
                        formattedTaskTime = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(nextCycle.getTime());
                    } else {
                        Calendar reminderTime = Calendar.getInstance();
                        reminderTime.setTime(nextCycle.getTime());
                        reminderTime.set(Calendar.HOUR_OF_DAY, task.getReminderHour());
                        reminderTime.set(Calendar.MINUTE, task.getReminderMinute());
                        reminderTime.add(Calendar.MINUTE, -task.getRemindBefore());
                        formattedTaskTime = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(nextCycle.getTime());
                    }

                } else if (RECURRENCE_WEEKLY.equalsIgnoreCase(task.getRecurrence())) {
                    Calendar nextCycle = Calendar.getInstance();
                    nextCycle.setTime(timeToBeConsidered);
                    nextCycle.add(Calendar.WEEK_OF_YEAR, 1);

                    if (nextCycle.get(Calendar.DAY_OF_YEAR) > currentDayDate && nextCycle.get(Calendar.MONTH) >= currentDayMonth) {
                        formattedTaskTime = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(nextCycle.getTime());
                    } else {
                        Calendar reminderTime = Calendar.getInstance();
                        reminderTime.setTime(nextCycle.getTime());
                        reminderTime.set(Calendar.HOUR_OF_DAY, task.getReminderHour());
                        reminderTime.set(Calendar.MINUTE, task.getReminderMinute());
                        reminderTime.add(Calendar.MINUTE, -task.getRemindBefore());

                        formattedTaskTime = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(nextCycle.getTime());
                    }
                } else {
                    Calendar nextCycle = Calendar.getInstance();
                    nextCycle.setTime(timeToBeConsidered);
                    nextCycle.add(Calendar.MONTH, 1);

                    if (nextCycle.get(Calendar.DAY_OF_YEAR) > currentDayDate && nextCycle.get(Calendar.MONTH) > currentDayMonth) {
                        formattedTaskTime = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).format(nextCycle.getTime());
                    } else {
                        Calendar reminderTime = Calendar.getInstance();
                        reminderTime.setTime(nextCycle.getTime());
                        reminderTime.set(Calendar.HOUR_OF_DAY, task.getReminderHour());
                        reminderTime.set(Calendar.MINUTE, task.getReminderMinute());
                        reminderTime.add(Calendar.MINUTE, -task.getRemindBefore());
                        formattedTaskTime = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(nextCycle.getTime());
                    }
                }

            }
        }

        return formattedTaskTime;
    }*/


    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
