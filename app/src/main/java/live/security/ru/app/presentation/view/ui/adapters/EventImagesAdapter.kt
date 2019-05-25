package ru.security.live.presentation.view.ui.adapters

import android.graphics.Bitmap
import android.os.Build
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import ru.security.live.R
/**
 * @author sardor
 */
class EventImagesAdapter(val onClick: (ImageView, Bitmap) -> Unit) : RecyclerView.Adapter<EventImagesAdapter.ViewHolder>() {

    private val images = ArrayList<ImageObject>()
    lateinit var onFileRemove: (Int) -> Unit

    fun addImage(bitmap: Bitmap, hash: Int) {
        val proportional = bitmap.width.toDouble() / bitmap.height.toDouble()
        val height = 600
        val width = (height * proportional).toInt()

        val thumbnail = Bitmap.createScaledBitmap(bitmap, width, height, false)
        images.add(ImageObject(bitmap, thumbnail, hash))
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setTransitionName(holder.image, "${images[position].hash}")
        }

        holder.image.setImageBitmap(images[position].thumbnail)
        holder.delete.setOnClickListener {
            onFileRemove(images[position].hash)
            images.removeAt(position)
            notifyDataSetChanged()
        }
        holder.image.setOnClickListener { onClick(holder.image, images[position].image) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val delete: ImageButton = view.findViewById(R.id.delete)
    }

    inner class ImageObject(
            val image: Bitmap,
            val thumbnail: Bitmap,
            val hash: Int
    )

}