package com.arc.agni.todotoday.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.activities.AddNewTaskActivity;
import com.arc.agni.todotoday.activities.HomeScreenActivity;
import com.arc.agni.todotoday.helper.TaskHelper;
import com.arc.agni.todotoday.model.Task;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LOW;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_MEDIUM;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ID;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Task> tasks;
    private Context context;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        boolean actionsEnabled;
        LinearLayout taskDetailLayout;
        View priorityView;
        TextView taskDescriptionView;
        ImageView moreOptions;
        LinearLayout actions;
        ImageView markTaskCompleted;
        ImageView editTask;
        ImageView deleteTask;
        ImageView permanentDoneIcon;

        MyViewHolder(View view) {
            super(view);
            taskDetailLayout = view.findViewById(R.id.task_detail_layout);
            priorityView = view.findViewById(R.id.priority_view);
            taskDescriptionView = view.findViewById(R.id.task_description);
            moreOptions = view.findViewById(R.id.more_options);
            actions = view.findViewById(R.id.actions);
            markTaskCompleted = view.findViewById(R.id.done_task);
            editTask = view.findViewById(R.id.edit_task);
            deleteTask = view.findViewById(R.id.delete_task);
            permanentDoneIcon = view.findViewById(R.id.perm_done_mark);
        }

    }

    public TaskAdapter(List<Task> items, Context context) {
        this.tasks = items;
        this.context = context;
        setHasStableIds(true);
    }

    public void restoreItem(Context context, Task item, int position) {
        TaskHelper.addTaskToDataBase(context, item);
        tasks.add(position, item);
        HomeScreenActivity.taskList = tasks;
        notifyItemInserted(position);
        notifyItemRangeChanged(position, tasks.size());
    }

    public List<Task> getData() {
        return tasks;
    }

    public void deleteTask(int position, int taskID) {
        TaskHelper.deleteTask(context, taskID);
        tasks.remove(position);
        HomeScreenActivity.taskList = tasks;
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());
    }

    private void markTaskCompleted(int position, int taskID) {
        TaskHelper.markTaskCompleted(context, taskID);
        tasks.get(position).setTaskCompleted(true);
        HomeScreenActivity.taskList = tasks;
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
        return task.getId();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Task currentTask = tasks.get(position);

        if (null != currentTask) {
            String taskDescription = currentTask.getDescription();
            String priority = currentTask.getPriority();
            boolean isTaskCompleted = currentTask.isTaskCompleted();
            int priorityColor = PRIORITY_LOW.equalsIgnoreCase(priority) ? context.getResources().getColor(R.color.low) :
                    (PRIORITY_MEDIUM.equalsIgnoreCase(priority) ? context.getResources().getColor(R.color.medium) :
                            context.getResources().getColor(R.color.high));

            holder.taskDescriptionView.setText(taskDescription);
            holder.priorityView.setBackgroundColor(priorityColor);

            // When user clicks 'More Options' icon show the action icons
            holder.moreOptions.setOnClickListener(v -> {
                if (holder.actionsEnabled) {
                    makeActionsLayoutInvisibleAgain(holder, context.getResources().getColor(R.color.pure_white));
                } else {
                    makeActionsLayoutVisible(holder);
                }
            });

            // Mark Task related logic
            // If task is already completed, show done_mark icon and hide edit icon
            if (isTaskCompleted) {
                Log.e("istaskcom-T", ("position" + position + " / Desc - " + currentTask.getDescription()));
                holder.permanentDoneIcon.setVisibility(View.VISIBLE);
                holder.markTaskCompleted.setVisibility(View.GONE);
                holder.editTask.setVisibility(View.GONE);
            } else {
                Log.e("istaskcom-F", ("position" + position + " / Desc - " + currentTask.getDescription()));
                holder.permanentDoneIcon.setVisibility(View.GONE);
                holder.markTaskCompleted.setVisibility(View.VISIBLE);
                holder.editTask.setVisibility(View.VISIBLE);
                // When user clicks 'Mark Done' icon show the popup
                holder.markTaskCompleted.setOnClickListener(v -> new AlertDialog.Builder(context)
                        .setTitle("Mark Completed")
                        .setMessage("Nice, Have you completed the task?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", (arg0, arg1) -> {
                            markTaskCompleted(position, currentTask.getId());
                            holder.permanentDoneIcon.setVisibility(View.VISIBLE);
                            holder.markTaskCompleted.setVisibility(View.GONE);
                            holder.editTask.setVisibility(View.GONE);
                            makeActionsLayoutInvisibleAgain(holder, context.getResources().getColor(R.color.pure_white));
                        }).create().show());
            }

            // Delete task related logic
            // When user clicks 'Delete task' icon
            holder.deleteTask.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle("Delete task")
                    .setMessage("Are you sure want to delete the task?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", (arg0, arg1) -> {
                        makeActionsLayoutInvisibleAgain(holder, context.getResources().getColor(R.color.pure_white));
                        deleteTask(position, currentTask.getId());
                    }).create().show());

            // Edit task related logic
            holder.editTask.setOnClickListener(v -> {
                Intent editIntent = new Intent(context, AddNewTaskActivity.class);
                editIntent.putExtra(TASK_ID, currentTask.getId());
                context.startActivity(editIntent);
            });

        }

    }

    private void makeActionsLayoutVisible(MyViewHolder holder) {
        holder.actions.setVisibility(View.VISIBLE);
        holder.taskDetailLayout.setAlpha(0.7F);
        holder.taskDescriptionView.setAlpha(0.7F);
        holder.taskDetailLayout.setBackgroundColor(context.getResources().getColor(R.color.dim_description_color));
        holder.taskDescriptionView.setBackgroundColor(context.getResources().getColor(R.color.dim_description_color));
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.right_to_left_anim);
        holder.actions.startAnimation(animation);
        holder.actionsEnabled = !holder.actionsEnabled;
    }

    private void makeActionsLayoutInvisibleAgain(MyViewHolder holder, int backgroundColor) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.left_to_right_anim);
        holder.actions.startAnimation(animation);
        new CountDownTimer(500, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                holder.actions.setVisibility(View.GONE);
                holder.taskDetailLayout.setAlpha(1F);
                holder.taskDescriptionView.setAlpha(1F);
                holder.taskDetailLayout.setBackgroundColor(backgroundColor);
                holder.taskDescriptionView.setBackgroundColor(backgroundColor);
            }
        }.start();
        holder.actionsEnabled = !holder.actionsEnabled;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
