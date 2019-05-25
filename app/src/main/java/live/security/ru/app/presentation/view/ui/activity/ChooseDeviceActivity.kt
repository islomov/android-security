package ru.security.live.presentation.view.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_choose_source.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.domain.entity.DeviceItem
import ru.security.live.presentation.presenter.ChooseDevicePresenter
import ru.security.live.presentation.view.iview.ChooseDeviceView
import ru.security.live.presentation.view.ui.adapters.EventDeviceAdapter
/**
 * @author sardor
 */
class ChooseDeviceActivity : BaseActivity(), ChooseDeviceView {

    @InjectPresenter
    lateinit var presenter: ChooseDevicePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_source)
        setUpHomeToolbar()
        tvToolbar.text = getString(R.string.chooseDevice)
        rvDevices.layoutManager = LinearLayoutManager(this)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.trim().length >= 3) {
                    presenter.searchDevice(s.toString().trim())
                } else {
                    rvDevices.adapter = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun showResult(list: ArrayList<DeviceItem>) {
        val adapter = EventDeviceAdapter(list, object : EventDeviceAdapter.OnEventDeviceItemClickListener {
            override fun onClick(item: DeviceItem) {
                val intent = Intent()
                intent.putExtra("device", item.name)
                intent.putExtra("deviceId", item.id)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })
        rvDevices.adapter = adapter
        tvEmptyView.visibility = View.GONE
    }

    override fun cleanList() {
        rvDevices.adapter = null
        tvEmptyView.visibility = View.VISIBLE
    }
}
