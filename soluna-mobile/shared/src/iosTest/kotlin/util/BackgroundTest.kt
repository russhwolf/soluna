package com.russhwolf.soluna.mobile.util

import platform.CoreFoundation.CFRunLoopRunInMode
import platform.CoreFoundation.kCFRunLoopDefaultMode
import platform.CoreFoundation.kCFRunLoopRunHandledSource

actual fun blockUntilIdle() {
    // Adapted from https://stackoverflow.com/a/16975344/2565340
    @Suppress("ControlFlowWithEmptyBody")
    while (CFRunLoopRunInMode(kCFRunLoopDefaultMode, 0.0, true) == kCFRunLoopRunHandledSource) {
    }
}
