package com.example.project_dictionary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    MyDBOpenHelper mydb;
    SQLiteDatabase mdb;
    Cursor cursor;
    CursorAdapter ca;
    ListView lv1;
    final int RQCode = 1;
    final int RQCode_Update = 2;
    ContentValues row;

    final int WORD_GROUP = 0;
    final int DELETE_GROUP = 1;
    final int WORD_ADD = Menu.FIRST;
    final int DELETE_ALL = Menu.FIRST + 1;
    final int DELETE = Menu.FIRST + 2;
    final int UPDATE = Menu.FIRST + 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv1 = findViewById(R.id.lv1);

        mydb = new MyDBOpenHelper(this, "mydb.db", null, 1);
        mdb = mydb.getWritableDatabase();
        cursor = mdb.rawQuery("select * from WordBook;", null);
        String[] strCol = {"word", "meaning"};
        ca = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, cursor, strCol,
                new int[]{android.R.id.text1, android.R.id.text2});
        lv1.setAdapter(ca);
        registerForContextMenu(lv1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int id = data.getIntExtra("_id", -1);
        String word = data.getStringExtra("word");
        String meaning = data.getStringExtra("meaning");

        row = new ContentValues() ;
        row.put("word", word) ;
        row.put("meaning", meaning) ;

        if (requestCode == RQCode && resultCode == RESULT_OK) {

            mdb.insert("WordBook", null, row) ;

        }
        else if (requestCode == RQCode_Update && resultCode == RESULT_OK){
            mdb.update("WordBook", row, "_id="+id,null);

        }
        cursor.requery();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v == lv1){
            menu.add(Menu.NONE, DELETE, Menu.NONE, "delete");
            menu.add(Menu.NONE, UPDATE, Menu.NONE, "update");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Intent i = new Intent(this, AddActivity.class);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        cursor.moveToPosition(position);
        int id = cursor.getInt(0);
        String word = cursor.getString(1);
        String meaning = cursor.getString(2);

        i.putExtra("word", word);
        i.putExtra("meaning", meaning);
        i.putExtra("_id", id );
        switch(item.getItemId())
        {
            case UPDATE:
                startActivityForResult( i, RQCode_Update);
                break;
            case DELETE:
                mdb.execSQL("DELETE FROM WordBook WHERE _id = "+id+";");
                cursor.requery();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu WordGroup = menu.addSubMenu(WORD_GROUP, Menu.NONE, Menu.NONE, "Word");
        SubMenu DeleteGroup =  menu.addSubMenu(DELETE_GROUP, Menu.NONE, Menu.NONE, "Delete");

        WordGroup.add(WORD_GROUP, WORD_ADD, 0, "Add");
        DeleteGroup.add(DELETE_GROUP, DELETE_ALL, 0, "All");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == DELETE_ALL){
            mdb.execSQL("DELETE FROM WordBook");
            cursor.requery();
        }else if (item.getItemId() == WORD_ADD){
            Intent i = new Intent(MainActivity.this, AddActivity.class);
            startActivityForResult(i, RQCode);
        }

        return super.onOptionsItemSelected(item);
    }
}

class MyDBOpenHelper extends SQLiteOpenHelper {
    public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // create table
        db.execSQL("CREATE TABLE WordBook (_id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT, meaning TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}