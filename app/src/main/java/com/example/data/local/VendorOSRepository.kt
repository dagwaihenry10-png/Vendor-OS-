package com.example.data.local

import android.content.Context
import com.example.data.models.*
import com.example.utils.SecureConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class VendorOSRepository(context: Context) {
    private val db = VendorOSDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val autoRuleDao = db.autoRuleDao()
    private val proofDao = db.proofDao()
    private val masterDao = db.masterDao()
    private val inquiryDao = db.inquiryDao()
    private val chatLogDao = db.chatLogDao()
    private val paymentDao = db.paymentDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    val userFlow: Flow<User?> = userDao.getUserFlow()
    val rulesFlow: Flow<List<AutoRule>> = autoRuleDao.getAllRules()
    val proofsFlow: Flow<List<Proof>> = proofDao.getAllProofs()
    val mastersFlow: Flow<List<Master>> = masterDao.getAllMasters()
    val inquiriesFlow: Flow<List<Inquiry>> = inquiryDao.getAllInquiries()
    val chatLogsFlow: Flow<List<ChatLog>> = chatLogDao.getAllLogs()
    val paymentsFlow: Flow<List<PaymentRecord>> = paymentDao.getAllPayments()

    fun getMastersByState(state: String): Flow<List<Master>> = masterDao.getMastersByState(state)

    suspend fun getUser(): User {
        return userDao.getUser() ?: User().also { userDao.insertUser(it) }
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun saveUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateActiveMode(mode: String) {
        val current = getUser()
        userDao.updateUser(current.copy(activeMode = mode))
    }

    suspend fun updateLocation(state: String, lga: String, area: String) {
        val current = getUser()
        userDao.updateUser(current.copy(selectedState = state, selectedLGA = lga, selectedArea = area))
    }

    suspend fun toggleAutoReply(enabled: Boolean) {
        val current = getUser()
        userDao.updateUser(current.copy(autoReplyOn = enabled))
    }

    suspend fun updateWorkingHours(start: String, end: String) {
        val current = getUser()
        userDao.updateUser(current.copy(workingHoursStart = start, workingHoursEnd = end))
    }

    // Rules
    suspend fun addRule(rule: AutoRule) {
        autoRuleDao.insertRule(rule)
    }

    suspend fun updateRule(rule: AutoRule) {
        autoRuleDao.updateRule(rule)
    }

    suspend fun deleteRule(rule: AutoRule) {
        autoRuleDao.deleteRule(rule)
    }

    suspend fun incrementRuleTrigger(ruleId: String) {
        autoRuleDao.incrementTriggerCount(ruleId)
    }

    // Process incoming message simulation for auto-reply
    suspend fun processIncomingMessage(sender: String, messageText: String): String? {
        val user = getUser()
        if (!user.autoReplyOn) return null

        val activeRules = autoRuleDao.getActiveRules()
        val lowerText = messageText.lowercase().trim()

        var matchedRule: AutoRule? = null
        for (rule in activeRules) {
            val hasMatch = rule.keywords.any { keyword ->
                lowerText.contains(keyword.lowercase().trim())
            }
            if (hasMatch) {
                matchedRule = rule
                break
            }
        }

        val reply = matchedRule?.replyText ?: if (activeRules.isNotEmpty()) activeRules.first().replyText else "Thank you for contacting us! We will reply shortly."
        matchedRule?.let { incrementRuleTrigger(it.ruleId) }

        chatLogDao.insertLog(
            ChatLog(
                logId = "LOG-${System.currentTimeMillis()}-${(100..999).random()}",
                userId = user.uid,
                customerName = sender,
                incomingMessage = messageText,
                repliedWith = reply,
                timestamp = System.currentTimeMillis()
            )
        )

        return reply
    }

    // Proofs
    suspend fun addProof(proof: Proof) {
        proofDao.insertProof(proof)
        val user = getUser()
        val newPending = user.pendingDeliveries + 1
        val newTotal = user.totalDeliveries + 1
        userDao.updateUser(user.copy(pendingDeliveries = newPending, totalDeliveries = newTotal))
    }

    suspend fun confirmProof(proofId: String) {
        val proofs = proofDao.getAllProofs().firstOrNull() ?: emptyList()
        val target = proofs.find { it.proofId == proofId } ?: return
        val updated = target.copy(status = "confirmed", confirmedAt = System.currentTimeMillis())
        proofDao.updateProof(updated)

        val user = getUser()
        val newPending = (user.pendingDeliveries - 1).coerceAtLeast(0)
        val newVerified = user.verifiedDeliveries + 1
        val newScore = ((newVerified.toDouble() / (user.totalDeliveries.coerceAtLeast(1))) * 1.0 + 4.0).coerceIn(4.0, 5.0)
        val roundedScore = Math.round(newScore * 10.0) / 10.0
        userDao.updateUser(user.copy(pendingDeliveries = newPending, verifiedDeliveries = newVerified, trustScore = roundedScore))
    }

    suspend fun deleteProof(proof: Proof) {
        proofDao.deleteProof(proof)
    }

    // Masters
    suspend fun addMaster(master: Master) {
        masterDao.insertMaster(master)
    }

    suspend fun promoteMaster(masterId: String) {
        val master = masterDao.getMasterById(masterId) ?: return
        masterDao.updateMaster(master.copy(isPromoted = true))
    }

    suspend fun incrementMasterViews(masterId: String) {
        masterDao.incrementViews(masterId)
    }

    // Inquiries
    suspend fun sendInquiry(inquiry: Inquiry) {
        inquiryDao.insertInquiry(inquiry)
    }

    suspend fun updateInquiryStatus(inquiryId: String, newStatus: String) {
        val inquiries = inquiryDao.getAllInquiries().firstOrNull() ?: emptyList()
        val target = inquiries.find { it.inquiryId == inquiryId } ?: return
        inquiryDao.updateInquiry(target.copy(status = newStatus))
    }

    // Payments
    suspend fun recordPayment(payment: PaymentRecord) {
        paymentDao.insertPayment(payment)
    }

    suspend fun activateProPlan(paymentId: String, plan: String) {
        val payment = paymentDao.getPaymentById(paymentId)
        if (payment != null) {
            paymentDao.updatePayment(payment.copy(status = "confirmed", confirmedAt = System.currentTimeMillis(), sqlSynced = true))
        }

        val user = getUser()
        val thirtyDays = 30L * 24L * 60L * 60L * 1000L
        val expiry = System.currentTimeMillis() + thirtyDays
        userDao.updateUser(user.copy(
            isPro = true,
            proPlan = plan,
            proExpiry = expiry,
            lastPaymentId = paymentId
        ))
    }

    private suspend fun seedInitialDataIfNeeded() {
        if (userDao.getUser() == null) {
            userDao.insertUser(
                User(
                    uid = "usr_99812",
                    name = "Tunde Adeleke",
                    email = "tunde.wears@vendoros.ng",
                    phone = "+2348123456789",
                    activeMode = "both",
                    selectedState = "Lagos",
                    selectedLGA = "Ikeja",
                    selectedArea = "Computer Village / Allen",
                    businessName = "Tunde Fashion Hub & Tech",
                    trustScore = 4.9,
                    totalDeliveries = 24,
                    verifiedDeliveries = 21,
                    pendingDeliveries = 3,
                    autoReplyOn = true,
                    isPro = true,
                    proPlan = "pro",
                    workingHoursStart = "08:00",
                    workingHoursEnd = "21:00"
                )
            )
        }

        val rules = autoRuleDao.getActiveRules()
        if (rules.isEmpty()) {
            val defaultRules = listOf(
                AutoRule(
                    ruleId = "rule_welcome_1",
                    userId = "usr_99812",
                    keywords = listOf("hello", "hi", "good morning", "price", "how much", "cost"),
                    replyText = "Hello! 👋 Welcome to our store. We deliver 100% verified authentic goods across Nigeria. Please let us know the item name or size you want, and we will package it right away! 🚀",
                    type = "welcome",
                    isActive = true,
                    triggeredCount = 142
                ),
                AutoRule(
                    ruleId = "rule_account_2",
                    userId = "usr_99812",
                    keywords = listOf("account", "bank", "send account", "transfer", "pay", "opay"),
                    replyText = "🔒 To protect your money, all payments are processed through our verified VendorOS Secure Gateway: https://vendoros.ng/pay - Once paid, our system automatically validates your order proof!",
                    type = "keyword",
                    isActive = true,
                    triggeredCount = 89
                ),
                AutoRule(
                    ruleId = "rule_away_3",
                    userId = "usr_99812",
                    keywords = listOf("night", "closed", "open tomorrow", "weekend"),
                    replyText = "🌙 We are currently away! Our working hours are 08:00 AM to 09:00 PM (Mon-Sat). Your message has been saved and we will attend to you first thing tomorrow morning!",
                    type = "away",
                    isActive = true,
                    triggeredCount = 37
                )
            )
            autoRuleDao.insertAll(defaultRules)
        }

        val proofs = proofDao.getAllProofs().firstOrNull()
        if (proofs.isNullOrEmpty()) {
            val sampleProofs = listOf(
                Proof(
                    proofId = "prf_101",
                    vendorId = "usr_99812",
                    vendorName = "Tunde Fashion Hub",
                    customerName = "Chioma Eze",
                    customerPhoneFull = "+2348039871122",
                    customerPhoneMasked = "***1122",
                    orderId = "ORD-82910",
                    photoUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500",
                    status = "confirmed",
                    rating = 5.0,
                    comment = "Delivered to Ikeja within 3 hours! Exact original watch. 100% trusted seller.",
                    publicLink = "https://vendoros.ng/p/ORD-82910",
                    qrData = "https://vendoros.ng/p/ORD-82910",
                    confirmedAt = System.currentTimeMillis() - 7200000L
                ),
                Proof(
                    proofId = "prf_102",
                    vendorId = "usr_99812",
                    vendorName = "Tunde Fashion Hub",
                    customerName = "Musa Ibrahim",
                    customerPhoneFull = "+2348145558900",
                    customerPhoneMasked = "***8900",
                    orderId = "ORD-44912",
                    photoUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500",
                    status = "confirmed",
                    rating = 5.0,
                    comment = "Red sneakers arrived clean in Abuja. Waybill received and verified.",
                    publicLink = "https://vendoros.ng/p/ORD-44912",
                    qrData = "https://vendoros.ng/p/ORD-44912",
                    confirmedAt = System.currentTimeMillis() - 86400000L
                ),
                Proof(
                    proofId = "prf_103",
                    vendorId = "usr_99812",
                    vendorName = "Tunde Fashion Hub",
                    customerName = "Bisi Adebayo",
                    customerPhoneFull = "+2348021113456",
                    customerPhoneMasked = "***3456",
                    orderId = "ORD-77123",
                    photoUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500",
                    status = "pending",
                    rating = 5.0,
                    comment = "Wireless headphones dispatched with dispatch rider to Lekki Phase 1.",
                    publicLink = "https://vendoros.ng/p/ORD-77123",
                    qrData = "https://vendoros.ng/p/ORD-77123"
                )
            )
            proofDao.insertAll(sampleProofs)
        }

        val masters = masterDao.getAllMasters().firstOrNull()
        if (masters.isNullOrEmpty()) {
            val sampleMasters = listOf(
                Master(
                    masterId = "mst_01",
                    userId = "usr_master_1",
                    businessName = "Supreme Clipperz Barbershop",
                    ownerName = "Master Emeka Okafor",
                    skillCategory = "Barber",
                    skillName = "Pro Barbering, Fade & Dreadlocks",
                    otherSkills = listOf("Beard Grooming", "Hair Dyeing"),
                    yearsExperience = 8,
                    description = "Master Emeka has trained over 45 successful barbers in Lagos. Learn fading, shaving, hairline carving, hot towel treatment, and salon management with live customers.",
                    state = "Lagos",
                    lga = "Ikeja",
                    area = "Allen Avenue / Toyin Street",
                    address = "14 Toyin Street, Ikeja, Lagos",
                    lat = 6.5960,
                    lng = 3.3515,
                    pricePerMonth = 6000,
                    pricePerWeek = 2000,
                    hasAccommodation = true,
                    accommodationFee = 8000,
                    images = listOf("https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=600"),
                    phone = "+2348031234567",
                    whatsapp = "2348031234567",
                    rating = 4.9,
                    totalStudents = 32,
                    totalReviews = 84,
                    views = 890,
                    isPromoted = true,
                    isVerified = true
                ),
                Master(
                    masterId = "mst_02",
                    userId = "usr_master_2",
                    businessName = "Kashamadupe Couture & Stitches",
                    ownerName = "Madam Folashade Alabi",
                    skillCategory = "Tailoring",
                    skillName = "Senator Wears, Agbada & Bridal Gowns",
                    otherSkills = listOf("Pattern Drafting", "Embroidery Machine"),
                    yearsExperience = 12,
                    description = "Learn authentic Nigerian bespoke fashion from cutting to industrial pressing. We take both male and female apprentices with full hands-on guidance.",
                    state = "Lagos",
                    lga = "Surulere",
                    area = "Adeniran Ogunsanya",
                    address = "22 Adeniran Ogunsanya St, Surulere, Lagos",
                    lat = 6.4969,
                    lng = 3.3582,
                    pricePerMonth = 7500,
                    pricePerWeek = 2500,
                    hasAccommodation = false,
                    accommodationFee = 0,
                    images = listOf("https://images.unsplash.com/photo-1558769132-cb1aea458c5e?w=600"),
                    phone = "+2348028889911",
                    whatsapp = "2348028889911",
                    rating = 5.0,
                    totalStudents = 45,
                    totalReviews = 112,
                    views = 1240,
                    isPromoted = true,
                    isVerified = true
                ),
                Master(
                    masterId = "mst_03",
                    userId = "usr_master_3",
                    businessName = "SmartFix Tech Laboratory",
                    ownerName = "Engineer Segun Johnson",
                    skillCategory = "Phone Repair",
                    skillName = "iPhone & Android Hardware & Software",
                    otherSkills = listOf("Motherboard Micro-soldering", "Screen Refurbishing"),
                    yearsExperience = 7,
                    description = "Specialized hardware IC replacement, schematic reading, jumper wires, face ID repair, and flashing. Real workshop experience in the heart of Computer Village.",
                    state = "Lagos",
                    lga = "Ikeja",
                    area = "Computer Village",
                    address = "5 Otigba Street, Computer Village, Ikeja, Lagos",
                    lat = 6.6018,
                    lng = 3.3444,
                    pricePerMonth = 8000,
                    pricePerWeek = 2800,
                    hasAccommodation = false,
                    accommodationFee = 0,
                    images = listOf("https://images.unsplash.com/photo-1588508065123-287b28e013da?w=600"),
                    phone = "+2348104445566",
                    whatsapp = "2348104445566",
                    rating = 4.8,
                    totalStudents = 28,
                    totalReviews = 65,
                    views = 640,
                    isPromoted = false,
                    isVerified = true
                ),
                Master(
                    masterId = "mst_04",
                    userId = "usr_master_4",
                    businessName = "Danladi Solar Power & Inverters",
                    ownerName = "Alhaji Danladi Garba",
                    skillCategory = "Solar Installation",
                    skillName = "Solar Panels, Lithium Inverters & Wiring",
                    otherSkills = listOf("CCTV Installation", "Earthing"),
                    yearsExperience = 9,
                    description = "High demand skill! Master energy load calculations, hybrid inverter setups, solar array angling, and battery BMS wiring. Apprentices graduate to direct project contracts.",
                    state = "FCT",
                    lga = "Abuja Municipal",
                    area = "Garki Area 11",
                    address = "Plot 304 Ahmadu Bello Way, Garki, Abuja",
                    lat = 9.0435,
                    lng = 7.4913,
                    pricePerMonth = 10000,
                    pricePerWeek = 3500,
                    hasAccommodation = true,
                    accommodationFee = 15000,
                    images = listOf("https://images.unsplash.com/photo-1509391365360-2e959784a276?w=600"),
                    phone = "+2348093332211",
                    whatsapp = "2348093332211",
                    rating = 4.9,
                    totalStudents = 19,
                    totalReviews = 48,
                    views = 710,
                    isPromoted = true,
                    isVerified = true
                ),
                Master(
                    masterId = "mst_05",
                    userId = "usr_master_5",
                    businessName = "AutoDrift Diagnostic & Mechatronics",
                    ownerName = "Engr. Kenneth Nwachukwu",
                    skillCategory = "Auto Mechanic",
                    skillName = "OBD2 Computer Diagnostics & Engine Overhaul",
                    otherSkills = listOf("Transmission Repair", "Electrical Wiring"),
                    yearsExperience = 11,
                    description = "Modern automotive engineering. Learn ECU scanning, Japanese & German engine timing, sensor fault tracing, and brake hydraulics.",
                    state = "Lagos",
                    lga = "Alimosho",
                    area = "Egbeda / Iyana Ipaja",
                    address = "Plot 8 Idimu Road, Egbeda, Lagos",
                    lat = 6.6022,
                    lng = 3.2842,
                    pricePerMonth = 5500,
                    pricePerWeek = 1800,
                    hasAccommodation = true,
                    accommodationFee = 6000,
                    images = listOf("https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=600"),
                    phone = "+2348057771122",
                    whatsapp = "2348057771122",
                    rating = 4.9,
                    totalStudents = 38,
                    totalReviews = 79,
                    views = 820,
                    isPromoted = false,
                    isVerified = true
                )
            )
            masterDao.insertAll(sampleMasters)
        }

        val logs = chatLogDao.getAllLogs().firstOrNull()
        if (logs.isNullOrEmpty()) {
            val sampleLogs = listOf(
                ChatLog(
                    logId = "log_1",
                    userId = "usr_99812",
                    customerName = "Blessing (WhatsApp)",
                    incomingMessage = "Hi, how much is the Nike Air running shoe?",
                    repliedWith = "Hello! 👋 Welcome to our store. We deliver 100% verified authentic goods across Nigeria. Please let us know the item name or size you want, and we will package it right away! 🚀",
                    timestamp = System.currentTimeMillis() - 1800000L
                ),
                ChatLog(
                    logId = "log_2",
                    userId = "usr_99812",
                    customerName = "Dave (Instagram DM)",
                    incomingMessage = "Please send your account number to pay for the watch",
                    repliedWith = "🔒 To protect your money, all payments are processed through our verified VendorOS Secure Gateway: https://vendoros.ng/pay - Once paid, our system automatically validates your order proof!",
                    timestamp = System.currentTimeMillis() - 5400000L
                )
            )
            chatLogDao.insertAll(sampleLogs)
        }

        val inquiries = inquiryDao.getAllInquiries().firstOrNull()
        if (inquiries.isNullOrEmpty()) {
            val sampleInquiries = listOf(
                Inquiry(
                    inquiryId = "inq_1",
                    learnerId = "usr_99812",
                    learnerName = "Tunde Adeleke",
                    learnerPhone = "+2348123456789",
                    masterId = "mst_01",
                    masterName = "Supreme Clipperz Barbershop",
                    skill = "Barber",
                    message = "Good day Master Emeka, I want to enroll for the 3-month intensive barbering course. Is accommodation still available?",
                    status = "accepted",
                    meetingDate = "Monday, 10:00 AM"
                ),
                Inquiry(
                    inquiryId = "inq_2",
                    learnerId = "usr_99812",
                    learnerName = "Tunde Adeleke",
                    learnerPhone = "+2348123456789",
                    masterId = "mst_04",
                    masterName = "Danladi Solar Power",
                    skill = "Solar Installation",
                    message = "Hello Alhaji, I want to learn solar inverter wiring and battery calculations.",
                    status = "pending"
                )
            )
            for (inq in sampleInquiries) {
                inquiryDao.insertInquiry(inq)
            }
        }
    }
}
