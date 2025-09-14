package com.example.chords2.data.repository

import com.example.chords2.data.model.post.Post

interface PostRepository {
    suspend fun getPosts(): Result<List<Post>>
    suspend fun getPostById(id: Int): Result<Post>
    suspend fun createPost(post: Post): Result<Post>
}