package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ShopKartAmber

@Composable
fun RatingBar(
    rating: Double,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
    starColor: Color = ShopKartAmber,
    onRatingChanged: ((Int) -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        for (i in 1..maxStars) {
            val isFull = rating >= i
            val isHalf = !isFull && rating >= (i - 0.5)

            val icon = when {
                isFull -> Icons.Default.Star
                isHalf -> Icons.Default.StarHalf
                else -> Icons.Default.StarBorder
            }

            val iconModifier = if (onRatingChanged != null) {
                Modifier
                    .size(starSize)
                    .clickable { onRatingChanged(i) }
            } else {
                Modifier.size(starSize)
            }

            Icon(
                imageVector = icon,
                contentDescription = "Rating $i of $maxStars",
                tint = if (isFull || isHalf) starColor else Color.LightGray,
                modifier = iconModifier
            )
        }
    }
}
