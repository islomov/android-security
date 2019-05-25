package ru.security.live.presentation.view.ui.activity

import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.crashlytics.android.Crashlytics
import com.jakewharton.rxbinding2.widget.RxTextView
import io.fabric.sdk.android.Fabric
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.data.account.AccountManagerHelper
import ru.security.live.data.pref.ServerPref
import ru.security.live.data.pref.UserPref
import ru.security.live.domain.entity.User
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.presenter.AuthPresenter
import ru.security.live.presentation.view.iview.AuthView
import ru.security.live.util.INVALID_TOKEN

/**
 * @author sardor
 */
class AuthActivity : BaseActivity(), AuthView {

    lateinit var presenter: AuthPresenter

//    @ProvidePresenter
//    fun providePresenter() = AuthPresenter(this@AuthActivity)

    private var mAccountAuthenticatorResponse: AccountAuthenticatorResponse? = null

    private var mResultBundle: Bundle? = null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AuthPresenter(this, this)
        Fabric.with(this, Crashlytics())
        mAccountAuthenticatorResponse = intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)
        mAccountAuthenticatorResponse?.onRequestContinued()

        UserPref.name = "Неавторизованный\nпользователь"
        UserPref.avatarUri = ""
        ServerPref.token = INVALID_TOKEN
        AccountManagerHelper.setToken(INVALID_TOKEN)

        setContentView(R.layout.activity_login)
        setUpToolbar()
        tvToolbar.visibility = View.GONE
        ivToolbar.visibility = View.VISIBLE

        val observables = arrayOf(
                RxTextView.textChanges(etLogin).map { it.isNotEmpty() },
                RxTextView.textChanges(etPassword).map { it.isNotEmpty() }
        )
        Observable.combineLatest(observables) { it.all { it is Boolean && it == true } }
                .subscribe {
                    toggleButtons(it)
                }

        btnLogin.setOnClickListener {
            presenter.tryLogin(etLogin.text.toString(), etPassword.text.toString())
        }
        btnLoginLdap.setOnClickListener {
            presenter.tryLdap(etLogin.text.toString(), etPassword.text.toString())
        }
        presenter.getAuthData()

    }

    override fun onResume() {
        super.onResume()
        presenter.updateInteractor()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.auth_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.info) {
            Navigator.navigateToMenu(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun success() {
        setResult(RESULT_OK, intent)
        Navigator.navigateToMain(this)
        finish()
    }

    override fun initAuthData(login: String, password: String) {
        etLogin.setText(login)
        etLogin.setSelection(etLogin.length())
        etPassword.setText(password)
    }

    override fun toggleButtons(enable: Boolean) {
        val backgroundRes = if (enable) R.drawable.button_active else R.drawable.button_inactive
        val colorRes = if (enable) R.color.activeColor else R.color.inactiveColor
        btnLogin.setBackgroundResource(backgroundRes)
        btnLoginLdap.setBackgroundResource(backgroundRes)
        btnLogin.setTextColor(resources.getColor(colorRes))
        btnLoginLdap.setTextColor(resources.getColor(colorRes))

        btnLogin.isEnabled = enable
        btnLoginLdap.isEnabled = enable
    }

    override fun error(error: Throwable) {
        etLogin.setBackgroundResource(R.drawable.input_shape_error)
        etPassword.setBackgroundResource(R.drawable.input_shape_error)
        tvError.text = error.message
        tvError.visibility = View.VISIBLE
    }

    override fun finish() {
        if (mResultBundle != null) {
            mAccountAuthenticatorResponse?.onResult(mResultBundle)
        } else {
            mAccountAuthenticatorResponse?.onError(AccountManager.ERROR_CODE_CANCELED, "canceled")
        }
        mAccountAuthenticatorResponse = null
        super.finish()
    }

    override fun saveUser(it: User) {

    }
}
