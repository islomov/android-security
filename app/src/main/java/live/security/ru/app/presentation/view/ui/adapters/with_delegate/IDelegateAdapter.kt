package ru.security.live.presentation.view.ui.adapters.with_delegate

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
/**
 * @author sardor
 */
interface IDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DelegateViewType)
}

interface DelegateViewType {
    val viewType: Int
}

typealias Delegate = Pair<IDelegateAdapter, Int>