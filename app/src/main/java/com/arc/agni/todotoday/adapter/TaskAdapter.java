package com.arc.agni.todotoday.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_LOW;
import static com.arc.agni.todotoday.constants.AppConstants.PRIORITY_MEDIUM;
import static com.arc.agni.todotoday.constants.AppConstants.TASK_ID;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Task> tasks;
    private Context context;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        boolean isEditEnabled;
        ConstraintLayout taskDetailLayout;
        ImageView priorityView;
        TextView taskDescriptionView;
        ImageView editTask;
        ImageView permanentDoneIcon;

        MyViewHolder(View view) {
            super(view);
            taskDetailLayout = view.findViewById(R.id.task_detail_layout);
            priorityView = view.findViewById(R.id.priority_view);
            taskDescriptionView = view.findViewById(R.id.task_description);
            editTask = view.findViewById(R.id.edit_task);
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

    public void deleteTask(int position, int taskID) {
        TaskHelper.deleteTask(context, taskID);
        tasks.remove(position);
        HomeScreenActivity.taskList = tasks;
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());
    }

    public void markTaskCompletionStatus(int position, int taskID, boolean status) {
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
        return task.getId();
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
                holder.editTask.setVisibility(View.GONE);
                holder.priorityView.setVisibility(View.GONE);
            } else {
                holder.permanentDoneIcon.setVisibility(View.GONE);
                holder.editTask.setVisibility(View.VISIBLE);
                holder.priorityView.setVisibility(View.VISIBLE);

                Drawable priorityLabel = PRIORITY_LOW.equalsIgnoreCase(priority) ? context.getResources().getDrawable(R.drawable.ic_label_low) :
                        (PRIORITY_MEDIUM.equalsIgnoreCase(priority) ? context.getResources().getDrawable(R.drawable.ic_label_medium) :
                                context.getResources().getDrawable(R.drawable.ic_label_high));
                holder.priorityView.setImageDrawable(priorityLabel);

                holder.taskDetailLayout.setOnClickListener(v -> {
                    if (holder.isEditEnabled) {
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.left_to_right_anim);
                        holder.editTask.startAnimation(animation);
                        holder.editTask.setVisibility(View.GONE);
                    } else {
                        holder.editTask.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.right_to_left_anim);
                        holder.editTask.startAnimation(animation);
                    }
                    holder.isEditEnabled = !holder.isEditEnabled;
                });

                holder.editTask.setOnClickListener(v -> {
                    Intent editIntent = new Intent(context, AddNewTaskActivity.class);
                    editIntent.putExtra(TASK_ID, currentTask.getId());
                    context.startActivity(editIntent);
                });
            }


            if (holder.isEditEnabled) {
                holder.editTask.setVisibility(View.VISIBLE);
            } else {
                holder.editTask.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
