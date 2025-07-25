package com.example.dolomessenger

import android.content.Context
import android.content.SharedPreferences
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
    private lateinit var sharedPref: SharedPreferences

    // var server = "https://doloserver-569184823432.us-east1.run.app"
    var server = "http://10.0.2.2:8080"

    private var uuid = ""
    private var device = ""

    fun create(context: Context) {
        requestQueue = Volley.newRequestQueue(context)

        sharedPref = context.getSharedPreferences("DoLo Messenger", Context.MODE_PRIVATE)

        server = sharedPref.getString("Server", server) ?: server

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

    fun updateServer(server: String) {
        this.server = server
        sharedPref.edit {
            putString("Server", server)
            apply()
        }
    }

    fun updateToken(token: String, rvm: ReceiverViewModel?) {
        val data = JSONObject()
        try {
            data.put("uuid", uuid)
            data.put("device", device)
            data.put("token", token)
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }

        val url = "$server/updatetoken"
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            data,
            {
                // Nothing to report on success
            },
            { error ->
                rvm?.appendNotification(error.message.toString())
            })

        requestQueue.add(request)
    }

    fun broadcastMessage(message: String, svm: SenderViewModel) {
        val data = JSONObject()
        try {
            data.put("title", "DoLo Message")
            data.put("body", message)
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }

        val url = "$server/broadcast"
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            data,
            { resp ->
                if (!resp.toString().contains("Success", ignoreCase = true)) {
                    svm.appendError(resp.toString())
                }
            },
            { error ->
                svm.appendError(error.message.toString())
            })

        requestQueue.add(request)
    }
}