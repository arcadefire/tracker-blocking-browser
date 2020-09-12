package org.angmarc.tracker_blocker_browser.browser.composables

import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.angmarc.tracker_blocker_browser.R

private val TranslationY = FloatPropKey()

private enum class AddressBarIcon { VISIBLE, HIDDEN }

private val definition = transitionDefinition<AddressBarIcon> {
    state(AddressBarIcon.HIDDEN) {
        this[TranslationY] = -42f
    }
    state(AddressBarIcon.VISIBLE) {
        this[TranslationY] = 0f
    }
    transition(
        fromState = AddressBarIcon.VISIBLE,
        toState = AddressBarIcon.HIDDEN
    ) {
        TranslationY using tween(
            easing = FastOutSlowInEasing,
            durationMillis = 500
        )
    }
    transition(
        fromState = AddressBarIcon.HIDDEN,
        toState = AddressBarIcon.VISIBLE
    ) {
        TranslationY using tween(
            easing = FastOutSlowInEasing,
            durationMillis = 500
        )
    }
}

@Composable
fun AddressBarIcon(
    modifier: Modifier,
    isBlockingSuspended: Boolean
) {
    val transitionState = transition(
        definition = definition,
        initState = if (isBlockingSuspended) AddressBarIcon.HIDDEN else AddressBarIcon.VISIBLE,
        toState = if (isBlockingSuspended) AddressBarIcon.VISIBLE else AddressBarIcon.HIDDEN
    )
    Icon(modifier, transitionState)
}

@Composable
private fun Icon(modifier: Modifier, transitionState: TransitionState) {
    Image(
        asset = vectorResource(id = R.drawable.ic_remove_circle_outline_24px),
        colorFilter = ColorFilter.tint(Color.White),
        modifier = modifier.absoluteOffset(y = transitionState[TranslationY].dp)
    )
}