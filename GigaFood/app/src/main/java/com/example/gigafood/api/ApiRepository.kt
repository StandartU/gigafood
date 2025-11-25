package com.example.gigafood.api


import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File

class ApiRepository(private val api: ApiService) {

    fun apiRequest(
        path: String,
        method: String = "GET",
        body: Any? = null,
        file: File? = null,
        headers: Map<String, String> = emptyMap()
    ): Call<ResponseBody> {

        return when {
            file != null -> {
                val requestFile = file.asRequestBody("multipart/form-data".toMediaType())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val mapBody = mutableMapOf<String, RequestBody>()
                if (body is Map<*, *>) {
                    body.forEach { (k, v) ->
                        mapBody[k.toString()] =
                            v.toString().toRequestBody("text/plain".toMediaType())
                    }
                }

                api.uploadFile(path, filePart, mapBody, headers)
            }


            method.uppercase() == "POST" -> {
                if (body != null) {
                    // POST с телом
                    api.postJson(path, body, headers)
                } else {
                    // POST без тела, отправляем пустой JSON
                    api.postJson(path, mapOf<String, String>(), headers)
                }
            }

            else -> {
                // GET запрос
                api.getJson(path, headers)
            }
        }
    }

    fun apiFileRequest(
        path: String,
        headers: Map<String, String>
    ): Call<ResponseBody> = api.downloadFile(path, headers)
}