package adapters

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class XOrderVerticalAdapter(private val context: Context, private val rows: Int): BaseAdapter() {

    private var xOrder = IntArray(rows)

    override fun getCount(): Int {
        return rows
    }

    override fun getItem(position: Int): Any {
        return xOrder[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setXOrder(newXOrder: IntArray){
        xOrder = newXOrder
        notifyDataSetChanged()
    }

    fun getXOrder(): IntArray{
        return xOrder
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = TextView(context)
        textView.textSize = 20f
        val row = position % rows
        textView.gravity= Gravity.CENTER
        if (row < rows-1) {
            textView.text = "X" + xOrder[row].toString()
        }
        if (row == rows-1) {
            textView.text = ""
        }
        return textView
    }
}