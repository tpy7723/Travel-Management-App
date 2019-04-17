package com.example.pks01.bottom_navi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, Integer version) {
        super(context, name, factory, version);
        this.context = context;
    }

    /**
     * Database가 존재하지 않을 때, 딱 한번 실행된다.
     * DB를 만드는 역할을 한다.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // String 보다 StringBuffer가 Query 만들기 편하다.
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE TEST_TABLE ( ");
        sb.append(" _ID INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" TITLE TEXT, ");
        sb.append(" SNIPPET TEXT, ");
        sb.append(" LONG DOUBLE, ");
        sb.append(" LATI DOUBLE, ");
        sb.append(" IMAGE TEXT, ");
        sb.append(" CATEGORY TEXT ) ");

        // SQLite Database로 쿼리 실행
        db.execSQL(sb.toString());

        Toast.makeText(context, "Table 생성완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        Toast.makeText(context, "버전이 내려갔습니다.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Application의 버전이 올라가서
     * Table 구조가 변경되었을 때 실행된다.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(context, "버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();
    }

    public void testDB() {
        SQLiteDatabase db = getReadableDatabase();
    }

    public void addDB_Item(DB_Item DB_Item) {
        // 1. 쓸 수 있는 DB 객체를 가져온다.
        SQLiteDatabase db = getWritableDatabase();

        // 2. DB_Item Data를 Insert한다.
        // _id는 자동으로 증가하기 때문에 넣지 않습니다.
        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO TEST_TABLE ( ");
        sb.append(" TITLE, SNIPPET, LONG, LATI, IMAGE, CATEGORY ) ");
        sb.append(" VALUES ( ?, ?, ?, ?, ?, ? ) ");

        db.execSQL(sb.toString(),
                new Object[]{
                        DB_Item.getTitle(),
                        DB_Item.getSnippet(),
                        DB_Item.getLongitude(),
                        DB_Item.getLatitude(),
                        DB_Item.getImage(),
                        DB_Item.getCategory()});
        Toast.makeText(context, "Insert 완료", Toast.LENGTH_SHORT).show();
    }

    public void removeDB_Item(DB_Item DB_Item) { //DELETE FROM 테이블명 WHERE 필드명 = '조건';
        // 1. 쓸 수 있는 DB 객체를 가져온다.
        SQLiteDatabase db = getWritableDatabase();

        Integer id = DB_Item.getID(); // primary key

        StringBuffer sb = new StringBuffer();
        sb.append(" DELETE FROM TEST_TABLE WHERE _ID = ");
        sb.append(Integer.toString(id));

        db.execSQL(sb.toString());
        Toast.makeText(context, "Remove 완료", Toast.LENGTH_SHORT).show();
    }

    public void removeAllDB_Item() { //DELETE FROM 테이블명;
        // 1. 쓸 수 있는 DB 객체를 가져온다.
        SQLiteDatabase db = getWritableDatabase();

        StringBuffer sb = new StringBuffer();
        sb.append("DROP TABLE TEST_TABLE"); // 테이블 자체를 삭제
        db.execSQL(sb.toString());

        onCreate(db); // 테이블 생성

        Toast.makeText(context, "전체 삭제 후 테이블 재생성", Toast.LENGTH_SHORT).show();
    }


    public List<DB_Item> getAllDB_ItemData() {

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT _ID, TITLE, SNIPPET, LONG, LATI, IMAGE, CATEGORY FROM TEST_TABLE ");

        // 읽기 전용 DB 객체를 만든다.
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(sb.toString(), null);

        List Item = new ArrayList();
        DB_Item DB_Item = null;

        // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while (cursor.moveToNext()) {
            DB_Item = new DB_Item();
            DB_Item.setid(cursor.getInt(0));
            DB_Item.setTitle(cursor.getString(1));
            DB_Item.setSnippet(cursor.getString(2));
            DB_Item.setLongitude(cursor.getDouble(3));
            DB_Item.setLatitude(cursor.getDouble(4));
            DB_Item.setImageStr(cursor.getString(5));
            DB_Item.setCategory(cursor.getString(6));
            Item.add(DB_Item);
        }

        return Item;

    }


    public List get_ItemDatabyCategory(String category_) {

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT _ID, TITLE, SNIPPET, LONG, LATI, IMAGE, CATEGORY FROM TEST_TABLE WHERE CATEGORY='"+category_+"'");

        // 읽기 전용 DB 객체를 만든다.
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(sb.toString(), null);

        List Item = new ArrayList();
        DB_Item DB_Item;

        // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while (cursor.moveToNext()) {
            DB_Item = new DB_Item();
            DB_Item.setid(cursor.getInt(0));
            DB_Item.setTitle(cursor.getString(1));
            DB_Item.setSnippet(cursor.getString(2));
            DB_Item.setLongitude(cursor.getDouble(3));
            DB_Item.setLatitude(cursor.getDouble(4));
            DB_Item.setImageStr(cursor.getString(5));
            DB_Item.setCategory(cursor.getString(6));
            Item.add(DB_Item);
        }

        return Item;

    }


    public DB_Item get_ItemDatabyid(Integer id) {

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT _ID, TITLE, SNIPPET, LONG, LATI, IMAGE, CATEGORY FROM TEST_TABLE WHERE _ID="
                +Integer.toString(id));

        // 읽기 전용 DB 객체를 만든다.
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(sb.toString(), null);

        DB_Item DB_Item;
        DB_Item = new DB_Item();

        // moveToNext 다음에 데이터가 있으면 true 없으면 false
        while (cursor.moveToNext()) {
            DB_Item.setid(cursor.getInt(0));
            DB_Item.setTitle(cursor.getString(1));
            DB_Item.setSnippet(cursor.getString(2));
            DB_Item.setLongitude(cursor.getDouble(3));
            DB_Item.setLatitude(cursor.getDouble(4));
            DB_Item.setImageStr(cursor.getString(5));
            DB_Item.setCategory(cursor.getString(6));
        }

        return DB_Item;

    }

}