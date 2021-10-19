package com.raywenderlich.android.omgandroid

import com.google.gson.annotations.SerializedName

data class Books (
    val docs: List<Book>
)


data class Book(val title: String?,

                @SerializedName("first_publish_year")
                val firstPublishYear: String?,

                @SerializedName("author_name")
                val author: List<String>?,

                @SerializedName("cover_i")
                val imageId: String?)

