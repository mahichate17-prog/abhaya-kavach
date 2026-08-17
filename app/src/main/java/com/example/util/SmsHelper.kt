package com.example.util

import android.telephony.SmsManager

object SmsHelper {

    fun sendEmergencySms(
        phoneNumber: String,
        message: String
    ): Boolean {
        return try {
            val cleanNumber = phoneNumber
                .replace(" ", "")
                .replace("-", "")
                .trim()

            if (cleanNumber.isBlank()) return false

            SmsManager.getDefault().sendTextMessage(
                cleanNumber,
                null,
                message,
                null,
                null
            )

            true
        } catch (e: Exception) {
            false
        }
    }
}
