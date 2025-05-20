package org.escalaralcoiaicomtat.app.data.generic

import androidx.compose.runtime.Composable
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed class SafeType(
    val amount: UInt?,
    val uncountableStringRes: StringResource,
    val countableStringRes: StringResource,
) {
    class Parabolts(amount: UInt?) :
        SafeType(amount, Res.string.path_safes_parabolts, Res.string.path_safes_parabolts_count)

    class Burils(amount: UInt?) :
        SafeType(amount, Res.string.path_safes_burils, Res.string.path_safes_burils_count)

    class Pitons(amount: UInt?) :
        SafeType(amount, Res.string.path_safes_pitons, Res.string.path_safes_pitons_count)

    class Spits(amount: UInt?) :
        SafeType(amount, Res.string.path_safes_spits, Res.string.path_safes_spits_count)

    class Tensors(amount: UInt?) :
        SafeType(amount, Res.string.path_safes_tensors, Res.string.path_safes_tensors_count)

    val isNotNull get() = amount != null

    @Composable
    fun text(): String? = amount?.let {
        if (amount == 0u) stringResource(uncountableStringRes)
        else stringResource(countableStringRes, amount)
    }
}
