package com.example.simpleshoppingkt

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FragmentBasket : Fragment() {

    lateinit var sqLiteDatabase: SQLiteDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {

        val root = inflater.inflate(R.layout.fragment_basket, container, false)

        //리사이클러뷰 생성
        val recyclerView : RecyclerView = root.findViewById(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        val adapterBasket = AdapterBasket()

        //DB 읽은 후 리사이클러뷰 나타내기
        sqLiteDatabase = MainActivity.myDBHelper.readableDatabase
        val cursor = sqLiteDatabase.rawQuery("Select * from basketList", null)
        for(i in 0 until cursor.count){
            cursor.moveToNext()
            val id = cursor.getInt(0)
            val title = cursor.getString(1)
            val link = cursor.getString(2)
            val image = cursor.getString(3)
            val lprice = cursor.getString(4)
            adapterBasket.addItem(ColumnBasket(id,title, link, image, lprice))
        }
        cursor.close()
        recyclerView.adapter = adapterBasket

        //토스트 설정
        val toast = Toast.makeText(requireActivity(), "삭제됐습니다.", Toast.LENGTH_SHORT)
        val toast2 = Toast.makeText(requireActivity(), "다시 클릭 해주세요.", Toast.LENGTH_SHORT)

        var positionB = 0

        //장바구니 삭제
        adapterBasket.setOnDeleteClick(object : BasketDeleteClickListener{
            override fun onDeleteClick(holder: AdapterBasket.ViewHolder, view: View, position: Int) {
                if(position != RecyclerView.NO_POSITION) {
                    positionB = position
                    sqLiteDatabase = MainActivity.myDBHelper.writableDatabase
                    val sql =
                        "delete from basketList where id = " + adapterBasket.basketItem[position].id
                    sqLiteDatabase.execSQL(sql)
                    adapterBasket.basketItem.removeAt(position)
                    adapterBasket.notifyItemRemoved(position)
                    toast.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        toast.cancel()
                    }, 500)
                }else{
                    recyclerView.removeAllViews()
                    recyclerView.adapter=adapterBasket
                    recyclerView.scrollToPosition(positionB-1)
                    toast2.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        toast2.cancel()
                    }, 800)
                }
            }
        })
        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        sqLiteDatabase.close()
    }
}