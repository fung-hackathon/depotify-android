package com.example.funhacks2022

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funhacks2022.ui.theme.FunHacks2022Theme

class InfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FunHacks2022Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    infoRootComposable()
                }

                BackHandler {
                    this.finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("InfoActivity", "Destroyed")
    }
}

@Composable
fun infoRootComposable(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        userIdComposable()

        Column(
            verticalArrangement = Arrangement.Bottom
        ){
            YOLPLicenceComposable()
        }
    }
}

@Composable
fun userIdComposable() {
    val uidPref = LocalContext.current.getSharedPreferences(stringResource(R.string.USERID_DATA), Context.MODE_PRIVATE)

    Column {
        Text("あなたの引き継ぎコード", fontSize = 15.sp)
        Text(
            text = uidPref.getString("UserId", "undefined").toString(),
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth().height(18.dp),
        )
    }
}

@Composable
fun YOLPLicenceComposable(){
    val urlString: AnnotatedString = buildAnnotatedString {
        val baseStr = "Web Services by Yahoo! JAPAN （https://developer.yahoo.co.jp/sitemap/）"
        append(baseStr)
        addStyle(
            style = SpanStyle(
                color = Color.Cyan,
                fontSize = 15.sp,
                textDecoration = TextDecoration.Underline
            ),
            start = baseStr.indexOf("https"),
            end = baseStr.indexOf("）")
        )

        addStringAnnotation(
            tag = "URL",
            annotation = "https://developer.yahoo.co.jp/sitemap/",
            start = baseStr.indexOf("https"),
            end = baseStr.indexOf("）")
        )
    }

    val uriHandler = LocalUriHandler.current
    
    ClickableText(
        text = urlString,
        style = TextStyle(color = MaterialTheme.typography.body1.color, fontSize = 15.sp),
        onClick = {
            urlString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}