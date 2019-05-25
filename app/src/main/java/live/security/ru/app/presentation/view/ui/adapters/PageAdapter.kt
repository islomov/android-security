package ru.security.live.presentation.view.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.security.live.R
/**
 * @author sardor
 */
class PageAdapter(val onClick: (Int) -> Unit, private val size: Int) : RecyclerView.Adapter<PageAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var lastPage: Int = 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapterPosition = holder.adapterPosition
        val number = "${adapterPosition + 1}"
        holder.tvNumber.text = number

        if (adapterPosition == lastPage) holder.iActive.visibility = View.VISIBLE
        else holder.iActive.visibility = View.INVISIBLE
        holder.itemLayout.setOnClickListener {
            onClick(adapterPosition)
            //Меняем выделение страницы
            holder.iActive.visibility = View.VISIBLE
            notifyItemChanged(lastPage)
            lastPage = adapterPosition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_events_pagination, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemLayout: View = view.findViewById(R.id.itemLayout)
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val iActive: ImageView = view.findViewById(R.id.iActive)
    }

    override fun getItemCount(): Int = size
}