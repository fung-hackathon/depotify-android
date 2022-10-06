package com.example.funhacks2022.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.funhacks2022.R

@OptIn(ExperimentalTextApi::class)
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

@OptIn(ExperimentalTextApi::class)
val MPlusRounded = GoogleFont(name = "Kosugi")

@OptIn(ExperimentalTextApi::class)
val MPlusRoundedFontFamily = FontFamily(
    Font(googleFont = MPlusRounded, fontProvider = provider),
    /*
    Font(googleFont = MPlusRounded, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = MPlusRounded, fontProvider = provider, weight = FontWeight.ExtraBold),
    Font(googleFont = MPlusRounded, fontProvider = provider, weight = FontWeight.Black)
     */
)

// Set of Material typography styles to start with
val LightTypography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        //fontFamily = MPlusRoundedFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = DarkSecondaryColor
    ),
    //Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        //fontFamily = MPlusRoundedFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        color = LightSecondaryColor
    )
/*
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

val DarkTypography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        //fontFamily = MPlusRoundedFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = LightSecondaryColor
    ),

    button = TextStyle(
        fontFamily = FontFamily.Default,
        //fontFamily = MPlusRoundedFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        color = LightSecondaryColor
    )
)