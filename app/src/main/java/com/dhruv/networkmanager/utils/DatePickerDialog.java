package com.dhruv.networkmanager.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dhruv.networkmanager.R;
import com.dhruv.networkmanager.fragments.AppUsageFragment;
import com.dhruv.networkmanager.listeners.OnDateSetListener;

import java.util.Calendar;

public class DatePickerDialog extends DialogFragment {

    OnDateSetListener listener;
    Calendar calendar;
    int position;

    public DatePickerDialog(OnDateSetListener listener, int position, Calendar calendar) {
        this.listener=listener;
        this.position = position;
        this.calendar = calendar;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        switch (position) {
            case AppUsageFragment.DAY:
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                return new android.app.DatePickerDialog(getActivity(), listener, year, month, day);
            case AppUsageFragment.MONTH:
                return monthYearDialog();
            case AppUsageFragment.YEAR:
                return yearDialog();
        }

        return null;

    }

    public Dialog monthYearDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_month_year, null);
        final NumberPicker yearPicker = view.findViewById(R.id.year);
        final NumberPicker monthPicker = view.findViewById(R.id.month);
        yearPicker.setMinValue(calendar.get(Calendar.YEAR) - 5);
        yearPicker.setMaxValue(calendar.get(Calendar.YEAR));
        yearPicker.setValue(calendar.get(Calendar.YEAR));
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = yearPicker.getValue();
                        int month = monthPicker.getValue();
                        listener.onMonthSet(month, year);
                    }
                });

        return builder.create();
    }

    public Dialog yearDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_year, null);
        final NumberPicker yearPicker = view.findViewById(R.id.year);
        yearPicker.setMinValue(calendar.get(Calendar.YEAR) - 5);
        yearPicker.setMaxValue(calendar.get(Calendar.YEAR));
        yearPicker.setValue(calendar.get(Calendar.YEAR));
        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = yearPicker.getValue();
                        listener.onYearSet(year);
                    }
                });

        return builder.create();
    }
}
