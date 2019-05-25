package ru.security.live.presentation.view.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.security.live.R
/**
 * @author sardor
 */

class SpinnerEventTypesAdapter(context: Activity, resouceId: Int, textviewId: Int, var list: List<String>) : ArrayAdapter<String>(context, resouceId, textviewId, list) {

    private var flater: LayoutInflater = context.layoutInflater

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val rowItem = getItem(position)

        val rowview = flater.inflate(R.layout.spinner_item_event_type, null, true)

        val tvSpinnerCityText = rowview.findViewById<TextView>(R.id.tvSpinnerTypeText)
        tvSpinnerCityText.text = rowItem

        return rowview
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = flater.inflate(R.layout.spinner_item_event_type, null)
        }

        val txtTestText = view!!.findViewById<TextView>(R.id.tvSpinnerTypeText)
        txtTestText.text = list[position]
        return view
    }
}