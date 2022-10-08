package com.example.funhacks2022

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.funhacks2022.ui.theme.DarkSecondaryColor
import com.example.funhacks2022.ui.theme.FunHacks2022Theme
import com.example.funhacks2022.ui.theme.LightSecondaryColor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FunHacks2022Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    homeRootComposable(UserIdViewModel(LocalContext.current))
                }
            }
        }
    }
}

@Composable
fun homeRootComposable(useridViewModel: UserIdViewModel){
    val aStatePref = LocalContext.current.getSharedPreferences(stringResource(R.string.ARRIVE_STATE), Context.MODE_PRIVATE)
    var isFirstLanding by remember { mutableStateOf(aStatePref.getBoolean("isFirstLanding", true)) }

    if (isFirstLanding) {
        firstLandingComposable(
            newUserClick = {
                useridViewModel.createUser(){
                    isFirstLanding = false
                    with(aStatePref.edit()){
                        putBoolean("isFirstLanding", false)
                        apply()
                    }
                }
            },
            onLoginSuccess = {
                isFirstLanding = false
                with(aStatePref.edit()){
                    putBoolean("isFirstLanding", false)
                    apply()
                }
            },
            UserIdViewModel(LocalContext.current)
        )
    }
    else {
        homeComposable()
    }
}

@Composable
fun firstLandingComposable(
    newUserClick: ()->Unit,
    onLoginSuccess: ()->Unit,
    useridViewModel: UserIdViewModel
) {
    var loginId by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painterResource(if (isSystemInDarkTheme()) R.drawable.applogo_dark else R.drawable.applogo), contentDescription = "", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.padding(100.dp))

        Button(
            onClick = newUserClick,
            modifier = Modifier.size(width = 275.dp, height = 55.dp),
            shape = RoundedCornerShape(50.dp),
        ){
            Text("新規登録", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.padding(20.dp))

        Text(
            text = "引き継ぎコードを持っていますか？",
            fontSize = 15.sp,
            modifier = Modifier.size(width = 275.dp, height = 20.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(5.dp))
        OutlinedTextField(
            value = loginId,
            onValueChange = { loginId = it },
            placeholder = { Text("引き継ぎコードを入力") },
            singleLine = true,
            modifier = Modifier.size(width = 275.dp, height = 55.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri)
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Button(
            onClick = {
                useridViewModel.loginWithId(
                    requestId = loginId,
                    onComplete = onLoginSuccess,
                    onError = {
                        Toast.makeText(context, "不正な引き継ぎコードです。\n再度、確認してください。", Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier.size(width = 275.dp, height = 55.dp),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text("データを引き継ぎ", fontSize = 16.sp)
        }
    }
}

@Composable
fun homeComposable() {
    val thisContext = LocalContext.current
    var dialogState by remember { mutableStateOf(0) }

    //How I can move "Getting Geo Location" codes to independent class?
    val REQUEST_CODE = 1234
    if (ContextCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
        println("ACCESS GRANTED")
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        userIdComposable()

        Spacer(modifier = Modifier.padding(5.dp))
        Surface(elevation = 10.dp, shape = RoundedCornerShape(20.dp)) { dataViewerComposable(UserDataViewModel(thisContext)) }
        Spacer(modifier = Modifier.padding(25.dp))

        Surface(elevation = 10.dp, shape = RoundedCornerShape(50.dp)) {
            Button(
                onClick = { dialogState = 1 },
                modifier = Modifier.size(width = 300.dp, height = 75.dp),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(text = "送迎を開始", fontSize = 28.sp)
            }
        }
    }

    if (dialogState == 1) {
        AlertDialog(
            onDismissRequest = { dialogState = 0 },
            dismissButton = { TextButton(onClick = { dialogState = 0 }) { Text("いいえ", color = if (isSystemInDarkTheme()) LightSecondaryColor else DarkSecondaryColor)  } },
            confirmButton = { TextButton(onClick = { dialogState = 2 }) { Text(text = "はい", color = if (isSystemInDarkTheme()) LightSecondaryColor else DarkSecondaryColor) } },
            title = { Text(text = "送迎を開始しますか?") },
            text = { Text(text = "同乗者を乗せてから開始してください。\n出発点と到着点が同じ場合は、記録が無効となることに注意してください。") },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background, shape = RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
    else if (dialogState == 2){
        Dialog(
            onDismissRequest = { dialogState = 0 },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(5.dp)
                    )
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
                        putString("startLatitude", (location.latitude).toString())
                        putString("startLongitude", (location.longitude).toString())
                        apply()
                    }

                    dialogState = 0

                    with(dStatePref.edit()){
                        putInt("drivingState", 1)
                        apply()
                    }

                    //Destroy this Activity
                    thisContext.startActivity(Intent(thisContext, DrivingActivity::class.java))
                    (thisContext as Activity).finish()
                }
        }
    }
}

@Composable
fun userIdComposable() {
    val uidPref = LocalContext.current.getSharedPreferences(stringResource(R.string.USERID_DATA), Context.MODE_PRIVATE)
    var showId by remember { mutableStateOf(false) }

    if (!showId){
        ClickableText(
            text = AnnotatedString("引き継ぎコードはここをタップして表示"),
            style = TextStyle( fontSize = 13.sp , color = MaterialTheme.typography.body1.color ),
            modifier = Modifier.size(width = 300.dp, height = 18.dp),
            onClick = { showId = true }
        )
    }
    else {
        Text(
            text = uidPref.getString("UserId", "undefined").toString(),
            fontSize = 13.sp,
            modifier = Modifier.size(width = 300.dp, height = 18.dp),
            textAlign = TextAlign.Left
        )
    }
}

@Composable
fun dataViewerComposable(vm: UserDataViewModel) {
    //Get user's cumulative score and recent emoticons(n = 30)
    val uidPref = LocalContext.current.getSharedPreferences(stringResource(R.string.USERID_DATA), Context.MODE_PRIVATE)
    var readyScore by remember { mutableStateOf(false) }

    vm.getCumulativeScore(
        userId = uidPref.getString("UserId", "undefined").toString(),
        onSuccess = {
            readyScore = true
        }
    )

    Column(
        modifier = Modifier
            //.border(width = 2.dp, color = Color.DarkGray, shape = RoundedCornerShape(20.dp))
            .size(width = 300.dp, height = 450.dp)
            .padding(25.dp)
    ) {
        Text("累計スコア:", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.padding(5.dp))
        Row (modifier = Modifier.width(300.dp), horizontalArrangement = Arrangement.End){
            if (DEMOMODE){
                Text("135 pts", fontSize = 40.sp)
            }
            else {
                if (!readyScore) Text("Loading...", fontSize = 40.sp)
                else Text("${vm.currentScore} pts", fontSize = 40.sp)
            }
        }

        Spacer(Modifier.padding(5.dp))
        Divider()
        Spacer(Modifier.padding(5.dp))

        Text("もらった絵文字", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("直近16件を表示しています", fontSize = 12.sp)

        EmojiComposable(UserDataViewModel(LocalContext.current), uidPref.getString("UserId", "undefined").toString())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiComposable(vm: UserDataViewModel, userId: String) {
    var readyEmoji by remember { mutableStateOf(false) }

    vm.getReceivedEmojies(userId, { readyEmoji = true })

    if (DEMOMODE){
        Column(Modifier.size(width = 300.dp, height = 220.dp)) {
            Row(Modifier.width(300.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Image(painterResource(R.drawable.emoji_100), contentDescription = "", modifier = Modifier.size(50.dp))
                Image(painterResource(R.drawable.emoji_heart), contentDescription = "", modifier = Modifier.size(50.dp))
                Image(painterResource(R.drawable.emoji_heartsmile), contentDescription = "", modifier = Modifier.size(50.dp))
                Image(painterResource(R.drawable.emoji_party), contentDescription = "", modifier = Modifier.size(50.dp))
            }
            Spacer(Modifier.padding(5.dp))
            Row(Modifier.width(300.dp)){
                Image(painterResource(id = R.drawable.emoji_heart), contentDescription = "", modifier = Modifier.size(50.dp))

            }
        }
    }
    else {
        Column(Modifier.size(width=300.dp, height=220.dp)){
            /* TODO */
            if (vm.errorMessage.isEmpty() && readyEmoji) {
                LazyVerticalGrid(
                    cells = GridCells.Fixed(4),
                    modifier = Modifier.size(width = 300.dp, height = 220.dp),
                    content = {
                        items(vm.emojies) { emojiId ->
                            Image(
                                painterResource(
                                    ConvEmojiIdToRid.getOrDefault(
                                        emojiId,
                                        R.drawable.blank,
                                    ),
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                            )
                        }
                    },
                    verticalArrangement = Arrangement.SpaceAround
                )
            }
            else {
                Text(vm.errorMessage, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}