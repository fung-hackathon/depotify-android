package com.example.funhacks2022

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funhacks2022.ui.theme.FunHacks2022Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /*TODO: Create Theme*/
            /*TODO: Get "isFirstLanding" from AppStorage*/
            FunHacks2022Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    mainComposable()
                }
            }
        }
    }
}

@Composable
fun mainComposable(){
    //isFirstLandingはAppStorageかなんかから持ってくる(暫定で値を設定している)
    var isFirstLanding by remember { mutableStateOf(false) }
    if (isFirstLanding) {
        firstLandingComposable(
            newUserClick = {
                /*TODO*/
                isFirstLanding = false
            },
            loginClick = {
                /*TODO*/
                isFirstLanding = false
            }
        )
    }
    else {
        homeComposable()
    }
}

@Composable
fun firstLandingComposable(
    newUserClick: ()->Unit,
    loginClick: ()->Unit,
) {
    var loginId by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome!", fontSize = 60.sp)

        Spacer(modifier = Modifier.padding(100.dp))

        Button(
            onClick = newUserClick,
            modifier = Modifier.size(width = 275.dp, height = 50.dp)
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
            modifier = Modifier.size(width = 275.dp, height = 50.dp)
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Button(
            onClick = {
                if (loginValidator(loginId)) {
                    loginClick
                }
                else {
                    Toast.makeText(
                        context,
                        "不正な引き継ぎコードです。\n再度、確認してください。",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier.size(width = 275.dp, height = 50.dp)
        ) {
                Text("データを引き継ぎ", fontSize = 16.sp)
        }
    }
}

fun loginValidator(loginId: String): Boolean{
    /*TODO*/
    return false
}

@Composable
fun homeComposable() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ){
        val userId = "undefined"
        var showId by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!showId){
                ClickableText(
                    text = AnnotatedString("引き継ぎコード: ここをタップして表示"),
                    style = TextStyle( fontSize = 14.sp ),
                    modifier = Modifier.size(width = 300.dp, height = 18.dp),
                    onClick = { showId = true }
                )
            }
            else {
                Text(
                    text = "引き継ぎコード: $userId",
                    fontSize = 14.sp,
                    modifier = Modifier.size(width = 300.dp, height = 18.dp)
                )
            }
            Spacer(modifier = Modifier.padding(5.dp))
            dataViewerComposable()
            Spacer(modifier = Modifier.padding(25.dp))
            Button(
                onClick = {},
                modifier = Modifier.size(width = 300.dp, height = 75.dp)
            ) {
                Text(text = "送迎を開始", fontSize = 28.sp)
            }
        }
    }
}

@Composable
fun dataViewerComposable() {
    Column(
        modifier = Modifier
            .border(2.dp, Color.Black)
            .size(width = 300.dp, height = 500.dp)
    ) {
        Text(text = "あああああ", fontSize = 50.sp)
        Spacer(modifier = Modifier.padding(75.dp))
        Text(text = "あああああ", fontSize = 50.sp)
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FunHacks2022Theme {
        mainComposable()
    }
}