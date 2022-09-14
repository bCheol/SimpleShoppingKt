package com.example.simpleshoppingkt

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdapterBasket : RecyclerView.Adapter<AdapterBasket.ViewHolder>(), BasketDeleteClickListener{

    var basketItem = mutableListOf<ColumnBasket>()
    lateinit var listener : BasketDeleteClickListener

    class ViewHolder(itemView: View, listener: BasketDeleteClickListener) : RecyclerView.ViewHolder(itemView) {

        val title: TextView
        val imageView: ImageView
        val lprice: TextView
        val shopBtn: Button
        val basketDeleteBtn: Button

        init {
            title = itemView.findViewById(R.id.title)
            imageView = itemView.findViewById(R.id.imageView)
            lprice = itemView.findViewById(R.id.lprice)
            shopBtn = itemView.findViewById(R.id.shopBtn)
            basketDeleteBtn = itemView.findViewById(R.id.basketDeleteBtn)
            //삭제 클릭
            basketDeleteBtn.setOnClickListener {
                val position = adapterPosition
                listener.onDeleteClick(ViewHolder(itemView, listener), it, position)
            }
        }

        fun setItem(item: ColumnBasket) {
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(parent.context)
        val itemView : View = inflater.inflate(R.layout.item_basket, parent, false)
        return ViewHolder(itemView, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item : ColumnBasket = basketItem[position]
        holder.setItem(item)
    }

    override fun getItemCount(): Int {
        return basketItem.size
    }

    fun setOnDeleteClick(listener: BasketDeleteClickListener){
        this.listener = listener
    }

    override fun onDeleteClick(holder:ViewHolder, view: View, position: Int) {
        listener.onDeleteClick(holder, view, position)
    }

    fun addItem(item : ColumnBasket){
        basketItem.add(item)
    }
}