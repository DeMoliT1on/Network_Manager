package com.dhruv.networkmanager.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dhruv.networkmanager.data.entities.FtpServer;
import com.dhruv.networkmanager.data.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ServerRepository {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private String[] allColumns={
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_HOST,
            DatabaseHelper.COLUMN_ANONYMOUS,
            DatabaseHelper.COLUMN_LOGIN,
            DatabaseHelper.COLUMN_PASSWORD,
            DatabaseHelper.COLUMN_PORT
    };

    public ServerRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    private void close() {
        dbHelper.close();
    }

    public List<FtpServer> getServers() {
        this.open();
        List<FtpServer> results = new ArrayList<FtpServer>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVER,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FtpServer server = this.cursorToServer(cursor);
            results.add(server);
            cursor.moveToNext();
        }

        cursor.close();
        this.close();

        return results;
    }

    public FtpServer getServer(int id) {
        this.open();

        FtpServer result = null;

        Cursor cursor = database.query(DatabaseHelper.TABLE_SERVER,
                allColumns, DatabaseHelper.COLUMN_ID + "=" + id, null, null, null, null);

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            result = this.cursorToServer(cursor);
        }

        cursor.close();
        this.close();

        return result;
    }

    public void addServer(FtpServer server) {
        this.open();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_NAME, server.getName());
        values.put(DatabaseHelper.COLUMN_HOST, server.getHost());
        values.put(DatabaseHelper.COLUMN_PORT, server.getPort());
        values.put(DatabaseHelper.COLUMN_ANONYMOUS, server.isAnonymous());
        values.put(DatabaseHelper.COLUMN_LOGIN, server.getLogin());
        values.put(DatabaseHelper.COLUMN_PASSWORD, server.getPassword());

        database.insert(DatabaseHelper.TABLE_SERVER, null, values);
        this.close();
    }

    public void updateServer(FtpServer server) {
        this.open();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_NAME, server.getName());
        values.put(DatabaseHelper.COLUMN_HOST, server.getHost());
        values.put(DatabaseHelper.COLUMN_PORT, server.getPort());
        values.put(DatabaseHelper.COLUMN_ANONYMOUS, server.isAnonymous());
        values.put(DatabaseHelper.COLUMN_LOGIN, server.getLogin());
        values.put(DatabaseHelper.COLUMN_PASSWORD, server.getPassword());

        database.update(DatabaseHelper.TABLE_SERVER, values, DatabaseHelper.COLUMN_ID + "=" + server.getId(), null);
        this.close();
    }

    public void deleteServer(int serverId) {
        this.open();
        database.delete(DatabaseHelper.TABLE_SERVER, DatabaseHelper.COLUMN_ID + "=" + serverId, null);
        this.close();
    }

    private FtpServer cursorToServer(Cursor cursor) {
        FtpServer server = new FtpServer();
        server.setId(cursor.getInt(0));
        server.setName(cursor.getString(1));
        server.setHost(cursor.getString(2));
        server.setAnonymous(cursor.getInt(3) > 0);
        server.setLogin(cursor.getString(4));
        server.setPassword(cursor.getString(5));
        server.setPort(cursor.getInt(6));
        return server;
    }


}
