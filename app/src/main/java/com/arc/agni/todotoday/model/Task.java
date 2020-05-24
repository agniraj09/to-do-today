package com.arc.agni.todotoday.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class Task implements Serializable {

    private int id;
    private String description;
    private String priority;
    private boolean autoDeleteByEOD;
    private Date dateCreated;
    private boolean isReminderNeeded;
    private int reminderHour;
    private int reminderMinute;
    private boolean isTaskCompleted;

    public Task() {
    }

    public Task(int id, String description, String priority, boolean autoDeleteByEOD, Date dateCreated, boolean isReminderNeeded, int reminderHour, int reminderMinute, boolean isTaskCompleted) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.autoDeleteByEOD = autoDeleteByEOD;
        this.dateCreated = dateCreated;
        this.isReminderNeeded = isReminderNeeded;
        this.reminderHour = reminderHour;
        this.reminderMinute = reminderMinute;
        this.isTaskCompleted = isTaskCompleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isAutoDeleteByEOD() {
        return autoDeleteByEOD;
    }

    public void setAutoDeleteByEOD(boolean autoDeleteByEOD) {
        this.autoDeleteByEOD = autoDeleteByEOD;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isReminderNeeded() {
        return isReminderNeeded;
    }

    public void setReminderNeeded(boolean reminderNeeded) {
        isReminderNeeded = reminderNeeded;
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

    public boolean isTaskCompleted() {
        return isTaskCompleted;
    }

    public void setTaskCompleted(boolean taskCompleted) {
        isTaskCompleted = taskCompleted;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", autoDeleteByEOD=" + autoDeleteByEOD +
                ", dateCreated=" + dateCreated +
                ", isReminderNeeded=" + isReminderNeeded +
                ", reminderHour=" + reminderHour +
                ", reminderMinute=" + reminderMinute +
                ", isTaskCompleted=" + isTaskCompleted +
                '}';
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
        return id;
    }
}
