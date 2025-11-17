package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.escalaralcoiaicomtat.app.data.Path
import org.jetbrains.compose.resources.stringResource

/**
 * - `null` indicates that the safe type is not present on the route.
 * - `0` indicates an unknown amount, but flags type.
 * Other amount indicates the actual amount of safes of that type.
 */
class SafesCount(
    val stringCount: UInt? = null,

    val parabolt: SafeType,
    val buril: SafeType,
    val piton: SafeType,
    val spit: SafeType,
    val tensor: SafeType,
) {
    constructor(path: Path) : this(
        path.stringCount,
        path.paraboltCount,
        path.burilCount,
        path.pitonCount,
        path.spitCount,
        path.tensorCount
    )

    constructor(
        stringCount: UInt? = null,
        paraboltCount: UInt? = null,
        burilCount: UInt? = null,
        pitonCount: UInt? = null,
        spitCount: UInt? = null,
        tensorCount: UInt? = null,
    ): this(
        stringCount,
        SafeType.Parabolts(paraboltCount),
        SafeType.Burils(burilCount),
        SafeType.Pitons(pitonCount),
        SafeType.Spits(spitCount),
        SafeType.Tensors(tensorCount),
    )

    private val all get() = listOf(parabolt, buril, piton, spit, tensor)

    val isNotNull get() = stringCount != null || all.any { it.isNotNull }

    @Composable
    fun toAnnotatedString(): AnnotatedString? {
        val types = all.filter { it.isNotNull }
        return if (types.isNotEmpty()) buildAnnotatedString {
            // If there's at least one type, enable dialog
            val (before, after) = stringResource(Res.string.path_safes_types).let {
                it.substringBefore("%1\$s") to it.substringAfter("%1\$s")
            }
            append(before)
            if (types.size == 1) withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                // Only one type, just show it
                append(types.first().text()!!)
            } else {
                // Multiple types, show them separated by commas and finish with "and"
                val butLast = types.dropLast(1)
                for (type in butLast) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(type.text()!!)
                    }
                    if (butLast.last() != type) append(", ")
                }
                append(' ')
                append(stringResource(Res.string.path_safes_and))
                append(' ')
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(types.last().text()!!)
                }
            }
            append(after)
        } else {
            // If there are no types (only string count), do not show any dialog
            null
        }
    }
}
