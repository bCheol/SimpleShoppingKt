package com.example.simpleshoppingkt

import android.view.View

interface BasketDeleteClickListener {
    fun onDeleteClick(holder: AdapterBasket.ViewHolder, view : View, position : Int)
}