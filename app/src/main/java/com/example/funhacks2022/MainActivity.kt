package com.example.funhacks2022

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.funhacks2022.ui.theme.FunHacks2022Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FunHacks2022Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //If the user closed app before finish driving then let user go to the Driving Activity.
                    //Save weather user is driving to inAppDB

                    val sharedPref = LocalContext.current.getSharedPreferences(stringResource(R.string.DRIVING_STATE), Context.MODE_PRIVATE)
                    var drivingState = -1

                    if (sharedPref == null) Log.d("Information", "Null")
                    else {
                        drivingState = sharedPref.getInt("drivingState", -1)
                    }
                    Log.d("Information","Log: $drivingState")

                    if (drivingState <= 0){
                        LocalContext.current.startActivity(Intent(LocalContext.current, HomeActivity::class.java))
                        (LocalContext.current as Activity).finish()
                    }
                    else if (drivingState == 1){
                        LocalContext.current.startActivity(Intent(LocalContext.current, DrivingActivity::class.java))
                        (LocalContext.current as Activity).finish()
                    }
                }
            }
        }
    }
}