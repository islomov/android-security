package ru.security.live.presentation.view.ui.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.data.pref.ServerPref
import ru.security.live.data.pref.UserPref
import ru.security.live.domain.entity.User
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.presenter.MenuPresenter
import ru.security.live.presentation.view.iview.MenuView
import ru.security.live.util.INVALID_TOKEN
import ru.security.live.util.InfoHelper
import ru.security.live.util.LoggingTool
import ru.security.live.util.showToast
import java.lang.Exception
/**
 * @author sardor
 */
class MenuActivity : BaseActivity(), MenuView {

    @InjectPresenter
    lateinit var presenter: MenuPresenter
    private var optionsMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        setUpHomeToolbar()
        tvToolbar.text = getString(R.string.information)

        tvServer.setOnClickListener {
            presenter.showDialog()
        }
        tvPhone.setOnClickListener {
            InfoHelper.phoneCall(this)
        }
        tvSupport.setOnClickListener {
            try {
                InfoHelper.emailSupport(this)
            } catch (e: Exception) {
                showToast("Приложения для отправки email не найдено")
            }
        }
        tvLicense.setOnClickListener {
            InfoHelper.openLicense(this)
        }
        tvCompany.setOnClickListener {
            InfoHelper.openSite(this)
        }

        LoggingTool.log("Token ${ServerPref.token}")
    }

    override fun error(error: Throwable) {
        super.error(error)
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (ServerPref.token != INVALID_TOKEN)
            menuInflater.inflate(R.menu.info_options, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.signOut) {
            presenter.logout()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        if (ServerPref.token != INVALID_TOKEN)
            showUser(User(UserPref.name, UserPref.avatarUri))
    }

    override fun showDialog(url1: String, url2: String) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_servers, null)
        builder.setView(view)

        val server1 = view.findViewById<EditText>(R.id.server1)
        server1.setText(url1)
        server1.setSelection(server1.length())
        val server2 = view.findViewById<EditText>(R.id.server2)
        server2.setText(url2)
        server2.setSelection(server2.length())

        builder.setMessage("Введите адреса серверов согласно рекомендациям по использованию приложения inOneApp")
        builder.setPositiveButton("Ок") { d, _ ->
            //LoggingTool.log("Server1:${server1.text.toString()},Server2:${server2.text.toString()}")
            //presenter.setServer(server1.text.toString(), server2.text.toString())
            presenter.settings(server2.text.toString())
            d.dismiss()
        }
        builder.run {
            setNegativeButton("Отмена", null)
            show()
        }
    }

    override fun success() {

    }

    override fun navigateToAuth() {
        Navigator.navigateToAuth(this)
    }

    override fun showUser(user: User) {
        tvName.text = user.name
        LoggingTool.log(user.imageUrl)
        if (user.imageUrl.isNotEmpty()) {
            Picasso.get()
                    .load(user.imageUrl)
                    .into(ivAvatar)
        }
    }

    override fun noUser() {

    }

    override fun logout() {
        Navigator.navigateToAuth(this)
        finish()
    }
}