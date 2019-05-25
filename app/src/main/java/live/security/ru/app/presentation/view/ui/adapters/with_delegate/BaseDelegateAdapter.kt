package ru.security.live.presentation.view.ui.adapters.with_delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ru.security.live.util.inflate
/**
 * @author sardor
 */
abstract class BaseDelegateAdapter<in T : DelegateViewType> : IDelegateAdapter {

    protected open val isRecyclable = true

    abstract val itemLayoutId: Int
    abstract fun View.onItemInflated(model: T, context: Context): Unit

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            BaseViewHolder(parent.inflate(itemLayoutId),
                    { model, v -> v.onItemInflated(model as T, parent.context) })

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                  item: DelegateViewType) {
        (holder as BaseViewHolder).apply {
            bind(item)
        }
    }

    abstract fun getViewType(): Int

    class BaseViewHolder(parent: View,
                         private val onItemInflated: (DelegateViewType, View) -> Unit) :
            RecyclerView.ViewHolder(parent) {
        fun bind(model: DelegateViewType) = onItemInflated.invoke(model, itemView)
    }
}