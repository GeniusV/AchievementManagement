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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by GeniusV on 3/24/18.
 */

class RequestCenter {

    companion object {
        val apiDomain = "http://192.168.1.109:8080"
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
            fun getCollages(page: Int, size: Int, context: Context, callback: (List<Collage>) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val url = "$apiDomain/collage"
                val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                        null,
                        Response.Listener<JSONObject> { processCollagesData(it, callback) },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            private fun processCollagesData(collageJSONObject: JSONObject, successCallback: (List<Collage>) -> Unit) {
                val embedded = collageJSONObject.getJSONObject("_embedded")
                val student: JSONArray = embedded.getJSONArray("collage")
                val result = ArrayList<Collage>()
                for (i in 0 until student.length()) {
                    val name = student.getJSONObject(i).getString("name")
                    val links = student.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()
                    result.add(Collage(id, name))
                }
                successCallback(result)
            }
        }
    }

}


