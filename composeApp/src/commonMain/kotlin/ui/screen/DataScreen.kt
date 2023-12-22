package ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import data.model.DataTypeWithDisplayName
import data.model.DataTypeWithImage
import ui.list.DataCard
import ui.model.DataScreenModel

abstract class DataScreen<Parent : DataTypeWithImage, Children : DataTypeWithDisplayName>(
    val id: Long,
    depth: Int,
    private val modelFactory: () -> DataScreenModel<Parent, Children>,
    private val subScreenFactory: ((id: Long) -> Screen)?
) : DepthScreen(depth) {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val model = rememberScreenModel { modelFactory() }

        val parentState: Parent? by model.parent.collectAsState(null)
        val childrenState: List<Children>? by model.children.collectAsState(null)
        val notFound: Boolean by model.notFound.collectAsState(false)

        LaunchedEffect(id) {
            model.load(id)
        }

        LaunchedEffect(notFound) {
            if (notFound) {
                navigator?.pop()
            }
        }

        AnimatedContent(
            targetState = parentState,
            transitionSpec = {
                val enter = if (initialState == null) {
                    slideInHorizontally { it }
                } else {
                    fadeIn()
                }
                val exit = if (targetState == null) {
                    slideOutHorizontally { -it }
                } else {
                    fadeOut()
                }
                enter togetherWith exit
            }
        ) { parent ->
            if (parent == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ContentView(parent, childrenState)
            }
        }
    }

    @Composable
    open fun ContentView(parentState: Parent, childrenState: List<Children>?) {
        val navigator = LocalNavigator.current

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(childrenState ?: emptyList()) { child ->
                if (child is DataTypeWithImage) {
                    DataCard(
                        item = child,
                        imageHeight = 200.dp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        val screen = subScreenFactory?.let { it(child.id) }
                        screen?.let { navigator?.push(screen) }
                    }
                }
            }
        }
    }
}
