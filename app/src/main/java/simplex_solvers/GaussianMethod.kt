package simplex_solvers

class GaussianMethod {


    companion object{
        fun eliminate(matrix: Array<Array<String>>): Array<Array<String>>{
            //val originalMatrix = InputTypeConverter().stringArrayToDoubleArray(matrix)
            val originalMatrix = matrix.map { it.map { it.toDouble() }.toDoubleArray() }.toTypedArray()
            val newMatrix = originalMatrix.copyOf(originalMatrix.size)
            newMatrix as? Array<DoubleArray>?:error("null error!")

            var divider: Double

            for (i in originalMatrix.indices) {
                if (newMatrix[i][i] != 0.0 && newMatrix[i][i] != 1.0) {
                    divider = newMatrix[i][i]
                    for (col in 0..<originalMatrix[0].size) {
                        newMatrix[i][col] = newMatrix[i][col] / divider
                    }
                }
                for (k in originalMatrix.indices) {
                    if (k != i && newMatrix[k][i] != 0.0) {
                        divider = newMatrix[k][i]
                        for (column in 0..<originalMatrix[i].size) {
                            newMatrix[k][column] -= divider * originalMatrix[i][column]
                        }
                    }
                }
            }
            return newMatrix.map { it.map { it.toString() }.toTypedArray() }.toTypedArray()
        }

        fun dropIdentityPart(){}

    }


}