package org.escalaralcoiaicomtat.app.ui.modifier

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
        with(animatedContentScope) {
            sharedElement(
                rememberSharedContentState(key),
                animatedVisibilityScope = animatedContentScope,
            ).animateEnterExit(
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
            )
        }
    }
}
