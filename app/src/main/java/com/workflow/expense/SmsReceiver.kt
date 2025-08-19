package com.workflow.expense

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.workflow.expense.data.sms.SmsForwarder
import org.koin.java.KoinJavaComponent.inject

/**
 * Manifest-declared receiver that listens for incoming SMS and delegates to [SmsForwarder].
 */
class SmsReceiver : BroadcastReceiver() {

    private val smsForwarder: SmsForwarder by inject(SmsForwarder::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return
        val bundle: Bundle? = intent.extras
        val pdus = bundle?.get("pdus") as? Array<*>
        if (pdus.isNullOrEmpty()) return

        val format = bundle.getString("format")
        val messages = pdus.mapNotNull {
            try {
                SmsMessage.createFromPdu(it as ByteArray, format)
            } catch (t: Throwable) {
                Log.e("SmsReceiver", "Failed to parse PDU", t)
                null
            }
        }

        val originatingAddress = messages.firstOrNull()?.displayOriginatingAddress ?: return
        val messageBody = messages.joinToString(separator = "") { it.displayMessageBody }

        smsForwarder.onIncomingSms(originatingAddress, messageBody)
    }
}


