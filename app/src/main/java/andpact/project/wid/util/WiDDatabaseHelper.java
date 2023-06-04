package andpact.project.wid.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import andpact.project.wid.model.WiD;

public class WiDDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WiDDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_WID = "wid_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DETAIL = "detail";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_START = "start";
    private static final String COLUMN_FINISH = "finish";
    private static final String COLUMN_DURATION = "duration";

    public String getTableWID() {
        return TABLE_WID;
    }

    public String getColumnID() {
        return COLUMN_ID;
    }

    public String getColumnTitle() {
        return COLUMN_TITLE;
    }

    public String getColumnDetail() {
        return COLUMN_DETAIL;
    }

    public String getColumnDate() {
        return COLUMN_DATE;
    }

    public String getColumnStart() {
        return COLUMN_START;
    }

    public String getColumnFinish() {
        return COLUMN_FINISH;
    }

    public String getColumnDuration() {
        return COLUMN_DURATION;
    }


    public WiDDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_WID + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_DETAIL + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_START + " TEXT," +
                COLUMN_FINISH + " TEXT," +
                COLUMN_DURATION + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WID);
        onCreate(db);
    }

//    public long insertWiD(WiD wid) {
//        SQLiteDatabase db = getWritableDatabase();
//
//        ContentValues values = wid.toContentValues();
//
//        long id = db.insert(TABLE_WID, null, values);
//
//        db.close();
//
//        return id;
//    }

    public WiD getWiDById(Long id) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DETAIL,
                COLUMN_DATE,
                COLUMN_START,
                COLUMN_FINISH,
                COLUMN_DURATION
        };

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                TABLE_WID,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        WiD wiD = null;
        if (cursor != null && cursor.moveToFirst()) {
            wiD = WiD.fromCursor(cursor);
            cursor.close();
        }

        db.close();

        return wiD;
    }

    public List<WiD> getWiDByDate(String date) {
        List<WiD> wiDList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DETAIL,
                COLUMN_DATE,
                COLUMN_START,
                COLUMN_FINISH,
                COLUMN_DURATION
        };

        String selection = COLUMN_DATE + " = ?";
        String[] selectionArgs = {date};

        Cursor cursor = db.query(
                TABLE_WID,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                WiD wid = WiD.fromCursor(cursor);
                wiDList.add(wid);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        return wiDList;
    }

//    public List<WiD> getAllWiD() {
//        List<WiD> wiDList = new ArrayList<>();
//
//        SQLiteDatabase db = getReadableDatabase();
//
//        String[] projection = {
//                COLUMN_ID,
//                COLUMN_TITLE,
//                COLUMN_DETAIL,
//                COLUMN_DATE,
//                COLUMN_START,
//                COLUMN_FINISH,
//                COLUMN_DURATION
//        };
//
//        Cursor cursor = db.query(
//                TABLE_WID,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                WiD wid = WiD.fromCursor(cursor);
//                wiDList.add(wid);
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//
//        db.close();
//
//        return wiDList;
//    }

    public void updateWiDDetailById(Long id, String newDetail) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DETAIL, newDetail);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        db.update(TABLE_WID, values, selection, selectionArgs);
        db.close();
    }

    public void deleteWiDById(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(TABLE_WID, selection, selectionArgs);
        db.close();
    }
}