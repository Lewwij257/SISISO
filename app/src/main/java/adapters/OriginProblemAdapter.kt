package adapters

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.sisiso.R
import utils.FractionViewConverter

class OriginProblemAdapter(private val context: Context,
                           private val columns: Int,
                           private val ordinaryFractionState: Boolean = false,
                           private val readOnlyState: Boolean = true): BaseAdapter() {

    private var originProblem = Array(columns){""}

    override fun getCount(): Int {
        return columns
    }


    override fun getItem(position: Int): Any {
        return originProblem[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun clearOriginProblem(){
    }

    fun setOriginProblem(newOriginProblem: Array<String>){
        originProblem = newOriginProblem
        notifyDataSetChanged()
    }

    fun getOriginProblem(): Array<String>{
        return originProblem
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        if (readOnlyState) {
            val textView = TextView(context)
            textView.gravity = Gravity.CENTER
            textView.textSize = 20f
            textView.background= ContextCompat.getDrawable(context, R.drawable.bg_for_unselected_cell)

            if (position!=originProblem.size-1){
                textView.text = FractionViewConverter.returnCorrectView(getItem(position).toString(), ordinaryFractionState)
            }
            else{
                textView.text = getItem(position).toString()
            }
            return textView
        }
        else {
            val editText = EditText(context)
            editText.gravity = Gravity.CENTER
            editText.textSize = 20f
            editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (position < originProblem.size) {
                        originProblem[position] = s.toString()
                    }
                }
            })

            val spinner = Spinner(context)
            val minMaxAdapter: ArrayAdapter<String> =
                ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, arrayOf("min", "max"))
            minMaxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.onItemSelectedListener = object:
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    originProblem[originProblem.lastIndex]=arrayOf("min","max")[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    originProblem[originProblem.lastIndex]="min"
                }
            }

            if (position == originProblem.size-1){
                spinner.adapter = minMaxAdapter
                originProblem[originProblem.lastIndex] = spinner.selectedItem.toString()
                return spinner
            }
            else{
                editText.setText(getItem(position).toString())
                return editText
            }
        }
    }
}