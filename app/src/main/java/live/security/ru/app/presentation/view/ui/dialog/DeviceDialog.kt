package ru.security.live.presentation.view.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_device.view.*
import ru.security.live.R
import ru.security.live.domain.entity.*
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.view.iview.DeviceView
import ru.security.live.util.TYPE_CODE_CAMERA
import ru.security.live.util.allChecked
/**
 * @author sardor
 */
class DeviceDialog : BottomSheetDialogFragment(), DeviceView {

    private lateinit var contentView: View
    lateinit var device: Device
    lateinit var info: Info

    override fun setupDialog(dialog: Dialog, style: Int) {
        contentView = View.inflate(context, R.layout.dialog_device, null)
        dialog.setContentView(contentView)
        (contentView.parent as View).setBackgroundColor(Color.TRANSPARENT)

        info = device.info
        val icon = if (device.broadcastAvailable) R.drawable.icon_green else R.drawable.icon_red

        contentView.ivAvailability.setImageResource(icon)
        contentView.tvTitle.text = info.title
        contentView.tvIncidents1.text = getString(R.string.forDay, device.incidents1)
        contentView.tvIncidents2.text = getString(R.string.forDay, device.incidents2)
        contentView.tvObject.text = device.scene
        contentView.tvPlacement.text = device.placement
        contentView.tvStatus.text = device.getStatus()

        if (info.type == TYPE_CODE_CAMERA) {
//            hideButtons()
//            DevicePresenter(this).getCamera(info.id)

            contentView.btnTwo.visibility = View.GONE

            contentView.btnOne.setText(R.string.broadcast)
            contentView.btnTwo.setText(R.string.archive)
            contentView.btnThree.setText(R.string.toEventList)


            contentView.btnOne.setOnClickListener {
                Navigator.navigateToVideo(activity as Activity, info.id, isFromMap = true)
            }


            contentView.btnTwo.setOnClickListener {
                //Navigator.navigateToArchiveFromDeviceDialog(activity as Activity, info.id)
            }

            contentView.btnThree.setOnClickListener {
                val locationItem = EventLocationItem(device.info.id, true, info.title, info.type!!, info.locationId
                        ?: "")
                locationItem.status = allChecked
                Navigator.navigateToEvents(activity as Activity, locationItem, 1)
            }


        } else {
            contentView.btnOne.visibility = View.GONE
            contentView.btnOne.setText(R.string.sensorIndicators)


            //contentView.btnTwo.visibility = if (info.events.isEmpty()) View.GONE else View.VISIBLE

            contentView.btnOne.setOnClickListener {
                if (info.type == TYPE_CODE_CAMERA) {
                    Navigator.navigateToVideo(activity as Activity, device.info.id)
                } else {
                    Navigator.navigateToEvent(info.id, activity as Activity)
                }
            }

            contentView.btnThree.visibility = View.GONE

            contentView.btnTwo.setOnClickListener {
                val locationItem = EventLocationItem(info.id, true, info.title, info.type
                        ?: "", info.locationId ?: "")
                locationItem.status = allChecked
                Navigator.navigateToEvents(activity as Activity, locationItem, 1)
            }

        }

    }

    override fun handleButton(cameraFull: CameraFull?, archive: ArchiveItem?) {
        contentView.btnOne.setText(R.string.broadcast)
        contentView.btnTwo.setText(R.string.archive)
        contentView.btnThree.setText(R.string.toEventList)

        contentView.btnOne.visibility = if (!device.broadcastAvailable) View.GONE else View.VISIBLE
        contentView.btnOne.setOnClickListener {
            Navigator.navigateToVideo(activity as Activity, info.id)
        }

        if (cameraFull != null && archive != null) {
            contentView.btnTwo.visibility = View.VISIBLE
            contentView.btnTwo.setOnClickListener {
                Navigator.navigateToArchive(activity as Activity,
                        cameraFull, archive)
            }
        } else {
            contentView.btnTwo.visibility = View.GONE
        }

        contentView.btnThree.visibility = if (device.info.locationId == null) View.GONE else View.VISIBLE
        contentView.btnThree.setOnClickListener {
            val locationItem = EventLocationItem(device.info.locationId!!, true, info.title, info.type!!, info.locationId!!)
            Navigator.navigateToEvents(activity as Activity, locationItem)
        }
        hideProgress()
        showButtons()
    }

    override fun error(error: Throwable) {
        error.printStackTrace()
        Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        contentView.progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        contentView.progressBar.visibility = View.GONE
    }

    override fun showButtons() {
        contentView.buttonsContainer.visibility = View.VISIBLE
        contentView.btnThree.visibility = View.VISIBLE
    }

    override fun hideButtons() {
        contentView.buttonsContainer.visibility = View.INVISIBLE
        contentView.btnThree.visibility = View.INVISIBLE
    }
}