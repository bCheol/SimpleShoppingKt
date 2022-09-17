package com.example.simpleshoppingkt

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentSearch : Fragment() {

    lateinit var recyclerView : RecyclerView
    lateinit var adapterSearch : AdapterSearch
    var spinnerCheck = true
    lateinit var toast: Toast
    lateinit var toast2: Toast

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {

        val root = inflater.inflate(R.layout.fragment_search, container, false)

        //editText 설정
        val editText : EditText = root.findViewById(R.id.editText)
        editText.imeOptions=EditorInfo.IME_ACTION_SEARCH
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //스피너 생성
        val spinner : Spinner = root.findViewById(R.id.spinner)
        spinner.adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.spinnerItem, android.R.layout.simple_spinner_dropdown_item)

        //리사이클러뷰 생성
        recyclerView = root.findViewById(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        adapterSearch = AdapterSearch()

        //레트로핏 생성
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitApi : RetrofitAPI = retrofit.create(RetrofitAPI::class.java)

        //네이버에서 발급 받은 id, pwd
        val id = "zaOWIdW8nHmrEsOkNRH2"
        val pwd = "XZymtAbD_W"

        //토스트 설정
        toast = Toast.makeText(requireActivity(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT)
        toast2 = Toast.makeText(requireActivity(), "찾는 상품이 없습니다.", Toast.LENGTH_SHORT)

        //editText 검색 클릭
        editText.setOnKeyListener { view, i, keyEvent ->
            if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP){

                //검색 클릭하면 키보드 내리기
                inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)

                //스피너 값 선택
                lateinit var sort :String
                if(editText.text.toString().isNotEmpty()){
                    if(spinner.selectedItem.toString() == "유사도순"){
                        sort = "sim"
                    } else if(spinner.selectedItem.toString() == "날짜순"){
                        sort = "date"
                    } else if(spinner.selectedItem.toString() == "가격낮은순"){
                        sort = "asc"
                    } else if(spinner.selectedItem.toString() == "가격높은순"){
                        sort = "dsc"
                    }

                    //상품명과 스피너 값으로 call 생성 후 가져오기
                    val call : Call<GetData> = retrofitApi.getData(editText.text.toString(), 100, sort, id, pwd)
                    getCall(call)

                } else {
                    runToast(toast)
                }
            }
            return@setOnKeyListener false
        }

        //스피너 클릭 시
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(spinnerCheck){
                    spinnerCheck = false
                } else {
                      if(editText.text.isNotEmpty()){
                          val call : Call<GetData>
                          when(p2){
                              0 -> {
                                  call  = retrofitApi.getData(editText.text.toString(), 100, "sim", id, pwd)
                                  getCall(call)
                              }
                              1 -> {
                                  call  = retrofitApi.getData(editText.text.toString(), 100, "date", id, pwd)
                                  getCall(call)
                              }
                              2 -> {
                                  call  = retrofitApi.getData(editText.text.toString(), 100, "asc", id, pwd)
                                  getCall(call)
                              }
                              3 -> {
                                  call  = retrofitApi.getData(editText.text.toString(), 100, "dsc", id, pwd)
                                  getCall(call)
                              }
                          }
                      } else {
                          runToast(toast)
                      }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        //플로팅 버튼 클릭
        val fab: View = root.findViewById(R.id.floatingButton)
        fab.setOnClickListener { view ->
            recyclerView.scrollToPosition(0)
        }
        return root
    }

    //call 값 가져오기
    fun getCall(call : Call<GetData>){
        adapterSearch.clearItem()
        call.enqueue(object : Callback<GetData>{
            override fun onResponse(call: Call<GetData>, response: Response<GetData>) {
                if(response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        if(data.total != 0) {
                            for (i in 0 until data.items.size) {
                                val title: String = data.items[i].changeTitle()
                                val link: String = data.items[i].changeLink()
                                val image: String = data.items[i].image
                                val lprice: String = data.items[i].changeLprice()
                                adapterSearch.addItem(RecyclerItemSearch(title, link, image, lprice))
                            }
                            recyclerView.adapter = adapterSearch
                            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                    super.onScrolled(recyclerView, dx, dy)
                                    val manager = recyclerView.layoutManager as LinearLayoutManager
                                    val lastPosition = manager.findLastCompletelyVisibleItemPosition()
                                    val itemTotalCount = adapterSearch.itemCount - 1
                                    if(lastPosition == itemTotalCount){
                                        Toast.makeText(requireActivity(),"마지막 입니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                        } else{
                            runToast(toast2)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<GetData>, t: Throwable) {
                Toast.makeText(requireContext(), t.toString(),Toast.LENGTH_SHORT).show()
            }
        })
    }

    //토스트 실행
    fun runToast(toast: Toast){
        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        },1000)
    }
}