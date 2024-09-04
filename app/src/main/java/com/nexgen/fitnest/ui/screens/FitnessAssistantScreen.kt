package com.nexgen.fitnest.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.ai.client.generativeai.GenerativeModel
import com.nexgen.fitnest.data.models.Message
import com.nexgen.fitnest.ui.components.MessageCard
import com.nexgen.fitnest.data.models.MessageType
import com.nexgen.fitnest.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessAssistantScreen(navController: NavHostController) {
    var prompt by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var loading by remember { mutableStateOf(false) }
    var showDefaultPrompts by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val customFontFamily = FontFamily(Font(R.font.robotomedium))
    val buttonHeight = 80.dp
    val fontSize = 18.sp
    val gradientColors = listOf(
        Color(0xFF025D93),
        Color(0xFF024974),
        Color(0xFF011C2D)
    )

    val defaultPrompts = listOf(
        "How can I improve my cardio?",
        "What are the best exercises for strength training?",
        "How do I build a balanced diet?",
        "Can you recommend a workout plan for beginners?"
    )

    // Create a LazyListState for controlling scrolling
    val listState = rememberLazyListState()

    // State to control button click animation
    var isClicked by remember { mutableStateOf(false) }

    // Animation scale based on click state
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.2f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Fitness Assistant",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    "Ask any fitness-related question and get personalized advice.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = prompt,
                    onValueChange = {
                        prompt = it
                        showDefaultPrompts = it.isEmpty()
                    },
                    label = { Text("Ask a question", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = TextStyle(fontFamily = customFontFamily, fontSize = fontSize)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        isClicked = true
                        coroutineScope.launch {
                            delay(300) // Short delay to allow animation to complete
                            isClicked = false
                            loading = true
                            val userMessage = Message(text = prompt, type = MessageType.USER)
                            messages = messages + userMessage
                            try {
                                val generativeModel = GenerativeModel(
                                    modelName = "gemini-1.5-flash",
                                    apiKey = "AIzaSyBcklilewKoMufw_r3zwlFgDqAzuUbujvs"
                                )
                                val response = generativeModel.generateContent(prompt)
                                // Remove '*' characters from response text
                                val filteredText = response.text?.replace("*", "") ?: "No response"
                                val aiMessage = Message(text = filteredText, type = MessageType.AI)
                                messages = messages + aiMessage
                            } catch (e: Exception) {
                                val aiMessage = Message(text = "Error: ${e.message}", type = MessageType.AI)
                                messages = messages + aiMessage
                            } finally {
                                loading = false
                            }
                            prompt = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD9D9D9),
                        contentColor = Color(0xFF025D93)
                    ),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp)
                        .height(buttonHeight)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .scale(scale) // Apply the animated scale
                ) {
                    Text(
                        text = "Submit",
                        fontSize = fontSize,
                        fontFamily = customFontFamily
                    )
                }
            }

            if (showDefaultPrompts) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    defaultPrompts.forEach { promptText ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    prompt = promptText
                                    showDefaultPrompts = false
                                    coroutineScope.launch {
                                        loading = true
                                        val userMessage =
                                            Message(text = prompt, type = MessageType.USER)
                                        messages = messages + userMessage
                                        try {
                                            val generativeModel = GenerativeModel(
                                                modelName = "gemini-1.5-flash",
                                                apiKey = "AIzaSyBcklilewKoMufw_r3zwlFgDqAzuUbujvs"
                                            )
                                            val response = generativeModel.generateContent(prompt)
                                            // Remove '*' characters from response text
                                            val filteredText =
                                                response.text?.replace("*", "") ?: "No response"
                                            val aiMessage = Message(
                                                text = filteredText,
                                                type = MessageType.AI
                                            )
                                            messages = messages + aiMessage
                                        } catch (e: Exception) {
                                            val aiMessage = Message(
                                                text = "Error: ${e.message}",
                                                type = MessageType.AI
                                            )
                                            messages = messages + aiMessage
                                        } finally {
                                            loading = false
                                        }
                                        prompt = ""
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF024974)),
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Text(
                                text = promptText,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // LazyColumn to display messages, user messages first
            LazyColumn(
                state = listState,  // Use the LazyListState here
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(messages) { message ->
                    MessageCard(message)
                }
            }

            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }

    // LaunchedEffect to scroll to the last message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
}