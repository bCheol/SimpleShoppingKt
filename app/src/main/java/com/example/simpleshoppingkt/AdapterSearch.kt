package com.example.simpleshoppingkt

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdapterSearch : RecyclerView.Adapter<AdapterSearch.ViewHolder>(){

    val searchItem = mutableListOf<RecyclerItemSearch>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val imageView: ImageView
        val lprice: TextView
        val shopBtn: TextView
        val basketAddBtn: TextView
        var toast: Toast
        init {
             title = itemView.findViewById(R.id.title)
             imageView= itemView.findViewById(R.id.imageView)
             lprice = itemView.findViewById(R.id.lprice)
             shopBtn = itemView.findViewById(R.id.shopBtn)
             basketAddBtn = itemView.findViewById(R.id.basketAddBtn)
            toast = Toast.makeText(itemView.context, "추가됐습니다.", Toast.LENGTH_SHORT)
        }
        fun setItem(item : RecyclerItemSearch){
            title.text = item.title
            Glide.with(itemView.context)
                .load(item.image)
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_error)
                .into(imageView)
            lprice.text = item.lprice
            //자세히 클릭
            shopBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                itemView.context.startActivity(intent)
            }
            //담기 클릭
            basketAddBtn.setOnClickListener {
                val sqLiteDatabase = MainActivity.myDBHelper.writableDatabase
                val sql = "Insert into basketList (title, link, image, lprice) values ('" + item.title + "','" + item.link + "','" + item.image + "','" + item.lprice + "');"
                sqLiteDatabase.execSQL(sql)
                sqLiteDatabase.close()
                toast.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    toast.cancel()
                },500)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        val itemView : View = inflater.inflate(R.layout.item_search, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item : RecyclerItemSearch = searchItem[position]
        holder.setItem(item)
    }

    override fun getItemCount(): Int {
        return searchItem.size
    }

    fun addItem(item : RecyclerItemSearch){
        searchItem.add(item)
    }

    fun clearItem(){
        searchItem.clear()
    }
}