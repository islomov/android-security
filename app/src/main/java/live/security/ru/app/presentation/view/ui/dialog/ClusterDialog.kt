package ru.security.live.presentation.view.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.dialog_list.view.*
import ru.security.live.R
import ru.security.live.domain.entity.Cluster
import ru.security.live.domain.entity.Point
import ru.security.live.presentation.view.ui.adapters.ClusterAdapter
/**
 * @author sardor
 */
class ClusterDialog : BottomSheetDialogFragment() {

    var cluster: Cluster? = null
    lateinit var onClick: (Point) -> Unit

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.dialog_list, null)
        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(Color.TRANSPARENT)

        val adapter = ClusterAdapter(cluster?.list ?: emptyList(), onClick)
        contentView.rvPoints.layoutManager = LinearLayoutManager(activity)
        contentView.rvPoints.adapter = adapter
    }
}