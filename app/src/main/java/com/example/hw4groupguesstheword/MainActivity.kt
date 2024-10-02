package com.example.hw4groupguesstheword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hw4groupguesstheword.ui.theme.HW4GroupGuessTheWordTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuessTheWord()
        }
    }
}

@Composable
fun calculateCurrentWindowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val isWideScreen = screenWidth >= 600

    return WindowInfo(
        isWideScreen = isWideScreen
    )
}

data class WindowInfo(
    val isWideScreen: Boolean
)

@Composable
fun GuessTheWord() {
    val windowInfo = calculateCurrentWindowInfo()
    val wordToGuess = "apple"
    val maxTries = 6

    var guessedLetters by rememberSaveable { mutableStateOf(listOf<Char>()) }
    var triesLeft by rememberSaveable { mutableStateOf(maxTries) }
    var gameStarted by rememberSaveable { mutableStateOf(false) }

    if (!gameStarted) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = { gameStarted = true },
            ) {
                Text(text = "Start Game")
            }
        }
    } else {
        if (windowInfo.isWideScreen) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    LetterButtons(('A'..'Z').toList(), guessedLetters) { letter ->
                        guessedLetters = guessedLetters + letter
                        if (!wordToGuess.contains(letter, ignoreCase = true)) {
                            triesLeft--
                        }
                    }
                    // Placeholder for Panel 2
                    Text(text = "Panel 2", modifier = Modifier.fillMaxWidth().padding(16.dp))
                }
                MainGameScreen(
                    word = wordToGuess,
                    guessedLetters = guessedLetters,
                    triesLeft = triesLeft,
                    onRestart = {
                        guessedLetters = listOf()
                        triesLeft = maxTries
                        gameStarted = false
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                MainGameScreen(
                    word = wordToGuess,
                    guessedLetters = guessedLetters,
                    triesLeft = triesLeft,
                    onRestart = {
                        guessedLetters = listOf()
                        triesLeft = maxTries
                        gameStarted = false
                    },
                    modifier = Modifier.weight(1f)
                )
                LetterButtons(('A'..'Z').toList(), guessedLetters) { letter ->
                    guessedLetters = guessedLetters + letter
                    if (!wordToGuess.contains(letter, ignoreCase = true)) {
                        triesLeft--
                    }
                }
            }
        }
    }
}

@Composable
fun LetterButtons(letters: List<Char>, guessedLetters: List<Char>, onLetterClick: (Char) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 40.dp, bottom = 60.dp)
    ) {
        letters.chunked(7).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { letter ->
                    Button(
                        onClick = {
                            onLetterClick(letter)
                        },
                        enabled = !guessedLetters.contains(letter),
                        shape = RoundedCornerShape(4.dp), // Less rounded corners
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White,
                        ),
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f) // Ensure buttons take equal space
                    ) {
                        Text(text = letter.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun MainGameScreen(word: String, guessedLetters: List<Char>, triesLeft: Int, onRestart: () -> Unit, modifier: Modifier = Modifier) {
    val displayWord = word.map { if (guessedLetters.contains(it.lowercaseChar())) it else '_' }.joinToString(" ")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Tries left: $triesLeft", style = MaterialTheme.typography.displaySmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = displayWord, style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(36.dp))
        if (displayWord.replace(" ", "") == word) {
            Text(text = "You won!", style = MaterialTheme.typography.displayMedium)
            Button(onClick = onRestart) {
                Text(text = "Restart")
            }
        } else if (triesLeft <= 0) {
            Text(text = "Better luck next time!", style = MaterialTheme.typography.displayMedium)
            Button(onClick = onRestart) {
                Text(text = "Restart")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}