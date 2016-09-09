package org.janastu.heritageapp.geoheritagev2.client.db;

/**
 * Created by Graphics-User on 1/21/2016.
 */import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class GeoTagMediaDBHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 2;




    public static final String DATABASE_NAME = "heritage.db";
    public static final String DATA_TABLE_NAME = "geotagdata";

    public static final String DATA_COLUMN_ID = "id";
    public static final String DATA_COLUMN_TITLE = "title";
    public static final String DATA_COLUMN_DESCRPITION = "description";
    public static final String DATA_COLUMN_LATITUDE = "latitude";
    public static final String DATA_COLUMN_LONGITUDE = "longitude";
    public static final String DATA_COLUMN_TIME = "time";
    public static final String DATA_COLUMN_ADDRESS = "address";
    public static final String DATA_COLUMN_CATEGORY = "category";
    public static final String DATA_COLUMN_LANGUAGE = "language";
    public static final String DATA_COLUMN_GROUP = "heritagegroup";
    public static final String DATA_COLUMN_FILE_NAME = "file_name";
    public static final String DATA_COLUMN_UPLOAD_STATUS = "upload_status";
    public static final String DATA_COLUMN_MEDIA_TYPE = "media_type";
    public static final String DATA_COLUMN_FILE_SIZE = "file_size";
    private static final String TAG = "GeoTagMediaDBHelper";
//

    // vars
    private static GeoTagMediaDBHelper mInstance = null;
    private GeoTagMediaDBOpenHelper mOH;
    private SQLiteDatabase mDB;
    private final Context mCTX;
    private boolean opened = false;

    // static creator, create first time or return existing one
    public static GeoTagMediaDBHelper getInstance(Context ctx) {
        if (mInstance==null) {
            mInstance = new GeoTagMediaDBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    //---- OPENHELPER ----------------------------------------------------------

    private static class GeoTagMediaDBOpenHelper extends SQLiteOpenHelper {
        // constructor
        GeoTagMediaDBOpenHelper(Context context) {

            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }
        // called if DB does not exist : create tables
        public void onCreate(SQLiteDatabase db) {
          //  db.execSQL(CREATE_T_CATS);
          //  db.execSQL(CREATE_T_EVENTS);
        }
        // called if DB version is increased : delete and recreate whole DB
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        //    db.execSQL("DROP TABLE IF EXISTS "+T_CATS);
         //   db.execSQL("DROP TABLE IF EXISTS "+T_EVENTS);
            onCreate(db);
        }
    }


    public GeoTagMediaDBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCTX = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        // Create Waypoint Table
        db.execSQL(
                "CREATE TABLE " + DATA_TABLE_NAME +
                        "(" + DATA_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DATA_COLUMN_TITLE + " TEXT, " +
                        DATA_COLUMN_DESCRPITION + " TEXT, " +

                        DATA_COLUMN_LATITUDE + " TEXT, " +
                        DATA_COLUMN_LONGITUDE + " TEXT, " +
                        DATA_COLUMN_TIME + " TEXT, " +
                        DATA_COLUMN_ADDRESS + " TEXT, " +
                        DATA_COLUMN_CATEGORY + " TEXT, " +
                        DATA_COLUMN_LANGUAGE + " TEXT, " +
                        DATA_COLUMN_GROUP + " TEXT, " +
                        DATA_COLUMN_UPLOAD_STATUS + " BOOLEAN, " +
                        DATA_COLUMN_MEDIA_TYPE + " INTEGER, " +
                        DATA_COLUMN_FILE_SIZE + " INTEGER, " +
                        DATA_COLUMN_FILE_NAME + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE_NAME);
        onCreate(db);
    }



    public long insertWaypoint(String title, String description, String address, Double latitude,
                                  Double longitude, String consolidatedTags, String urlOrfileLink,
                                  String heritageCategory, String heritageLanguage, String heritageGroup, Integer mediaType, Long fileSize) {

//writing into DB;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATA_COLUMN_TITLE, title);
        Log.d("inserting ", title);
        contentValues.put(DATA_COLUMN_DESCRPITION, description);
        Log.d("inserting ", description);
        contentValues.put(DATA_COLUMN_LATITUDE, latitude);
        Log.d("latitude ", latitude.toString());
        contentValues.put(DATA_COLUMN_LONGITUDE, longitude);
        Log.d("longitude ", longitude.toString());
        String  currentTime = null;;
        contentValues.put(DATA_COLUMN_TIME, currentTime);
        contentValues.put(DATA_COLUMN_ADDRESS,address );
        Log.d("insertingaddress ", address);
        contentValues.put(DATA_COLUMN_CATEGORY, heritageCategory);
        Log.d("insert   Category ", heritageCategory);
        contentValues.put(DATA_COLUMN_LANGUAGE, heritageLanguage);
        Log.d(" ins Language ", heritageLanguage);
        contentValues.put(DATA_COLUMN_GROUP, heritageGroup);
        Log.d("inserting heritageGroup", heritageGroup);
        contentValues.put(DATA_COLUMN_UPLOAD_STATUS, false);
        contentValues.put(DATA_COLUMN_MEDIA_TYPE, mediaType);

        Log.d("inserting mediaType", mediaType.toString());
        contentValues.put(DATA_COLUMN_FILE_SIZE,fileSize.intValue() );
        contentValues.put(DATA_COLUMN_FILE_NAME,urlOrfileLink );
        long id = db.insert(DATA_TABLE_NAME, null, contentValues);
        return id;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, DATA_TABLE_NAME);
        return numRows;
    }





    public Integer deleteWaypoint(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DATA_TABLE_NAME,
                DATA_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }



    public Cursor getWaypoint(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + DATA_TABLE_NAME + " WHERE " +
                DATA_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }


    public Cursor getAllWaypoint() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + DATA_TABLE_NAME, null );
        return res;
    }


    public ArrayList<String > getAllFiles() {

        String[] files = null;
        ArrayList<String > strList = new ArrayList<String >();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "SELECT * FROM " + DATA_TABLE_NAME, null );
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {

               String file =  cursor.getString(13);
                Log.d(TAG, "getAllFiles " + file );
                strList.add(file);
                cursor.moveToNext();
            }
        }
        cursor.close();


        return strList ;
    }

    public Boolean updateWaypointToDownloaded(String title, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "title " +title + "id" + id);

      //  db.execSQL("UPDATE " +DATA_TABLE_NAME +  " "+ DATA_COLUMN_UPLOAD_STATUS +  " = " +" '1' "  + "WHERE id = '"+id +"'"  );


      //  Cursor res =  db.rawQuery("UPDATE  " + DATA_TABLE_NAME + " "+ DATA_COLUMN_UPLOAD_STATUS +  " = " +" '1' "+ " WHERE " +
        //        DATA_COLUMN_ID + "=?", new String[]{Integer.toString(id)});

        ContentValues values = new ContentValues();
        values.put(DATA_COLUMN_UPLOAD_STATUS, 1);

        db.update(DATA_TABLE_NAME, values , DATA_COLUMN_ID +" = ?", new String[] { Integer.toString(id) });
        return true;
    }

    public SQLiteDatabase openDB() {
        if (!opened) {
            mDB = mOH.getWritableDatabase();
            opened = true;
        }
        return mDB;
    }

    // get handle (open database if closed)
    public SQLiteDatabase getHandleDB() {
       // return openDB();

        SQLiteDatabase db = this.getWritableDatabase();
        return db;
    }

    // close database
    public void closeDB() {
        if (opened) mOH.close();
        opened = false;
    }

    // reset database
    public void resetDB() {
        openDB();
        mOH.onUpgrade(this.mDB,1,1);
        closeDB();
    }






}