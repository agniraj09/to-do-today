package com.arc.agni.todotoday.helper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import static com.arc.agni.todotoday.constants.AppConstants.DATABASE_NAME;
import static com.arc.agni.todotoday.constants.AppConstants.DELETE_ALL_DATA;
import static com.arc.agni.todotoday.constants.AppConstants.QUERY_CREATE_TABLE;
import static com.arc.agni.todotoday.constants.AppConstants.QUERY_DROP_TALE;
import static com.arc.agni.todotoday.constants.AppConstants.QUERY_GET_ITEM;
import static com.arc.agni.todotoday.constants.AppConstants.QUERY_SELECT_DATA;
import static com.arc.agni.todotoday.constants.AppConstants.TASKS_COLUMN_ID;
import static com.arc.agni.todotoday.constants.AppConstants.TASKS_COLUMN_TASK_DETAIL;
import static com.arc.agni.todotoday.constants.AppConstants.TASKS_TABLE_NAME;

public class DBHelper extends SQLiteOpenHelper {

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(QUERY_DROP_TALE);
        onCreate(db);
    }

    void insertTask(int id, String taskdetail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASKS_COLUMN_ID, id);
        contentValues.put(TASKS_COLUMN_TASK_DETAIL, taskdetail);
        db.insert(TASKS_TABLE_NAME, null, contentValues);
    }

    String getTask(int id) {
        String task = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_GET_ITEM + id, null);
        if (cursor != null && cursor.moveToFirst()) {
            task = cursor.getString(cursor.getColumnIndex(TASKS_COLUMN_TASK_DETAIL));
            cursor.close();
        }
        return task;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TASKS_TABLE_NAME);
    }

    void updateTask(int id, String taskdetail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASKS_COLUMN_ID, id);
        contentValues.put(TASKS_COLUMN_TASK_DETAIL, taskdetail);
        db.update(TASKS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }

    void deleteTask(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TASKS_TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    void deleteAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_ALL_DATA);
    }

    ArrayList<String> getAllTasks() {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(QUERY_SELECT_DATA, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(TASKS_COLUMN_TASK_DETAIL)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}