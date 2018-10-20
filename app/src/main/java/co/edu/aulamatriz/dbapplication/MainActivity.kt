package co.edu.aulamatriz.dbapplication

import android.app.LoaderManager
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import co.edu.aulamatriz.dbapplication.adapters.CustomRecyclerAdapter
import co.edu.aulamatriz.dbapplication.databases.DBHelper
import co.edu.aulamatriz.dbapplication.providers.ExampleProvider
import co.edu.aulamatriz.dbapplication.services.MyService
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
        LoaderManager.LoaderCallbacks<Cursor>{

    val JOKE_URI = Uri.parse("content://${ExampleProvider.AUTHORITY}/${DBHelper.TABLE_1}")
    lateinit var adapter: CustomRecyclerAdapter
    var myReceiver: MyBroadcastReceiver

    init {
        myReceiver = MyBroadcastReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        AppCenter.start(getApplication(), "c3620d61-6b57-449c-9168-1e4b4228cd58",
                Analytics::class.java,
                Crashes::class.java)

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager = GridLayoutManager(this, 1)
        recycler.layoutManager = layoutManager
        adapter = CustomRecyclerAdapter()
        recycler.adapter = adapter

        loaderManager.initLoader(0, null, this)

        fab.setOnClickListener { view ->

            syncData()
        }

//        startActivity(Intent(this, TabsActivity::class.java))
//        startActivity(Intent(this, TabsActivity::class.java))
//        startActivity(Intent(this, TabsActivity::class.java))
//        startActivity(Intent(this, TabsActivity::class.java))
//        startActivity(Intent(this, TabsActivity::class.java))
//        startActivity(Intent(this, TabsActivity::class.java))
//        startActivity(Intent(this, TabsActivity::class.java))
//        startActivity(Intent(this, TabsActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        startService(Intent(this, MyService::class.java))
    }

    override fun onStop() {
        super.onStop()
        stopService(Intent(this, MyService::class.java))
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(MyService.ACTION_FROM_SERVICE)
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
    }


    private fun syncData() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://api.icndb.com/jokes/random/600"

        val stringRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener<JSONObject> { response ->

                    Log.w("getSongsFromAPI", "Response is:" +
                            " ${response.toString().substring(0, 500)}")
                    processData(response.getJSONArray("value"))

                },
                Response.ErrorListener {

                })
        queue.add(stringRequest)

    }

    private fun processData(jsonArray: JSONArray) {

        contentResolver.delete(JOKE_URI, null, null)

        val cvs = ArrayList<ContentValues>()
        val currentime = System.currentTimeMillis()
        for (i in 0..(jsonArray.length() - 1)) {
            val jObj = jsonArray.getJSONObject(i)
            val cv = ContentValues()
            cv.put("id", jObj.getInt("id"))
            cv.put("joke", jObj.getString("joke"))
            cv.put("categories", jObj.getString("categories"))
            cvs.add(cv)
        }

        val array = arrayOfNulls<ContentValues>(cvs.size)
        cvs.toArray(array)

        contentResolver.bulkInsert(JOKE_URI, array)
        val time = System.currentTimeMillis()

        Log.w("Main", "duration ${time - currentime} size: ${jsonArray.length()}")
        loaderManager.restartLoader(0, null, this)
//        val cal = Calendar.getInstance()
//        cal.time
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        return CursorLoader(this, JOKE_URI,
                null, null,
                null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, cursor: Cursor?) {
        adapter.swap(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        adapter.swap(null)
    }

    inner class MyBroadcastReceiver: BroadcastReceiver(){
        /**
         * This method is called when the BroadcastReceiver is receiving an Intent
         * broadcast.  During this time you can use the other methods on
         * BroadcastReceiver to view/modify the current result values.  This method
         * is always called within the main thread of its process, unless you
         * explicitly asked for it to be scheduled on a different thread using
         * [android.content.Context.registerReceiver]. When it runs on the main
         * thread you should
         * never perform long-running operations in it (there is a timeout of
         * 10 seconds that the system allows before considering the receiver to
         * be blocked and a candidate to be killed). You cannot launch a popup dialog
         * in your implementation of onReceive().
         *
         *
         * **If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
         * then the object is no longer alive after returning from this
         * function.** This means you should not perform any operations that
         * return a result to you asynchronously. If you need to perform any follow up
         * background work, schedule a [android.app.job.JobService] with
         * [android.app.job.JobScheduler].
         *
         * If you wish to interact with a service that is already running and previously
         * bound using [bindService()][android.content.Context.bindService],
         * you can use [.peekService].
         *
         *
         * The Intent filters used in [android.content.Context.registerReceiver]
         * and in application manifests are *not* guaranteed to be exclusive. They
         * are hints to the operating system about how to find suitable recipients. It is
         * possible for senders to force delivery to specific recipients, bypassing filter
         * resolution.  For this reason, [onReceive()][.onReceive]
         * implementations should respond only to known actions, ignoring any unexpected
         * Intents that they may receive.
         *
         * @param context The Context in which the receiver is running.
         * @param intent The Intent being received.
         */
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent!!.extras != null){
                Log.d("MainActivity", intent.extras.getString("value"))
            }
        }

    }
}
