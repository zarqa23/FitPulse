package com.nexgen.fitnest.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nexgen.fitnest.data.models.Message
import com.nexgen.fitnest.data.models.MessageType

@Composable
fun MessageCard(message: Message) {
    val cardColor = when (message.type) {
        MessageType.USER -> Color(0xFF025D93) // User message color matching theme
        MessageType.AI -> Color(0xFF011C2D) // AI response color matching theme
    }
    val textColor = when (message.type) {
        MessageType.USER -> Color.White
        MessageType.AI -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Text(
            text = message.text,
            color = textColor,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
    }
}