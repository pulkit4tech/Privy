package com.pulkit4tech.privy.utilities;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

import static com.pulkit4tech.privy.utilities.DatabaseHelper.TABLE_NAME;

public class PrivyProvider extends ContentProvider {

    private static final String PROVIDER_NAME = "com.pulkit4tech.privy.PrivyProvider";
    public static final String URL = "content://" + PROVIDER_NAME + "/api";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String id = "id";
    public static final String name = "name";
    public static final String lat = "lat";
    public static final String lng = "lng";
    public static final String rating = "rating";
    public static final String vicinity = "vicinity";
    private static final int API = 1;
    private static final int API_GET = 2;
    private static final UriMatcher uriMatcher;
    private static HashMap<String, String> values;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "api", API);
        uriMatcher.addURI(PROVIDER_NAME, "api/*", API_GET);
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        db = new DatabaseHelper(getContext()).getWritableDatabase();
        return db != null ? true : false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        qb.setProjectionMap(values);

        switch (uriMatcher.match(uri)) {
            case API:
                break;
            case API_GET:
                qb.appendWhere(id + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }
        if (sortOrder == null || sortOrder.equals("")) {
            sortOrder = name;
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {
            case API:
                long rowID = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                throw new SQLException("Failed to add a record into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int delCount = 0;
        switch (uriMatcher.match(uri)) {
            case API:
                delCount = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case API_GET:
                String _id = uri.getLastPathSegment();
                String where = id + "=" + _id;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(TABLE_NAME, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        // TODO : No need right now
        return 0;
    }
}
