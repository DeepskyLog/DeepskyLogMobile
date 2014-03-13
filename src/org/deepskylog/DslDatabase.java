package org.deepskylog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DslDatabase {


    private static final String DATABASE_NAME = "dsl";
    private static final int DATABASE_VERSION = 5;

    private static final String DATABASE_CREATE =
        "create table deepskyObservations (deepskyObservationId integer primary key, "
        						  +"observerName text not null, "
        						  +"objectName text not null, "
        						  +"observationDate text not null, " 
        						  +"instrumentName text not null, " 
        						  +"observationDescription text);";
 
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqlLiteDatabase;
    
    public static void open() throws SQLException {
        if(databaseHelper==null) databaseHelper=new DatabaseHelper(MainActivity.mainActivity);
        try { if(sqlLiteDatabase==null) sqlLiteDatabase=databaseHelper.getWritableDatabase(); }
        catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "Database exception 1 "+e.toString(), Toast.LENGTH_LONG).show(); }
    }
    
    public static long insert(String table, ContentValues values) {
    	open();
    	return sqlLiteDatabase.insert(table, null, values);
    }
    
    public static Cursor execSql(String sql) {
        open();
        return sqlLiteDatabase.rawQuery(sql, null);
    }
     
    public static void close() {
    	sqlLiteDatabase.close();
    	databaseHelper.close();
    	sqlLiteDatabase=null;
    	databaseHelper=null;
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper  {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
     
        @Override
        public void onCreate(SQLiteDatabase db) {
        	//Toast.makeText(MainActivity.mainActivity, "Database create", Toast.LENGTH_LONG).show();
        	db.execSQL(DATABASE_CREATE);
        }
     
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	//Toast.makeText(MainActivity.mainActivity, "Database upgrade", Toast.LENGTH_LONG).show();
        	db.execSQL("DROP TABLE IF EXISTS deepskyObservations");
            onCreate(db);
        }
    }
}
