/*
 * Copyright (c) 2015 github.com/4-k
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.fourk.app.sos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactRepo {
    private MyDBHelper dbHelper;
    private Context _context;

    public ContactRepo(Context context) {
        try {
            _context = context;
            dbHelper = new MyDBHelper(context);
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public int insertUser(User user) {
        try {
            SQLiteDatabase _db = dbHelper.getWritableDatabase();
            _db.delete(User.TABLE, null, null);
            _db.close(); // Closing database connection

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(User.KEY_id, user.email_id);
            values.put(User.KEY_pass, user.email_pass);

            // Inserting Row
            long contact_Id = db.insert(User.TABLE, null, values);
            db.close(); // Closing database connection
            return (int) contact_Id;
        } catch (Exception err) {
            Toast.makeText(_context, err.getMessage(), Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    public int insert(Contact contact) {
        try {
            //Open connection to write data
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Contact.KEY_phone, contact.contact_phone);
            values.put(Contact.KEY_name, contact.contact_name);

            // Inserting Row
            long contact_Id = db.insert(Contact.TABLE, null, values);
            db.close(); // Closing database connection
            return (int) contact_Id;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    public int insertEmail(Email email) {
        try {
            //Open connection to write data
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Email.KEY_id, email.email_id);
            values.put(Email.KEY_name, email.email_name);

            // Inserting Row
            long email_Id = db.insert(Email.TABLE, null, values);
            db.close(); // Closing database connection
            return (int) email_Id;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    public String getLastLocation() {
        try {
            String latlng = "";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selectQuery = "SELECT  " +
                    LocationData.KEY_lat + "," +
                    LocationData.KEY_lon +
                    " FROM " + LocationData.TABLE;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                latlng = cursor.getString(cursor.getColumnIndex(LocationData.KEY_lat)) + "|" +
                        cursor.getString(cursor.getColumnIndex(LocationData.KEY_lon));
            }
            cursor.close();
            db.close();
            return latlng;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return "";
        }
    }

    public String getCredentialE() {
        try {
            String em = "";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selectQuery = "SELECT  " +
                    User.KEY_id + "," +
                    User.KEY_pass +
                    " FROM " + User.TABLE;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                em = cursor.getString(cursor.getColumnIndex(User.KEY_id));
            }
            cursor.close();
            db.close();
            return em;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return ex.getMessage();
        }
    }

    public String getCredentialP() {
        try {
            String em = "";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selectQuery = "SELECT  " +
                    User.KEY_id + "," +
                    User.KEY_pass +
                    " FROM " + User.TABLE;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                em = cursor.getString(cursor.getColumnIndex(User.KEY_pass));
            }
            cursor.close();
            db.close();
            return em;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return "";
        }
    }

    public List getEmContact() {
        try {
            String em = "";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selectQuery = "SELECT  " +
                    Email.KEY_id + "," +
                    Email.KEY_name +
                    " FROM " + Email.TABLE;
            Cursor cursor = db.rawQuery(selectQuery, null);
            List eem = new ArrayList();
            if (cursor.moveToFirst()) {
                do {
                    eem.add(cursor.getString(cursor.getColumnIndex(Email.KEY_id)));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return eem;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public int LastLocationUpdate(LocationData location) {
        try {
            SQLiteDatabase _db = dbHelper.getWritableDatabase();
            _db.delete(LocationData.TABLE, null, null);
            _db.close(); // Closing database connection

            //Open connection to write data
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(LocationData.KEY_lat, location.lat);
            values.put(LocationData.KEY_lon, location.lon);

            // Inserting Row
            long location_Id = db.insert(LocationData.TABLE, null, values);
            db.close(); // Closing database connection
            return (int) location_Id;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    public void delete(String contact_Name) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // It's a good practice to use parameter ?, instead of concatenate string
            db.delete(Contact.TABLE, Contact.KEY_name + "= ?", new String[]{String.valueOf(contact_Name)});
            db.close(); // Closing database connection
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void update(Contact contact) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Contact.KEY_phone, contact.contact_phone);
            // It's a good practice to use parameter ?, instead of concatenate string
            db.update(Contact.TABLE, values, Contact.KEY_name + "= ?", new String[]{String.valueOf(contact.contact_name)});
            db.close(); // Closing database connection
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<HashMap<String, String>> getContactList() {
        //Open connection to read only
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selectQuery = "SELECT  " +
                    Contact.KEY_name + "," +
                    Contact.KEY_phone +
                    " FROM " + Contact.TABLE;

            ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list

            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> contact = new HashMap<String, String>();
                    contact.put("name", cursor.getString(cursor.getColumnIndex(Contact.KEY_name)));
                    contact.put("phone", cursor.getString(cursor.getColumnIndex(Contact.KEY_phone)));
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return contactList;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

    }

    public ArrayList<HashMap<String, String>> getEmailList() {
        //Open connection to read only
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selectQuery = "SELECT  " +
                    Email.KEY_name + "," +
                    Email.KEY_id +
                    " FROM " + Email.TABLE;

            ArrayList<HashMap<String, String>> emailList = new ArrayList<HashMap<String, String>>();

            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list

            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> email = new HashMap<String, String>();
                    email.put("name", cursor.getString(cursor.getColumnIndex(Email.KEY_name)));
                    email.put("id", cursor.getString(cursor.getColumnIndex(Email.KEY_id)));
                    emailList.add(email);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return emailList;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

    }

    public ArrayList<HashMap<String, String>> getUserList() {
        //Open connection to read only
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selectQuery = "SELECT  " +
                    User.KEY_id + "," +
                    User.KEY_pass +
                    " FROM " + User.TABLE;

            ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();

            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list

            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> user = new HashMap<String, String>();
                    user.put("id", cursor.getString(cursor.getColumnIndex(User.KEY_id)));
                    user.put("pass", cursor.getString(cursor.getColumnIndex(User.KEY_pass)));
                    userList.add(user);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return userList;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public Contact getContactByNo(String Id) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String selectQuery = "SELECT  " +
                    Contact.KEY_name + "," +
                    Contact.KEY_phone +
                    " FROM " + Contact.TABLE
                    + " WHERE " +
                    Contact.KEY_phone + "=?";// It's a good practice to use parameter ?, instead of concatenate string

            int iCount = 0;
            Contact contact = new Contact();

            Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});

            if (cursor.moveToFirst()) {
                do {
                    contact.contact_name = cursor.getString(cursor.getColumnIndex(Contact.KEY_name));
                    contact.contact_phone = cursor.getString(cursor.getColumnIndex(Contact.KEY_phone));

                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return contact;
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
