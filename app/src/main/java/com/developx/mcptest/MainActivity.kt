package com.developx.mcptest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.developx.mcptest.ui.home.ScheduleNotesApp
import com.developx.mcptest.ui.theme.MCPTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MCPTestTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ScheduleNotesApp()
                }
            }
        }
    }
}
