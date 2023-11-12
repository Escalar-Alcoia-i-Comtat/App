package data.generic

import kotlinx.serialization.Serializable

@Serializable
enum class Ending {
    NONE,
    PLATE,
    PLATE_RING,
    PLATE_LANYARD,
    PLATE_CARABINER,
    CHAIN_RING,
    CHAIN_CARABINER,
    PITON,
    LANYARD,
    WALKING,
    RAPPEL
}
