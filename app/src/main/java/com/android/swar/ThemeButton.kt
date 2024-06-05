package com.android.swar

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.swar.ui.theme.AppTheme
import com.android.swar.ui.theme.YellowDark
import com.android.swar.ui.theme.YellowLight

@Composable
fun ThemedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppTheme {
        val colorScheme = MaterialTheme.colorScheme
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                disabledContainerColor = YellowDark,
                disabledContentColor = YellowLight
            ),
            modifier = modifier
        ) {
            Text("Back Button")
        }
    }
}