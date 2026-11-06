package com.example.ac2_laberinto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VictoryScreen(
    onBackToMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101D35)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "VICTORY!",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF80FFB0)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = onBackToMenu,
                modifier = Modifier.width(220.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF38B879)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "BACK TO MENU",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}
