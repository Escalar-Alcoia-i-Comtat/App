package org.escalaralcoiaicomtat.app.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import escalaralcoiaicomtat.composeapp.generated.resources.*
import org.escalaralcoiaicomtat.app.data.Sector
import org.jetbrains.compose.resources.stringResource

sealed class KidsAptFilter(value: Boolean) : Filter<Sector>(value) {
    data object KidsApt : KidsAptFilter(true)
    data object KidsNotApt : KidsAptFilter(false)

    override val valueFalse: Filter<Sector> = KidsNotApt
    override val valueTrue: Filter<Sector> = KidsApt

    @Composable
    override fun Label() {
        Text(stringResource(Res.string.search_filter_kids_apt))
    }

    override fun show(obj: Sector): Boolean = obj.kidsApt
}
