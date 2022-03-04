package com.arc.agni.todotoday.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class Task implements Serializable, Parcelable {

    private long id;
    private String description;
    private String priority;
    private String recurrence;
    private boolean autoDeleteByEOD;
    private Calendar dateCreated;
    private boolean isReminderSet;
    private int reminderHour;
    private int reminderMinute;
    private int remindBefore;
    private String reminderType;
    private boolean isTaskCompleted;
    private Calendar completedDate;

    public Task() {
    }

    public Task(String description, String priority, String recurrence, boolean autoDeleteByEOD, Calendar dateCreated, boolean isReminderSet, int reminderHour, int reminderMinute, int remindBefore, String reminderType, boolean isTaskCompleted) {
        this.description = description;
        this.priority = priority;
        this.recurrence = recurrence;
        this.autoDeleteByEOD = autoDeleteByEOD;
        this.dateCreated = dateCreated;
        this.isReminderSet = isReminderSet;
        this.reminderHour = reminderHour;
        this.reminderMinute = reminderMinute;
        this.remindBefore = remindBefore;
        this.reminderType = reminderType;
        this.isTaskCompleted = isTaskCompleted;
    }


    protected Task(Parcel in) {
        id = in.readLong();
        description = in.readString();
        priority = in.readString();
        recurrence = in.readString();
        autoDeleteByEOD = in.readByte() != 0;
        isReminderSet = in.readByte() != 0;
        reminderHour = in.readInt();
        reminderMinute = in.readInt();
        remindBefore = in.readInt();
        reminderType = in.readString();
        isTaskCompleted = in.readByte() != 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public boolean isAutoDeleteByEOD() {
        return autoDeleteByEOD;
    }

    public void setAutoDeleteByEOD(boolean autoDeleteByEOD) {
        this.autoDeleteByEOD = autoDeleteByEOD;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isReminderSet() {
        return isReminderSet;
    }

    public void setReminderSet(boolean reminderSet) {
        isReminderSet = reminderSet;
    }

    public int getReminderHour() {
        return reminderHour;
    }

    public void setReminderHour(int reminderHour) {
        this.reminderHour = reminderHour;
    }

    public int getReminderMinute() {
        return reminderMinute;
    }

    public void setReminderMinute(int reminderMinute) {
        this.reminderMinute = reminderMinute;
    }

    public int getRemindBefore() {
        return remindBefore;
    }

    public void setRemindBefore(int remindBefore) {
        this.remindBefore = remindBefore;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public boolean isTaskCompleted() {
        return isTaskCompleted;
    }

    public void setTaskCompleted(boolean taskCompleted) {
        isTaskCompleted = taskCompleted;
    }

    public Calendar getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Calendar completedDate) {
        this.completedDate = completedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id == task.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", recurrence='" + recurrence + '\'' +
                ", autoDeleteByEOD=" + autoDeleteByEOD +
                ", dateCreated=" + dateCreated +
                ", isReminderSet=" + isReminderSet +
                ", reminderHour=" + reminderHour +
                ", reminderMinute=" + reminderMinute +
                ", remindBefore=" + remindBefore +
                ", reminderType='" + reminderType + '\'' +
                ", isTaskCompleted=" + isTaskCompleted +
                ", completedDate=" + completedDate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(description);
        dest.writeString(priority);
        dest.writeString(recurrence);
        dest.writeByte((byte) (autoDeleteByEOD ? 1 : 0));
        dest.writeByte((byte) (isReminderSet ? 1 : 0));
        dest.writeInt(reminderHour);
        dest.writeInt(reminderMinute);
        dest.writeInt(remindBefore);
        dest.writeString(reminderType);
        dest.writeByte((byte) (isTaskCompleted ? 1 : 0));
    }
}
