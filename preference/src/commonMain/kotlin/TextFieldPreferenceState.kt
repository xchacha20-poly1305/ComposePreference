/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.zhanghai.compose.preference

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Stable
public class TextFieldPreferenceState<T>(
    public val textFieldState: TextFieldState,
    private val value: () -> T,
    private val onValueChange: (T) -> Unit,
    private val valueToText: (T) -> String,
    private val textToValue: (String) -> T?,
) {
    public val editedValue: T? by derivedStateOf { textToValue(textFieldState.text.toString()) }

    public val isValid: Boolean
        get() = editedValue != null

    public fun commit(): Boolean {
        onValueChange(editedValue ?: return false)
        return true
    }

    public fun revert() {
        textFieldState.setTextAndPlaceCursorAtEnd(valueToText(value()))
    }
}

@Composable
public fun <T> rememberTextFieldPreferenceState(
    value: T,
    onValueChange: (T) -> Unit,
    valueToText: (T) -> String,
    textToValue: (String) -> T?,
    textFieldState: TextFieldState = rememberTextFieldState(valueToText(value)),
): TextFieldPreferenceState<T> {
    // The state outlives the compositions that create it, while rememberPreferenceState() returns a
    // new MutableState instance for each composition, so read everything indirectly.
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentValueToText by rememberUpdatedState(valueToText)
    val currentTextToValue by rememberUpdatedState(textToValue)
    return remember(textFieldState) {
        TextFieldPreferenceState(
            textFieldState = textFieldState,
            value = { currentValue },
            onValueChange = { currentOnValueChange(it) },
            valueToText = { currentValueToText(it) },
            textToValue = { currentTextToValue(it) },
        )
    }
}
