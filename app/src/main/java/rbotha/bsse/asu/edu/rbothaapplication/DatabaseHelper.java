package rbotha.bsse.asu.edu.rbothaapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * Copyright 2018 Ruan Botha,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Purpose: Assignment for week 5 demonstrating multiple views, database
 * integration (SQLite), lists, and some maths.
 *
 * Ser423 Mobile Applications
 * see http://pooh.poly.asu.edu/Mobile
 * @author Ruan Botha rbotha@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2018
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "place_library.db";
    public static final String TABLE_NAME = "pl_places";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "DESCRIPTION";
    public static final String COL_3 = "CATEGORY";
    public static final String COL_4 = "ADDRESS_TITLE";
    public static final String COL_5 = "ADDRESS_STREET";
    public static final String COL_6 = "ELEVATION";
    public static final String COL_7 = "LATITUDE";
    public static final String COL_8 = "LONGITUDE";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + "(NAME TEXT PRIMARY KEY, DESCRIPTION TEXT, CATEGORY TEXT, " +
                "ADDRESS_TITLE TEXT, ADDRESS_STREET TEXT, ELEVATION REAL, LATITUDE REAL, LONGITUDE REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String name, String description, String category,
                              String address_title, String address_street, double elevation,
                              double latitude, double longitude ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, description);
        contentValues.put(COL_3, category);
        contentValues.put(COL_4, address_title);
        contentValues.put(COL_5, address_street);
        contentValues.put(COL_6, elevation);
        contentValues.put(COL_7, latitude);
        contentValues.put(COL_8, longitude);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public int updateData(String oldName, String name, String description, String category,
                              String address_title, String address_street, double elevation,
                              double latitude, double longitude ){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, name);
        contentValues.put(COL_2, description);
        contentValues.put(COL_3, category);
        contentValues.put(COL_4, address_title);
        contentValues.put(COL_5, address_street);
        contentValues.put(COL_6, elevation);
        contentValues.put(COL_7, latitude);
        contentValues.put(COL_8, longitude);

        return db.update(TABLE_NAME, contentValues, "NAME = ?", new String[] {String.valueOf(oldName)});
    }

    public int deleteData (String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"NAME = ?", new String[] {String.valueOf(oldName)});
    }
}
