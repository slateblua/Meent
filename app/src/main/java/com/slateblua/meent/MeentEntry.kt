package com.slateblua.meent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.slateblua.meent.ui.theme.MeentTheme

class MeentEntry : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "CS219",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                // to create a new branch for testing, run
                // git checkout -b <branch-name>
                // to see where you are right now you can use
                // git status
                // mostly the ide provides ui to interact with git
                // but it's good to know the commands too
                // to push your changes to github, you can use
                // git add .
                // git commit -m "your message"
                // git push origin <branch-name>
                // then you can create a pull request on github
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MeentTheme {
        Greeting("Android")
    }
}