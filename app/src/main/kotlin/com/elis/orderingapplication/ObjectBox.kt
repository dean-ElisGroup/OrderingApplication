package com.elis.orderingapplication

import android.content.Context
import android.util.Log
import com.elis.orderingapplication.model.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.Admin

object ObjectBox {
    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()

        if (BuildConfig.DEBUG) {
            val started = Admin(store).start(context.applicationContext);
            Log.i("ObjectBoxAdmin", "Started: $started");
        }


    }


}


