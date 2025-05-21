package com.example.flyaway.ui.transitions.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomImageCarouselWithControls(images: List<String>, onDismiss: () -> Unit) {
    var currentIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (images.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(images[currentIndex]),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            currentIndex = (currentIndex - 1 + images.size) % images.size
                        }) {
                            Text("◀️ Prev")
                        }
                        Text("${currentIndex + 1} / ${images.size}")
                        TextButton(onClick = {
                            currentIndex = (currentIndex + 1) % images.size
                        }) {
                            Text("Next ▶️")
                        }
                    }
                }
            }
        }
    )
}