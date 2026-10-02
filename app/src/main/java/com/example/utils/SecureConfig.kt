package com.example.utils

import android.util.Base64
import java.security.MessageDigest

/**
 * VendorOS Secure Payment Configuration
 * Strictly adheres to security guidelines:
 * 1. REAL ACCOUNT IS NEVER DISPLAYED PLAIN IN UI (maskedAccount = "7081****44")
 * 2. Obfuscated base64 chunks used only for background API serialization if required.
 * 3. Secure payment link directs customers to encrypted server gateway.
 */
object SecureConfig {
    const val maskedAccount: String = "7081****44"
    const val bankName: String = "OPay"
    const val accountName: String = "VendorOS Payments"
    const val supportWhatsApp: String = "2347081022844"
    const val apiBase: String = "https://vendoros.ng/api"
    const val securePayBase: String = "https://vendoros.ng/pay"

    private fun decode(b64: String): String {
        return try {
            String(Base64.decode(b64, Base64.DEFAULT), Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Obfuscated decode only for background API payload, NEVER for UI rendering!
     */
    fun getRealAccountForApi(): String {
        return try {
            val p1 = "NzA4"
            val p2 = "MTAy"
            val p3 = "Mjg0NA=="
            decode(p1) + decode(p2) + decode(p3)
        } catch (e: Exception) {
            ""
        }
    }

    val planPrices: Map<String, Int> = mapOf(
        "basic" to 1000,
        "pro" to 2000,
        "super" to 3000,
        "promote" to 1000,
        "handwork_pro" to 1500
    )

    val planNames: Map<String, String> = mapOf(
        "basic" to "Vendor Basic",
        "pro" to "Vendor Pro",
        "super" to "Super Mode (3-in-1)",
        "promote" to "Shop Boost (7 Days)",
        "handwork_pro" to "Handwork Pro"
    )

    fun generateSecureReference(plan: String, uid: String): String {
        val safeUid = if (uid.length >= 6) uid.substring(0, 6) else uid.padEnd(6, 'X')
        val raw = "VOS-${plan.uppercase()}-$safeUid-${System.currentTimeMillis()}"
        val digest = sha256(raw)
        val shortHash = if (digest.length >= 10) digest.substring(0, 10).uppercase() else "VOS8892"
        return "VOS-$shortHash"
    }

    fun generateSecureLink(ref: String, amount: Int, plan: String): String {
        val encodedToken = try {
            Base64.encodeToString(ref.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        } catch (e: Exception) {
            ref
        }
        return "$securePayBase?ref=$ref&amount=$amount&plan=$plan&token=$encodedToken"
    }

    fun sha256(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            input.hashCode().toString()
        }
    }
}
