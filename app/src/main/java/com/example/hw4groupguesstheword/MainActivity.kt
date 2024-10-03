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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hw4groupguesstheword.ui.theme.HW4GroupGuessTheWordTheme
import kotlinx.coroutines.delay

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
fun FlowerDisplay(petalsRemaining: Int) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        for (i in 0 until petalsRemaining) {
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            ) {
                rotate(degrees = i * (360f / 6)) {
                    drawCircle(
                        color = Color.Magenta,
                        radius = 40f,
                        center = center.copy(y = center.y - 80f)
                    )
                }
            }
        }
        Text(
            text = "Flower",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun GuessTheWord() {
    val windowInfo = calculateCurrentWindowInfo()
    val wordToGuess = "apple"
    val maxTries = 6

    var guessedLetters by rememberSaveable { mutableStateOf(listOf<Char>()) }
    var triesLeft by rememberSaveable { mutableIntStateOf(maxTries) }
    var gameStarted by rememberSaveable { mutableStateOf(false) }
    var hintsUsed by rememberSaveable { mutableStateOf(0) }
    var snackbarMessage by remember { mutableStateOf("") }

    val wordsWithHints = mapOf(
        "APPLE" to "A fruit that keeps the doctor away."
    )

    val onHintClick: () -> Unit = {
        when (hintsUsed) {
            0 -> {
                snackbarMessage = wordsWithHints[wordToGuess.uppercase()] ?: "No hint available."
                hintsUsed++
            }
            1 -> {
                val remainingLetters = ('A'..'Z').filter { it !in guessedLetters && it !in wordToGuess.uppercase() }
                val lettersToDisable = remainingLetters.shuffled().take(remainingLetters.size / 2)
                guessedLetters = guessedLetters + lettersToDisable
                triesLeft--
                hintsUsed++
                snackbarMessage = "Half of the incorrect letters have been disabled."
            }
            2 -> {
                val vowels = wordToGuess.filter { it.uppercaseChar() in "AEIOU" }.toSet()
                guessedLetters = guessedLetters + vowels
                triesLeft--
                hintsUsed++
                snackbarMessage = "All vowels in the word have been revealed."
            }
        }
    }

    if (!gameStarted) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = { gameStarted = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White,
                ),
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
                    LetterButtons(('A'..'Z').toList(), guessedLetters.map { it.uppercaseChar() }) { letter ->
                        if (letter !in guessedLetters) {
                            guessedLetters = guessedLetters + letter
                            if (!wordToGuess.contains(letter, ignoreCase = true)) {
                                triesLeft--
                            }
                        }
                    }
                    FlowerDisplay(triesLeft)
                    HintButton(hintsUsed, onHintClick)
                }
                MainGameScreen(
                    word = wordToGuess,
                    guessedLetters = guessedLetters,
                    triesLeft = triesLeft,
                    onRestart = {
                        guessedLetters = listOf()
                        triesLeft = maxTries
                        gameStarted = false
                        hintsUsed = 0
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
                        hintsUsed = 0
                    },
                    modifier = Modifier.weight(1f)
                )
                FlowerDisplay(triesLeft)
                LetterButtons(('A'..'Z').toList(), guessedLetters.map { it.uppercaseChar() }) { letter ->
                    if (letter !in guessedLetters) {
                        guessedLetters = guessedLetters + letter
                        if (!wordToGuess.contains(letter, ignoreCase = true)) {
                            triesLeft--
                        }
                    }
                }
                HintButton(hintsUsed, onHintClick)
            }
        }

        if (snackbarMessage.isNotEmpty()) {
            Snackbar(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(snackbarMessage)
            }
            LaunchedEffect(snackbarMessage) {
                delay(3000)
                snackbarMessage = ""
            }
        }
    }
}

@Composable
fun HintButton(hintsUsed: Int, onHintClick: () -> Unit) {
    Button(
        onClick = onHintClick,
        enabled = hintsUsed < 3,
        modifier = Modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray,
            contentColor = Color.White,
        )
    ) {
        Text("It is a fruit")
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
    val displayWord = word.map { wordChar ->
        if (guessedLetters.any { it.equals(wordChar, ignoreCase = true) }) wordChar else '_'
    }.joinToString(" ")

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