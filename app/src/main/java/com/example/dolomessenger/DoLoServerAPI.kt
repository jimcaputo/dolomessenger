package com.example.dolomessenger

import android.content.Context
import android.os.Build
import androidx.core.content.edit
import com.android.volley.RequestQueue
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.util.UUID
import org.json.JSONException
import org.json.JSONObject

object DoLoServerAPI {
    private lateinit var requestQueue: RequestQueue

    // private const val SERVER = "https://doloserver-569184823432.us-east1.run.app"
    private const val SERVER = "http://10.0.2.2:8080"

    private var uuid = ""
    private var device = ""

    fun create(context: Context) {
        requestQueue = Volley.newRequestQueue(context)

        val sharedPref = context.getSharedPreferences("DoLo Messenger", Context.MODE_PRIVATE)

        uuid = sharedPref.getString("UUID", "") ?: ""
        if (uuid == "") {
            uuid = UUID.randomUUID().toString()
            sharedPref.edit {
                putString("UUID", uuid)
                apply()
            }
        }

        device = sharedPref.getString("Device", "") ?: ""
        if (device == "") {
            device = Build.MODEL
            sharedPref.edit {
                putString("Device", device)
                apply()
            }
        }
    }

    fun updateToken(token: String) {
        val data = JSONObject()
        try {
            data.put("uuid", uuid)
            data.put("device", device)
            data.put("token", token)
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }

        val url = "$SERVER/updatetoken"
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            data,
            { resp ->
                // TODO - do something with successful response
                val response = resp.toString()
            },
            { error ->
                // TODO - do something with error response
                val response = error.message.toString()
            })

        requestQueue.add(request)
    }

    fun broadcastMessage(message: String) {
        val data = JSONObject()
        try {
            data.put("title", "DoLo Message")
            data.put("body", message)
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }

        val url = "$SERVER/broadcast"
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            data,
            { resp ->
                // TODO - do something with successful response
                val response = resp.toString()
            },
            { error ->
                // TODO - do something with error response
                val response = error.message.toString()
            })

        requestQueue.add(request)
    }
}