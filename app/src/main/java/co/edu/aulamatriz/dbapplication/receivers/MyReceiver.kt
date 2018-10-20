package co.edu.aulamatriz.dbapplication.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import co.edu.aulamatriz.dbapplication.services.MyService

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){
            MyService.ACTION_FROM_SERVICE ->
            {
                if (intent.extras != null){
                    Log.d("MyReceiver", intent.extras.getString("value"))
                }
            }
        }

    }
}
