package data.generic

import escalaralcoiaicomtat.composeapp.generated.resources.Res
import escalaralcoiaicomtat.composeapp.generated.resources.ending_chain_carabiner
import escalaralcoiaicomtat.composeapp.generated.resources.ending_chain_ring
import escalaralcoiaicomtat.composeapp.generated.resources.ending_lanyard
import escalaralcoiaicomtat.composeapp.generated.resources.ending_none
import escalaralcoiaicomtat.composeapp.generated.resources.ending_piton
import escalaralcoiaicomtat.composeapp.generated.resources.ending_plate
import escalaralcoiaicomtat.composeapp.generated.resources.ending_plate_carabiner
import escalaralcoiaicomtat.composeapp.generated.resources.ending_plate_lanyard
import escalaralcoiaicomtat.composeapp.generated.resources.ending_plate_ring
import escalaralcoiaicomtat.composeapp.generated.resources.ending_rappel
import escalaralcoiaicomtat.composeapp.generated.resources.ending_walking
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@Serializable
enum class Ending(val displayName: StringResource) {
    NONE(Res.string.ending_none),
    PLATE(Res.string.ending_plate),
    PLATE_RING(Res.string.ending_plate_ring),
    PLATE_LANYARD(Res.string.ending_plate_lanyard),
    PLATE_CARABINER(Res.string.ending_plate_carabiner),
    CHAIN_RING(Res.string.ending_chain_ring),
    CHAIN_CARABINER(Res.string.ending_chain_carabiner),
    PITON(Res.string.ending_piton),
    LANYARD(Res.string.ending_lanyard),
    WALKING(Res.string.ending_walking),
    RAPPEL(Res.string.ending_rappel)
}
