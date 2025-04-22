package ui.drag

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.channels.Channel

@Composable
fun DraggableLazyColumn(
    itemsSize: Int,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    userDragEnabled: Boolean = true,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    content: LazyListScope.(modifier: (idx: Int) -> Modifier) -> Unit
) {
    var draggingItemIndex: Int? by remember { mutableStateOf(null) }
    var delta: Float by remember { mutableFloatStateOf(0f) }
    var draggingItem: LazyListItemInfo? by remember { mutableStateOf(null) }

    val scrollChannel = Channel<Float>()
    LaunchedEffect(state) {
        while (true) {
            val diff = scrollChannel.receive()
            state.scrollBy(diff)
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .pointerInput(state, userDragEnabled) {
                if (!userDragEnabled) {
                    draggingItem = null
                    draggingItemIndex = null
                    delta = 0f
                    return@pointerInput
                }

                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        state.layoutInfo.visibleItemsInfo
                            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
                            ?.also { itemInfo ->
                                (itemInfo.contentType as? DraggableItem)?.let { draggableItem ->
                                    draggingItem = itemInfo
                                    draggingItemIndex = draggableItem.index
                                }
                            }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        delta += dragAmount.y

                        val currentDraggingItemIndex = draggingItemIndex ?: return@detectDragGesturesAfterLongPress
                        val currentDraggingItem = draggingItem ?: return@detectDragGesturesAfterLongPress

                        val startOffset = currentDraggingItem.offset + delta
                        val endOffset = currentDraggingItem.offset + currentDraggingItem.size + delta
                        val middleOffset = startOffset + (endOffset - startOffset) / 2

                        val targetItem =
                            state.layoutInfo.visibleItemsInfo.find { item ->
                                middleOffset.toInt() in item.offset..item.offset + item.size &&
                                        currentDraggingItem.index != item.index &&
                                        item.contentType is DraggableItem
                            }

                        if (targetItem != null) {
                            val targetIndex = (targetItem.contentType as DraggableItem).index
                            onMove(currentDraggingItemIndex, targetIndex)
                            draggingItemIndex = targetIndex
                            delta += currentDraggingItem.offset - targetItem.offset
                            draggingItem = targetItem
                        } else {
                            val startOffsetToTop = startOffset - state.layoutInfo.viewportStartOffset
                            val endOffsetToBottom = endOffset - state.layoutInfo.viewportEndOffset
                            val scroll = when {
                                startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
                                endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(0f)
                                else -> 0f
                            }
                            val canScrollDown =
                                currentDraggingItemIndex != itemsSize - 1 && endOffsetToBottom > 0
                            val canScrollUp = currentDraggingItemIndex != 0 && startOffsetToTop < 0
                            if (scroll != 0f && (canScrollUp || canScrollDown)) {
                                scrollChannel.trySend(scroll)
                            }
                        }
                    },
                    onDragEnd = {
                        draggingItem = null
                        draggingItemIndex = null
                        delta = 0f
                    },
                    onDragCancel = {
                        draggingItem = null
                        draggingItemIndex = null
                        delta = 0f
                    },
                )
            }
            .then(modifier),
        state,
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalAlignment,
        flingBehavior,
        userScrollEnabled,
    ) {
        content { index ->
            if (draggingItemIndex == index) {
                Modifier
                    .zIndex(1f)
                    .graphicsLayer { translationY = delta }
            } else {
                Modifier
            }
        }
    }
}
