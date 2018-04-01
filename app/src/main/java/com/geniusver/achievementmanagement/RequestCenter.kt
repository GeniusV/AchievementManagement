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
        val apiDomain = "http://192.168.43.224:8080"
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
            fun getCollages(page: Int, size: Int, context: Context, successCallback: (List<Collage>) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                        null,
                        Response.Listener<JSONObject> { processCollagesData(it, successCallback) },
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

            fun processCollageData(collageJSONObject: JSONObject, successCallback: (Collage) -> Unit) {
                val name = collageJSONObject.getString("name")
                val links = collageJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                successCallback(Collage(id, name))
            }

            fun postCollage(collage: Collage, context: Context, successCallBack: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val name = mapOf(Pair("name", collage.name))
                val jsonObject = JSONObject(name)
                val request = PostJsonObjectRequest(Request.Method.POST, url, jsonObject,
                        Response.Listener { successCallBack() },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            fun deleteCollages(collages: List<Collage>, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val requestQueue = Volley.newRequestQueue(context)
                collages.forEachIndexed { index, collage ->
                    val id = collage.id
                    val request = PostJsonObjectRequest(Request.Method.DELETE, "$url/$id",
                            null,
                            Response.Listener<JSONObject> { if (index == collages.lastIndex) successCallback() },
                            Response.ErrorListener { errorCallback(it) })
                    requestQueue.add(request)
                }
            }

            fun patchCollage(collage: Collage, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("name", collage.name))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.PATCH, "$url/${collage.id}", jsonObject,
                        Response.Listener { successCallback() }, Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }
        }
    }

    class MajorRequester {
        companion object {
            val url = "$apiDomain/major"
            fun getMajors(page: Int, size: Int, context: Context, successCallback: (List<Major>) -> Unit, errorCallback: (VolleyError) -> Unit, collage: Collage? = null) {
                if (collage != null) {
                    val request = JsonObjectRequest(Request.Method.GET, "$url/search/findByCollage?page=$page&size=$size&collage=${CollageRequester.url}/${collage.id}",
                            null,
                            Response.Listener<JSONObject> { processMajorsData(it, successCallback) },
                            Response.ErrorListener { errorCallback(it) })
                    Volley.newRequestQueue(context).add(request)
                } else {
                    val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                            null,
                            Response.Listener<JSONObject> { processMajorsData(it, successCallback) },
                            Response.ErrorListener { errorCallback(it) })
                    Volley.newRequestQueue(context).add(request)
                }

            }


            fun getMajorCollage(major: Major, context: Context, successCallback: (Collage) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val request = JsonObjectRequest(Request.Method.GET, "$url/${major.id}/collage", null,
                        Response.Listener<JSONObject> { CollageRequester.processCollageData(it, successCallback) },
                        Response.ErrorListener(errorCallback))
                Volley.newRequestQueue(context).add(request)
            }

            fun processMajorsData(majorJSONObject: JSONObject, successCallback: (List<Major>) -> Unit) {
                val embedded = majorJSONObject.getJSONObject("_embedded")
                val major: JSONArray = embedded.getJSONArray("major")
                val result = ArrayList<Major>()
                for (i in 0 until major.length()) {
                    val name = major.getJSONObject(i).getString("name")
                    val links = major.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()
                    result.add(Major(id, name, null))
                }
                successCallback(result)
            }

            fun getMajor(context: Context, successCallBack: (Major) -> Unit, errorCallback: (VolleyError) -> Unit, id: Long? = 0, name: String = "") {

                val requestUrl = if (name == "") "$url/$id" else "$url/search/findByName?name=$name"

                val request = JsonObjectRequest(Request.Method.GET, requestUrl, null,
                        Response.Listener<JSONObject> { processMajorData(it, successCallBack) },
                        Response.ErrorListener { errorCallback(it) }
                )
                Volley.newRequestQueue(context).add(request)

            }

            fun processMajorData(majorJSONObject: JSONObject, successCallback: (Major) -> Unit) {
                val name = majorJSONObject.getString("name")
                val links = majorJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                successCallback(Major(id, name, null))
            }

            fun postMajor(major: Major, context: Context, successCallBack: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("name", major.name), Pair("collage", "${CollageRequester.url}/${major.collage!!.id}"))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.POST, url, jsonObject,
                        Response.Listener { successCallBack() },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            fun deleteMajors(majors: List<Major>, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val requestQueue = Volley.newRequestQueue(context)
                majors.forEachIndexed { index, major ->
                    val id = major.id
                    val request = PostJsonObjectRequest(Request.Method.DELETE, "$url/$id",
                            null,
                            Response.Listener<JSONObject> { if (index == majors.lastIndex) successCallback() },
                            Response.ErrorListener { errorCallback(it) })
                    requestQueue.add(request)
                }
            }

            fun patchMajor(major: Major, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("name", major.name), Pair("collage", "${CollageRequester.url}/${major.collage!!.id}"))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.PATCH, "$url/${major.id}", jsonObject,
                        Response.Listener { successCallback() }, Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }
        }
    }


    class CourseRequester {
        companion object {
            val url = "$apiDomain/course"
            fun getCourses(page: Int, size: Int, context: Context, successCallback: (List<Course>) -> Unit, errorCallback: (VolleyError) -> Unit, collage: Collage? = null) {
                if (collage != null) {
                    val request = JsonObjectRequest(Request.Method.GET, "$url/search/findByCollage?page=$page&size=$size&collage=${CollageRequester.url}/${collage.id}",
                            null,
                            Response.Listener<JSONObject> { processCoursesData(it, successCallback) },
                            Response.ErrorListener { errorCallback(it) })
                    Volley.newRequestQueue(context).add(request)
                } else {
                    val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                            null,
                            Response.Listener<JSONObject> { processCoursesData(it, successCallback) },
                            Response.ErrorListener { errorCallback(it) })
                    Volley.newRequestQueue(context).add(request)
                }

            }


            fun getCourseCollage(course: Course, context: Context, successCallback: (Collage) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val request = JsonObjectRequest(Request.Method.GET, "$url/${course.id}/collage", null,
                        Response.Listener<JSONObject> { CollageRequester.processCollageData(it, successCallback) },
                        Response.ErrorListener(errorCallback))
                Volley.newRequestQueue(context).add(request)
            }

            private fun processCoursesData(courseJSONObject: JSONObject, successCallback: (List<Course>) -> Unit) {
                val embedded = courseJSONObject.getJSONObject("_embedded")
                val course: JSONArray = embedded.getJSONArray("course")
                val result = ArrayList<Course>()
                for (i in 0 until course.length()) {
                    val name = course.getJSONObject(i).getString("name")
                    val links = course.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()
                    result.add(Course(id, name, null))
                }
                successCallback(result)
            }

            fun getCourse(context: Context, successCallBack: (Course) -> Unit, errorCallback: (VolleyError) -> Unit, id: Long? = 0, name: String = "") {

                val requestUrl = if (name == "") "$url/$id" else "$url/search/findByName?name=$name"

                val request = JsonObjectRequest(Request.Method.GET, requestUrl, null,
                        Response.Listener<JSONObject> { processCourseData(it, successCallBack) },
                        Response.ErrorListener { errorCallback(it) }
                )
                Volley.newRequestQueue(context).add(request)

            }

            private fun processCourseData(courseJSONObject: JSONObject, successCallback: (Course) -> Unit) {
                val name = courseJSONObject.getString("name")
                val links = courseJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                successCallback(Course(id, name, null))
            }

            fun postCourse(course: Course, context: Context, successCallBack: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("name", course.name), Pair("collage", "${CollageRequester.url}/${course.collage!!.id}"))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.POST, url, jsonObject,
                        Response.Listener { successCallBack() },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            fun deleteCourses(courses: List<Course>, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val requestQueue = Volley.newRequestQueue(context)
                courses.forEachIndexed { index, course ->
                    val id = course.id
                    val request = PostJsonObjectRequest(Request.Method.DELETE, "$url/$id",
                            null,
                            Response.Listener<JSONObject> { if (index == courses.lastIndex) successCallback() },
                            Response.ErrorListener { errorCallback(it) })
                    requestQueue.add(request)
                }
            }

            fun patchCourse(course: Course, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("name", course.name), Pair("collage", "${CollageRequester.url}/${course.collage!!.id}"))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.PATCH, "$url/${course.id}", jsonObject,
                        Response.Listener { successCallback() }, Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }
        }
    }

    class ClaxxRequester {
        companion object {
            val url = "$apiDomain/claxx"
            fun getClaxxs(page: Int, size: Int, context: Context, successCallback: (List<Claxx>) -> Unit, errorCallback: (VolleyError) -> Unit, major: Major? = null) {
                if (major != null) {
                    val request = JsonObjectRequest(Request.Method.GET, "$url/search/findByMajor?page=$page&size=$size&major=${MajorRequester.url}/${major.id}",
                            null,
                            Response.Listener<JSONObject> { processClaxxsData(it, successCallback) },
                            Response.ErrorListener { errorCallback(it) })
                    Volley.newRequestQueue(context).add(request)
                } else {
                    val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                            null,
                            Response.Listener<JSONObject> { processClaxxsData(it, successCallback) },
                            Response.ErrorListener { errorCallback(it) })
                    Volley.newRequestQueue(context).add(request)
                }

            }


            fun getClaxxMajor(claxx: Claxx, context: Context, successCallback: (Major) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val request = JsonObjectRequest(Request.Method.GET, "$url/${claxx.id}/major", null,
                        Response.Listener<JSONObject> { MajorRequester.processMajorData(it, successCallback) },
                        Response.ErrorListener(errorCallback))
                Volley.newRequestQueue(context).add(request)
            }

            private fun processClaxxsData(claxxJSONObject: JSONObject, successCallback: (List<Claxx>) -> Unit) {
                val embedded = claxxJSONObject.getJSONObject("_embedded")
                val claxx: JSONArray = embedded.getJSONArray("claxx")
                val result = ArrayList<Claxx>()
                for (i in 0 until claxx.length()) {
                    val name = claxx.getJSONObject(i).getString("name")
                    val links = claxx.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()
                    result.add(Claxx(id, name, null))
                }
                successCallback(result)
            }

            fun getClaxx(context: Context, successCallBack: (Claxx) -> Unit, errorCallback: (VolleyError) -> Unit, id: Long? = 0, name: String = "") {

                val requestUrl = if (name == "") "$url/$id" else "$url/search/findByName?name=$name"

                val request = JsonObjectRequest(Request.Method.GET, requestUrl, null,
                        Response.Listener<JSONObject> { processClaxxData(it, successCallBack) },
                        Response.ErrorListener { errorCallback(it) }
                )
                Volley.newRequestQueue(context).add(request)

            }

            private fun processClaxxData(claxxJSONObject: JSONObject, successCallback: (Claxx) -> Unit) {
                val name = claxxJSONObject.getString("name")
                val links = claxxJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                successCallback(Claxx(id, name, null))
            }

            fun postClaxx(claxx: Claxx, context: Context, successCallBack: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("name", claxx.name), Pair("major", "${MajorRequester.url}/${claxx.major!!.id}"))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.POST, url, jsonObject,
                        Response.Listener { successCallBack() },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            fun deleteClaxxs(claxxs: List<Claxx>, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val requestQueue = Volley.newRequestQueue(context)
                claxxs.forEachIndexed { index, claxx ->
                    val id = claxx.id
                    val request = PostJsonObjectRequest(Request.Method.DELETE, "$url/$id",
                            null,
                            Response.Listener<JSONObject> { if (index == claxxs.lastIndex) successCallback() },
                            Response.ErrorListener { errorCallback(it) })
                    requestQueue.add(request)
                }
            }

            fun patchClaxx(claxx: Claxx, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("name", claxx.name), Pair("major", "${MajorRequester.url}/${claxx.major!!.id}"))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.PATCH, "$url/${claxx.id}", jsonObject,
                        Response.Listener { successCallback() }, Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }
        }
    }

}


