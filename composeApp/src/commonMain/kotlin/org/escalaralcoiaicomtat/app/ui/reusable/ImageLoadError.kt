package org.escalaralcoiaicomtat.app.ui.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ImageLoadError(
    throwable: Throwable,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = stringResource(Res.string.error_image_load_title),
                modifier = Modifier.padding(bottom = 8.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = stringResource(Res.string.error_image_load_title),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = throwable::class.simpleName ?: stringResource(Res.string.error_image_unknown),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelMedium,
            )
            throwable.message?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun ImageLoadError_Preview() {
    ImageLoadError(
        throwable = IllegalArgumentException("This is a test exception"),
        modifier = Modifier.size(300.dp, 150.dp),
    )
}
