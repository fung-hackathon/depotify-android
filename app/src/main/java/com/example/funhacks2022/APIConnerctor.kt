package com.example.funhacks2022

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import retrofit2.http.GET

val APIBASEURL = "https://hogehoge.com"

interface DepotifyAPI {
    suspend fun createUser(): String {
        return "test"
    }

    suspend fun getUserData(): String {
        return "test"
    }
}

fun generateFinishQR(
    userId: String, startLat: String, startLot: String, finishLat: String, finishLot: String
): ImageBitmap {
    val targetUrl = "$APIBASEURL/arrive?userId=$userId?olat=$startLat?olng=$startLot?dlat=$finishLat?dlng=$finishLot"
    return BarcodeEncoder().encodeBitmap(targetUrl, BarcodeFormat.QR_CODE, 275, 275).asImageBitmap()
}

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