package pl.domain.application.petreco.data.utils

import android.app.Activity

fun Activity.lockOrientation(orientation: Int) {
    requestedOrientation = orientation
}

fun Activity.unlockOrientation() {
    requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}
