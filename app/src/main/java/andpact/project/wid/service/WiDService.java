package andpact.project.wid.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import andpact.project.wid.model.WiD;
import andpact.project.wid.util.WiDDatabaseHelper;

public class WiDService {
    private static final String DATABASE_NAME = "WiDDatabase";
    private static final int DATABASE_VERSION = 1;
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DETAIL = "detail";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_FINISH = "finish";
    public static final String COLUMN_DURATION = "duration";
    public static final String TABLE_WID = "wid_table";

//    public List<WiD> getWiDByDate(Context context, String date) {
//        List<WiD> wiDList = new ArrayList<>();
//
//        // Get a readable database instance
//        WiDDatabaseHelper databaseHelper = new WiDDatabaseHelper(context);
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//
//        // Define the columns to retrieve
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
//        // Define the selection criteria
//        String selection = COLUMN_DATE + " = ?";
//        String[] selectionArgs = {date};
//
//        // Perform the database query
//        Cursor cursor = db.query(
//                TABLE_WID,
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                null
//        );
//
//        // Process the cursor and create WiD objects
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                WiD wid = WiD.fromCursor(cursor);
//                wiDList.add(wid);
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//
//        // Close the database
//        db.close();
//
//        return wiDList;
//    }

//    public List<WiD> getAllWiD(Context context) {
//        List<WiD> wiDList = new ArrayList<>();
//
//        // Get a readable database instance
//        WiDDatabaseHelper databaseHelper = new WiDDatabaseHelper(context);
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//
//        // Define the columns to retrieve
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
//        // Perform the database query
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
//        // Process the cursor and create WiD objects
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                WiD wid = WiD.fromCursor(cursor);
//                wiDList.add(wid);
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//
//        // Close the database
//        db.close();
//
//        return wiDList;
//    }

}
