package org.bosf.pooch.data.repository

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()

    data object Loading : NetworkResult<Nothing>()
}

suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<T>): NetworkResult<T> {
    return try {
        val response = call()

        if (response.isSuccessful) {
            val body = response.body()

            if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error("Empty response body", response.code())
            }
        } else {
            val errorMsg = response.errorBody()?.string()
                ?.let { parseErrorMessage(it) }
                ?: "Request failed with code ${response.code()}"

            NetworkResult.Error(errorMsg, response.code())
        }
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "An unexpected error occurred")
    }
}

private fun parseErrorMessage(errorBody: String): String {
    return try {
        val gson = com.google.gson.Gson()
        val error = gson.fromJson(errorBody, org.bosf.pooch.data.api.models.ApiError::class.java)

        error.detail
    } catch (e: Exception) {
        errorBody.take(200)
    }
}
