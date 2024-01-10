package ui.reusable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.reusable.form.FormDropdown

object IntroPage {
    abstract class Action {
        abstract val text: @Composable () -> String
        open val icon: (@Composable () -> Icon)? = null

        abstract fun onClick()
    }

    abstract class Options <K: Any> (
        open val defaultIndex: Int = 0
    ) {
        /**
         * The set of options to display to the user.
         */
        abstract val values: Map<K, String>

        abstract val label: @Composable () -> String

        /**
         * Gets called whenever the user selects one of the options. The UI is updated accordingly
         * automatically if `true` is returned. Otherwise the UI does nothing.
         * @param key The key from [values] of the selected option
         */
        abstract fun onSelected(key: K): Boolean
    }
}

@Composable
fun <T: Any> IntroPage(
    icon: Icon,
    title: String,
    message: String,
    iconColor: Color = LocalContentColor.current,
    action: IntroPage.Action? = null,
    options: IntroPage.Options<T>? = null
) {
    // Icon size: 96x96

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                // Restrict width so that big displays do not use whole span
                .widthIn(max = 600.dp)
                .fillMaxSize()
        ) {
            Icon(
                icon = icon,
                contentDescription = title,
                modifier = Modifier
                    .padding(top = 156.dp, bottom = 36.dp)
                    .size(96.dp),
                tint = iconColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            )

            action?.let {
                OutlinedButton(onClick = action::onClick) {
                    action.icon?.let {
                        val actionIcon = it()
                        actionIcon.Content(contentDescription = action.text())
                    }
                    Text(action.text())
                }
            }

            options?.let { opt ->
                val values = remember { opt.values.toList() }
                var index by remember { mutableIntStateOf(opt.defaultIndex) }

                FormDropdown(
                    selection = values[index],
                    onSelectionChanged = {
                        if (opt.onSelected(it.first)) {
                            index = values.indexOf(it)
                        }
                    },
                    options = values,
                    label = opt.label(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp, top = 4.dp),
                    toString = { it.second }
                )
            }
        }
    }
}
