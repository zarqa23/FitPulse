package com.nexgen.fitnest.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyPolicyContent() {
    // Privacy Policy content with sample text
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Effective Date: September 1, 2024",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "1. Introduction",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Welcome to our app! Your privacy is very important to us. This policy explains how we handle your information in a way that keeps you informed and protected. Please take a moment to read through it.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "2. Information We Collect",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "We collect information that helps us provide you with a better experience. This includes basic details you provide and how you interact with the app. We do this to improve our services and ensure a smooth user experience.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "3. How We Use Your Information",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Your information helps us understand how you use our app and lets us personalize your experience. We use it to keep you informed, improve our services, and ensure the app works as expected.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "4. Sharing Your Information",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "We value your privacy and only share your information in certain situations, such as complying with legal requirements or working with trusted partners who help us run the app effectively.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "5. Security Measures",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "We take reasonable steps to protect your information and use secure methods to store and handle it. While no method is completely foolproof, we are committed to keeping your data safe.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "6. Changes to This Policy",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "We may update this policy occasionally. Any changes will be posted on this page, and we encourage you to review it regularly to stay informed about how we protect your information.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "7. Contact Us",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "If you have any questions or concerns about this policy, feel free to contact us at: \n" +
                    "Email: nexgencoders@outlook.com \n" +
                    "Address: Johar Town, Lahore Pakistan",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}