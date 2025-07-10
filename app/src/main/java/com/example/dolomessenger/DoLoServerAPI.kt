package com.example.dolomessenger

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

object DoLoServerAPI {
    lateinit var requestQueue: RequestQueue

    val url = "https://doloserver-569184823432.us-east1.run.app/broadcast"
    //val url = "http://10.0.2.2:8080/broadcast"

    fun create(context: Context) {
        requestQueue = Volley.newRequestQueue(context)
    }

    fun broadcastMessage(message: String) {
        val data = JSONObject()
        try {
            data.put("title", "Real Title")
            data.put("body", message)
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }

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