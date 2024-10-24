package data

sealed class EDataType(val id: Long) {
    class Area(id: Long) : EDataType(id)
    class Zone(id: Long) : EDataType(id)
    class Sector(id: Long) : EDataType(id)
    class Path(id: Long, val sectorId: Long) : EDataType(id)
}
