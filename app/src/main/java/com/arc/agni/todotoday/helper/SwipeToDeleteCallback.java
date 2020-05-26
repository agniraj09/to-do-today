package com.arc.agni.todotoday.helper;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.arc.agni.todotoday.R;
import com.arc.agni.todotoday.activities.HomeScreenActivity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

abstract public class SwipeToDeleteCallback extends ItemTouchHelper.Callback {

    private Context mContext;
    private Paint mClearPaint;
    private ColorDrawable mBackground;

    protected SwipeToDeleteCallback(Context context) {
        mContext = context;
        mBackground = new ColorDrawable();
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (HomeScreenActivity.taskList.get(viewHolder.getAdapterPosition()).isTaskCompleted()) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT);
        }
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        // Delete
        if (dX < 0) {
            mBackground.setColor(mContext.getResources().getColor(R.color.high));
            mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            mBackground.draw(c);

            Drawable deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_delete_task_white);
            int intrinsicWidth = deleteDrawable.getIntrinsicWidth();
            int intrinsicHeight = deleteDrawable.getIntrinsicHeight();
            int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;

            int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;
            deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteDrawable.draw(c);
        } else if (dX > 0) {
            mBackground.setColor(mContext.getResources().getColor(R.color.green));
            mBackground.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
            mBackground.draw(c);

            Drawable doneDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_done_task_white);
            int intrinsicWidth = doneDrawable.getIntrinsicWidth();
            int intrinsicHeight = doneDrawable.getIntrinsicHeight();
            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            int deleteIconLeft = deleteIconMargin;
            int deleteIconRight = deleteIconMargin + intrinsicWidth;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;
            doneDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            doneDrawable.draw(c);
        } else {
            mBackground.setBounds(0, 0, 0, 0);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);

    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.9f;
    }
}

