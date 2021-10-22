/*
 * Copyright (c) 2017 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.android.omgandroid

import Endpoints
import ResponseInterface
import android.app.AlertDialog
import android.content.Context
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import android.content.DialogInterface
import android.content.Intent
import androidx.core.view.MenuItemCompat
import androidx.appcompat.widget.ShareActionProvider
import android.text.InputType
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.android.omgandroid.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val MainActivityModule = module {
    single { (listener: BookAdapterRec.OnItemClickListener) -> BookAdapterRec(androidContext(), listener)  }
    single { androidContext().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE) }
    single<Bar> {
        object : Bar {
            override fun test(): Int {
                return 10
            }
        }
    }
}

interface Bar {
    fun test() : Int
}

class Foo(val bar: Bar) {
    fun add() {
        bar.test() + bar.test()
    }
}

class MainActivity : AppCompatActivity(), BookAdapterRec.OnItemClickListener {
    private var shareActionProvider: ShareActionProvider? = null
    private val sharedPreferences: SharedPreferences by inject()
    private lateinit var binding: ActivityMainBinding

    private val api: Endpoints by inject()
    private val adapter: BookAdapterRec by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val model: BookViewModel by viewModels()
        model.getBooks().observe(this, { books ->
            adapter.updateData(books)
        })

        binding.mainButton.setOnClickListener {
            binding.progressBar.isIndeterminate = true
            model.queryBooks(binding.mainEdittext.text.toString(), object : ResponseInterface {
                override fun onSucess() {
                    binding.emptyView.visibility = View.GONE
                    binding.mainListview.visibility = View.VISIBLE
                    binding.progressBar.isIndeterminate = false
                }

                override fun onFailure(code: String) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.mainListview.visibility = View.GONE
                    binding.progressBar.isIndeterminate = false
                    Toast.makeText(this@MainActivity, code, Toast.LENGTH_SHORT).show()
                }
            })
        }

        // 7. Greet the user, or ask for their name if new
        displayWelcome()

        binding.mainListview.adapter = adapter
        binding.mainListview.layoutManager = LinearLayoutManager(this)
    }

    private fun displayWelcome() {
        // Read the user's name,
        // or an empty string if nothing found
        val name = sharedPreferences.getString("name", "")
        if (name?.length ?: 0 > 0) {
            Toast.makeText(this, "Welcome back, $name!", Toast.LENGTH_LONG).show()
        } else {

            // otherwise, show a dialog to ask for their name
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Hello!")
            alert.setMessage("What is your name?")

            // Create EditText for entry
            val input = EditText(this)
            input.setLines(1)
            input.maxLines = 1
            input.inputType = InputType.TYPE_CLASS_TEXT
            alert.setView(input)

            // Make an "OK" button to save the name
            alert.setPositiveButton(
                "OK"
            ) { _: DialogInterface?, _: Int ->

                // Grab the EditText's input
                val inputName = input.text.toString()

                // Put it into memory (don't forget to commit!)
                sharedPreferences.edit()
                    .putString("name", inputName)
                    .apply()

                // Welcome the new user
                Toast.makeText(
                    applicationContext, "Welcome, $inputName!",
                    Toast.LENGTH_LONG
                ).show()
            }

            // Make a "Cancel" button
            // that simply dismisses the alert
            alert.setNegativeButton("Cancel") { _: DialogInterface?, _: Int -> }
            alert.show()
        }
    }

    override fun onItemClicked(book: Book) {
        val coverID = book.imageId
        val detailIntent = Intent(this, DetailActivity::class.java)
        detailIntent.putExtra("coverID", coverID)
        startActivity(detailIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val shareItem = menu.findItem(R.id.menu_item_share)
        if (shareItem != null) {
            shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        }

        setShareIntent()
        return true
    }

    private fun setShareIntent() {
        // create an Intent with the contents of the TextView
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development")
        shareIntent.putExtra(Intent.EXTRA_TEXT, binding.mainTextview.text)

        // Make sure the provider knows
        // it should work with that Intent
        shareActionProvider!!.setShareIntent(shareIntent)
    }

    private fun queryBooks2(searchString: String) {
        val call = api.getBooks(searchString)
        binding.progressBar.isIndeterminate = true
        call.enqueue(object : Callback<Books> {
            override fun onResponse(call: Call<Books>, response: Response<Books>) {
                binding.progressBar.isIndeterminate = false
                if (response.isSuccessful) {
                    val books = response.body()?.docs
                    if (books != null && books.isNotEmpty()) {
                        binding.emptyView.visibility = View.GONE
                        binding.mainListview.visibility = View.VISIBLE
                        adapter.updateData(books)
                    } else {
                        binding.emptyView.visibility = View.VISIBLE
                        binding.mainListview.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@MainActivity, "${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Books>, t: Throwable) {
                binding.progressBar.isIndeterminate = false
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}