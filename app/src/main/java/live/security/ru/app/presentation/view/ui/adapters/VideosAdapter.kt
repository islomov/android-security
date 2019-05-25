package ru.security.live.presentation.view.ui.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ru.security.live.R
import ru.security.live.data.pref.ServerPref
import ru.security.live.domain.entity.CameraFull
import ru.security.live.util.LoggingTool
import java.lang.Exception
/**
 * @author sardor
 */
class VideosAdapter(val onClick: (CameraFull, Int) -> Unit, val text: String) : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

    val list = mutableListOf<CameraFull>()
    val imgMap = HashMap<String, Bitmap>()

    fun submitList(newList: List<CameraFull>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun recycleBitmaps() {
        for (btm in imgMap.values)
            btm.recycle()
        imgMap.clear()
    }

    fun addItem(camera: CameraFull) {
        list.add(camera)
        notifyItemInserted(itemCount)
    }

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosAdapter.ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_desktop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = list.get(position)
            val link = "http://${ServerPref.url2}/videomonitoring/getsnapshot?id=${model.id}&url=${model.url}&fileName=file.png&token=${ServerPref.token}"
            LoggingTool.log("Image $position $link")
            holder.itemLayout.setOnClickListener { onClick(model, holder.adapterPosition) }
            holder.tvName.text = model.name

            if (holder.image.tag != link) {
                holder.image.setImageBitmap(null)
                holder.image.setImageResource(R.color.charcoal_grey)
            }
            if (imgMap[link] != null)
                holder.image.setImageBitmap(imgMap[link])
            else {
                val target = object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        holder.image.setImageResource(R.color.charcoal_grey)
                        holder.tvError.visibility = View.VISIBLE
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        holder.tvError.visibility = View.GONE
                        holder.image.setImageBitmap(bitmap)
                        holder.image.tag = link
                        if (bitmap != null)
                            imgMap.put(link, bitmap)
                    }
                }
                holder.tvName.tag = target
                Picasso.get().load(link)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .placeholder(R.drawable.video_placeholder_background)
                        .noFade()
                        .into(target)
            }
        }
    }

    override fun getItemCount() = list.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val itemLayout: View = view.findViewById(R.id.itemLayout)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val image: ImageView = view.findViewById(R.id.image)
        val tvError: TextView = view.findViewById(R.id.tvError)
    }


    object diffCallback : DiffUtil.ItemCallback<CameraFull>() {
        override fun areItemsTheSame(oldItem: CameraFull?, newItem: CameraFull?): Boolean {
            return oldItem?.url == newItem?.url
        }

        override fun areContentsTheSame(oldItem: CameraFull?, newItem: CameraFull?): Boolean {
            return oldItem == newItem
        }
    }
}