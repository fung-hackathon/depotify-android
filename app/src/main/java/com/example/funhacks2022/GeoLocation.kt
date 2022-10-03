package com.example.funhacks2022

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task

class GeoLocationGetter(parentContext: Context) {
    var fusedLocationManager: FusedLocationProviderClient = FusedLocationProviderClient(parentContext)
    var context: Context = parentContext
    var cancellationSource = CancellationTokenSource()

    fun getCurrentLocation(): Task<Location>? {
        val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        var res: Task<Location>? = null

        if (isGranted == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PackageManager.PERMISSION_GRANTED
            )
        }

        if (isGranted == PackageManager.PERMISSION_GRANTED) {
            println("PERMISSION GRANTED")
            res = fusedLocationManager.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationSource.token)
            while(!res.isComplete) {
                println("WAITING")
            }
        }

        //println("${res!!.result.latitude}, ${res!!.result.longitude}")

        return res
    }
}