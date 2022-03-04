package com.arc.agni.todotoday.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.activities.AddNewTaskActivity;
import com.arc.agni.todotoday.activities.HomeScreenActivity;
import com.arc.agni.todotoday.helper.DateHelper;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Map;

import static com.arc.agni.todotoday.constants.AppConstants.LAST_OCCURRENCE_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.NEXT_OCCURRENCE_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.PATTERN_FULL_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.PATTERN_SHORT_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LABELS;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_VALUES;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_NONE;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_TIME;


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

                Drawable priorityLabel = context.getResources().getDrawable(PRIORITY_LABELS.get(PRIORITY_VALUES.indexOf(priority)));
                holder.priorityView.setImageDrawable(priorityLabel);
            }

            holder.taskDetailLayout.setOnClickListener(v -> {
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);
                View sheetView = ((Activity) context).getLayoutInflater().inflate(R.layout.task_options_bottom_sheet, null);
                Map<String, String> occurrences = DateHelper.calculateOccurrenceDatesFromDateCreated(currentTask);

                // 1. Task Description
                ((TextView) sheetView.findViewById(R.id.bs_task_description)).setText(taskDescription);
                // 1.1 Reminder Icon
                sheetView.findViewById(R.id.reminder_icon).setVisibility(currentTask.isReminderSet() ? View.VISIBLE : View.GONE);
                // 2. Task Time
                ((TextView) sheetView.findViewById(R.id.bs_task_time)).setText(occurrences.get(TASK_TIME));
                // 3. Date Created
                ((TextView) sheetView.findViewById(R.id.bs_date_created)).setText("Date created - " + DateHelper.formatDate(currentTask.getDateCreated(), PATTERN_SHORT_DATE));
                // 4. Priority
                ((TextView) sheetView.findViewById(R.id.bs_task_priority)).setText(priority + " Priority");
                // 5. Status
                ((TextView) sheetView.findViewById(R.id.bs_task_status)).setText(currentTask.isTaskCompleted() ? "Completed" : "In Progress");
                // 6. Recurrence
                String recurrence = (RECURRENCE_NONE.equalsIgnoreCase(currentTask.getRecurrence()) ? "One Time Task" : (currentTask.getRecurrence() + " Recurrence"));
                ((TextView) sheetView.findViewById(R.id.bs_recurrence_value)).setText(recurrence);

                // 7. Completed Date
                if (currentTask.isTaskCompleted()) {
                    ((TextView) sheetView.findViewById(R.id.completed_date)).setText("Completed on " + DateHelper.formatDate(currentTask.getCompletedDate(), PATTERN_FULL_DATE));
                    sheetView.findViewById(R.id.completed_group).setVisibility(View.VISIBLE);
                    sheetView.findViewById(R.id.edit_task).setVisibility(View.GONE);
                } else {
                    sheetView.findViewById(R.id.completed_group).setVisibility(View.GONE);
                    sheetView.findViewById(R.id.edit_task).setVisibility(View.VISIBLE);
                }

                // 8. Recurrence Dates
                if (!currentTask.isTaskCompleted() && !RECURRENCE_NONE.equalsIgnoreCase(currentTask.getRecurrence())) {
                    ((TextView) sheetView.findViewById(R.id.last_occurrence)).setText("Last Occurrence - " + occurrences.get(LAST_OCCURRENCE_DATE));
                    ((TextView) sheetView.findViewById(R.id.next_occurrence)).setText("Next Occurrence - " + occurrences.get(NEXT_OCCURRENCE_DATE));
                    sheetView.findViewById(R.id.occurrence_group).setVisibility(View.VISIBLE);
                } else {
                    sheetView.findViewById(R.id.occurrence_group).setVisibility(View.GONE);
                }

                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();

                sheetView.findViewById(R.id.edit_task).setOnClickListener(v1 -> {
                    Intent editIntent = new Intent(context, AddNewTaskActivity.class);
                    editIntent.putExtra(TASK_ID, currentTask.getId());
                    if (mBottomSheetDialog.isShowing()) {
                        mBottomSheetDialog.dismiss();
                    }
                    context.startActivity(editIntent);
                });
            });
        }

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
