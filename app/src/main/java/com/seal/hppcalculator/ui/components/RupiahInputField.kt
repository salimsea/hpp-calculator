package com.seal.hppcalculator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seal.hppcalculator.ui.theme.CardBorderSubtle

/**
 * VisualTransformation that formats digit strings into Indonesian Rupiah format
 * with dot ('.') thousand separators without modifying underlying raw digits.
 * Example: "50000" -> "50.000", "1500000" -> "1.500.000"
 */
class RupiahVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formatted = buildString {
            val len = raw.length
            for (i in 0 until len) {
                append(raw[i])
                val remaining = len - 1 - i
                if (remaining > 0 && remaining % 3 == 0) {
                    append('.')
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val safeOffset = offset.coerceIn(0, raw.length)
                var dots = 0
                for (i in 0 until safeOffset) {
                    val remaining = raw.length - 1 - i
                    if (remaining > 0 && remaining % 3 == 0) {
                        dots++
                    }
                }
                return (safeOffset + dots).coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val safeOffset = offset.coerceIn(0, formatted.length)
                var dots = 0
                for (i in 0 until safeOffset) {
                    if (formatted[i] == '.') {
                        dots++
                    }
                }
                return (safeOffset - dots).coerceIn(0, raw.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

/**
 * Reusable Rupiah Input Field with prefix "Rp " and automatic thousand separators.
 * Raw value emitted to [onValueChange] is pure digits string for safe numerical parsing.
 */
@Composable
fun RupiahInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "0",
    supportingText: String? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    shape: Shape = RoundedCornerShape(14.dp),
    prefixText: String = "Rp ",
    prefixColor: Color = MaterialTheme.colorScheme.primary,
    colors: TextFieldColors? = null,
    textStyle: TextStyle? = null,
    maxDigits: Int = 15
) {
    // Sanitize value to pure digits so visual transformation operates predictably
    val rawDigits = remember(value) {
        if (value.endsWith(".0")) {
            value.substringBefore(".0").filter { it.isDigit() }
        } else {
            value.filter { it.isDigit() }
        }
    }

    val defaultColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = CardBorderSubtle
    )

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
            )
        }
        OutlinedTextField(
            value = rawDigits,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }
                val sanitized = if (digits.length > 1 && digits.startsWith("0")) {
                    digits.trimStart('0').ifEmpty { "0" }
                } else {
                    digits
                }
                if (sanitized.length <= maxDigits) {
                    onValueChange(sanitized)
                }
            },
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            },
            prefix = {
                Text(
                    text = prefixText,
                    fontWeight = FontWeight.Bold,
                    color = prefixColor,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 2.dp)
                )
            },
            visualTransformation = remember { RupiahVisualTransformation() },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = if (supportingText != null) {
                {
                    Text(
                        text = supportingText,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else null,
            singleLine = singleLine,
            enabled = enabled,
            readOnly = readOnly,
            shape = shape,
            textStyle = textStyle ?: MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            colors = colors ?: defaultColors,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
