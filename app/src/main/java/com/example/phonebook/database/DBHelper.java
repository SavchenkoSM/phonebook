package com.example.phonebook.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.phonebook.models.PersonInfo;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "android.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "PhoneBook";

    // Поля таблицы для хранения ФИО и Номера телефона (id формируется автоматически)
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LastName = "LastName";
    public static final String COLUMN_Name = "Name";
    public static final String COLUMN_MiddleName = "MiddleName";
    public static final String COLUMN_Phone = "Phone";

    private SQLiteDatabase db;
    private ContentValues values;

    private int lastNameColumnIndex, nameColumnIndex, middleNameColumnIndex,
            phoneColumnIndex, personIdColumnIndex, personId;
    private String lastName, name, middleName, phone;

    // Запрос для создания БД
    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_LastName
            + " text not null, " + COLUMN_Name + " text not null,"
            + COLUMN_MiddleName + " text not null," + COLUMN_Phone + " text not null" + ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PhoneBook");
        onCreate(db);
    }

    /**
     * Создание нового контакта
     */
    public void createNewRow(String LastName, String Name,
                             String MiddleName, String Phone) {
        db = this.getWritableDatabase();
        values = createContentValues(LastName, Name, MiddleName, Phone);
        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }

    /**
     * Обновление контакта
     */
    public void updateContact(int id, String lastName, String name,
                              String middleName, String phone) {
        db = this.getWritableDatabase();
        values = createContentValues(lastName, name, middleName, phone);

        db.update(DATABASE_TABLE, values, COLUMN_ID + " = " + id, null);
        db.close();
    }

    /**
     * Удаление контакта
     */
    public void deleteContact(long rowId) {
        db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, COLUMN_ID + "=" + rowId, null);
        db.close();
    }

    /**
     * Описание структуры данных
     */
    private ContentValues createContentValues(String LastName, String Name,
                                              String MiddleName, String Phone) {
        values = new ContentValues();

        values.put(COLUMN_LastName, LastName);
        values.put(COLUMN_Name, Name);
        values.put(COLUMN_MiddleName, MiddleName);
        values.put(COLUMN_Phone, Phone);
        return values;
    }

    /**
     * Получение всех данных об имеющихся контактах
     */
    public List<PersonInfo> getAllData() {
        db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DATABASE_TABLE;
        List<PersonInfo> personInfoList = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            lastNameColumnIndex = cursor.getColumnIndex(COLUMN_LastName);
            nameColumnIndex = cursor.getColumnIndex(COLUMN_Name);
            middleNameColumnIndex = cursor.getColumnIndex(COLUMN_MiddleName);
            phoneColumnIndex = cursor.getColumnIndex(COLUMN_Phone);
            personIdColumnIndex = cursor.getColumnIndex(COLUMN_ID);

            lastName = cursor.getString(lastNameColumnIndex);
            name = cursor.getString(nameColumnIndex);
            middleName = cursor.getString(middleNameColumnIndex);
            phone = cursor.getString(phoneColumnIndex);
            personId = cursor.getInt(personIdColumnIndex);

            PersonInfo personInfo = new PersonInfo(personId, lastName, name, middleName, phone);
            personInfoList.add(personInfo);
        }
        db.close();
        return personInfoList;
    }
}