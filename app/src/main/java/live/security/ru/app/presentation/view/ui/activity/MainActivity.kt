package ru.security.live.presentation.view.ui.activity

import android.content.Context
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.ActivityCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.presenter.MainPresenter
import ru.security.live.presentation.view.iview.MainView
import ru.security.live.util.PERMISSIONS
import ru.security.live.util.PERMISSION_ALL
import android.content.pm.PackageManager

/**
 * @author sardor
 */
class MainActivity : BaseActivity(), MainView {

    @InjectPresenter
    lateinit var presenter: MainPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpToolbar()
        tvToolbar.visibility = View.GONE
        ivToolbar.visibility = View.VISIBLE
        presenter.getPermissions()
        //Change persmissionn to RxPermission

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }

    }

    fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }


    override fun showSections(monitoring: Boolean?, map: Boolean?, events: Boolean?) {
        val items = ArrayList<Item>()
        items.clear()

        if (monitoring!!) {
            items.add(
                    Item("Видеонаблюдение", R.drawable.icon_video) {
                        Navigator.navigateToVideo(this)
                    }
            )
            items.add(
                    Item("Список камер", R.drawable.icon_camers_list) {
                        Navigator.navigateToDevices(this)
                    }
            )
        }
        if (events!!) {
            items.add(
                    Item("События", R.drawable.icon_events) {
                        Navigator.navigateToEvents(this)
                    }
            )
        }

        if (map!!) {
            items.add(
                    Item("Карта", R.drawable.icon_map) {
                        Navigator.navigateToMap(this)
                    }
            )
        }
        if (events) {
            items.add(
                    Item("Пользовательское событие", R.drawable.icon_user_events) {
                        Navigator.navigateToAddEvent(this)
                    }
            )
        }


        items.forEach { item ->
            val view = layoutInflater.inflate(R.layout.item_main_activity, container, false)
            view.findViewById<TextView>(R.id.tvTitle).text = item.title
            view.findViewById<ImageView>(R.id.ivImage).setImageResource(item.resId)
            view.setOnClickListener { item.event() }
            container.addView(view)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.auth_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.info -> {
                Navigator.navigateToMenu(this)
            }
            R.id.main_menu_exit -> {
                presenter.logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun doLogout() {
        Navigator.navigateToAuth(this)
        finish()
    }

    private class Item(
            val title: String,
            @DrawableRes val resId: Int,
            val event: () -> Unit
    )
}
