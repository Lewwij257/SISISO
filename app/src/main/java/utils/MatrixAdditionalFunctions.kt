package utils

class MatrixAdditionalFunctions {
    companion object {

        fun createMatrixOfGoodCells(matrix: Array<DoubleArray>): Array<BooleanArray> {
            val matrixOfGoodCells =
                Array(matrix.size - 1) { BooleanArray(matrix[0].size - 1) { false } }
            val matrixOfCalculatedResults =
                Array(matrix.size - 1) { DoubleArray(matrix[0].size - 1) { 1000000.0 } }
            for (yCoordinate in 0..<matrix.size - 1) {
                for (xCoordinate in 0..<matrix[0].size - 1) {
                    if (matrix[yCoordinate][xCoordinate] > 0 && matrix[yCoordinate].last() >= 0) {
                        matrixOfCalculatedResults[yCoordinate][xCoordinate] =
                            matrix[yCoordinate].last() / matrix[yCoordinate][xCoordinate]
                    }
                }
            }
            for (xCoordinate in 0..<matrix[0].size - 1) {
                if (matrix.last()[xCoordinate] < 0) {
                    var minNumberInColumn = 1000000000.0
                    for (i in 0..<matrix.size - 1) {
                        if (matrixOfCalculatedResults[i][xCoordinate] < minNumberInColumn) {
                            minNumberInColumn = matrixOfCalculatedResults[i][xCoordinate]
                        }
                    }

                    for (yCoordinate in 0..<matrix.size - 1) {
                        if (matrixOfCalculatedResults[yCoordinate][xCoordinate] == minNumberInColumn) {
                            matrixOfGoodCells[yCoordinate][xCoordinate] = true
                        }
                    }
                }
            }
            return matrixOfGoodCells
        }

        fun deepCopyTwoDimStringArray(array: Array<Array<String>>): Array<Array<String>> {
            val newArray = Array(array.size) { Array(array[0].size) { "" } }
            for (i in array.indices) {
                for (j in array[i].indices) {
                    newArray[i][j] = array[i][j]
                }
            }
            return newArray
        }

        fun deepCopyOneDimStringArray(array: Array<String>): Array<String> {
            val newArray = Array(array.size) { "" }
            for (i in array.indices) {
                newArray[i] = array[i]
            }
            return newArray
        }

        fun convertString2DArrayToDouble2DArray(stringArray: Array<Array<String>>):Array<DoubleArray>{
            val doubleArray = Array(stringArray.size){DoubleArray(stringArray.first().size)}
            for (i in stringArray.indices){
                for (j in stringArray.first().indices){
                    val doubleNumber: Double
                    if (stringArray[i][j].contains("/")){
                        val parts = stringArray[i][j].split("/")
                        doubleNumber = parts[0].toDouble()/parts[1].toDouble()
                    }else{
                        doubleNumber = stringArray[i][j].toDouble()
                    }
                    doubleArray[i][j] = doubleNumber
                }
            }
            return doubleArray
        }



    }
}