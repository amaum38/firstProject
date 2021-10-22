package com.raywenderlich.android.omgandroid

import Endpoints
import ResponseInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookViewModel() : ViewModel(), KoinComponent {
    val books: MutableLiveData<List<Book>> by lazy {
        MutableLiveData<List<Book>>()
    }

    fun getBooks(): LiveData<List<Book>> {
        return books
    }

    fun queryBooks(searchString: String, responseInt: ResponseInterface) {
        val api: Endpoints by inject()
        val call = api.getBooks(searchString)
        call.enqueue(object : Callback<Books> {
            override fun onResponse(call: Call<Books>, response: Response<Books>) {
                if (response.isSuccessful) {
                    books.value = response.body()?.docs
                    //if (books != null && books.isNotEmpty()) {
                    //} else {
                    // }

                    responseInt.onSucess()

                } else {
                    responseInt.onFailure(response.code().toString())
                }
            }

            override fun onFailure(call: Call<Books>, t: Throwable) {
                responseInt.onFailure(t.message ?: "")
            }
        })
    }
}