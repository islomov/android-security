package ru.security.live.data.account

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import ru.security.live.R
import ru.security.live.presentation.view.ui.activity.AuthActivity

class Authenticator(private val context: Context) : AbstractAccountAuthenticator(context) {
    private val handler = Handler()

    @Throws(NetworkErrorException::class)
    override fun addAccount(
            response: AccountAuthenticatorResponse,
            accountType: String,
            authTokenType: String?,
            requiredFeatures: Array<out String>?,
            options: Bundle?
    ): Bundle? {
        val manager = AccountManager.get(context)
        val accounts = manager.getAccountsByType(context.getString(R.string.account_type))

        if (accounts.isNotEmpty()) {
            val error = "Возможно создать лишь один аккаунт"

            val bundle = Bundle()
            bundle.putInt(AccountManager.KEY_ERROR_CODE, 1)
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, error)

            handler.post {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }

            return bundle
        }

        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthTokenLabel(authTokenType: String?): String {
        throw UnsupportedOperationException()
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(response: AccountAuthenticatorResponse?, account: Account?, options: Bundle?) = null

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(response: AccountAuthenticatorResponse?, account: Account?, authTokenType: String?, options: Bundle?): Bundle {
        throw UnsupportedOperationException()
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(response: AccountAuthenticatorResponse?, account: Account?, features: Array<out String>?): Bundle {
        throw UnsupportedOperationException()
    }

    @Throws(NetworkErrorException::class)
    override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle {
        throw UnsupportedOperationException()
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, options: Bundle): Bundle {
        throw UnsupportedOperationException()
    }
}