package com.lawapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lawapp.android.ui.navigation.LawAppNavGraph
import com.lawapp.android.ui.theme.LawAppTheme

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LawAppTheme {
                LawAppNavGraph()
            }
        }
    }
}

