package com.example.funhacks2022

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.funhacks2022.ui.theme.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource

class DrivingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FunHacks2022Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    drivingMainComposable()
                }
            }
        }
    }
}

@Composable
fun drivingMainComposable() {
    var isFinished by remember { mutableStateOf(false) }
    var dialogState by remember { mutableStateOf(0) }

    val thisContext = LocalContext.current

    val REQUEST_CODE = 1234
    if (ContextCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
        Log.d("GPS permission", "GPS access granted")
    }
    else {
        ActivityCompat.requestPermissions(
            thisContext as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    val dStatePref = thisContext.getSharedPreferences(stringResource(R.string.DRIVING_STATE), Context.MODE_PRIVATE)
    val locationDataPref = thisContext.getSharedPreferences(stringResource(R.string.LOCATION_DATA), Context.MODE_PRIVATE)

    if (!isFinished) {
        drivingComposable(clickFinish = { dialogState = 1 },
        clickCancel = { dialogState = 2 })
    }
    else {
        drivingEndedComposable(clickBack = { dialogState = 3 })
    }

    when(dialogState) {
        1 -> { //Finish Driving
            AlertDialog(
                onDismissRequest = { dialogState = 0 },
                dismissButton = { TextButton(onClick = { dialogState = 0 }) { Text(text = "いいえ", color = if (isSystemInDarkTheme()) LightSecondaryColor else DarkSecondaryColor) } },
                confirmButton = { TextButton(onClick = { dialogState = 4 }) { Text(text = "はい", color = if (isSystemInDarkTheme()) LightSecondaryColor else DarkSecondaryColor) } },
                title = { Text(text = "送迎を終了しますか?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .background(MaterialTheme.colors.background),
                shape = RoundedCornerShape(5.dp),
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
            )
        }
        2 -> { //Cancel Driving
            AlertDialog(
                onDismissRequest = { dialogState = 0 },
                dismissButton = { TextButton(onClick = { dialogState = 0 }) { Text(text = "いいえ", color = if (isSystemInDarkTheme()) LightSecondaryColor else DarkSecondaryColor) } },
                confirmButton = {
                    TextButton(onClick = {
                        dialogState = 0

                        //Update driving state
                        with(dStatePref.edit()){
                            putInt("drivingState", 0)
                            apply()
                        }

                        //Back to HomeActivity directly
                        thisContext.startActivity(Intent(thisContext, HomeActivity::class.java))
                        (thisContext as Activity).finish()
                    }) {
                        Text(text = "はい", color = if (isSystemInDarkTheme()) LightSecondaryColor else DarkSecondaryColor)
                    }
                },
                title = { Text(text = "送迎を中止(キャンセル)しますか?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .background(MaterialTheme.colors.background),
                shape = RoundedCornerShape(5.dp),
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
            )
        }
        3 -> { //Return to home after the driving finished
            AlertDialog(
                onDismissRequest = { dialogState = 0 },
                dismissButton = { TextButton(onClick = { dialogState = 0 }) { Text(text = "いいえ", color = if (isSystemInDarkTheme()) LightSecondaryColor else DarkSecondaryColor) } },
                confirmButton = {
                    TextButton(onClick = {
                        dialogState = 0

                        //Update driving state
                        with(dStatePref.edit()){
                            putInt("drivingState", 0)
                            apply()
                        }

                        //Back to HomeActivity directly
                        thisContext.startActivity(Intent(thisContext, HomeActivity::class.java))
                        (thisContext as Activity).finish()
                    }) {
                        Text(text = "はい", color = if (isSystemInDarkTheme()) LightSecondaryColor else DarkSecondaryColor)
                    }
                },
                text = { Text(text = "QRコードは再発行できません。\nご注意ください。") },
                title = { Text(text = "ホームに戻りますか?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .background(MaterialTheme.colors.background),
                shape = RoundedCornerShape(5.dp),
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
            )
        }
        4 -> { //Getting Location by GPS
            Dialog(
                onDismissRequest = { dialogState = 0 },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colors.background, shape = RoundedCornerShape(5.dp))
                        .size(height = 100.dp, width = 200.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.padding(15.dp))
                    Text("GPS測位中です。\nしばらくお待ち下さい")
                }
                FusedLocationProviderClient(thisContext).getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            thisContext, "GPS測位に失敗しました。位置情報サービスなどを確かめた上で、再度お試しください", Toast.LENGTH_LONG
                        ).show()
                        dialogState = 0
                    }
                    .addOnSuccessListener { location ->
                        with(locationDataPref.edit()) {
                            putString("finishLatitude", (location.latitude).toString())
                            putString("finishLongitude", (location.longitude).toString())
                            apply()
                        }

                        dialogState = 0

                        with(dStatePref.edit()){
                            putInt("drivingState", 0)
                            apply()
                        }

                        //Move to drivingEndedComposable
                        isFinished = true
                    }
            }
        }
        else -> {}
    }
}

@Composable
fun drivingComposable(clickFinish: ()->Unit, clickCancel: ()->Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("送迎中です", fontSize = 50.sp)

        Spacer(modifier = Modifier.padding(5.dp))

        Text("この状態で別のアプリを起動することもできます。\n目的地で、「送迎を終了」を押してください。", textAlign = TextAlign.Center, fontSize = 15.sp)

        Spacer(modifier = Modifier.padding(100.dp))

        Button(
            onClick = clickFinish,
            modifier = Modifier.size(width = 275.dp, height = 50.dp),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(text = "送迎を終了", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.padding(20.dp))

        Button(
            onClick = clickCancel,
            modifier = Modifier.size(width = 275.dp, height = 50.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.typography.body1.color, backgroundColor = midGray)
        ) {
            Text(text = "送迎を中止", color = WarningTextColor, fontSize = 16.sp)
        }
    }
}

@Composable
fun drivingEndedComposable(clickBack: ()->Unit) {
    val thisContext = LocalContext.current
    val locationDataPref = thisContext.getSharedPreferences(stringResource(R.string.LOCATION_DATA), Context.MODE_PRIVATE)
    val uidPref = thisContext.getSharedPreferences(stringResource(R.string.USERID_DATA), Context.MODE_PRIVATE)
    val userId = uidPref.getString("UserId", "undefined").toString()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "終了しました",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.size(width = 350.dp, height = 75.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = "運転お疲れ様でした！\n\n表示されているQRコードを\n読み取ってもらってください",
            fontSize = 15.sp,
            modifier = Modifier.size(width = 240.dp, height = 80.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(15.dp))

        Log.d("QR generating", "UserId: $userId")

        Image(
            bitmap = generateFinishQR(
                userId = userId,
                startLat = locationDataPref.getString("startLatitude", "null").toString(), startLot = locationDataPref.getString("startLongitude", "null").toString(),
                finishLat = locationDataPref.getString("finishLatitude", "null").toString(), finishLot = locationDataPref.getString("finishLongitude", "null").toString()
            ),
            contentDescription = "qrCode",
            modifier = Modifier.size(width = 275.dp, height = 275.dp)
        )

        Spacer(modifier = Modifier.padding(30.dp))

        Button(
            onClick = clickBack,
            modifier = Modifier.size(width = 275.dp, height = 50.dp),
            shape = RoundedCornerShape(50.dp)
        ){
            Text("ホームに戻る", fontSize = 16.sp)
        }
    }
}