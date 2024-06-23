package simplex_solvers

import utils.MatrixAdditionalFunctions

class ArtificialBasisSolver {

    companion object{

        fun createSimplexTable(array: Array<Array<String>>): Array<Array<String>>{
            val doubleArray = MatrixAdditionalFunctions.convertString2DArrayToDouble2DArray(array)
            val lastSimplexString = Array(doubleArray[0].size) { i -> doubleArray.sumOf { it[i] } }
            return array + lastSimplexString.map { it.toString() }.toTypedArray()
        }

    }

}