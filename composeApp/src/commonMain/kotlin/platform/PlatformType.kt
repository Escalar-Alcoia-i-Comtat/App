package platform

import io.github.alexzhirkevich.cupertino.adaptive.Theme

enum class PlatformType(val theme: Theme) {
    Android(Theme.Material3),
    Desktop(Theme.Material3),
    IOS(Theme.Cupertino)
}
