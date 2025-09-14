package com.example.chords2.data.repository

import com.example.chords2.data.model.post.Post
import com.example.chords2.data.remote.JsonPlaceholderApiService
import com.example.chords2.data.remote.toPost
import com.example.chords2.data.remote.toPostDto
import java.io.IOException

class PostRepositoryImpl(
    private val apiService: JsonPlaceholderApiService
) : PostRepository {
    override suspend fun getPosts(): Result<List<Post>> {
        return try {
            val response = apiService.getPosts()
            if (response.isSuccessful) {
                val postDtos = response.body()
                Result.success(postDtos?.map { it.toPost() } ?: emptyList())
            } else {
                Result.failure(IOException("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPostById(id: Int): Result<Post> {
        return try {
            val response = apiService.getPostById(id)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(dto.toPost())
                } ?: Result.failure(IOException("Post not found or empty response body."))
            } else {
                Result.failure(IOException("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPost(post: Post): Result<Post> {
        return try {
            val postDto = post.toPostDto()

            val response = apiService.createPost(postDto)
            if (response.isSuccessful) {
                response.body()?.let { createdDto ->
                    Result.success(createdDto.toPost())
                } ?: Result.failure(IOException("Failed to create post or empty response body."))
            } else {
                Result.failure(IOException("API Error creating post: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}