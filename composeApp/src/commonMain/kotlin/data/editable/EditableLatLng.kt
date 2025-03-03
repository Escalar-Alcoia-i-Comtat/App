package data.editable

import data.generic.LatLng

data class EditableLatLng(
    val latitude: String = "",
    val longitude: String = "",
): Editable<LatLng> {
    override fun validate(): Boolean {
        return latitude.toDoubleOrNull() != null && longitude.toDoubleOrNull() != null
    }

    override fun build(): LatLng {
        return LatLng(latitude.toDouble(), longitude.toDouble())
    }
}
