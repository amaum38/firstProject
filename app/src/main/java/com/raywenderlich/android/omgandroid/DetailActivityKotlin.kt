
package com.raywenderlich.android.omgandroid

import android.content.Intent
import android.os.Bundle
import androidx.core.view.MenuItemCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.widget.ImageView
import androidx.appcompat.widget.ShareActionProvider
import com.squareup.picasso.Picasso

class DetailActivityKotlin : AppCompatActivity() {

    private val imageUrlBase = "http://covers.openlibrary.org/b/id/"
    private var imageURL = ""
    private var shareActionProvider: ShareActionProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        val imageView = findViewById<ImageView>(R.id.img_cover)

        this.intent.extras.getString("coverID")

        intent.getStringExtra("CoverID")
        val coverId = this.intent.extras?.getString("coverID")

        val len = coverId?.length ?: 0

        if (len > 0) {
            imageURL = imageUrlBase + coverId + "-L.jpg"
            Picasso.with(this).load(imageURL).placeholder(R.drawable.img_books_loading).into(imageView)
        }
    }

    private fun setShareIntent() {

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Book Recommendation!")
        shareIntent.putExtra(Intent.EXTRA_TEXT, imageURL)

        shareActionProvider?.setShareIntent(shareIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main, menu)

        val shareItem = menu.findItem(R.id.menu_item_share)

        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider

        setShareIntent()

        return true
    }
}
