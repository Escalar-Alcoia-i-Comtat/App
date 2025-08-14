package org.escalaralcoiaicomtat.app.data.editable

import org.escalaralcoiaicomtat.app.data.generic.Point

data class EditablePoint(
    val icon: Point.Name = Point.Name.DEFAULT,
    val location: EditableLatLng = EditableLatLng(),
    val label: String = "",
    val description: String = "",
): Editable<Point> {
    override fun validate(): Boolean = location.validate()

    override fun build(): Point = Point(
        icon = icon,
        location = location.build(),
        label = label,
        description = description.takeIf(String::isNotBlank),
    )
}
