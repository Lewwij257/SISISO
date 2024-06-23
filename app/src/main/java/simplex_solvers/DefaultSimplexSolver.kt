package simplex_solvers

import android.content.Context
import android.content.Intent
import com.example.sisiso.SolutionActivity

class DefaultSimplexSolver {

    companion object{

        /**
        gets equals transformed by gaussian method without identity matrix in left side.
         @param array equals transformed by gaussian method without identity matrix.
         @param originProblem origin problem.
         @param returnType 0 - Array<Array<String>> 1 - Array<DoubleArray>
         @return simplex table for default simplex method
         */
        fun createSimplexTable(array: Array<Array<String>>, originProblem: Array<String>): Array<DoubleArray>{
            val lastSimplexTableString = Array(array.first().size){"0.0"}
            val normalizedArray = array.map{it.mapIndexed{index, num -> if (index==it.size - 1) num else (num.toDouble() * -1 ).toString() }}
            for (i in normalizedArray.indices){
                val additionalString = (normalizedArray[i].map {it.toDouble() * originProblem[i].toDouble()})
                for (j in additionalString.indices){
                    lastSimplexTableString[j] = (lastSimplexTableString[j].toDouble() + additionalString[j]).toString()
                }
            }
            for (i in array.size..<originProblem.size){
                lastSimplexTableString[i-array.size] = (lastSimplexTableString[i-array.size].toDouble() + originProblem[i].toDouble()).toString()
            }
            val simplexTable = array + lastSimplexTableString
            return simplexTable.map { it.map { it.toDouble() }.toDoubleArray() }.toTypedArray()
        }

        fun checkIfUnsolvableSimplexTable(simplexTable: Array<DoubleArray>): Boolean{
            for (i in simplexTable.first().indices){
                var allNegative = true
                for (j in simplexTable.indices){
                    if (simplexTable[j][i] > 0){
                        allNegative = false
                    }
                }
                if (allNegative){
                    return true
                }
            }
            return false
        }

        fun checkIfSolvedSimplexTable(simplexTable: Array<DoubleArray>): Boolean {
            return (simplexTable.last().dropLast(1).all { it >= 0 })
        }

        fun simplexAutoSolve(simplexTable: SimplexTable): SimplexTable{

            var resultSimplexTable = SimplexTable()
            resultSimplexTable = simplexTable.deepCopy()

            while (true){
                if (checkIfSolvedSimplexTable(resultSimplexTable.doubleSimplexTable!!)){
                    break
                }
                if(checkIfUnsolvableSimplexTable(resultSimplexTable.doubleSimplexTable!!)){
                    break
                }
                else{
                    resultSimplexTable = simplexSingleStep(resultSimplexTable)
                }
            }
            return resultSimplexTable

        }

        fun simplexSingleStep(simplexTable: SimplexTable, selectedXIndex: Int? = -2, selectedYIndex: Int? = -2): SimplexTable{
            val doubleEquationsMatrix = simplexTable.doubleSimplexTable!!
            val xOrderArray = simplexTable.xOrderArray!!
            val supportElementXIndex: Int
            val supportElementYIndex: Int
            val newSimplexTable = doubleEquationsMatrix.map { it.copyOf() }.toTypedArray()


            if (selectedXIndex == -2 && selectedYIndex == -2){
                supportElementXIndex = doubleEquationsMatrix.last().dropLast(1).indexOfFirst{it < 0}
                supportElementYIndex = findSupportElementYIndex(doubleEquationsMatrix, supportElementXIndex)
            }
            else{
                supportElementXIndex = selectedXIndex!!
                supportElementYIndex = selectedYIndex!!
            }

            val xIndexToSwap = xOrderArray[0][supportElementXIndex]
            val yIndexToSwap = xOrderArray[1][supportElementYIndex]
            xOrderArray[0][supportElementXIndex] = yIndexToSwap
            xOrderArray[1][supportElementYIndex] = xIndexToSwap

            val supportElement = doubleEquationsMatrix[supportElementYIndex][supportElementXIndex]
            val newRow = DoubleArray(doubleEquationsMatrix[0].size)

            for (col in newSimplexTable[supportElementYIndex].indices){
                newSimplexTable[supportElementYIndex][col] = doubleEquationsMatrix[supportElementYIndex][col]/supportElement
            }
            val supportRow = newSimplexTable[supportElementYIndex].copyOf(newSimplexTable[supportElementYIndex].size)
            for (row in newSimplexTable.indices){
                for (i in newSimplexTable[row].indices){
                    newRow[i] = doubleEquationsMatrix[row][i] - supportRow[i] * doubleEquationsMatrix[row][supportElementXIndex]
                }
                newSimplexTable[row] = newRow.copyOf()
            }
            for (row in newSimplexTable.indices){
                newSimplexTable[row][supportElementXIndex] = doubleEquationsMatrix[row][supportElementXIndex]/supportElement * -1
            }
            for (col in newSimplexTable[supportElementYIndex].indices){
                newSimplexTable[supportElementYIndex][col] = doubleEquationsMatrix[supportElementYIndex][col]/supportElement
            }
            newSimplexTable[supportElementYIndex][supportElementXIndex] = 1/supportElement

            simplexTable.doubleSimplexTable = newSimplexTable
            simplexTable.xOrderArray = xOrderArray
            simplexTable.stringSimplexTable = newSimplexTable.map { it.map { it.toString() }.toTypedArray() }.toTypedArray()
            return simplexTable

        }

        fun findSupportElementYIndex(simplexTable: Array<DoubleArray>, supportElementXIndex: Int): Int {
            var minIndex = -1
            var minResult = Double.MAX_VALUE
            for (i in simplexTable.indices) {
                val divisor = simplexTable[i][supportElementXIndex]
                if (divisor > 0) {
                    val result = simplexTable[i][simplexTable[i].size - 1] / divisor
                    if (result < minResult) {
                        minResult = result
                        minIndex = i
                    }
                }
            }
            return minIndex
        }

    }

}