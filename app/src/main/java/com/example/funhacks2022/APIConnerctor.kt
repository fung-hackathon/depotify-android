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
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

val APIBASEURL = "https://depotify.onrender.com"

class UserDataViewModel: ViewModel() {
    private val _todoList = mutableStateListOf<ToDo>()
    var errorMessage: String by mutableStateOf("")
    val todoList: List<ToDo>
        get() = _todoList

    fun getTodoList() {
        viewModelScope.launch {
            val apiService = DepotifyAPI.Instance()
            try {
                _todoList.clear()
                _todoList.addAll(apiService.getTodos())

            } catch (e: Exception) {
                errorMessage = e.message.toString()
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
    suspend fun getUserData(): Score

    @GET("/emotion")
    suspend fun getEmojiData(): Emojies

    @GET("/score")
    suspend fun checkUserExist(
        @Query("userId") userId: String
    ): Score

    @GET("/todos")
    suspend fun getTodos(): List<ToDo>

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
    val targetUrl = "$APIBASEURL/arrive?userId=$userId?olat=$startLat?olng=$startLot?dlat=$finishLat?dlng=$finishLot"
    return BarcodeEncoder().encodeBitmap(targetUrl, BarcodeFormat.QR_CODE, 275, 275).asImageBitmap()
}

data class ToDo(
    var userId: Int,
    var id: Int,
    var title: String,
    var compleated: Boolean
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

data class ApiError(
    val code: Int,
    val description: String
)

data class ApiHealth(
    val message: String
)