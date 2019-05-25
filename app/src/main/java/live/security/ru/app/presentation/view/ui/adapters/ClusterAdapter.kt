package ru.security.live.presentation.view.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.security.live.R
import ru.security.live.domain.entity.Point
import ru.security.live.util.TYPE_CODE_CAMERA
import ru.security.live.util.TYPE_CODE_CONTROLLER
import ru.security.live.util.TYPE_CODE_SENSOR
/**
 * @author sardor
 */
class ClusterAdapter(
        private val items: List<Point>,
        private val listener: (item: Point) -> Unit
) : RecyclerView.Adapter<ClusterAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val icon = when (item.info.type) {
            TYPE_CODE_CAMERA -> R.drawable.icon_camera
            TYPE_CODE_SENSOR -> R.drawable.icon_sensor
            TYPE_CODE_CONTROLLER -> R.drawable.icon_controller
            else -> R.drawable.icon_home
        }
        holder.tvTitle.text = item.info.title
        holder.ivIcon.setImageResource(icon)
        holder.itemView.setOnClickListener { listener(item) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_point, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
    }
}