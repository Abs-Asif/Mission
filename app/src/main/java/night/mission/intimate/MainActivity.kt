package night.mission.intimate

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var isAuthenticated by remember { mutableStateOf(false) }

                    if (isAuthenticated) {
                        MissionApp(onExit = { finish() })
                    } else {
                        PasswordScreen(
                            onPasswordCorrect = { isAuthenticated = true }
                        )
                    }
                }
            }
        }
    }
}

object PasswordValidator {
    const val REQUIRED_PASSWORD = "sex"

    fun validate(input: String): Boolean {
        return input == REQUIRED_PASSWORD
    }
}

@Composable
fun PasswordScreen(onPasswordCorrect: () -> Unit) {
    var passwordInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val handleLogin = {
        if (PasswordValidator.validate(passwordInput)) {
            onPasswordCorrect()
        } else {
            showError = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = {
                passwordInput = it
                if (showError) showError = false
            },
            label = { Text("Password") },
            singleLine = true,
            isError = showError,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { handleLogin() }),
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Incorrect password",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = handleLogin,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Unlock", fontSize = 18.sp)
        }
    }
}

@Composable
fun MissionApp(onExit: () -> Unit) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    var imageList by remember { mutableStateOf<List<String>>(emptyList()) }
    val ratings = remember { mutableStateMapOf<String, Int>() }
    var currentImage by remember { mutableStateOf<String?>(null) }

    // Intercept back button to show exit confirmation dialog
    BackHandler {
        showExitDialog = true
    }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("mission_ratings", Context.MODE_PRIVATE)
        val files = try {
            context.assets.list("positions")?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        imageList = files

        // Load saved ratings
        files.forEach { file ->
            val savedRating = prefs.getInt(file, 0)
            ratings[file] = savedRating
        }

        if (files.isNotEmpty()) {
            currentImage = WeightCalculator.pickWeightedRandomImage(files, ratings, null)
        }
    }

    val onTossClicked = {
        if (imageList.isNotEmpty()) {
            currentImage = WeightCalculator.pickWeightedRandomImage(imageList, ratings, currentImage)
        }
    }

    val onRatingSelected = { newRating: Int ->
        currentImage?.let { img ->
            ratings[img] = newRating
            val prefs = context.getSharedPreferences("mission_ratings", Context.MODE_PRIVATE)
            prefs.edit().putInt(img, newRating).apply()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Part 1: Image space taking exactly half the screen with white background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            currentImage?.let { imgName ->
                AssetImageView(
                    assetPath = "positions/$imgName",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Part 2 & 3: Bottom half containing Orange 5-Star Rating Bar and Toss Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Part 3: 5-Star Button Pallet (Orange)
            val currentRating = currentImage?.let { ratings[it] } ?: 0
            StarRatingBar(
                rating = currentRating,
                onRatingChanged = { selectedStar ->
                    onRatingSelected(selectedStar)
                }
            )

            // Part 2: Huge Toss Button
            Button(
                onClick = onTossClicked,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(80.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63) // Vibrant Pinkish Color
                )
            ) {
                Text(
                    text = "TOSS",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Exit Mission") },
            text = { Text(text = "Are you sure you want to exit?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onExit()
                    }
                ) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AssetImageView(assetPath: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap = remember(assetPath) {
        try {
            context.assets.open(assetPath).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun StarRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isSelected = i <= rating
            IconButton(
                onClick = { onRatingChanged(i) },
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Star $i",
                    tint = if (isSelected) Color(0xFFFF9800) else Color(0xFFB0BEC5), // Orange when active
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
