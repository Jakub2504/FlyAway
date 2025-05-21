package com.example.flyaway.ui.transitions.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.flyaway.domain.model.Reservation


@Composable
fun ReservationCard(
    item: Reservation,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {

            /* ---------- thumbnail ---------- */
            Image(
                painter = rememberAsyncImagePainter(item.hotel.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
            )

            /* ---------- info ---------- */
            Column(
                Modifier
                    .padding(12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.hotel?.name ?: "Unknown hotel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.room?.roomType?.replaceFirstChar { it.uppercase() } ?: "",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${item.startDate} → ${item.endDate}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = (item.hotel?.rating ?: 0).toString(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "€${item.room?.price ?: 0}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            /* ---------- delete ---------- */
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}