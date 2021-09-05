package com.russhwolf.soluna.android.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmationDialog(
    confirmButtonContent: @Composable RowScope.() -> Unit,
    dismissButtonContent: @Composable RowScope.() -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                content = confirmButtonContent
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                content = dismissButtonContent
            )
        },
        title = content
    )
}
