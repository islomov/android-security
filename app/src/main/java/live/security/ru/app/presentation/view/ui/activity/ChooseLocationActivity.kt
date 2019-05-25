package ru.security.live.presentation.view.ui.activity

import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.presentation.presenter.LocationListPresenter
import ru.security.live.presentation.view.iview.ChooseLocationView
import ru.security.live.presentation.view.ui.fragment.ChooseListLocationFragment
/**
 * @author sardor
 */
class ChooseLocationActivity : BaseActivity(), ChooseLocationView {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "Location")
    lateinit var presenter: LocationListPresenter

    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locationlist_activity)
        setUpHomeToolbar()
        tvToolbar.text = getString(R.string.chooseLocation)

        fragmentManager = supportFragmentManager
        fragmentManager.popBackStack()

        presenter.getLocationListStructure()
        fragmentManager.beginTransaction()
                .replace(R.id.container, ChooseListLocationFragment.newInstance())
                .commit()
    }

    override fun openNext(data: List<EventLocationItem>) {
        if (presenter.path.isNotEmpty()) {

            fragmentManager.beginTransaction()
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setCustomAnimations(R.animator.slide_from_right, R.animator.slide_in_left)
                    .replace(R.id.container, ChooseListLocationFragment.newInstance())
                    .addToBackStack("folder")
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (presenter.path.isNotEmpty()) presenter.path.removeLast()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.path.clear()
        presenter.locationData.clear()
    }

    override fun setDeviceListData(data: List<EventLocationItem>) {

    }
}