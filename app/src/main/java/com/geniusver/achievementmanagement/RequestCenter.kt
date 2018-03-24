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
import org.json.JSONObject

/**
 * Created by GeniusV on 3/24/18.
 */

class RequestCenter {

    companion object {
        val apiDomain = "http://localhost:8080"

        fun getStudents(page: Int, size: Int, context: Context, callback: (List<Student>) -> Unit, errorCallback: (VolleyError) -> Unit) {
            val url  = "$apiDomain/student"
            val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                    null,
                    Response.Listener<JSONObject> { processStudentData(it, callback) },
                    Response.ErrorListener {errorCallback(it)})
            Volley.newRequestQueue(context).add(request)
        }

        private fun processStudentsData(studentsJSONObject: JSONObject, successCallback: (List<Student>) -> Unit){
            //todo
        }

        fun getStudent(id: Int, name: String = "", context: Context, callback: (List<Student>) -> Unit){
            //todo
        }



        private fun processStudentData(studentsJSONObject: JSONObject, successCallback: (List<Student>) -> Unit) {
            //todo
        }


    }

}


