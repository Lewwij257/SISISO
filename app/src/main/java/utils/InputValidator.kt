package utils

import android.text.Editable
import android.widget.EditText

class InputValidator {

    companion object{
        fun goodFractionEnteredCheck(string: String): Boolean {

            return string.all { char ->
                char.isDigit() || char == '.' || char == '/' || char == ' ' || char == '-'
            }

        }

        fun isValidRowsAndColumnsInput(etVariablesCount: EditText, etConstraintsCount: EditText):Boolean{
            return try {
                (etVariablesCount.text.isNotEmpty() && etConstraintsCount.text.isNotEmpty() &&
                        etVariablesCount.text.toString().toInt() in 1..16 &&
                        etConstraintsCount.text.toString().toInt() in 1..16)
            } catch (e: Exception) {
                false
            }
        }
    }
}