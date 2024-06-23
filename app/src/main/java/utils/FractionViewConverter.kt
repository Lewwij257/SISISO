package utils
import kotlin.math.pow
class FractionViewConverter {

    companion object{

        fun decimalToOrdinary(decimalFraction: String): String {
            val isNegative = decimalFraction.startsWith('-')
            var decimal = decimalFraction
            if (isNegative) decimal = decimal.drop(1)
            val (wholePart, fractionalPart) = if (decimal.contains('.')) {
                val (whole, fractional) = decimal.split('.')
                whole.toInt() to fractional.toInt()
            }
            else if (decimal.contains('/')){
                val (whole, fractional) = decimal.split('/')
                whole.toInt() to fractional.toInt()
            }
            else{
                decimal.toInt() to 0
            }

            val denominator = 10.0.pow(fractionalPart.toString().length).toInt()
            val numerator = (wholePart * denominator) + fractionalPart
            val gcd = greatestCommonDivisor(numerator, denominator)
            return if (isNegative) '-' + (numerator / gcd).toString() + '/' + (denominator / gcd).toString() else (numerator / gcd).toString() + '/' + (denominator / gcd).toString()
        }

        private fun greatestCommonDivisor(a: Int, b: Int): Int {
            var x = a
            var y = b
            while (y != 0) {
                val temp = y
                y = x % y
                x = temp
            }
            return x
        }

        fun ordinaryToDecimal(ordinaryFraction: String): String{
            val (numerator, denominator) = ordinaryFraction.split('/')
            return (numerator.toInt()/denominator.toInt()).toString()
        }

        fun reduceNumbersInFraction(fraction: String): String{
            if (fraction.contains('/')) {
                val parts = fraction.split('/')
                val sign = if (parts[0].startsWith('-')) "-" else ""
                val numerator = parts[0].replace("-", "").toLong()
                val denominator = parts[1].toLong()
                val simplifiedNumerator = if (numerator.toString().length > 3) {
                    numerator.toString().substring(0, 3).toLong()
                } else {
                    numerator
                }
                val simplifiedDenominator = if (denominator.toString().length > 3) {
                    denominator.toString().substring(0, 3).toLong()
                } else {
                    denominator
                }
                return "$sign$simplifiedNumerator/$simplifiedDenominator"
            } else {

                if(!(fraction.contains('.'))){
                    return fraction.toDouble().toString()
                }

                val parts = fraction.split('.')
                val sign = if (parts[0].startsWith('-')) "-" else ""
                val integerPart = parts[0].replace("-", "").toLong()
                val decimalPart = parts[1]
                val simplifiedDecimalPart = if (decimalPart.length > 3) {
                    decimalPart.substring(0, 3)
                } else {
                    decimalPart
                }
                return "$sign$integerPart.$simplifiedDecimalPart"
            }
        }

        fun returnCorrectView(fraction: String, ordinaryFractionState: Boolean): String{
            return if (ordinaryFractionState){
                if (fraction.contains('/')){
                    reduceNumbersInFraction(fraction)
                }
                else {
                    reduceNumbersInFraction(decimalToOrdinary(fraction))
                }
            } else {
                if (fraction.contains('/')){
                    reduceNumbersInFraction(ordinaryToDecimal(fraction))
                } else {
                    reduceNumbersInFraction(fraction)
                }
            }
        }

    }
}