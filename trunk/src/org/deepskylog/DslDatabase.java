package org.deepskylog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DslDatabase {


    private static final String DATABASE_NAME="dsl";
    private static final int DATABASE_VERSION=12;

    private static final String DATABASE_CREATE_DEEPSKYOBSERVATIONS=
    	    "create table " +
	    		"deepskyObservations " +
	    	   "(deepskyObservationId 				integer primary key, "
		  	   +"observerName 						text not null, "
		  	   +"deepskyObjectName 					text not null, "
		  	   +"deepskyObservationDate 			text not null, " 
		  	   +"instrumentName 					text not null, " 
		  	   +"deepskyObservationDescription		text);";
    
    private static final String DATABASE_CREATE_DEEPSKYOBSERVATIONSLIST=
    	    "create table " +
	    		"deepskyObservationsList " +
	    	   "(deepskyObservationId 				integer primary key, "
		  	   +"observerName 						text not null, "
		  	   +"deepskyObjectName 					text not null, "
		  	   +"deepskyObservationDate 			text not null);";
    
	 private static final String DATABASE_CREATE_DEEPSKYOBSERVATIONSLISTDAYS=
    	    "create table " +
	    		"deepskyObservationsListDays " +
	    	   "(deepskyObservationsListDateCount	integer primary key, "
		  	   +"deepskyObservationsListDate 		text not null);";



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
        
    public static long insertOrUpdate(String table, ContentValues values) {
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
        	db.execSQL(DATABASE_CREATE_DEEPSKYOBSERVATIONS);
        	db.execSQL(DATABASE_CREATE_DEEPSKYOBSERVATIONSLIST);
        	db.execSQL(DATABASE_CREATE_DEEPSKYOBSERVATIONSLISTDAYS);
        }
     
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	//Toast.makeText(MainActivity.mainActivity, "Database upgrade", Toast.LENGTH_LONG).show();
        	db.execSQL("DROP TABLE IF EXISTS deepskyObservations");
        	db.execSQL("DROP TABLE IF EXISTS deepskyObservationsList");
        	db.execSQL("DROP TABLE IF EXISTS deepskyObservationsListDays");
            onCreate(db);
        }
    }
}
