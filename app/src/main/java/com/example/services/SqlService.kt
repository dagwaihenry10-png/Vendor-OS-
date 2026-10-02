package com.example.services

import android.content.Context
import com.example.data.models.PaymentRecord
import com.example.utils.SecureConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SqlService(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun fetchPaymentsFromServer(uid: String): List<PaymentRecord> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${SecureConfig.apiBase}/get_payments.php?uid=$uid")
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val resBody = response.body?.string() ?: ""
                val json = JSONObject(resBody)
                val paymentsArray = json.optJSONArray("payments") ?: return@withContext emptyList()
                val list = mutableListOf<PaymentRecord>()
                for (i in 0 until paymentsArray.length()) {
                    val item = paymentsArray.getJSONObject(i)
                    list.add(
                        PaymentRecord(
                            paymentId = item.optString("payment_id", "PAY-$i"),
                            uid = item.optString("uid", uid),
                            email = "",
                            plan = item.optString("plan", "pro"),
                            amount = item.optInt("amount", 2000),
                            reference = item.optString("reference", "VOS-REF"),
                            status = item.optString("status", "confirmed"),
                            createdAt = System.currentTimeMillis() - (i * 86400000L),
                            sqlSynced = true,
                            maskedAccount = SecureConfig.maskedAccount
                        )
                    )
                }
                list
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
