package com.dhruv.networkmanager.data.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DeMoliT1on.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SERVER = "ftpServers";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_HOST = "host";
    public static final String COLUMN_ANONYMOUS = "anonymous";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PORT = "port";

    public static final String TABLE_PACKAGE="packageList";
    public static final String COLUMN_PACKAGE_NAME = "packageName";
    public static final String COLUMN_PACKAGE_UID="packageUid";

    private static final String CREATE_SERVER_TABLE = "create table " + TABLE_SERVER + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null,"
            + COLUMN_HOST + " text not null,"
            + COLUMN_ANONYMOUS + " int not null,"
            + COLUMN_LOGIN + " text null,"
            + COLUMN_PASSWORD + " text null,"
            + COLUMN_PORT + " int not null);";

    private static final String CREATE_PACKAGE_TABLE="create table "+ TABLE_PACKAGE + "("
            + COLUMN_PACKAGE_UID + " integer primary key,"
            + COLUMN_PACKAGE_NAME + " text,"
            + COLUMN_NAME + " text );";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_SERVER_TABLE);
        database.execSQL(CREATE_PACKAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER);
        onCreate(db);
    }
}
