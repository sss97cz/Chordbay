package com.example.chords2.data.remote

import com.example.chords2.data.model.post.Post
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostDto(
    @Json(name = "userId") val userId: Int,
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "body") val body: String
)

fun PostDto.toPost(): Post =
    Post(
        userId = userId,
        id = id,
        title = title,
        body = body
    )
fun Post.toPostDto(): PostDto =
    PostDto(
        userId = userId,
        id = id,
        title = title,
        body = body
    )