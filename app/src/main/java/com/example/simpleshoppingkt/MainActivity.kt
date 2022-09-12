package com.example.simpleshoppingkt

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var myDBHelper : DBHelper
    }

    lateinit var sqLiteDatabase:SQLiteDatabase
    private var backpressedTime : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myDBHelper = DBHelper(this)
        sqLiteDatabase = myDBHelper.writableDatabase
        myDBHelper.onCreate(sqLiteDatabase)

        //프래그먼트
        val fragmentSearch = FragmentSearch()
        val fragmentBasket = FragmentBasket()
        supportFragmentManager.beginTransaction().add(R.id.fragmentView,fragmentSearch).commit()

        //바텀네비게이션 설정, 클릭
        val bottomNavigation : BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.searchView -> {
                    supportFragmentManager.beginTransaction().remove(fragmentBasket).commit()
                    supportFragmentManager.beginTransaction().show(fragmentSearch).commit()
                }
                R.id.shoppingBasket -> {
                    supportFragmentManager.beginTransaction().hide(fragmentSearch).commit()
                    supportFragmentManager.beginTransaction().add(R.id.fragmentView,fragmentBasket).commit()
                }
            }
            true
        }
    }

    class DBHelper(context : Context) : SQLiteOpenHelper(context, "basketList", null, 1) {
        override fun onCreate(sqLiteDatabase : SQLiteDatabase?) {
            val sql = "CREATE TABLE if not exists basketList (id integer primary key autoincrement, title text, link text, image text, lprice text)"
            sqLiteDatabase!!.execSQL(sql)
        }
        override fun onUpgrade(db : SQLiteDatabase?, oldVersion : Int, newVersion : Int) {
        }
    }

    //뒤로가기 두 번 클릭
    override fun onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis()
            Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish()
        }
    }

    override fun onDestroy() {
        myDBHelper.close()
        super.onDestroy()
    }
}