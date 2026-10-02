package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VendorOSRepository
import com.example.data.models.*
import com.example.services.PaymentService
import com.example.services.SqlService
import com.example.utils.SecureConfig
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VendorOSViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = VendorOSRepository(application)
    val paymentService = PaymentService(application)
    val sqlService = SqlService(application)

    val user: StateFlow<User> = repository.userFlow
        .map { it ?: User() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), User())

    val rules: StateFlow<List<AutoRule>> = repository.rulesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val proofs: StateFlow<List<Proof>> = repository.proofsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masters: StateFlow<List<Master>> = repository.mastersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inquiries: StateFlow<List<Inquiry>> = repository.inquiriesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatLogs: StateFlow<List<ChatLog>> = repository.chatLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentRecord>> = repository.paymentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state
    var selectedSkillCategory = MutableStateFlow("All")
        private set

    var searchQuery = MutableStateFlow("")
        private set

    var selectedProofTab = MutableStateFlow("All") // "All", "Pending", "Confirmed"
        private set

    var selectedInquiryTab = MutableStateFlow("Sent") // "Sent", "Received"
        private set

    var selectedPaymentPlan = MutableStateFlow("pro")
        private set

    var currentPayment = MutableStateFlow<PaymentRecord?>(null)
        private set

    var paymentVerificationState = MutableStateFlow<String?>(null)
        private set

    var testReplyResult = MutableStateFlow<String?>(null)
        private set

    var selectedMaster = MutableStateFlow<Master?>(null)
        private set

    var mapRadiusKm = MutableStateFlow(10f)
        private set

    init {
        // Initialize default pending payment on launch
        viewModelScope.launch {
            val u = repository.getUser()
            val initialRecord = paymentService.createPaymentRecord(u.uid, u.email, "pro")
            currentPayment.value = initialRecord
            repository.recordPayment(initialRecord)
        }
    }

    fun setSkillCategory(cat: String) {
        selectedSkillCategory.value = cat
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setProofTab(tab: String) {
        selectedProofTab.value = tab
    }

    fun setInquiryTab(tab: String) {
        selectedInquiryTab.value = tab
    }

    fun setPaymentPlan(plan: String) {
        selectedPaymentPlan.value = plan
        val u = user.value
        val newRecord = paymentService.createPaymentRecord(u.uid, u.email, plan)
        currentPayment.value = newRecord
        viewModelScope.launch {
            repository.recordPayment(newRecord)
            paymentService.syncToSQL(newRecord)
        }
    }

    fun selectMaster(master: Master?) {
        selectedMaster.value = master
        master?.let {
            viewModelScope.launch {
                repository.incrementMasterViews(it.masterId)
            }
        }
    }

    fun setMapRadius(radius: Float) {
        mapRadiusKm.value = radius
    }

    // User Operations
    fun updateActiveMode(mode: String) {
        viewModelScope.launch {
            repository.updateActiveMode(mode)
        }
    }

    fun updateLocation(state: String, lga: String, area: String) {
        viewModelScope.launch {
            repository.updateLocation(state, lga, area)
        }
    }

    fun toggleAutoReply(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAutoReply(enabled)
        }
    }

    fun updateWorkingHours(start: String, end: String) {
        viewModelScope.launch {
            repository.updateWorkingHours(start, end)
        }
    }

    // Auto Rule Operations
    fun addRule(keywords: List<String>, replyText: String, type: String = "keyword") {
        viewModelScope.launch {
            val rule = AutoRule(
                ruleId = "rule_${System.currentTimeMillis()}",
                userId = user.value.uid,
                keywords = keywords,
                replyText = replyText,
                type = type,
                isActive = true,
                triggeredCount = 0
            )
            repository.addRule(rule)
        }
    }

    fun toggleRuleActive(rule: AutoRule) {
        viewModelScope.launch {
            repository.updateRule(rule.copy(isActive = !rule.isActive))
        }
    }

    fun deleteRule(rule: AutoRule) {
        viewModelScope.launch {
            repository.deleteRule(rule)
        }
    }

    fun testMessageSimulator(senderName: String, messageText: String) {
        viewModelScope.launch {
            val reply = repository.processIncomingMessage(senderName, messageText)
            testReplyResult.value = reply ?: "Auto-Reply is currently OFF in settings."
        }
    }

    // Proof Operations
    fun createProof(
        customerName: String,
        customerPhone: String,
        photoUrl: String,
        comment: String
    ) {
        viewModelScope.launch {
            val randNum = (10000..99999).random()
            val orderId = "ORD-$randNum"
            val proofId = "prf_${System.currentTimeMillis()}"
            val maskedPhone = if (customerPhone.length >= 4) {
                "***" + customerPhone.takeLast(4)
            } else {
                "***1234"
            }
            val publicLink = "https://vendoros.ng/p/$orderId"

            val proof = Proof(
                proofId = proofId,
                vendorId = user.value.uid,
                vendorName = user.value.businessName,
                customerName = customerName,
                customerPhoneFull = customerPhone,
                customerPhoneMasked = maskedPhone,
                orderId = orderId,
                photoUrl = if (photoUrl.isBlank()) "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600" else photoUrl,
                status = "pending",
                comment = if (comment.isBlank()) "Standard order dispatched to customer." else comment,
                publicLink = publicLink,
                qrData = publicLink
            )
            repository.addProof(proof)
        }
    }

    fun confirmProof(proofId: String) {
        viewModelScope.launch {
            repository.confirmProof(proofId)
        }
    }

    fun deleteProof(proof: Proof) {
        viewModelScope.launch {
            repository.deleteProof(proof)
        }
    }

    // Payment Operations
    fun launchPayNow() {
        val curr = currentPayment.value ?: return
        paymentService.launchSecurePayment(curr.secureLink)
    }

    fun launchWhatsAppHelp() {
        val curr = currentPayment.value ?: return
        paymentService.launchWhatsAppSupport(curr.plan, curr.amount, curr.reference, curr.uid)
    }

    fun verifyPaymentNow() {
        val curr = currentPayment.value ?: return
        viewModelScope.launch {
            paymentVerificationState.value = "Verifying with VendorOS Secure Gateway..."
            val success = paymentService.verifyPaymentSecure(
                paymentId = curr.paymentId,
                uid = curr.uid,
                plan = curr.plan,
                reference = curr.reference
            )
            if (success) {
                repository.activateProPlan(curr.paymentId, curr.plan)
                paymentVerificationState.value = "✅ Payment verified! ${SecureConfig.planNames[curr.plan]} activated."
            } else {
                paymentVerificationState.value = "❌ Transfer not yet detected. Please allow 1-2 minutes or tap WhatsApp Support."
            }
        }
    }

    // Master / Handwork Operations
    fun becomeMaster(
        businessName: String,
        ownerName: String,
        skillCategory: String,
        skillName: String,
        yearsExperience: Int,
        description: String,
        state: String,
        lga: String,
        area: String,
        address: String,
        monthlyPrice: Int,
        weeklyPrice: Int?,
        hasAccommodation: Boolean,
        accommodationFee: Int,
        phone: String,
        whatsapp: String
    ) {
        viewModelScope.launch {
            val u = user.value
            val master = Master(
                masterId = "mst_${System.currentTimeMillis()}",
                userId = u.uid,
                businessName = businessName,
                ownerName = ownerName,
                skillCategory = skillCategory,
                skillName = skillName,
                yearsExperience = yearsExperience,
                description = description,
                state = state,
                lga = lga,
                area = area,
                address = address,
                lat = 6.5244 + ((-20..20).random() * 0.005),
                lng = 3.3792 + ((-20..20).random() * 0.005),
                pricePerMonth = monthlyPrice,
                pricePerWeek = weeklyPrice,
                hasAccommodation = hasAccommodation,
                accommodationFee = accommodationFee,
                images = listOf("https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=600"),
                phone = phone,
                whatsapp = whatsapp,
                rating = 5.0,
                totalStudents = 0,
                totalReviews = 0,
                views = 1,
                isPromoted = false,
                isVerified = true
            )
            repository.addMaster(master)
        }
    }

    fun promoteMasterShop(masterId: String) {
        viewModelScope.launch {
            repository.promoteMaster(masterId)
        }
    }

    fun sendInquiry(master: Master, message: String) {
        viewModelScope.launch {
            val u = user.value
            val inq = Inquiry(
                inquiryId = "inq_${System.currentTimeMillis()}",
                learnerId = u.uid,
                learnerName = u.name,
                learnerPhone = u.phone,
                masterId = master.masterId,
                masterName = master.businessName,
                skill = master.skillCategory,
                message = message,
                status = "pending"
            )
            repository.sendInquiry(inq)
        }
    }

    fun updateInquiryStatus(inquiryId: String, status: String) {
        viewModelScope.launch {
            repository.updateInquiryStatus(inquiryId, status)
        }
    }
}
