package ru.security.live.data.account

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import ru.security.live.R
import ru.security.live.data.pref.ServerPref
import ru.security.live.util.LoggingTool

object AccountManagerHelper {
    private lateinit var accountType: String
    private lateinit var accountManager: AccountManager

    fun init(context: Context) {
        accountType = context.resources.getString(R.string.account_type)
        accountManager = AccountManager.get(context)
    }

    fun hasAccount(): Boolean {
        return accountManager.getAccountsByType(accountType).isNotEmpty()
    }

    fun createAccount(login: String, password: String): Boolean {
        removeAccount()
        val account = Account(login, accountType)
        val result = accountManager.addAccountExplicitly(account, password, null)
        if (result && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accountManager.notifyAccountAuthenticated(account)
        }
        LoggingTool.log("Saving $login $password")
        return result
    }

    fun removeAccount() {
        val accounts = accountManager.getAccountsByType(accountType)
        for (account in accounts) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(account)
            } else {
                accountManager.removeAccount(account, null, null)
            }
        }
    }

    fun getAuthData(): Pair<String, String>? {
        return if (!hasAccount())
            null
        else {
            val account = accountManager.getAccountsByType(accountType).first()
            val login = account.name
            val password = accountManager.getPassword(account)
            LoggingTool.log("Saved $login $password")

            login to password
        }
    }

    fun getToken(): String? {
        return if (!hasAccount()) ""
        else {
            val account = accountManager.getAccountsByType(accountType).first()
            val token = accountManager.blockingGetAuthToken(account, accountType, false)
            token
        }
    }

    fun setToken(token: String) {
        if (hasAccount()) {
            ServerPref.token = token
            LoggingTool.log("Token ${ServerPref.token}")
            val account = accountManager.getAccountsByType(accountType).first()
            accountManager.setAuthToken(account, accountType, token)
        }
    }
}