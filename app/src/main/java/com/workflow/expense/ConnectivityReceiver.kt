package com.workflow.expense

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.workflow.expense.data.sms.SmsForwarder
import org.koin.java.KoinJavaComponent.inject

class ConnectivityReceiver : BroadcastReceiver() {
    private val smsForwarder: SmsForwarder by inject(SmsForwarder::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ConnectivityManager.CONNECTIVITY_ACTION) return
        if (hasNetwork(context)) {
            smsForwarder.flushPendingIfAny()
        }
    }

    private fun hasNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}


