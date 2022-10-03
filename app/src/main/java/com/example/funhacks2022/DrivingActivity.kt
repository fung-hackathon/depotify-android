package com.example.funhacks2022

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funhacks2022.ui.theme.FunHacks2022Theme

class DrivingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /*TODO: Create Theme*/

            var isFinished by remember { mutableStateOf(false) }
            FunHacks2022Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (!isFinished) {
                        drivingComposable(clickFinish = { isFinished = true })
                    }
                    else {
                        drivingEndedComposable()
                    }
                }
            }
        }
    }
}

@Composable
fun drivingComposable(clickFinish: ()->Unit) {
    val thisContext = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("送迎中です", fontSize = 50.sp)

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
            onClick = {
                //If cancelled, back to MainActivity directly
                thisContext.startActivity(Intent(thisContext, MainActivity::class.java))
                (thisContext as Activity).finish()
            },
            modifier = Modifier.size(width = 275.dp, height = 50.dp),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(text = "送迎を中止", color = Color.Red, fontSize = 16.sp)
        }
    }
}

@Composable
fun drivingEndedComposable() {
    val thisContext = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "送迎を終了しました",
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.size(width = 275.dp, height = 50.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = "表示されているQRコードを\n読み取ってもらってください",
            fontSize = 15.sp,
            modifier = Modifier.size(width = 240.dp, height = 50.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(35.dp))

        Canvas(modifier = Modifier.size(width = 275.dp, height = 275.dp)){
            drawRect(color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.padding(60.dp))

        Button(
            onClick = {
                /* Is it all that back to home menu that I have to do here? */
                thisContext.startActivity(Intent(thisContext, MainActivity::class.java))
                (thisContext as Activity).finish()
            },
            modifier = Modifier.size(width = 275.dp, height = 50.dp),
            shape = RoundedCornerShape(50.dp)
        ){
            Text("ホームに戻る", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FunHacks2022Theme {
        //drivingComposable()
        drivingEndedComposable()
    }
}