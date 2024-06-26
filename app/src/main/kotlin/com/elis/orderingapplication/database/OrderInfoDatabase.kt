package com.elis.orderingapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.elis.orderingapplication.pojo2.DeliveryAddress
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.pojo2.PointsOfService
import com.elis.orderingapplication.pojo2.Order
import com.elis.orderingapplication.pojo2.Article
import com.elis.orderingapplication.pojo2.DateTypeConverter
import com.elis.orderingapplication.pojo2.OrderingGroup

//@Database(entities = [OrderInfo::class], version = 1, exportSchema = false)
@Database(entities = [DeliveryAddress::class, PointsOfService::class, Order::class, Article::class, OrderingGroup::class], version = 27, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class OrderInfoDatabase : RoomDatabase() {

    abstract val orderInfoDao: OrderInfoDao

    companion object {

        @Volatile
        private var INSTANCE: OrderInfoDatabase? = null

        fun getInstance(context: Context): OrderInfoDatabase {
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
            synchronized(this) {
                // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                // Smart cast is only available to local variables.
                var instance = INSTANCE
                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        OrderInfoDatabase::class.java,
                        "order_info_database"
                    )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // Migration is not part of this lesson. You can learn more about
                        // migration with Room in this blog post:
                        // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                        .fallbackToDestructiveMigration()
                        .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }
                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }
}