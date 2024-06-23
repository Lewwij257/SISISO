package adapters

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import utils.MatrixAdditionalFunctions
import androidx.core.content.ContextCompat
import com.example.sisiso.MainActivity
import com.example.sisiso.R
import utils.FractionViewConverter
import utils.InputValidator

class EquationsSystemAdapter(private val context: Context,
                             private val columns: Int,
                             private val rows: Int,
                             private val ordinaryFractionState: Boolean,
                             private var readOnlyState: Boolean): BaseAdapter() {

    private var equationsSystem = Array(rows) { Array(columns) { "" } }
    private var selectedPosition = -1
    private var selectableCellsMatrix = Array(rows - 1) { BooleanArray(columns - 1) }

    override fun getCount(): Int {
        return rows*columns
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        val col = position % columns
        val row = position / columns
        return equationsSystem[row][col]
    }

    fun setReadOnlyState(newState: Boolean){
        readOnlyState = newState
        notifyDataSetChanged()
    }

    fun setSelectableCellsMatrix(matrix: Array<Array<String>>){
        selectableCellsMatrix = MatrixAdditionalFunctions.createMatrixOfGoodCells(matrix.map { it.map { it.toDouble() }.toDoubleArray() }.toTypedArray())
        notifyDataSetChanged()
    }

    fun setMatrix(newMatrix: Array<Array<String>>){
        equationsSystem = newMatrix
        notifyDataSetChanged()
    }

    fun getMatrix(): Array<Array<String>>{
        return equationsSystem
    }

    fun getSelectedCellXYPosition(): Pair<Int,Int> {
        var counter = 0
        for (i in equationsSystem.indices) {
            for (j in equationsSystem[0].indices) {
                if (counter==selectedPosition){
                    return j to i
                }
                counter+=1
            }
        }
        return -1 to -1
    }

    fun resetSelectedPosition(){
        selectedPosition = -1
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        if (readOnlyState){

            selectableCellsMatrix = MatrixAdditionalFunctions.createMatrixOfGoodCells(equationsSystem.map { it.map { it.toDouble() }.toDoubleArray() }.toTypedArray())

            val textView = TextView(context)
            val columnPosition = position % columns
            val rowPosition = position / columns

            textView.textSize = 20f
            textView.gravity = Gravity.CENTER

            if (rowPosition < rows && columnPosition < columns) {
                textView.text = FractionViewConverter.returnCorrectView(equationsSystem[rowPosition][columnPosition],ordinaryFractionState)
            }

            if (position == selectedPosition) {
                textView.background= ContextCompat.getDrawable(context, R.drawable.bg_for_selected_cell)
            } else if (textView.background!= ContextCompat.getDrawable(context, R.drawable.bg_for_selected_cell)){
                textView.background= ContextCompat.getDrawable(context, R.drawable.bg_for_unselected_cell)
            }

            textView.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
            }

            if (context !is MainActivity){
                try {
                    if (selectableCellsMatrix[rowPosition][columnPosition] && position!=selectedPosition){
                        textView.background = ContextCompat.getDrawable(context, R.drawable.bg_for_good_cell)
                        notifyDataSetChanged()
                    }
                }
                catch (_: Exception){
                }
            }

            if (rowPosition==equationsSystem.size-1 || columnPosition==equationsSystem[0].size-1 || !selectableCellsMatrix[rowPosition][columnPosition]) {
                textView.isClickable = false
            } else {
                textView.isClickable = true
            }
            return textView

        }
        else {
            val editText = EditText(context)
            editText.inputType =
                InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL
            val columnPosition = position % columns
            val rowPosition = position / columns

            if (rowPosition < rows && columnPosition < columns) {
                editText.setText(equationsSystem[rowPosition][columnPosition])
                //equationsSystem[rowPosition][columnPosition] =
            }
            editText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {
                    if (rowPosition < rows && columnPosition < columns){
                        if (InputValidator.goodFractionEnteredCheck(s.toString())){
                            equationsSystem[rowPosition][columnPosition] = s.toString()
                        }
                        else{
                            equationsSystem[rowPosition][columnPosition] = "0.0"
                            editText.setText("0.0")
                        }
                    }
                }
            })
            return editText
        }

    }
}
