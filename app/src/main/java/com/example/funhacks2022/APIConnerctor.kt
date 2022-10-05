package com.example.funhacks2022

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class APIConnerctor {
    fun generateFinishQR(
        userId: String, startLat: String, startLot: String, finishLat: String, finishLot: String
    ): ImageBitmap {
        val putStr = "userId:$userId\nstartLat: $startLat, startLot: $startLot\nfinishLat: $finishLat, finishLot: $finishLot"
        return BarcodeEncoder().encodeBitmap(putStr, BarcodeFormat.QR_CODE, 275, 275).asImageBitmap()
    }
}