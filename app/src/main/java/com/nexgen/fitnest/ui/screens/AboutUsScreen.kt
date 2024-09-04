package com.nexgen.fitnest.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexgen.fitnest.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    // Function to open a URL in the browser
    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    // Use a Scrollable Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF025D93)) // Setting the background color
            .verticalScroll(rememberScrollState()), // Enable vertical scrolling
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar with a back button
        TopAppBar(
            title = {
                Text(text = "About Us")
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between AppBar and content

        // Display logo at the top with increased size and circular shape
        Image(
            painter = painterResource(id = R.drawable.nexgen_ion), // Replace with your logo resource ID
            contentDescription = "Company Logo",
            modifier = Modifier
                .size(150.dp) // Adjust size as needed
                .clip(CircleShape) // Make image circular
                .background(Color.White) // Add background color
        )

        // Title text
        Text(
            text = "NexGen Coders",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp), // Adjust text style and size as needed
            color = Color.White, // Set text color to white for contrast
            fontFamily = FontFamily(Font(R.font.robotobolditalic)),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .padding(8.dp)
                .align(Alignment.Start)
        )

        // Company description text with increased text size
        Text(
            text = "At NexGen Coders, we are passionate about transforming ideas into cutting-edge mobile applications that resonate with the next generation of users. With a keen focus on innovation, we blend creativity with technical expertise to deliver mobile solutions that not only meet but exceed our clients' expectations. Our diverse team of developers, designers, and marketers work together to craft seamless user experiences and powerful digital solutions.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp), // Increased font size
            color = Color.White, // Set text color to white for contrast
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .padding(8.dp)
        )

        // Mission statement with only the heading bold
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Our Mission:\n")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                    append("To empower businesses by creating mobile applications that drive engagement, enhance user experiences, and foster growth in the digital landscape.")
                }
            },
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp), // Increased font size
            color = Color.White, // Set text color to white for contrast
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(bottom = 16.dp)
        )

        // Vision statement with only the heading bold
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Our Vision:\n")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                    append("To be a leader in mobile application development and social media marketing, setting the standard for innovation and excellence in the industry.")
                }
            },
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp), // Increased font size
            color = Color.White, // Set text color to white for contrast
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Push the social media icons to the bottom

        // Social Media Icons
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp) // Adjust padding as needed
        ) {
            IconButton(onClick = { openUrl("https://www.linkedin.com/company/nexgen-coders/") }) {
                Image(
                    painter = painterResource(id = R.drawable.inkdin), // Replace with your LinkedIn icon resource ID
                    contentDescription = "LinkedIn",
                    modifier = Modifier
                        .size(40.dp) // Adjust icon size as needed
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // Space between icons

            IconButton(onClick = { openUrl("https://www.instagram.com/nexgencoders2/") }) {
                Image(
                    painter = painterResource(id = R.drawable.iconsinstagram), // Replace with your Instagram icon resource ID
                    contentDescription = "Instagram",
                    modifier = Modifier
                        .size(40.dp) // Adjust icon size as needed
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // Space between icons

            IconButton(onClick = { openUrl("https://www.facebook.com/profile.php?id=61564873703113") }) {
                Image(
                    painter = painterResource(id = R.drawable.iconsfacebook), // Replace with your Facebook icon resource ID
                    contentDescription = "Facebook",
                    modifier = Modifier
                        .size(40.dp) // Adjust icon size as needed
                        .clip(CircleShape)
                )
            }
        }
    }
}