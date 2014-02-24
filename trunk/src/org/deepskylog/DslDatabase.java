package org.deepskylog;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DslDatabase {


    private static final String DATABASE_NAME = "dsl";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
        "create table observations (observationid integer primary key, "
        						  +"observername text not null, "
        						  +"objectname text not null, "
        						  +"observationdate text not null, " 
        						  +"observationdescription text);";
 
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqlLiteDatabase;
    
    public DslDatabase() {
    }
     
    public static void open() throws SQLException {
        databaseHelper=new DatabaseHelper(MainActivity.mainActivity);
        try { sqlLiteDatabase=databaseHelper.getWritableDatabase(); }
        catch(Exception e) { Toast.makeText(MainActivity.mainActivity, "Database xception "+e.toString(), Toast.LENGTH_LONG).show(); }
    }
    
    public static long insertObservation(JSONObject jsonObject) {
        ContentValues initialValues = new ContentValues();
        try {
        	initialValues.put("observationid", jsonObject.getString("observationid"));
            initialValues.put("objectname", jsonObject.getString("objectname"));
            initialValues.put("observername", jsonObject.getString("observername"));
            initialValues.put("observationdescription", jsonObject.getString("observationdescription"));
            initialValues.put("observationdate", jsonObject.getString("observationdate"));
        } catch (Exception e) {
            Toast.makeText(MainActivity.mainActivity, "Database Exception 1 "+e.toString(), Toast.LENGTH_LONG).show();
        }
        return sqlLiteDatabase.insert("observations", null, initialValues);
    }
    public static long insertObservation(String result) {
    	ContentValues initialValues = new ContentValues();
        try {
	    	JSONArray jsonArray = new JSONArray(result);
		    JSONObject jsonObject=jsonArray.getJSONObject(0);
        	initialValues.put("observationid", jsonObject.getString("observationid"));
            initialValues.put("objectname", jsonObject.getString("objectname"));
            initialValues.put("observername", jsonObject.getString("observername"));
            initialValues.put("observationdescription", jsonObject.getString("observationdescription"));
            initialValues.put("observationdate", jsonObject.getString("observationdate"));
        } catch (Exception e) {
            Toast.makeText(MainActivity.mainActivity, "Database Exception 1 "+e.toString(), Toast.LENGTH_LONG).show();
        }
        return sqlLiteDatabase.insert("observations", null, initialValues);
    }
    
    public static int getAllEntries() {
        Cursor cursor=sqlLiteDatabase.rawQuery("SELECT COUNT(observationid) FROM observations", null);
        if(cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        return cursor.getInt(0);
    }
        
    public static Cursor getObservation(String observationid) {
        return sqlLiteDatabase.rawQuery("SELECT observations.* FROM observations WHERE observationid=\""+observationid+"\"", null);
    }
     
    public static void close() {
    	databaseHelper.close();
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper  {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
     
        @Override
        public void onCreate(SQLiteDatabase db) {
        	Toast.makeText(MainActivity.mainActivity, "Database create", Toast.LENGTH_LONG).show();
        	db.execSQL(DATABASE_CREATE);
        }
     
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	Toast.makeText(MainActivity.mainActivity, "Database upgrade", Toast.LENGTH_LONG).show();
        	db.execSQL("DROP TABLE IF EXISTS observations");
            onCreate(db);
        }
    }
}
