package org.escalaralcoiaicomtat.app.ui.modifier

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.escalaralcoiaicomtat.app.ui.composition.LocalAnimatedContentScope
import org.escalaralcoiaicomtat.app.ui.composition.LocalSharedTransitionScope
import org.escalaralcoiaicomtat.app.utils.currentOrThrow

@Composable
@ExperimentalSharedTransitionApi
fun Modifier.sharedElement(key: String): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.currentOrThrow
    val animatedContentScope = LocalAnimatedContentScope.currentOrThrow

    return with(sharedTransitionScope) {
        sharedElement(
            rememberSharedContentState(key),
            animatedVisibilityScope = animatedContentScope,
        )
    }
}
