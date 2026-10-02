package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.models.*

@Database(
    entities = [
        User::class,
        AutoRule::class,
        Proof::class,
        Master::class,
        Inquiry::class,
        ChatLog::class,
        PaymentRecord::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VendorOSDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun autoRuleDao(): AutoRuleDao
    abstract fun proofDao(): ProofDao
    abstract fun masterDao(): MasterDao
    abstract fun inquiryDao(): InquiryDao
    abstract fun chatLogDao(): ChatLogDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: VendorOSDatabase? = null

        fun getInstance(context: Context): VendorOSDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VendorOSDatabase::class.java,
                    "vendoros_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
