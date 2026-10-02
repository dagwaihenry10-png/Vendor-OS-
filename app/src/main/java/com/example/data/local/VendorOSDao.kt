package com.example.data.local

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUserFlow(): Flow<User?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface AutoRuleDao {
    @Query("SELECT * FROM rules ORDER BY createdAt DESC")
    fun getAllRules(): Flow<List<AutoRule>>

    @Query("SELECT * FROM rules WHERE isActive = 1")
    suspend fun getActiveRules(): List<AutoRule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: AutoRule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<AutoRule>)

    @Update
    suspend fun updateRule(rule: AutoRule)

    @Delete
    suspend fun deleteRule(rule: AutoRule)

    @Query("UPDATE rules SET triggeredCount = triggeredCount + 1 WHERE ruleId = :ruleId")
    suspend fun incrementTriggerCount(ruleId: String)
}

@Dao
interface ProofDao {
    @Query("SELECT * FROM proofs ORDER BY createdAt DESC")
    fun getAllProofs(): Flow<List<Proof>>

    @Query("SELECT * FROM proofs WHERE status = :status ORDER BY createdAt DESC")
    fun getProofsByStatus(status: String): Flow<List<Proof>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProof(proof: Proof)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(proofs: List<Proof>)

    @Update
    suspend fun updateProof(proof: Proof)

    @Delete
    suspend fun deleteProof(proof: Proof)

    @Query("SELECT COUNT(*) FROM proofs WHERE status = 'confirmed'")
    suspend fun countConfirmedProofs(): Int

    @Query("SELECT COUNT(*) FROM proofs WHERE status = 'pending'")
    suspend fun countPendingProofs(): Int
}

@Dao
interface MasterDao {
    @Query("SELECT * FROM masters ORDER BY isPromoted DESC, rating DESC")
    fun getAllMasters(): Flow<List<Master>>

    @Query("SELECT * FROM masters WHERE state = :state ORDER BY isPromoted DESC, rating DESC")
    fun getMastersByState(state: String): Flow<List<Master>>

    @Query("SELECT * FROM masters WHERE masterId = :masterId")
    suspend fun getMasterById(masterId: String): Master?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaster(master: Master)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(masters: List<Master>)

    @Update
    suspend fun updateMaster(master: Master)

    @Query("UPDATE masters SET views = views + 1 WHERE masterId = :masterId")
    suspend fun incrementViews(masterId: String)
}

@Dao
interface InquiryDao {
    @Query("SELECT * FROM inquiries ORDER BY createdAt DESC")
    fun getAllInquiries(): Flow<List<Inquiry>>

    @Query("SELECT * FROM inquiries WHERE learnerId = :uid ORDER BY createdAt DESC")
    fun getSentInquiries(uid: String): Flow<List<Inquiry>>

    @Query("SELECT * FROM inquiries WHERE masterId = :masterId ORDER BY createdAt DESC")
    fun getReceivedInquiries(masterId: String): Flow<List<Inquiry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: Inquiry)

    @Update
    suspend fun updateInquiry(inquiry: Inquiry)
}

@Dao
interface ChatLogDao {
    @Query("SELECT * FROM chat_logs ORDER BY timestamp DESC LIMIT 50")
    fun getAllLogs(): Flow<List<ChatLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ChatLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<ChatLog>)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    fun getAllPayments(): Flow<List<PaymentRecord>>

    @Query("SELECT * FROM payments WHERE paymentId = :paymentId")
    suspend fun getPaymentById(paymentId: String): PaymentRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecord)

    @Update
    suspend fun updatePayment(payment: PaymentRecord)
}
