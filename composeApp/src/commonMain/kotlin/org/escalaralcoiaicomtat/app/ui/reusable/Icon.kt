package org.escalaralcoiaicomtat.app.ui.reusable

import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

class Icon private constructor(
    private val imageVector: ImageVector?,
    private val painter: Painter?
) {
    constructor(imageVector: ImageVector) : this(imageVector, null)
    constructor(painter: Painter?) : this(null, painter)

    @Composable
    fun Content(
        contentDescription: String?,
        modifier: Modifier = Modifier,
        tint: Color = LocalContentColor.current
    ) {
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint
            )
        } else if (painter != null) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = modifier
            )
        }
    }
}

@Composable
fun Icon(
    icon: Icon,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    icon.Content(contentDescription, modifier, tint)
}

val Painter.icon: Icon get() = Icon(this)
