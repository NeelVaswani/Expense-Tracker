package com.example.expensemanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

    public static String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy");
        return simpleDateFormat.format(date);
    }

    public static String formatDateByMonth(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");
        return dateFormat.format(date);
    }
}

