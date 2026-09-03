package com.spendsync.app.presentation.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

@Composable
fun AccountSwitcher(
    currentAccount: String,
    accounts: List<String>,
    onSelectAccount: (String) -> Unit,
    onAddAccount: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isAdding by remember { mutableStateOf(false) }
    var newAccountText by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        // Account pill button
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { isExpanded = !isExpanded },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = currentAccount,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Switch Account",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val visibleState = remember { androidx.compose.animation.core.MutableTransitionState(false) }
        visibleState.targetState = isExpanded

        if (visibleState.currentState || visibleState.targetState) {
            androidx.compose.ui.window.Popup(
                alignment = Alignment.TopStart,
                offset = androidx.compose.ui.unit.IntOffset(0, 120),
                onDismissRequest = {
                    isExpanded = false
                    isAdding = false
                },
                properties = PopupProperties(focusable = true)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visibleState = visibleState,
                    enter = androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(200)) + 
                            androidx.compose.animation.scaleIn(initialScale = 0.9f, transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f), animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.8f, stiffness = 400f)),
                    exit = androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(150)) + 
                           androidx.compose.animation.scaleOut(targetScale = 0.95f, transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f), animationSpec = androidx.compose.animation.core.tween(150))
                ) {
                    Surface(
                        modifier = Modifier
                            .width(240.dp)
                            .padding(8.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        shadowElevation = 16.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "ACCOUNTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )

                            accounts.forEach { account ->
                                val isSelected = account == currentAccount
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelectAccount(account)
                                            isExpanded = false
                                        }
                                        .padding(horizontal = 20.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = account,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )

                            if (isAdding) {
                                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                    OutlinedTextField(
                                        value = newAccountText,
                                        onValueChange = { newAccountText = it },
                                        placeholder = { Text("Account name...", style = MaterialTheme.typography.bodySmall) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = MaterialTheme.typography.bodySmall,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = { isAdding = false },
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Text("Cancel", fontSize = 13.sp)
                                        }
                                        Button(
                                            onClick = {
                                                if (newAccountText.isNotBlank()) {
                                                    onAddAccount(newAccountText.trim())
                                                    newAccountText = ""
                                                    isAdding = false
                                                    isExpanded = false
                                                }
                                            },
                                            enabled = newAccountText.isNotBlank(),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                                        ) {
                                            Text("Add", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isAdding = true }
                                        .padding(horizontal = 20.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Text(
                                        "Add New Account",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
