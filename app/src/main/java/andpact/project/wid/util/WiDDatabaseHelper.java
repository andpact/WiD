package andpact.project.wid.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}
