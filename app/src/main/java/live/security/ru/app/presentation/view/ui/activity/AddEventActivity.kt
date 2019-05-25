package ru.security.live.presentation.view.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CheckedTextView
import android.widget.ImageView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.jakewharton.rxbinding2.widget.RxTextView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.domain.entity.CreateEventData
import ru.security.live.domain.entity.EventTypeItem
import ru.security.live.presentation.presenter.AddEventPresenter
import ru.security.live.presentation.view.iview.AddEventView
import ru.security.live.presentation.view.ui.adapters.EventImagesAdapter
import ru.security.live.presentation.view.ui.adapters.SpinnerEventTypesAdapter
import ru.security.live.util.*
import ru.security.live.util.ui.OneButtonDialog
import java.io.File
/**
 * @author sardor
 */
class AddEventActivity : BaseActivity(), AddEventView {

    companion object {
        private const val CAMERA_REQUEST = 1488
        private const val GALLERY_REQUEST = 1489
        private const val MAP_REQUEST = 999
        private const val LOCATION_REQUEST = 998
        private const val DEVICE_REQUEST = 997
    }

    @InjectPresenter
    lateinit var presenter: AddEventPresenter

    private val permissions by lazy {
        RxPermissions(this)
    }

    private val adapter = EventImagesAdapter(::onClickImage)
    private var mLocationId = ""
    private var mDeviceId = ""
    private var mTypeId = ""
    private var location1 = ""
    private var location2 = ""
    private var location3 = ""
    private var location4 = ""
    private val fileIds = HashMap<Int, String>()

    @SuppressLint("PrivateResource", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        setUpHomeToolbar()
        tvToolbar.text = getString(R.string.userEvent)

        presenter.getEventTypes()

        adapter.onFileRemove = ::onFileRemove
        rvImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvImages.adapter = adapter

        val list = arrayOf(RxTextView.textChanges(etAddEventShortInfo).map { it.isNotEmpty() },
                RxTextView.textChanges(etDescription).map { it.isNotEmpty() })

        Observable.combineLatest(list) { item ->
            item.all {
                it is Boolean && it == true
            }
        }.subscribe { toggleCreateButton(it) }

        btnCamera.setOnClickListener { _ ->
            permissions.request(Manifest.permission.CAMERA)
                    .subscribe {
                        if (it) {
                            openCamera()
                        } else {
                            error(Throwable(getString(R.string.noPermission)))
                        }
                    }
        }

        btnGallery.setOnClickListener { _ ->
            permissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe {
                        if (it) {
                            openGallery()
                        } else {
                            error(Throwable(getString(R.string.noPermission)))
                        }
                    }
        }

        btnCreate.setOnClickListener {
            val ids = ArrayList<String>()
            ids.addAll(fileIds.values)
            presenter.createEvent(
                    CreateEventData(etAddEventShortInfo.text.toString(), etDescription.text.toString(),
                            mLocationId, mDeviceId, mTypeId, ids)
            )
        }

        tvAddEventCurrentGeo.setOnClickListener { _ ->
            permissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe {
                        if (it) {
                            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                                getLocation()
                            else error(Throwable(getString(R.string.errorGPS)))
                        } else {
                            error(Throwable(getString(R.string.noPermission)))
                        }
                    }
        }

        val params = intent.getParcelableExtra(INTENT_EVENT_PARAMS) as? Params
        params?.let {
            val bitmap = BitmapFactory.decodeFile(params.imagePath)
            val file = ScreenshotHelper.convertToFile(this, bitmap)
            etLocation.setText(params.location)
            presenter.uploadFile(file)
            if (file != null)
                adapter.addImage(bitmap, file.hashCode())
        }

        for (pos in 0 until llAddEventBtn.childCount) {
            val child = llAddEventBtn.getChildAt(pos)
            child.setOnClickListener {
                onChooseGeoClickListener(child)
            }
        }

        ctv3.performClick()

        ivChooseDevice.setOnClickListener {
            val intent = Intent(this, ChooseDeviceActivity::class.java)
            startActivityForResult(intent, DEVICE_REQUEST)
        }
    }

    override fun toggleCreateButton(enable: Boolean) {
        val e = enable //&& mTypeId?.isNotEmpty()
        val backgroundRes = if (e) R.drawable.button_active else R.drawable.button_inactive
        val colorRes = if (e) R.color.activeColor else R.color.inactiveColor
        btnCreate.setBackgroundResource(backgroundRes)
        btnCreate.setTextColor(resources.getColor(colorRes))
        btnCreate.isEnabled = e
    }

    private fun openCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val file = createImageFile()

            if (file != null) {
                val uri = FileProvider.getUriForFile(
                        this,
                        "ru.headpoint.inoneapp.fileprovider",
                        file
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, CAMERA_REQUEST)
            }
        }
    }

    private var currentPath: String? = null

    private fun createImageFile(): File? {
        val storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                "abc",
                ".jpg",
                storage
        )
        currentPath = image.absolutePath
        return image
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        if (intent.resolveActivity(packageManager) != null) {
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(intent, GALLERY_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    if (currentPath != null) {
                        val bitmap = BitmapFactory.decodeFile(currentPath)
                        val file = File(currentPath)
                        adapter.addImage(bitmap, file.hashCode())
                        Handler().postDelayed({ presenter.uploadFile(file) }, 1000)
                    }
                }
                GALLERY_REQUEST -> {
                    val uri = data?.data
                    val inStream = contentResolver.openInputStream(uri)
                    //TODO remove scale if it is too low quality
                    val bitmap = BitmapFactory.decodeStream(inStream)
                    val bitmapScaled = Bitmap.createScaledBitmap(bitmap, 640, 360, true)
                    val file = File(ImagePicker.getPath(this, uri))
                    adapter.addImage(bitmapScaled, file.hashCode())
                    Handler().postDelayed({ presenter.uploadFile(file) }, 1000)
                }
                MAP_REQUEST -> {
                    val stringExtra = data?.getStringExtra("name")
                    etLocation.setText(stringExtra)
                }
                LOCATION_REQUEST -> {
                    //Когда вернулись с выбора позиции
                    val stringExtra = data?.getStringExtra("location")
                    mLocationId = data?.getStringExtra("locationId")!!
                    LoggingTool.log("Create Event location request id:$mLocationId")
                    etLocation.setText(stringExtra)
                }
                DEVICE_REQUEST -> {
                    //Когда вернулись с выбора позиции
                    val stringExtra = data?.getStringExtra("device")
                    mDeviceId = data?.getStringExtra("deviceId")!!
                    LoggingTool.log("Create Event device id:$mLocationId")
                    etDevice.setText(stringExtra)
                }
            }
        }
    }

    private fun onChooseGeoClickListener(view: View) {
        if (view is CheckedTextView) {
            when (view.id) {
                R.id.ctv1 -> {
                    if (!view.isChecked) {
                        etLocation.isEnabled = true
                        ivAddEventGeoChooser.visibility = View.GONE
                        if (ctv2.isChecked) {
                            location2 = etLocation.text.toString()
                        }
                        if (ctv3.isChecked) {
                            location3 = etLocation.text.toString()
                        }
                        ctv2.isChecked = false
                        ctv3.isChecked = false
                        etLocation.setText(location1)
                        view.toggle()
                    }
                }

                R.id.ctv2 -> {
                    if (!view.isChecked) {
                        etLocation.isEnabled = false
                        ivAddEventGeoChooser.visibility = View.VISIBLE
                        ivAddEventGeoChooser.setOnClickListener {
                            //open MAP
                            val intent = Intent(this, MapChoosePositionActivity::class.java)
                            startActivityForResult(intent, MAP_REQUEST)
                        }
                        if (ctv1.isChecked) {
                            location1 = etLocation.text.toString()
                        }
                        if (ctv3.isChecked) {
                            location3 = etLocation.text.toString()
                        }
                        ctv1.isChecked = false
                        ctv3.isChecked = false
                        etLocation.setText(location2)
                        view.toggle()
                    }
                }

                R.id.ctv3 -> {
                    if (!view.isChecked) {
                        etLocation.isEnabled = false
                        ivAddEventGeoChooser.visibility = View.VISIBLE
                        ivAddEventGeoChooser.setOnClickListener {
                            //open ChooseLocation
                            val intent = Intent(this, ChooseLocationActivity::class.java)
                            startActivityForResult(intent, LOCATION_REQUEST)
                        }
                        if (ctv1.isChecked) {
                            location1 = etLocation.text.toString()
                        }
                        if (ctv2.isChecked) {
                            location2 = etLocation.text.toString()
                        }
                        ctv1.isChecked = false
                        ctv2.isChecked = false
                        etLocation.setText(location3)
                        view.toggle()
                    }
                }
//                R.id.ctv4 -> {
//                    if (!view.isChecked) {
//                        etLocation.isEnabled = false
//                        ivAddEventGeoChooser.visibility = View.VISIBLE
//                        ivAddEventGeoChooser.setOnClickListener {
//                            //open ChooseLocation
//                            val intent = Intent(this, ChooseDeviceActivity::class.java)
//                            startActivityForResult(intent, DEVICE_REQUEST)
//                        }
//                        if (ctv1.isChecked) {
//                            location1 = etLocation.text.toString()
//                        }
//                        if (ctv2.isChecked) {
//                            location2 = etLocation.text.toString()
//                        }
//                        if (ctv3.isChecked) {
//                            location3 = etLocation.text.toString()
//                        }
//                        ctv1.isChecked = false
//                        ctv2.isChecked = false
//                        ctv3.isChecked = false
//                        etLocation.setText(location4)
//                        view.toggle()
//                    }
//                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateSpinner(list: List<EventTypeItem>) {
        val mAdapter = SpinnerEventTypesAdapter(this, R.layout.spinner_item_event_type, R.id.tvSpinnerTypeText, list.map { it.name })
        mAdapter.setDropDownViewResource(R.layout.spinner_item_event_type)
        spAddEventType.adapter = mAdapter
        spAddEventType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                mTypeId = list[position].id
                toggleCreateButton(etAddEventShortInfo.text.isNotEmpty() && etDescription.text.isNotEmpty())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    override fun init(list: List<EventTypeItem>) {
        updateSpinner(list)
    }

    override fun onEventCreated() {
        val dialog = OneButtonDialog(this)
        dialog.mTitle = "Успех"
        dialog.mMessage = "Событие успешно создано"
        dialog.onClick = {
            finish()
        }
        dialog.show()
    }

    override fun addFileId(hash: Int, id: String) {
        fileIds[hash] = id
        LoggingTool.log("FileIds ${fileIds.keys}")
        LoggingTool.log("FileIds ${fileIds.values}")
    }

    private fun onFileRemove(hash: Int) {
        LoggingTool.log("Has $hash")
        fileIds.remove(hash)
        LoggingTool.log("FileIds ${fileIds.keys}")
        LoggingTool.log("FileIds ${fileIds.values}")
    }

    private fun onClickImage(view: ImageView, bitmap: Bitmap) {
        FullscreenImageActivity.imageBitmap = bitmap
        val intent = Intent(this, FullscreenImageActivity::class.java)
        startActivity(intent)
    }

    override fun getLocation() {
        SingleShotLocationProvider.requestSingleUpdate(this) {
            val location = "${it.latitude}, ${it.longitude}"
            LoggingTool.log("Location $location")
            etLocation.setText(location)
        }
    }

    @Parcelize
    data class Params(
            val title: String,
            val location: String,
            val imagePath: String
    ) : Parcelable
}
