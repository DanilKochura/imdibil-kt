package com.example.imdibil.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imdibil.ui.theme.Gold

@Preview
@Composable
fun GoldButton(text: String = "Кнопка", onClick: () -> Unit = {})
{
    Button(onClick = onClick, colors = ButtonDefaults.buttonColors(Gold), shape = RoundedCornerShape(50.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp) ) {
        Text(text = text)
    }
}


@Preview
@Composable
fun GoldText(text: String = "Текст", size: TextUnit = 14.sp, fontWheight: Int = 400)
{
    Text(text = text, color = Gold, fontSize = size, fontWeight = FontWeight(fontWheight))
}