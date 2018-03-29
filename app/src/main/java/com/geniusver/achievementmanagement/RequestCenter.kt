/*
 * Copyright (c) 2018 GeniusV
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.geniusver.achievementmanagement

import android.content.Context
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by GeniusV on 3/24/18.
 */

class RequestCenter {

    companion object {
        val apiDomain = "http://192.168.0.101:8080"
    }

    class StudentRequester {
        companion object {
            fun getStudents(page: Int, size: Int, context: Context, callback: (List<Student>) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val url = "$apiDomain/student"
                val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                        null,
                        Response.Listener<JSONObject> { processStudentsData(it, callback) },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            private fun processStudentsData(studentsJSONObject: JSONObject, successCallback: (List<Student>) -> Unit) {
                val embedded = studentsJSONObject.getJSONObject("_embedded")
                val student: JSONArray = embedded.getJSONArray("student")
                val result = ArrayList<Student>()
                for (i in 0 until student.length()) {
                    val name = student.getJSONObject(i).getString("name")
                    val links = student.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()
                    result.add(Student(id, name))
                }
                successCallback(result)
            }
        }
    }

    class CollageRequester {
        companion object {
            val url = "$apiDomain/collage"
            fun getCollages(page: Int, size: Int, context: Context, callback: (List<Collage>) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                        null,
                        Response.Listener<JSONObject> { processCollagesData(it, callback) },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            private fun processCollagesData(collageJSONObject: JSONObject, successCallback: (List<Collage>) -> Unit) {
                val embedded = collageJSONObject.getJSONObject("_embedded")
                val collage: JSONArray = embedded.getJSONArray("collage")
                val result = ArrayList<Collage>()
                for (i in 0 until collage.length()) {
                    val name = collage.getJSONObject(i).getString("name")
                    val links = collage.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()
                    result.add(Collage(id, name))
                }
                successCallback(result)
            }

            fun getCollage(context: Context, successCallBack: (Collage) -> Unit, errorCallback: (VolleyError) -> Unit, id: Long? = 0, name: String = "") {

                val requestUrl = if (name == "") "$url/$id" else "$url/search/findByName?name=$name"

                val request = JsonObjectRequest(Request.Method.GET, requestUrl, null,
                        Response.Listener<JSONObject> { processCollageData(it, successCallBack) },
                        Response.ErrorListener { errorCallback(it) }
                )
                Volley.newRequestQueue(context).add(request)

            }

            private fun processCollageData(collageJSONObject: JSONObject, successCallback: (Collage) -> Unit) {
                val name = collageJSONObject.getString("name")
                val links = collageJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                successCallback(Collage(id, name))
            }

            fun postCollage(collage: Collage, context: Context, successCallBack: (Boolean) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val name = mapOf<String, String>(Pair<String, String>("name", collage.name))
                val jsonObject = JSONObject(name)
                val request = PostJsonObjectRequest(Request.Method.POST, url, jsonObject, Response.Listener { successCallBack(true) }, Response.ErrorListener { Log.e("request", "error", it);errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }
        }
    }

    class MajorRequester {
        companion object {
            fun getMajors(page: Int, size: Int, context: Context, callback: (List<Major>) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val url = "$apiDomain/major"
                val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                        null,
                        Response.Listener<JSONObject> { processMajorsData(it, callback) },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            private fun processMajorsData(majorJSONObject: JSONObject, successCallback: (List<Major>) -> Unit) {
                val embedded = majorJSONObject.getJSONObject("_embedded")
                val student: JSONArray = embedded.getJSONArray("major")
                val result = ArrayList<Major>()
                for (i in 0 until student.length()) {
                    val name = student.getJSONObject(i).getString("name")
                    val links = student.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()
                    result.add(Major(id, name))
                }
                successCallback(result)
            }
        }
    }

}


