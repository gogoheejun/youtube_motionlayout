package com.hjhj.nothingbookapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjhj.nothingbookapi.adapter.BookAdapter
import com.hjhj.nothingbookapi.api.BookService
import com.hjhj.nothingbookapi.databinding.ActivityMainBinding
import com.hjhj.nothingbookapi.model.BestSellerDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var bookService: BookService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        //어댑터연결
        initBookRecyclerView()

        //
        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object:Callback<BestSellerDTO>{
                override fun onResponse(call: Call<BestSellerDTO>, response: Response<BestSellerDTO>
                ) {
                    if(response.isSuccessful.not()){
                        return
                    }
                    response.body()?.let{
                        it.books.forEach{book ->
                            Log.d("MainActivity",book.toString())
                        }
                        adapter.submitList(it.books)
                    }
                }

                override fun onFailure(call: Call<BestSellerDTO>, t: Throwable) {
                    Log.d("MainActivity", t.toString())
                }
            })
    }

    private fun initBookRecyclerView(){
        adapter = BookAdapter()

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }

}