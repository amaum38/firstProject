package com.raywenderlich.android.omgandroid

import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BookAdapterRec(context: Context, val itemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<BookAdapterRec.ViewHolder>() {

    private val IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/"
    private var books: List<Book> = emptyList()
    val context = context

   /* override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
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
    }*/

    fun updateData(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = books[position]

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_book, viewGroup, false)

        return ViewHolder(view, context)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val book = this.books[position]
        viewHolder.titleTextView.text = book.title + if (book.firstPublishYear == null) "" else "(" + book.firstPublishYear + ")"
        viewHolder.authorTextView.text = book.author?.get(0)

        val imageId = book.imageId ?: 0
        if (imageId != 0) {
            val imageURL = (IMAGE_URL_BASE
                    + imageId
                    + "-S.jpg")

            Picasso.with(context)
                .load(imageURL)
                .placeholder(R.drawable.ic_books)
                .into(viewHolder.thumbnailImageView)
        } else {
            viewHolder.thumbnailImageView.setImageResource(R.drawable.ic_books)
        }

        viewHolder.itemView.setOnClickListener {
            itemClickListener.onItemClicked(books[position])
        }
    }

    override fun getItemCount() = books.size

    override fun getItemId(position: Int): Long = position.toLong()

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
        val thumbnailImageView: ImageView = view.findViewById(R.id.img_thumbnail)
        val titleTextView: TextView = view.findViewById(R.id.text_title)
        val authorTextView: TextView = view.findViewById(R.id.text_author)
    }

    interface OnItemClickListener{
        fun onItemClicked(book: Book)
    }
}