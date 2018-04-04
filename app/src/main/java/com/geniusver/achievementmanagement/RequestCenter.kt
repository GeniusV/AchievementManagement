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
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.geniusver.achievementmanagement.RequestCenter.CollageRequester.Companion.getMaxPageSize
import com.geniusver.achievementmanagement.RequestCenter.CollageRequester.Companion.processCollageData
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by GeniusV on 3/24/18.
 */

class RequestCenter {

    companion object {
        val apiDomain = "http://192.168.0.101:8080"
    }

    class CollageRequester {
        companion object {

            fun getMaxPageSize(jsonObject: JSONObject): Int{
                val page = jsonObject.getJSONObject("page")
                val totalPage = page.getString("totalPages").toInt()
                return totalPage
            }

            val url = "$apiDomain/collage"
            fun getCollages(page: Int, size: Int, context: Context, successCallback: (List<Collage>, Int) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                        null,
                        Response.Listener<JSONObject> { processCollagesData(it, successCallback) },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            private fun processCollagesData(collageJSONObject: JSONObject, successCallback: (List<Collage>, Int) -> Unit) {
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
                successCallback(result, getMaxPageSize(collageJSONObject))
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
            fun getMajors(page: Int, size: Int, context: Context, successCallback: (List<Major>, Int) -> Unit, errorCallback: (VolleyError) -> Unit, collage: Collage? = null) {
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

            fun processMajorsData(majorJSONObject: JSONObject, successCallback: (List<Major>, Int) -> Unit) {
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
                successCallback(result, getMaxPageSize(majorJSONObject))
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
            fun getCourses(page: Int, size: Int, context: Context, successCallback: (List<Course>, Int) -> Unit, errorCallback: (VolleyError) -> Unit, collage: Collage? = null) {
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

            private fun processCoursesData(courseJSONObject: JSONObject, successCallback: (List<Course>, Int) -> Unit) {
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
                successCallback(result, getMaxPageSize(courseJSONObject))
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
            fun getClaxxs(page: Int, size: Int, context: Context, successCallback: (List<Claxx>, Int) -> Unit, errorCallback: (VolleyError) -> Unit, major: Major? = null) {
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

            private fun processClaxxsData(claxxJSONObject: JSONObject, successCallback: (List<Claxx>, Int) -> Unit) {
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
                successCallback(result, getMaxPageSize(claxxJSONObject))
            }

            fun getClaxx(context: Context, successCallBack: (Claxx) -> Unit, errorCallback: (VolleyError) -> Unit, id: Long? = 0, name: String = "") {

                val requestUrl = if (name == "") "$url/$id" else "$url/search/findByName?name=$name"

                val request = JsonObjectRequest(Request.Method.GET, requestUrl, null,
                        Response.Listener<JSONObject> { processClaxxData(it, successCallBack) },
                        Response.ErrorListener { errorCallback(it) }
                )
                Volley.newRequestQueue(context).add(request)

            }

            fun processClaxxData(claxxJSONObject: JSONObject, successCallback: (Claxx) -> Unit) {
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


    class StudentRequester {
        companion object {
            val url = "$apiDomain/student"
            fun getStudents(page: Int, size: Int, context: Context, successCallback: (List<Student>, Int) -> Unit, errorCallback: (VolleyError) -> Unit, claxx: Claxx? = null) {
                if (claxx != null) {
                    val request = JsonObjectRequest(Request.Method.GET, "$url/search/findByClaxx?page=$page&size=$size&claxx=${ClaxxRequester.url}/${claxx.id}",
                            null,
                            Response.Listener<JSONObject> { processStudentsData(it, successCallback) },
                            Response.ErrorListener { errorCallback(it) })
                    Volley.newRequestQueue(context).add(request)
                } else {
                    val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                            null,
                            Response.Listener<JSONObject> { processStudentsData(it, successCallback) },
                            Response.ErrorListener { errorCallback(it) })
                    Volley.newRequestQueue(context).add(request)
                }

            }


            fun getStudentClaxx(student: Student, context: Context, successCallback: (Claxx) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val request = JsonObjectRequest(Request.Method.GET, "$url/${student.id}/claxx", null,
                        Response.Listener<JSONObject> { ClaxxRequester.processClaxxData(it, successCallback) },
                        Response.ErrorListener(errorCallback))
                Volley.newRequestQueue(context).add(request)
            }

            private fun processStudentsData(studentJSONObject: JSONObject, successCallback: (List<Student>, Int) -> Unit) {
                val embedded = studentJSONObject.getJSONObject("_embedded")
                val student: JSONArray = embedded.getJSONArray("student")
                val result = ArrayList<Student>()
                for (i in 0 until student.length()) {
                    val name = student.getJSONObject(i).getString("name")
                    val links = student.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()
                    result.add(Student(id, name, null))
                }
                successCallback(result,  getMaxPageSize(studentJSONObject))
            }

            fun getStudent(context: Context, successCallBack: (Student) -> Unit, errorCallback: (VolleyError) -> Unit, id: Long? = 0, name: String = "") {

                val requestUrl = if (name == "") "$url/$id" else "$url/search/findByName?name=$name"

                val request = JsonObjectRequest(Request.Method.GET, requestUrl, null,
                        Response.Listener<JSONObject> { processStudentData(it, successCallBack) },
                        Response.ErrorListener { errorCallback(it) }
                )
                Volley.newRequestQueue(context).add(request)

            }

            private fun processStudentData(studentJSONObject: JSONObject, successCallback: (Student) -> Unit) {
                val name = studentJSONObject.getString("name")
                val links = studentJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                successCallback(Student(id, name, null))
            }

            fun postStudent(student: Student, context: Context, successCallBack: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("name", student.name), Pair("claxx", "${ClaxxRequester.url}/${student.claxx!!.id}"))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.POST, url, jsonObject,
                        Response.Listener { successCallBack() },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            fun deleteStudents(students: List<Student>, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val requestQueue = Volley.newRequestQueue(context)
                students.forEachIndexed { index, student ->
                    val id = student.id
                    val request = PostJsonObjectRequest(Request.Method.DELETE, "$url/$id",
                            null,
                            Response.Listener<JSONObject> { if (index == students.lastIndex) successCallback() },
                            Response.ErrorListener { errorCallback(it) })
                    requestQueue.add(request)
                }
            }

            fun patchStudent(student: Student, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("name", student.name), Pair("claxx", "${ClaxxRequester.url}/${student.claxx!!.id}"))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.PATCH, "$url/${student.id}", jsonObject,
                        Response.Listener { successCallback() }, Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }
        }
    }


    class TermRequester {
        companion object {
            val url = "$apiDomain/term"
            fun getTerms(page: Int, size: Int, context: Context, successCallback: (List<Term>, Int) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val request = JsonObjectRequest(Request.Method.GET, "$url?page=$page&size=$size",
                        null,
                        Response.Listener<JSONObject> { processTermsData(it, successCallback) },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            private fun processTermsData(termJSONObject: JSONObject, successCallback: (List<Term>, Int) -> Unit) {
                val embedded = termJSONObject.getJSONObject("_embedded")
                val term: JSONArray = embedded.getJSONArray("term")
                val result = ArrayList<Term>()
                for (i in 0 until term.length()) {
                    val value = term.getJSONObject(i).getString("value")
                    val links = term.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()
                    result.add(Term(id, value))
                }
                successCallback(result, getMaxPageSize(termJSONObject))
            }

            fun getTerm(context: Context, successCallBack: (Term) -> Unit, errorCallback: (VolleyError) -> Unit, id: Long? = 0, value: String = "") {
                val requestUrl = if (value == "") "$url/$id" else "$url/search/findByValue?value=$value"
                val request = JsonObjectRequest(Request.Method.GET, requestUrl, null,
                        Response.Listener<JSONObject> { processTermData(it, successCallBack) },
                        Response.ErrorListener { errorCallback(it) }
                )
                Volley.newRequestQueue(context).add(request)
            }

            fun processTermData(termJSONObject: JSONObject, successCallback: (Term) -> Unit) {
                val value = termJSONObject.getString("value")
                val links = termJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                successCallback(Term(id, value))
            }

            fun postTerm(term: Term, context: Context, successCallBack: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val value = mapOf(Pair("value", term.value))
                val jsonObject = JSONObject(value)
                val request = PostJsonObjectRequest(Request.Method.POST, url, jsonObject,
                        Response.Listener { successCallBack() },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            fun deleteTerms(terms: List<Term>, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val requestQueue = Volley.newRequestQueue(context)
                terms.forEachIndexed { index, term ->
                    val id = term.id
                    val request = PostJsonObjectRequest(Request.Method.DELETE, "$url/$id",
                            null,
                            Response.Listener<JSONObject> { if (index == terms.lastIndex) successCallback() },
                            Response.ErrorListener { errorCallback(it) })
                    requestQueue.add(request)
                }
            }

            fun patchTerm(term: Term, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("value", term.value))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.PATCH, "$url/${term.id}", jsonObject,
                        Response.Listener { successCallback() }, Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }
        }
    }


    class ScoreRequester {
        companion object {
            val url = "$apiDomain/score"
            fun getScores(page: Int, size: Int, context: Context, successCallback: (List<Score>, Int) -> Unit, errorCallback: (VolleyError) -> Unit, student: Student? = null, course: Course? = null, term: Term? = null) {
                val fieldList = ArrayList<String>()
                val paramList = ArrayList<String>()

                if (student != null) fieldList.add("Student"); paramList.add("student=${StudentRequester.url}/${student?.id}")
                if (course != null) fieldList.add("Course"); paramList.add("course=${CourseRequester.url}/${course?.id}")
                if (term != null) fieldList.add("Term"); paramList.add("term=${TermRequester.url}/${term?.id}")

                val methodName = "findBy" + fieldList.joinToString("And")
                val paramString = "&projection=scoreProjection&page=$page&size=$size&" + paramList.joinToString("&")

                val request = JsonObjectRequest(Request.Method.GET, "$url/search/$methodName?$paramString",
                        null,
                        Response.Listener<JSONObject> { processScoresData(it, successCallback) },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            private fun processScoresData(scoreJSONObject: JSONObject, successCallback: (List<Score>, Int) -> Unit) {
                val embedded = scoreJSONObject.getJSONObject("_embedded")
                val score = embedded.getJSONArray("score")
                val result = ArrayList<Score>()
                for (i in 0 until score.length()) {
                    val value = score.getJSONObject(i).getString("value")
                    val links = score.getJSONObject(i).getJSONObject("_links")
                    val self = links.getJSONObject("self")
                    val href = self.getString("href")
                    val id = href.split("/").last().toLong()

                    val student = score.getJSONObject(i).getJSONObject("student")
                    val studentId = student.getLong("id")
                    val studentName = student.getString("name")

                    val course = score.getJSONObject(i).getJSONObject("course")
                    val courseId = course.getLong("id")
                    val courseName = course.getString("name")

                    val term = score.getJSONObject(i).getJSONObject("term")
                    val termId = term.getLong("id")
                    val termValue = term.getString("value")

                    var scoreObject = Score(id, value.toInt()).apply {
                        this.student = Student(studentId, studentName, null)
                        this.course = Course(courseId, courseName, null)
                        this.term = Term(termId, termValue)
                    }

                    result.add(scoreObject)
                }
                successCallback(result, getMaxPageSize(scoreJSONObject))
            }

            fun getScoreCascade(score: Score, position: Int, successCallback: (Score, Int) -> Unit, errorCallback: (VolleyError) -> Unit, context: Context) {
                val queue = Volley.newRequestQueue(context)
                getScoreStudent(score, position, successCallback, errorCallback, queue)
            }

            fun getScoreStudent(score: Score, position: Int, successCallback: (Score, Int) -> Unit, errorCallback: (VolleyError) -> Unit, queue: RequestQueue) {
                val request = JsonObjectRequest(Request.Method.GET, "$url/${score.id}/student", null,
                        Response.Listener<JSONObject> { processScoreStudentAndForwardToCourse(it, score, position, successCallback, errorCallback, queue) },
                        Response.ErrorListener(errorCallback))
                queue.add(request)
            }

            fun processScoreStudentAndForwardToCourse(studentJSONObject: JSONObject, score: Score, position: Int, successCallback: (Score, Int) -> Unit, errorCallback: (VolleyError) -> Unit, queue: RequestQueue) {
                val name = studentJSONObject.getString("name")
                val links = studentJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                score.student = Student(id, name, null)
                getScoreCourse(score, position, successCallback, errorCallback, queue)
            }


            fun getScoreCourse(score: Score, position: Int, successCallback: (Score, Int) -> Unit, errorCallback: (VolleyError) -> Unit, queue: RequestQueue) {
                val request = JsonObjectRequest(Request.Method.GET, "$url/${score.id}/course", null,
                        Response.Listener<JSONObject> { processScoreCourseAndForwardToTerm(it, score, position, successCallback, errorCallback, queue) },
                        Response.ErrorListener(errorCallback))
                queue.add(request)
            }

            fun processScoreCourseAndForwardToTerm(courseJSONObject: JSONObject, score: Score, position: Int, successCallback: (Score, Int) -> Unit, errorCallback: (VolleyError) -> Unit, queue: RequestQueue) {
                val name = courseJSONObject.getString("name")
                val links = courseJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                score.course = Course(id, name, null)
                getScoreTerm(score, position, successCallback, errorCallback, queue)
            }

            fun getScoreTerm(score: Score, position: Int, successCallback: (Score, Int) -> Unit, errorCallback: (VolleyError) -> Unit, queue: RequestQueue) {
                val request = JsonObjectRequest(Request.Method.GET, "$url/${score.id}/term", null,
                        Response.Listener<JSONObject> { processScoreTermAndReturn(it, score, position, successCallback, errorCallback, queue) },
                        Response.ErrorListener(errorCallback))
                queue.add(request)

            }

            fun processScoreTermAndReturn(termJSONObject: JSONObject, score: Score, position: Int, successCallback: (Score, Int) -> Unit, errorCallback: (VolleyError) -> Unit, queue: RequestQueue) {
                val value = termJSONObject.getString("value")
                val links = termJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                score.term = Term(id, value)
                successCallback(score, position)
            }

            fun getScore(context: Context, id: Long?, successCallback: (Score, Int) -> Unit, errorCallback: (VolleyError) -> Unit) {
                val request = JsonObjectRequest(Request.Method.GET, "$url/$id",null,
                Response.Listener<JSONObject> { processScoreData(it, successCallback, errorCallback, context) },
                Response.ErrorListener { errorCallback(it) }
                )
                Volley.newRequestQueue(context).add(request)
            }

            fun processScoreData(scoreJSONObject: JSONObject, successCallback: (Score, Int) -> Unit, errorCallback: (VolleyError) -> Unit, context: Context) {
                val value = scoreJSONObject.getString("value").toInt()
                val links = scoreJSONObject.getJSONObject("_links")
                val self = links.getJSONObject("self")
                val href = self.getString("href")
                val id = href.split("/").last().toLong()
                getScoreCascade(Score(id, value), 0, successCallback, errorCallback,context)
            }

            fun postScore(score: Score, context: Context, successCallBack: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val value = mapOf(Pair("value", score.value))
                val jsonObject = JSONObject(value)
                val request = PostJsonObjectRequest(Request.Method.POST, url, jsonObject,
                        Response.Listener { successCallBack() },
                        Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }

            fun deleteScores(scores: List<Score>, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit, student: Student? = null, course: Course? = null, term: Term? = null) {
                val requestQueue = Volley.newRequestQueue(context)
                scores.forEachIndexed { index, score ->
                    val id = score.id
                    val request = PostJsonObjectRequest(Request.Method.DELETE, "$url/$id",
                            null,
                            Response.Listener<JSONObject> { if (index == scores.lastIndex) successCallback() },
                            Response.ErrorListener { errorCallback(it) })
                    requestQueue.add(request)
                }
            }

            fun patchScore(score: Score, context: Context, successCallback: () -> Unit, errorCallback: (VolleyError) -> Unit) {
                val data = mapOf(Pair("value", score.value))
                val jsonObject = JSONObject(data)
                val request = PostJsonObjectRequest(Request.Method.PATCH, "$url/${score.id}", jsonObject,
                        Response.Listener { successCallback() }, Response.ErrorListener { errorCallback(it) })
                Volley.newRequestQueue(context).add(request)
            }
        }
    }


}


