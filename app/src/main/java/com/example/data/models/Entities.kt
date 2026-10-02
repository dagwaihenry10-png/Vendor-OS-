package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String = "user_default_1",
    val name: String = "Vendor Boss",
    val email: String = "vendor@vendoros.ng",
    val phone: String = "+2348000000000",
    val photoUrl: String = "",
    val activeMode: String = "both", // "vendor", "skills", "both"
    val selectedState: String = "Lagos",
    val selectedLGA: String = "Ikeja",
    val selectedArea: String = "Computer Village / Allen",
    val businessName: String = "Lagos Prime Wears & Tech",
    val trustScore: Double = 4.9,
    val totalDeliveries: Int = 18,
    val verifiedDeliveries: Int = 14,
    val pendingDeliveries: Int = 4,
    val autoReplyOn: Boolean = true,
    val isPro: Boolean = false,
    val proPlan: String = "none", // "none", "basic", "pro", "super"
    val workingHoursStart: String = "08:00",
    val workingHoursEnd: String = "21:00",
    val createdAt: Long = System.currentTimeMillis(),
    val proExpiry: Long? = null,
    val paymentMethod: String = "opay",
    val opayRef: String? = null,
    val lastPaymentId: String? = null
)

@Entity(tableName = "rules")
data class AutoRule(
    @PrimaryKey val ruleId: String,
    val userId: String,
    val keywords: List<String>,
    val replyText: String,
    val imageUrl: String? = null,
    val type: String = "keyword", // "welcome", "keyword", "away", "busy"
    val isActive: Boolean = true,
    val triggeredCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "proofs")
data class Proof(
    @PrimaryKey val proofId: String,
    val vendorId: String,
    val vendorName: String,
    val customerName: String,
    val customerPhoneFull: String,
    val customerPhoneMasked: String, // e.g. "***1234"
    val orderId: String, // e.g. "ORD-10284"
    val photoUrl: String,
    val videoUrl: String? = null,
    val status: String = "pending", // "pending", "confirmed"
    val rating: Double = 5.0,
    val comment: String = "Order delivered promptly with full satisfaction.",
    val publicLink: String,
    val qrData: String,
    val createdAt: Long = System.currentTimeMillis(),
    val confirmedAt: Long? = null
)

@Entity(tableName = "masters")
data class Master(
    @PrimaryKey val masterId: String,
    val userId: String,
    val businessName: String,
    val ownerName: String,
    val skillCategory: String,
    val skillName: String,
    val otherSkills: List<String> = emptyList(),
    val yearsExperience: Int = 5,
    val description: String,
    val state: String,
    val lga: String,
    val area: String,
    val address: String,
    val lat: Double = 6.5244,
    val lng: Double = 3.3792,
    val pricePerMonth: Int = 5000,
    val pricePerWeek: Int? = 1500,
    val hasAccommodation: Boolean = false,
    val accommodationFee: Int = 0,
    val images: List<String> = emptyList(),
    val videoUrl: String? = null,
    val phone: String,
    val whatsapp: String,
    val instagram: String = "",
    val isAvailable: Boolean = true,
    val rating: Double = 4.9,
    val totalStudents: Int = 18,
    val totalReviews: Int = 42,
    val views: Int = 320,
    val isPromoted: Boolean = false,
    val isVerified: Boolean = true,
    val openingHoursOpen: String = "08:00",
    val openingHoursClose: String = "20:00",
    val openingHoursDays: String = "Mon-Sat",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "inquiries")
data class Inquiry(
    @PrimaryKey val inquiryId: String,
    val learnerId: String,
    val learnerName: String,
    val learnerPhone: String,
    val masterId: String,
    val masterName: String,
    val skill: String,
    val message: String,
    val status: String = "pending", // "pending", "accepted", "rejected", "completed"
    val createdAt: Long = System.currentTimeMillis(),
    val meetingDate: String? = null
)

@Entity(tableName = "chat_logs")
data class ChatLog(
    @PrimaryKey val logId: String,
    val userId: String,
    val customerName: String,
    val incomingMessage: String,
    val repliedWith: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "replied"
)

@Entity(tableName = "payments")
data class PaymentRecord(
    @PrimaryKey val paymentId: String,
    val uid: String,
    val email: String,
    val plan: String, // "basic", "pro", "super", "promote", "handwork_pro"
    val amount: Int,
    val method: String = "opay_secure_link",
    val reference: String,
    val status: String = "pending", // "pending", "confirmed", "failed"
    val createdAt: Long = System.currentTimeMillis(),
    val confirmedAt: Long? = null,
    val sqlSynced: Boolean = false,
    val encryptedRef: String = "",
    val secureToken: String = "",
    val clientHash: String = "",
    val secureLink: String = "",
    val maskedAccount: String = "7081****44",
    val proofScreenshotUri: String? = null
)

class Converters {
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value == null) "[]" else adapter.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrBlank()) emptyList() else adapter.fromJson(value) ?: emptyList()
    }
}
