package com.raywenderlich.android.omgandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class BookAdapter(private val context: Context, private val inflater: LayoutInflater): BaseAdapter() {

    private val IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/"
    private var books: List<Book> = emptyList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val newView: View

        if (convertView == null) {
            newView = inflater.inflate(R.layout.row_book, null)
            holder = ViewHolder(newView.findViewById(R.id.img_thumbnail),
                newView.findViewById(R.id.text_title),
                newView.findViewById(R.id.text_author))

            newView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            newView = convertView
        }

        val book = getItem(position)
        holder.titleTextView.text = book.title + if (book.firstPublishYear == null) "" else "(" + book.firstPublishYear + ")"
        holder.authorTextView.text = book.author?.get(0)

        val imageId = book.imageId ?: 0
        if (imageId != 0) {
            val imageURL = (IMAGE_URL_BASE
                    + imageId
                    + "-S.jpg")

            Picasso.with(context)
                .load(imageURL)
                .placeholder(R.drawable.ic_books)
                .into(holder.thumbnailImageView)
        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.ic_books)
        }

        return newView
    }

    fun updateData(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    override fun getCount() = books.size

    override fun getItem(position: Int) = books[position]

    override fun getItemId(position: Int): Long = position.toLong()

    data class ViewHolder(
        var thumbnailImageView: ImageView,
        var titleTextView: TextView,
        var authorTextView: TextView)
}