package com.example.android.dizajnzaspomenar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;
import android.util.Log;

import static android.R.attr.id;
import static com.example.android.dizajnzaspomenar.MainActivity.BROJ_PITANJA;
import static com.example.android.dizajnzaspomenar.MainActivity.TitleList;
import static com.example.android.dizajnzaspomenar.R.drawable.c;

/**
 * Created by Tena on 5/7/2017.
 */

public class DBAdapter {
    //tablica pitanja
    static final String KEY_ROWID = "_id";
    static final String KEY_QUEST = "question";
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "DBSpomenar";
    static final String DATABASE_TABLE = "pitanja";
    static final int DATABASE_VERSION = 3;

    static final String DATABASE_CREATE =
            "create table pitanja (_id integer primary key autoincrement, "
                    + "question text not null);";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
                db.execSQL(DATABASE_CREATE2);
                db.execSQL(DATABASE_CREATE3);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a question into the database---
    public long insertQuestion(String q)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QUEST, q);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---insert a new (user defined) question into the database---
    public long insertNewQuestion(String q)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QUEST, q);
        long insert =  db.insert(DATABASE_TABLE, null, initialValues); // ovo vraca last inserted id
        TitleList.add(q);
        ++BROJ_PITANJA;
        if (insert != -1)
        {
            Cursor c = getAllContacts();
            if (c.moveToFirst()) {
                do {
                    insertAnswer(Integer.parseInt(c.getString(0)), c.getString(1), (int)insert, "---" );
                } while (c.moveToNext());
            }
            return 1;
        }
        else { return 0; }
    }

    //---deletes a particular question--- //to vjerojatno necemo koristiti
    public boolean deleteQuestion(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---deletes all questions---
    public void deleteQuestionsTable() { db.execSQL("delete from " + DATABASE_TABLE); }

    //---retrieves all the questions---
    public Cursor getAllQuestions()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_QUEST}, null, null, null, null, null);
    }

    //---retrieves a particular question---
    public Cursor getQuestion(long rowId) throws SQLException
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]
                {KEY_ROWID, KEY_QUEST}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a question--- //isto vjv ne treba
    public boolean updateQuestion(long rowId, String q)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_QUEST, q);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }


    //tablica odgovora
    static final String KEY_ID_USER = "id_user";
    static final String KEY_USER = "username";
    static final String KEY_NMB_QUEST = "questionNmb";
    static final String KEY_ANSW = "answer";

    static final String DATABASE_TABLE2 = "odgovori";

    static final String DATABASE_CREATE2 =
            "create table odgovori (id_user integer not null, " + "username text not null, " +
                    "questionNmb integer not null, " + "answer text not null);";

    //---insert a answer into the database---
    public long insertAnswer(Integer id, String user, Integer nmb ,String ans)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ID_USER, id);
        initialValues.put(KEY_USER, user);
        initialValues.put(KEY_NMB_QUEST, nmb);
        initialValues.put(KEY_ANSW, ans);
        return db.insert(DATABASE_TABLE2, null, initialValues);
    }

    //---retrieves all the answer to specific question
    public Cursor getAnswers(Integer nm_quest ) //neznam jel radi
    {
        return db.query(DATABASE_TABLE2, new String[]{KEY_ID_USER, KEY_USER, KEY_NMB_QUEST, KEY_ANSW}, KEY_NMB_QUEST + " = " + nm_quest, null, null, null, KEY_ID_USER+" ASC");
    }

    public Cursor getAllAnswers( )
    {
        return db.query(DATABASE_TABLE2, new String[]{KEY_ID_USER, KEY_USER, KEY_NMB_QUEST, KEY_ANSW}, null, null, null, null, KEY_ID_USER+" ASC");
    }

    public int notAnswered(int user_id, int quest_nmb )
    {
        String neodgovoreno = "---";

        int i = db.query(DATABASE_TABLE2, new String[]{KEY_ID_USER, KEY_USER, KEY_NMB_QUEST, KEY_ANSW},
                KEY_ID_USER + "=? AND " +
                        KEY_NMB_QUEST + "=? AND " +
                        KEY_ANSW + "=?",
                new String[]{String.valueOf(user_id), String.valueOf(quest_nmb),neodgovoreno},
                null, null, null, null).getCount();
        return i;
    }

    public void deleteAnswersTable(){ db.execSQL("delete from " + DATABASE_TABLE2); }

    public int updateAnswer(Integer id_usera, Integer id_pitanja ,String odgovor)
    {
        ContentValues cv = new ContentValues();
        //cv.put(KEY_ID_USER, id_usera);
        //cv.put(KEY_USER, user);
        //cv.put(KEY_NMB_QUEST, id_pitanja);
        cv.put(KEY_ANSW, odgovor);

        //MainActivity.setAdapter(id_pitanja-1);
        return db.update(DATABASE_TABLE2, cv, KEY_ID_USER + "=" + id_usera +
                        " AND " + KEY_NMB_QUEST + "=" + id_pitanja, null);
        // vraca broj promjenjenih redaka
    }

    //tablica korisnika
    static final String KEY_NAME = "name";
    static final String KEY_EMAIL = "email";
    static final String KEY_PASS = "password";
    static final String KEY_ADMIN = "admin";

    static final String DATABASE_TABLE3 = "korisnici";

    static final String DATABASE_CREATE3 =
            "create table korisnici (_id integer primary key autoincrement, "
                    + "name text not null, email text not null, password text not null, admin integer not null);";

    //---insert a contact into the database---
    public long insertContact(String name, String email, String password, int admin)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_PASS, password);
        initialValues.put(KEY_ADMIN, admin);

        long user_id =  db.insert(DATABASE_TABLE3, null, initialValues);
        long prazni_odgovori = 0;
        Cursor c = getAllQuestions();
        if (c.moveToFirst()) {
            do {            //user_id, username, question_id, odgovor
                prazni_odgovori =
                        insertAnswer((int)user_id, name, Integer.parseInt(c.getString(0)), "---" );
            } while (c.moveToNext());
        }
        return user_id * prazni_odgovori;
    }

    //---deletes a particular contact---
    public boolean deleteContact(long rowId)
    {
        return db.delete(DATABASE_TABLE3, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the contacts---
    public Cursor getAllContacts()
    {
        return db.query(DATABASE_TABLE3, new String[]{KEY_ROWID, KEY_NAME,
                KEY_EMAIL, KEY_PASS, KEY_ADMIN}, null, null, null, null, null);
    }

    //---retrieves a particular contact---
    public Cursor getContact(long rowId) throws SQLException
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE3, new String[] {KEY_ROWID, KEY_NAME,
                KEY_EMAIL, KEY_PASS, KEY_ADMIN}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean isAdmin(int user_id )
    {
        /*int i = db.query(true, DATABASE_TABLE3, new String[] {KEY_ROWID, KEY_NAME,
                KEY_EMAIL, KEY_PASS, KEY_ADMIN}, KEY_ROWID + "=" + user_id + " AND " + KEY_NAME
                + "=" + username + " AND " + KEY_ADMIN + "=" + 1, null, null, null, null, null).getCount();*/
        int i = db.query(true, DATABASE_TABLE3, new String[] {KEY_ROWID, KEY_NAME,
                KEY_EMAIL, KEY_PASS, KEY_ADMIN}, KEY_ROWID + "=" + user_id + " AND " + KEY_ADMIN + "=" + 1, null, null, null, null, null).getCount();
        if( i > 0 )
            return true;
        return false;
    }

    public Cursor Admin()
    {
        return db.query(true, DATABASE_TABLE3, new String[] {KEY_ROWID, KEY_NAME,
                KEY_EMAIL, KEY_PASS, KEY_ADMIN}, KEY_ADMIN + "=" + 1, null, null, null, null, null);
    }

    //---updates a contact--- //ne koristimo
    public boolean updateContact(long rowId, String name, String email, String password, int admin)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_EMAIL, email);
        args.put(KEY_PASS, password);
        args.put(KEY_ADMIN, admin);
        return db.update(DATABASE_TABLE3, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public void deleteUsersTable(){ db.execSQL("delete from " + DATABASE_TABLE3); }
}
