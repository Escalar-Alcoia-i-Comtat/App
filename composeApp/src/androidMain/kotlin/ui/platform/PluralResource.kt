package ui.platform

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.format
import org.escalaralcoiaicomtat.android.applicationContext

@Composable
actual fun pluralResource(plural: PluralsResource, number: Int, vararg args: Any): String {
    return plural.format(number, *args).toString(applicationContext)
}
