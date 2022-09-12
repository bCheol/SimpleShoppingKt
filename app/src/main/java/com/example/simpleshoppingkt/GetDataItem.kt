package com.example.simpleshoppingkt

data class GetDataItem(val title : String, //검색 결과 문서 제목
                       val link : String,  //검색 결과 링크
                       val image : String, //이미지 url
                       val lprice : Int    //최저가 정보
                       ){

    fun changeTitle() : String{
        return title.replace("<b>","").replace("</b>","")
    }
    fun changeLink():String{
        val a = link.indexOf("=")
        val b = link.substring(a+1)
        return "https://msearch.shopping.naver.com/product/" + b
    }
    fun changeLprice() : String{
        val chprice : String = lprice.toString()
        return chprice
    }
}