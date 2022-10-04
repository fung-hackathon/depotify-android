package com.example.funhacks2022

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

                    val isDriving = false
                    if (!isDriving){
                        LocalContext.current.startActivity(Intent(LocalContext.current, HomeActivity::class.java))
                        (LocalContext.current as Activity).finish()
                    }
                    else {
                        LocalContext.current.startActivity(Intent(LocalContext.current, DrivingActivity::class.java))
                        (LocalContext.current as Activity).finish()
                    }
                }
            }
        }
    }
}