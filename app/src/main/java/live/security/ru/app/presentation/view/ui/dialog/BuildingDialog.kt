package ru.security.live.presentation.view.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import kotlinx.android.synthetic.main.dialog_building.view.*
import ru.security.live.R
import ru.security.live.domain.entity.Building
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.presentation.Navigator
import ru.security.live.util.TYPE_CODE_BUILDING
import ru.security.live.util.someChecked
/**
 * @author sardor
 */
class BuildingDialog : BottomSheetDialogFragment() {

    lateinit var building: Building
    lateinit var onPlanClick: (String) -> Unit

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, R.layout.dialog_building, null)
        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(Color.TRANSPARENT)

        val info = building.info
        val location = "${getString(R.string.latitude, info.lat.toString())} ${getString(R.string.longitude, info.long.toString())}"
        val placement = getString(R.string.placementDescription, building.description)

        contentView.tvTitle.text = info.title
        contentView.tvLocation.text = location
        contentView.tvPlacement.text = placement
        contentView.btnPlan.visibility = if (building.planId == null) View.GONE else View.VISIBLE
        contentView.btnEvents.visibility = if (building.info.locationId == null) View.GONE else View.VISIBLE

        contentView.btnPlan.setOnClickListener {
            onPlanClick(building.planId!!)
        }

        contentView.btnEvents.setOnClickListener {
            val id = info.id
            val locationItem = EventLocationItem(id, true, info.title, TYPE_CODE_BUILDING, id)
            locationItem.status = someChecked
            Navigator.navigateToEvents(activity as Activity, locationItem)
        }
    }

}