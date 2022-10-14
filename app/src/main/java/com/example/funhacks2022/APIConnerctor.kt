package com.example.funhacks2022

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

val APIBASEURL = "https://depotify.onrender.com"

class UserDataViewModel(originContext: Context): ViewModel() {
    val udPref = originContext.getSharedPreferences(originContext.getString(R.string.USERID_DATA), Context.MODE_PRIVATE)

    var currentScore by mutableStateOf(udPref.getString("UserScore", "0").toString().toInt())
    var scoreReady by mutableStateOf(false)
    var emojiReady by mutableStateOf(false)
    var errorMessage: String by mutableStateOf("")

    private val _emojies = mutableStateListOf<String>()
    val emojies: List<String>
        get() = _emojies

    fun getCumulativeScore(userId: String){//, onSuccess: ()->Unit) {
        viewModelScope.launch {
            val apiService = DepotifyAPI.Instance()
            try {
                val newScore = apiService.getUserScore(userId).score

                Log.d("CurrentScore", newScore.toString())

                if (currentScore != newScore) {
                    currentScore = newScore
                    with(udPref.edit()) {
                        putString("UserScore", currentScore.toString())
                        apply()
                    }
                }

                scoreReady = true
                Log.d("API SUCCESS", "currentScore updated")
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("APIERROR", errorMessage)
            }
        }
    }

    fun getReceivedEmojies(userId: String) {//, onSuccess: ()->Unit) {
        viewModelScope.launch {
            val apiService = DepotifyAPI.Instance()
            try {
                _emojies.clear()
                _emojies.addAll(apiService.getEmojiData(userId).emotion.toMutableStateList())
                //_emojies.addAll(listOf("100", "yum", "100", "100", "100", "yum", "100", "100","100", "yum", "100", "100","100", "yum", "100", "100","100", "yum", "yum"))

                if (_emojies.size > 16) {
                    _emojies.removeRange(16, _emojies.size)
                }

                emojiReady = true
                Log.d("API SUCCESS", "EmojiData updated")
            } catch (e: Exception) {
                errorMessage = e.toString()
            }
        }
    }
}

class UserIdViewModel(originContext: Context): ViewModel() {
    val uidPref = originContext.getSharedPreferences(originContext.getString(R.string.USERID_DATA), Context.MODE_PRIVATE)
    var currentId by mutableStateOf(uidPref.getString("UserId", "undefined"))

    var errorMessage: String by mutableStateOf("")

    fun createUser(onSuccess: ()->Unit) {
        viewModelScope.launch {
            val apiService = DepotifyAPI.Instance()
            try {
                currentId = apiService.createUser().userid

                with(uidPref.edit()){
                    Log.d("NewId", currentId.toString())
                    putString("UserId", currentId)
                    apply()
                }

                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    fun loginWithId(requestId: String, onComplete: ()->Unit, onError: ()->Unit) {
        viewModelScope.launch {
            val apiService = DepotifyAPI.Instance()
            try {
                val id = apiService.checkUserExist(requestId).userid

                Log.d("Login", "Returned From the API: $id")
                if (id.equals(requestId)) {
                    currentId = requestId
                    with(uidPref.edit()){
                        Log.d("LoggedInId", currentId.toString())
                        putString("UserId", currentId)
                        apply()
                    }
                    onComplete()
                }
                else onError()
            } catch (e: Exception) {
                //Write toast initiator in the function below.
                onError()

                errorMessage = e.message.toString()
            }
        }
    }
}

interface DepotifyAPI {
    @POST("/user")
    suspend fun createUser(): UserId

    @GET("/score")
    suspend fun getUserScore(
        @Query("userid") userId: String
    ): Score

    @GET("/emotion")
    suspend fun getEmojiData(
        @Query("userid") userId: String
    ): Emojies

    @GET("/score")
    suspend fun checkUserExist(
        @Query("userid") userId: String
    ): Score

    companion object {
        var depotifyAPI: DepotifyAPI? = null
        fun Instance(): DepotifyAPI{
            if (depotifyAPI == null){
                depotifyAPI = Retrofit.Builder()
                    .baseUrl(APIBASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(DepotifyAPI::class.java)
            }
            return depotifyAPI!!
        }
    }
}

fun generateFinishQR(
    userId: String, startLat: String, startLot: String, finishLat: String, finishLot: String
): ImageBitmap {
    val targetUrl = "$APIBASEURL/arrive?userid=$userId&olat=$startLat&olng=$startLot&dlat=$finishLat&dlng=$finishLot"
    return BarcodeEncoder().encodeBitmap(targetUrl, BarcodeFormat.QR_CODE, 275, 275).asImageBitmap()
}

val ConvEmojiIdToRid = mapOf(
    "100" to R.drawable.emoji_100,
    "heart" to R.drawable.emoji_heart,
    "blush" to R.drawable.emoji_smile,
    "smiling_face_with_3_hearts" to R.drawable.emoji_heartsmile,
    "yum" to R.drawable.emoji_yummy,
    "partying_face" to R.drawable.emoji_party
)

data class Emojies(
    val userid: String,
    val emotion: List<String>
)

data class UserId(
    val userid: String
)

data class Score(
    val userid: String,
    val score: Int
)