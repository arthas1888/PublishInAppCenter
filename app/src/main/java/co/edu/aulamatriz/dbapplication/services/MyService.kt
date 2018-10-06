package co.edu.aulamatriz.dbapplication.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log

class MyService : Service() {

    val handler = Handler()
    var runable: Runnable = Runnable {
        Log.d("MyService", "mensaje desde el servicio")
        launchHandler()
    }

    private fun launchHandler() {
        handler.removeCallbacks(runable)
        handler.postDelayed(runable, 5 * 1000)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Log.w("MyService", "onCreate")
        handler.postDelayed(runable, 5 * 1000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.w("MyService", "onStartCommand")
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runable)
        Log.e("MyService", "onDestroy")
    }
}
