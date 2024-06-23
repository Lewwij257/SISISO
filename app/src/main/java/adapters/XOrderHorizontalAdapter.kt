package adapters

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class XOrderHorizontalAdapter(private val context: Context, private val columns: Int): BaseAdapter() {

    private var xOrder = IntArray(columns)

    override fun getCount(): Int {
        return columns
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
        val col = position % columns
        textView.gravity= Gravity.CENTER
        textView.textSize = 20f
        if (col < columns) {
            textView.text = "X" + xOrder[col].toString()
            convertView?.isClickable = false
        }
        if (col == columns){
            textView.text = "b"
            convertView?.isClickable = false
        }
        return textView
    }
}