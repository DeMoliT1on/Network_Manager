package com.dhruv.networkmanager.listeners;

public interface OnDateSetListener extends android.app.DatePickerDialog.OnDateSetListener {

    void onMonthSet(int month, int year);

    void onYearSet(int year);
}
