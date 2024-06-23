package simplex_solvers

import kotlinx.serialization.Serializable

@Serializable

class SimplexTable(
    var doubleSimplexTable: Array<DoubleArray>? = null,
    var stringSimplexTable: Array<Array<String>>? = null,
    var xOrderArray: Array<IntArray>? = null,
    var stringOriginProblem: Array<String>? = null,
    var ordinaryFractionState: Boolean? = null,
    ) {

    fun deepCopy(): SimplexTable {
        val newTable = SimplexTable()

        newTable.doubleSimplexTable = if (doubleSimplexTable != null) {
            Array(doubleSimplexTable!!.size) { i ->
                doubleSimplexTable!![i].copyOf()
            }
        } else {
            null
        }

        newTable.stringSimplexTable = if (stringSimplexTable != null) {
            Array(stringSimplexTable!!.size) { i ->
                stringSimplexTable!![i].copyOf()
            }
        } else {
            null
        }

        newTable.xOrderArray = if (xOrderArray != null) {
            Array(xOrderArray!!.size) { i ->
                xOrderArray!![i].copyOf()
            }
        } else {
            null
        }

        newTable.stringOriginProblem = if (stringOriginProblem != null) {
            stringOriginProblem!!.copyOf()
        } else {
            null
        }

        newTable.ordinaryFractionState = ordinaryFractionState

        return newTable
    }

    fun getSolutionArray(): DoubleArray{
        val solutionArray = DoubleArray(xOrderArray!![1].size + xOrderArray!![0].size){0.0}
        for (i in xOrderArray!![1]){
            solutionArray[i-1] = doubleSimplexTable!![xOrderArray!![1].indexOf(i)].last()
        }
        return solutionArray
    }

    fun getSolution(): Double{
        return doubleSimplexTable!!.last().last()*-1
    }

}