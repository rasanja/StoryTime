package cs477.gmu.project3_rdelphec.ui.create

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateStoryViewModel : ViewModel() {

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap = _bitmap.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap?){
        _bitmap.value = bitmap
    }

    var storyText by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun generateStory(prompt: String) {
        val photo = _bitmap.value ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                withContext(Dispatchers.IO) {
                    val base64Image = photo.toBase64()
                    val responseText = sendToOpenAI(prompt, base64Image)
                    storyText = responseText
                }
            } catch (e: Exception) {
                storyText = "Failed to generate story: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    suspend fun sendToOpenAI(prompt: String, base64Image: String): String {
        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->

            val client = OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS) 
                .build()

            val fullBase64 = "data:image/png;base64,$base64Image"
            val json = JSONObject().apply {
                put("prompt", prompt)
                put("imageBase64", fullBase64)
            }

            val requestBody: RequestBody = json.toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://generatestory-y3x57rkn4a-uc.a.run.app")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    continuation.resumeWith(Result.failure(e))
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    response.use {
                        if (!it.isSuccessful) {
                            continuation.resumeWith(
                                Result.failure(IOException("Unexpected code $it"))
                            )
                            return
                        }

                        val body = it.body?.string()
                        if (body != null) {
                            val story = JSONObject(body).optString("story", "No story received.")
                            storyText = story
                            continuation.resumeWith(Result.success(story))
                        } else {
                            continuation.resumeWith(
                                Result.failure(IOException("Empty response body"))
                            )
                        }
                    }
                }
            })
        }
    }

    fun Bitmap.toBase64(): String{
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray,Base64.NO_WRAP)
    }
}