package com.dhruv.networkmanager.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.widget.RemoteViews;

import com.dhruv.networkmanager.R;

import java.util.Locale;

final class NotificationGenerator {

    private Context context;

    private static final int NOTIFICATION_ID = 1;

    private Paint iconSpeedPaint, iconUnitPaint;
    private Bitmap iconBitmap;
    private Canvas iconCanvas;

    private RemoteViews notificationContentView;

    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;

    NotificationGenerator(Context context) {
        this.context = context;

        setup();
    }

    void start(Service serviceContext) {
        serviceContext.startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    void stop(Service serviceContext) {
        serviceContext.stopForeground(true);
    }

    void hideNotification() {
        notificationBuilder.setPriority(Notification.PRIORITY_MIN);
    }

    void showNotification() {
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
    }

    void updateNotification(Speed speed, Used used) {

        notificationBuilder.setSmallIcon(
                getIndicatorIcon(speed.speed, speed.speedUnit)
        );

        RemoteViews contentView = notificationContentView.clone();

        contentView.setTextViewText(
                R.id.notificationSpeedValue,
                speed.speed
        );

        contentView.setTextViewText(
                R.id.notificationSpeedUnit,
                speed.speedUnit
        );

        contentView.setTextViewText(
                R.id.notificationText,
                String.format(
                        Locale.ENGLISH, "Mobile Data: " + used.used + used.usedUnit
                )
        );

        notificationBuilder.setContent(contentView);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    private void setup() {
        setupIndicatorIconGenerator();

        notificationContentView = new RemoteViews(context.getPackageName(), R.layout.notification_view);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new Notification.Builder(context)
                .setSmallIcon(getIndicatorIcon("", ""))
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_SECRET)
                .setContent(notificationContentView)
                .setOngoing(true)
                .setLocalOnly(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channel_id = "1";
            NotificationChannel channel = new NotificationChannel(
                    channel_id, "Indicator", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channel_id);
        }


    }

    private void setupIndicatorIconGenerator() {
        iconSpeedPaint = new Paint();
        iconSpeedPaint.setColor(Color.WHITE);
        iconSpeedPaint.setAntiAlias(true);
        iconSpeedPaint.setTextSize(65);
        iconSpeedPaint.setTextAlign(Paint.Align.CENTER);
        iconSpeedPaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));

        iconUnitPaint = new Paint();
        iconUnitPaint.setColor(Color.WHITE);
        iconUnitPaint.setAntiAlias(true);
        iconUnitPaint.setTextSize(40);
        iconUnitPaint.setTextAlign(Paint.Align.CENTER);
        iconUnitPaint.setTypeface(Typeface.DEFAULT_BOLD);

        iconBitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888);

        iconCanvas = new Canvas(iconBitmap);
    }

    private Icon getIndicatorIcon(String speedValue, String speedUnit) {
        iconCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        iconCanvas.drawText(speedValue, 48, 52, iconSpeedPaint);
        iconCanvas.drawText(speedUnit, 48, 95, iconUnitPaint);

        return Icon.createWithBitmap(iconBitmap);
    }

}
