package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.runtime.Composable
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.escalaralcoiaicomtat.app.data.Path
import org.jetbrains.compose.resources.stringResource

data class RequiredRouteMaterial(
    val nutRequired: Boolean,
    val friendRequired: Boolean,
    val lanyardRequired: Boolean,
    val nailRequired: Boolean,
    val pitonRequired: Boolean,
    val stapesRequired: Boolean
) {
    constructor(path: Path) : this(
        nutRequired = path.nutRequired,
        friendRequired = path.friendRequired,
        lanyardRequired = path.lanyardRequired,
        nailRequired = path.nailRequired,
        pitonRequired = path.pitonRequired,
        stapesRequired = path.stapesRequired
    )

    /**
     * Indicates if at least one of the required materials is true
     */
    fun isNotFalse(): Boolean {
        return nutRequired || friendRequired || lanyardRequired || nailRequired || pitonRequired || stapesRequired
    }

    @Composable
    fun text(): String {
        val amountOfTrues = listOf(
            nutRequired,
            friendRequired,
            lanyardRequired,
            nailRequired,
            pitonRequired,
            stapesRequired
        ).count { it }

        val text = when (amountOfTrues) {
            0 -> {
                // should not be possible, run isNotTrue before calling this method
                return ""
            }
            1 -> {
                when {
                    nutRequired -> stringResource(Res.string.path_required_material_nut)
                    friendRequired -> stringResource(Res.string.path_required_material_friend)
                    lanyardRequired -> stringResource(Res.string.path_required_material_lanyard)
                    nailRequired -> stringResource(Res.string.path_required_material_nail)
                    pitonRequired -> stringResource(Res.string.path_required_material_piton)
                    stapesRequired -> stringResource(Res.string.path_required_material_stapes)
                    else -> "" // should not be possible
                }
            }
            else -> {
                val materials = mutableListOf<String>()
                if (nutRequired) materials.add(stringResource(Res.string.path_required_material_nut))
                if (friendRequired) materials.add(stringResource(Res.string.path_required_material_friend))
                if (lanyardRequired) materials.add(stringResource(Res.string.path_required_material_lanyard))
                if (nailRequired) materials.add(stringResource(Res.string.path_required_material_nail))
                if (pitonRequired) materials.add(stringResource(Res.string.path_required_material_piton))
                if (stapesRequired) materials.add(stringResource(Res.string.path_required_material_stapes))

                materials.joinToString(separator = ", ")
            }
        }

        return stringResource(Res.string.path_required_material_message, text)
    }
}
