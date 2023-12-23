package search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import database.Sector

sealed class KidsAptFilter(value: Boolean) : Filter<Sector>(value) {
    data object KidsApt : KidsAptFilter(true)
    data object KidsNotApt : KidsAptFilter(false)

    override val valueFalse: Filter<Sector> = KidsNotApt
    override val valueTrue: Filter<Sector> = KidsApt

    @Composable
    override fun Label() {
        // TODO : Translations
        Text("Kids Apt")
    }

    override fun show(obj: Sector): Boolean = obj.kidsApt
}
