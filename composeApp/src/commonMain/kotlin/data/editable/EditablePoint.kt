package data.editable

import data.generic.Point

data class EditablePoint(
    val icon: Point.Name = Point.Name.DEFAULT,
    val location: EditableLatLng = EditableLatLng(),
    val label: String = ""
): Editable<Point> {
    override fun validate(): Boolean = location.validate()

    override fun build(): Point = Point(icon, location.build(), label)
}
