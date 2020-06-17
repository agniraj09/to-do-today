package com.arc.agni.todotoday.helper;

import com.arc.agni.todotoday.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.arc.agni.todotoday.constants.AppConstants.LAST_OCCURRENCE_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.NEXT_OCCURRENCE_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.PATTERN_FULL_DATE;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_DAILY;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_MONTHLY;
import static com.arc.agni.todotoday.constants.AppConstants.RECURRENCE_WEEKLY;

public class DateHelper {

    public static Map<String, String> calculateOccurrenceDatesFromDateCreated(Task task) {
        Map<String, String> occurences = new TreeMap<>();

        String recurrence = task.getRecurrence();
        Calendar dateCreated = task.getDateCreated();

        if (null != dateCreated && null != recurrence) {
            Calendar taskTime = (task.isReminderSet() ? setTaskTime(dateCreated, task.getReminderHour(), task.getReminderMinute()) : dateCreated);

            if (RECURRENCE_DAILY.equalsIgnoreCase(recurrence)) {
                Calendar today = Calendar.getInstance();
                if (today.get(Calendar.DAY_OF_YEAR) == dateCreated.get(Calendar.DAY_OF_YEAR)) {
                    if (today.get(Calendar.HOUR_OF_DAY) < taskTime.get(Calendar.HOUR_OF_DAY) || (today.get(Calendar.HOUR_OF_DAY) == taskTime.get(Calendar.HOUR_OF_DAY) && today.get(Calendar.MINUTE) <= taskTime.get(Calendar.MINUTE))) {
                        occurences.put(LAST_OCCURRENCE_DATE, "None");
                        occurences.put(NEXT_OCCURRENCE_DATE, "Today");
                        return occurences;
                    } else {
                        occurences.put(LAST_OCCURRENCE_DATE, "Today");
                        occurences.put(NEXT_OCCURRENCE_DATE, "Tomorrow");
                        return occurences;
                    }
                } else {
                    if (today.get(Calendar.HOUR_OF_DAY) < taskTime.get(Calendar.HOUR_OF_DAY) || (today.get(Calendar.HOUR_OF_DAY) == taskTime.get(Calendar.HOUR_OF_DAY) && today.get(Calendar.MINUTE) <= taskTime.get(Calendar.MINUTE))) {
                        Calendar lastOccurrenceDate = Calendar.getInstance();
                        lastOccurrenceDate.add(Calendar.DAY_OF_YEAR, -1);
                        setTaskTime(lastOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));
                        occurences.put(LAST_OCCURRENCE_DATE, formatDate(lastOccurrenceDate, PATTERN_FULL_DATE));
                        occurences.put(NEXT_OCCURRENCE_DATE, "Today");
                        return occurences;
                    } else {
                        Calendar nextOccurrenceDate = Calendar.getInstance();
                        nextOccurrenceDate.add(Calendar.DAY_OF_YEAR, 1);
                        setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));
                        occurences.put(LAST_OCCURRENCE_DATE, "Today");
                        occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                        return occurences;
                    }
                }
            }

            if (RECURRENCE_WEEKLY.equalsIgnoreCase(recurrence)) {
                Calendar today = Calendar.getInstance();
                int weekOfDayComparison = Integer.compare(today.get(Calendar.DAY_OF_WEEK), taskTime.get(Calendar.DAY_OF_WEEK));

                // If same week
                if (today.get(Calendar.WEEK_OF_YEAR) == taskTime.get(Calendar.WEEK_OF_YEAR)) {

                    switch (weekOfDayComparison) {

                        //If same day
                        case 0: {
                            if (today.get(Calendar.HOUR_OF_DAY) < taskTime.get(Calendar.HOUR_OF_DAY) || (today.get(Calendar.HOUR_OF_DAY) == taskTime.get(Calendar.HOUR_OF_DAY) && today.get(Calendar.MINUTE) <= taskTime.get(Calendar.MINUTE))) {
                                occurences.put(LAST_OCCURRENCE_DATE, "None");
                                occurences.put(NEXT_OCCURRENCE_DATE, "Today");
                                return occurences;
                            } else {
                                Calendar nextOccurrenceDate = Calendar.getInstance();
                                nextOccurrenceDate.add(Calendar.WEEK_OF_YEAR, 1);
                                setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));
                                occurences.put(LAST_OCCURRENCE_DATE, "Today");
                                occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                                return occurences;
                            }
                        }

                        // Today is greater then task day
                        case 1: {
                            Calendar lastOccurrenceDate = Calendar.getInstance();
                            int differenceBetweenDays = today.get(Calendar.DAY_OF_WEEK) - taskTime.get(Calendar.DAY_OF_WEEK);
                            lastOccurrenceDate.add(Calendar.DAY_OF_YEAR, -differenceBetweenDays);
                            setTaskTime(lastOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            Calendar nextOccurrenceDate = Calendar.getInstance();
                            nextOccurrenceDate.setTimeInMillis(lastOccurrenceDate.getTimeInMillis());
                            nextOccurrenceDate.add(Calendar.DAY_OF_WEEK, 1);
                            setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            nextOccurrenceDate.add(Calendar.WEEK_OF_YEAR, 1);
                            occurences.put(LAST_OCCURRENCE_DATE, formatDate(lastOccurrenceDate, PATTERN_FULL_DATE));
                            occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                            return occurences;
                        }
                    }

                }
                // If different weeks
                else {
                    switch (weekOfDayComparison) {

                        //If same day
                        case 0: {
                            if (today.get(Calendar.HOUR_OF_DAY) < taskTime.get(Calendar.HOUR_OF_DAY) || (today.get(Calendar.HOUR_OF_DAY) == taskTime.get(Calendar.HOUR_OF_DAY) && today.get(Calendar.MINUTE) <= taskTime.get(Calendar.MINUTE))) {
                                Calendar lastOccurrenceDate = Calendar.getInstance();
                                lastOccurrenceDate.add(Calendar.WEEK_OF_YEAR, -1);
                                setTaskTime(lastOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));
                                occurences.put(LAST_OCCURRENCE_DATE, formatDate(lastOccurrenceDate, PATTERN_FULL_DATE));
                                occurences.put(NEXT_OCCURRENCE_DATE, "Today");
                                return occurences;
                            } else {
                                Calendar nextOccurrenceDate = Calendar.getInstance();
                                nextOccurrenceDate.add(Calendar.WEEK_OF_YEAR, 1);
                                setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));
                                occurences.put(LAST_OCCURRENCE_DATE, "Today");
                                occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                                return occurences;
                            }
                        }

                        // Today is greater then task day
                        case 1: {
                            Calendar lastOccurrenceDate = Calendar.getInstance();
                            int differenceBetweenDays = today.get(Calendar.DAY_OF_WEEK) - taskTime.get(Calendar.DAY_OF_WEEK);
                            lastOccurrenceDate.add(Calendar.DAY_OF_YEAR, -differenceBetweenDays);
                            setTaskTime(lastOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            Calendar nextOccurrenceDate = Calendar.getInstance();
                            nextOccurrenceDate.setTimeInMillis(lastOccurrenceDate.getTimeInMillis());
                            nextOccurrenceDate.add(Calendar.WEEK_OF_YEAR, 1);
                            setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            occurences.put(LAST_OCCURRENCE_DATE, formatDate(lastOccurrenceDate, PATTERN_FULL_DATE));
                            occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                            return occurences;
                        }

                        //Today is less than task day
                        case -1: {
                            Calendar nextOccurrenceDate = Calendar.getInstance();
                            int differenceBetweenDays = taskTime.get(Calendar.DAY_OF_WEEK) - today.get(Calendar.DAY_OF_WEEK);
                            nextOccurrenceDate.add(Calendar.DAY_OF_YEAR, differenceBetweenDays);
                            setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            Calendar lastOccurrenceDate = Calendar.getInstance();
                            lastOccurrenceDate.setTimeInMillis(lastOccurrenceDate.getTimeInMillis());
                            lastOccurrenceDate.add(Calendar.WEEK_OF_YEAR, -1);
                            setTaskTime(lastOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            occurences.put(LAST_OCCURRENCE_DATE, formatDate(lastOccurrenceDate, PATTERN_FULL_DATE));
                            occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                            return occurences;
                        }
                    }
                }
            }

            if (RECURRENCE_MONTHLY.equalsIgnoreCase(recurrence)) {
                Calendar today = Calendar.getInstance();

                int dayofMonthComparison = Integer.compare(today.get(Calendar.DAY_OF_MONTH), taskTime.get(Calendar.DAY_OF_MONTH));

                // If same month
                if (today.get(Calendar.MONTH) == taskTime.get(Calendar.MONTH)) {

                    switch (dayofMonthComparison) {

                        //If same day
                        case 0: {
                            if (today.get(Calendar.HOUR_OF_DAY) < taskTime.get(Calendar.HOUR_OF_DAY) || (today.get(Calendar.HOUR_OF_DAY) == taskTime.get(Calendar.HOUR_OF_DAY) && today.get(Calendar.MINUTE) <= taskTime.get(Calendar.MINUTE))) {
                                occurences.put(LAST_OCCURRENCE_DATE, "None");
                                occurences.put(NEXT_OCCURRENCE_DATE, "Today");
                                return occurences;
                            } else {
                                Calendar nextOccurrenceDate = Calendar.getInstance();
                                nextOccurrenceDate.add(Calendar.MONTH, 1);
                                setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));
                                occurences.put(LAST_OCCURRENCE_DATE, "Today");
                                occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                                return occurences;
                            }
                        }

                        // Today is greater then task day
                        case 1: {
                            Calendar lastOccurrenceDate = Calendar.getInstance();
                            int differenceBetweenDays = today.get(Calendar.DAY_OF_MONTH) - taskTime.get(Calendar.DAY_OF_MONTH);
                            lastOccurrenceDate.add(Calendar.DAY_OF_YEAR, -differenceBetweenDays);
                            setTaskTime(lastOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            Calendar nextOccurrenceDate = Calendar.getInstance();
                            nextOccurrenceDate.setTimeInMillis(lastOccurrenceDate.getTimeInMillis());
                            nextOccurrenceDate.add(Calendar.MONTH, 1);
                            setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            occurences.put(LAST_OCCURRENCE_DATE, formatDate(lastOccurrenceDate, PATTERN_FULL_DATE));
                            occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                            return occurences;
                        }
                    }

                }
                // If different weeks
                else {
                    switch (dayofMonthComparison) {

                        //If same day
                        case 0: {
                            if (today.get(Calendar.HOUR_OF_DAY) < taskTime.get(Calendar.HOUR_OF_DAY) || (today.get(Calendar.HOUR_OF_DAY) == taskTime.get(Calendar.HOUR_OF_DAY) && today.get(Calendar.MINUTE) <= taskTime.get(Calendar.MINUTE))) {
                                Calendar lastOccurrenceDate = Calendar.getInstance();
                                lastOccurrenceDate.add(Calendar.MONTH, -1);
                                setTaskTime(lastOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));
                                occurences.put(LAST_OCCURRENCE_DATE, formatDate(lastOccurrenceDate, PATTERN_FULL_DATE));
                                occurences.put(NEXT_OCCURRENCE_DATE, "Today");
                                return occurences;
                            } else {
                                Calendar nextOccurrenceDate = Calendar.getInstance();
                                nextOccurrenceDate.add(Calendar.MONTH, 1);
                                setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));
                                occurences.put(LAST_OCCURRENCE_DATE, "Today");
                                occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                                return occurences;
                            }
                        }

                        // Today is greater then task day
                        case 1: {
                            Calendar lastOccurrenceDate = Calendar.getInstance();
                            int differenceBetweenDays = today.get(Calendar.DAY_OF_MONTH) - taskTime.get(Calendar.DAY_OF_MONTH);
                            lastOccurrenceDate.add(Calendar.DAY_OF_YEAR, -differenceBetweenDays);
                            setTaskTime(lastOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            Calendar nextOccurrenceDate = Calendar.getInstance();
                            nextOccurrenceDate.setTimeInMillis(lastOccurrenceDate.getTimeInMillis());
                            nextOccurrenceDate.add(Calendar.MONTH, 1);
                            setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            occurences.put(LAST_OCCURRENCE_DATE, formatDate(lastOccurrenceDate, PATTERN_FULL_DATE));
                            occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                            return occurences;
                        }

                        //Today is less than task day
                        case -1: {
                            Calendar nextOccurrenceDate = Calendar.getInstance();
                            int differenceBetweenDays = taskTime.get(Calendar.DAY_OF_MONTH) - today.get(Calendar.DAY_OF_MONTH);
                            nextOccurrenceDate.add(Calendar.DAY_OF_YEAR, differenceBetweenDays);
                            setTaskTime(nextOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            Calendar lastOccurrenceDate = Calendar.getInstance();
                            lastOccurrenceDate.setTimeInMillis(lastOccurrenceDate.getTimeInMillis());
                            lastOccurrenceDate.add(Calendar.MONTH, -1);
                            setTaskTime(lastOccurrenceDate, taskTime.get(Calendar.HOUR_OF_DAY), taskTime.get(Calendar.MINUTE));

                            occurences.put(LAST_OCCURRENCE_DATE, formatDate(lastOccurrenceDate, PATTERN_FULL_DATE));
                            occurences.put(NEXT_OCCURRENCE_DATE, formatDate(nextOccurrenceDate, PATTERN_FULL_DATE));
                            return occurences;
                        }
                    }
                }

            }
        }
        return occurences;
    }

    private static Calendar setTaskTime(Calendar date, int hour, int minute) {
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, minute);
        return date;
    }

    public static String formatDate(Calendar dateToCompare, String pattern) {
        Calendar dateToCompareCopy = Calendar.getInstance();
        dateToCompareCopy.setTimeInMillis(dateToCompare.getTimeInMillis());

        if (PATTERN_FULL_DATE.equalsIgnoreCase(pattern)) {
            if (dateToCompareCopy.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                return "Today";
            }

            dateToCompareCopy.add(Calendar.DAY_OF_YEAR, -1);
            if (dateToCompareCopy.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                return "Yesterday";
            }

            dateToCompareCopy.add(Calendar.DAY_OF_YEAR, 2);
            if (dateToCompareCopy.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                return "Tomorrow";
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        return dateFormat.format(dateToCompare.getTime());
    }
}
