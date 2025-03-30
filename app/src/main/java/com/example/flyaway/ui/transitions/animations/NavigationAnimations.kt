package com.example.flyaway.ui.transitions.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

private const val ANIMATION_DURATION = 300

@ExperimentalAnimationApi
fun enterTransition() = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(ANIMATION_DURATION)
) + fadeIn(animationSpec = tween(ANIMATION_DURATION))

@ExperimentalAnimationApi
fun exitTransition() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(ANIMATION_DURATION)
) + fadeOut(animationSpec = tween(ANIMATION_DURATION))

@ExperimentalAnimationApi
fun popEnterTransition() = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(ANIMATION_DURATION)
) + fadeIn(animationSpec = tween(ANIMATION_DURATION))

@ExperimentalAnimationApi
fun popExitTransition() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(ANIMATION_DURATION)
) + fadeOut(animationSpec = tween(ANIMATION_DURATION))

@ExperimentalAnimationApi
fun splashExitTransition() = fadeOut(animationSpec = tween(ANIMATION_DURATION)) 