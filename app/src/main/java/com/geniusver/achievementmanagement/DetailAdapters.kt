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
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.VolleyError

/**
 * Created by GeniusV on 3/28/18.
 */
class CollageDetailAdapter(context: Context, val collage: Collage) : DetailAdapter<Collage>(context, collage) {
    override val id: Long
        get() = collage.id

    override fun generateList() {
        values = listOf(
                DetailAdapter.DetailData("ID: " + entity.id, false),
                DetailAdapter.DetailData("Name: " + entity.name, false)
        )
        notifyDataSetChanged()
    }


    override fun queryDetail(successCallback: (Collage) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.CollageRequester.getCollage(context, successCallback, errorCallback, id)
    }
}

class MajorDetailAdapter(context: Context, val major: Major) : DetailAdapter<Major>(context, major) {

    lateinit var mcollage: Collage

    init {
        ensureCollageName()
    }

    fun ensureCollageName() {
        RequestCenter.MajorRequester.getMajorCollage(major, context, ::onCollageNameReceived, ::errorHandle)
    }

    fun onCollageNameReceived(collage: Collage){
        mcollage = collage
        entity = Major(entity.id, entity.name, collage)
        generateList()
    }

    override fun defaultItemViewClickListener(view: View): View.OnClickListener {
        return View.OnClickListener {
            if (view.findViewById<TextView>(R.id.detail_text).text.toString().startsWith("collage") &&
                    view.findViewById<ImageView>(R.id.detail_icon).visibility == View.VISIBLE) {
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(IntentKey.TYPE, "collage")
                    putExtra(IntentKey.ITEM, mcollage)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun refresh() {
        ensureCollageName()
        super.refresh()
    }

    override val id: Long
        get() = major.id

    override fun generateList() {
        values = listOf(
                DetailAdapter.DetailData("ID: " + entity.id, false),
                DetailAdapter.DetailData("Name: " + entity.name, false),
                DetailAdapter.DetailData("collage: " + entity.collage?.name, true)
        )
        notifyDataSetChanged()
    }

    override fun queryDetail(successCallback: (Major) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.MajorRequester.getMajor(context, successCallback, errorCallback, id)
    }
}


class CourseDetailAdapter(context: Context, val course: Course) : DetailAdapter<Course>(context, course) {

    lateinit var mcollage: Collage

    init {
        ensureCollageName()
    }

    fun ensureCollageName() {
        RequestCenter.CourseRequester.getCourseCollage(course, context, ::onCollageNameReceived, ::errorHandle)
    }

    fun onCollageNameReceived(collage: Collage){
        mcollage = collage
        entity = Course(entity.id, entity.name, collage)
        generateList()
    }

    override fun defaultItemViewClickListener(view: View): View.OnClickListener {
        return View.OnClickListener {
            if (view.findViewById<TextView>(R.id.detail_text).text.toString().startsWith("collage") &&
                    view.findViewById<ImageView>(R.id.detail_icon).visibility == View.VISIBLE) {
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(IntentKey.TYPE, "collage")
                    putExtra(IntentKey.ITEM, mcollage)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun refresh() {
        ensureCollageName()
        super.refresh()
    }

    override val id: Long
        get() = course.id

    override fun generateList() {
        values = listOf(
                DetailAdapter.DetailData("ID: " + entity.id, false),
                DetailAdapter.DetailData("Name: " + entity.name, false),
                DetailAdapter.DetailData("collage: " + entity.collage?.name, true)
        )
        notifyDataSetChanged()
    }

    override fun queryDetail(successCallback: (Course) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.CourseRequester.getCourse(context, successCallback, errorCallback, id)
    }
}

class ClaxxDetailAdapter(context: Context, val claxx: Claxx) : DetailAdapter<Claxx>(context, claxx) {

    lateinit var mmajor: Major

    init {
        ensureMajorName()
    }

    fun ensureMajorName() {
        RequestCenter.ClaxxRequester.getClaxxMajor(claxx, context, ::onMajorNameReceived, ::errorHandle)
    }

    fun onMajorNameReceived(major: Major){
        mmajor = major
        entity = Claxx(entity.id, entity.name, major)
        generateList()
    }

    override fun defaultItemViewClickListener(view: View): View.OnClickListener {
        return View.OnClickListener {
            if (view.findViewById<TextView>(R.id.detail_text).text.toString().startsWith("major") &&
                    view.findViewById<ImageView>(R.id.detail_icon).visibility == View.VISIBLE) {
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(IntentKey.TYPE, "major")
                    putExtra(IntentKey.ITEM, mmajor)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun refresh() {
        ensureMajorName()
        super.refresh()
    }

    override val id: Long
        get() = claxx.id

    override fun generateList() {
        values = listOf(
                DetailAdapter.DetailData("ID: " + entity.id, false),
                DetailAdapter.DetailData("Name: " + entity.name, false),
                DetailAdapter.DetailData("major: " + entity.major?.name, true)
        )
        notifyDataSetChanged()
    }

    override fun queryDetail(successCallback: (Claxx) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.ClaxxRequester.getClaxx(context, successCallback, errorCallback, id)
    }
}

class StudentDetailAdapter(context: Context, val student: Student) : DetailAdapter<Student>(context, student) {

    lateinit var mclaxx: Claxx

    init {
        ensureClaxxName()
    }

    fun ensureClaxxName() {
        RequestCenter.StudentRequester.getStudentClaxx(student, context, ::onClaxxNameReceived, ::errorHandle)
    }

    fun onClaxxNameReceived(claxx: Claxx){
        mclaxx = claxx
        entity = Student(entity.id, entity.name, claxx)
        generateList()
    }

    override fun defaultItemViewClickListener(view: View): View.OnClickListener {
        return View.OnClickListener {
            if (view.findViewById<TextView>(R.id.detail_text).text.toString().startsWith("claxx") &&
                    view.findViewById<ImageView>(R.id.detail_icon).visibility == View.VISIBLE) {
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(IntentKey.TYPE, "claxx")
                    putExtra(IntentKey.ITEM, mclaxx)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun refresh() {
        ensureClaxxName()
        super.refresh()
    }

    override val id: Long
        get() = student.id

    override fun generateList() {
        values = listOf(
                DetailAdapter.DetailData("ID: " + entity.id, false),
                DetailAdapter.DetailData("Name: " + entity.name, false),
                DetailAdapter.DetailData("claxx: " + entity.claxx?.name, true)
        )
        notifyDataSetChanged()
    }

    override fun queryDetail(successCallback: (Student) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.StudentRequester.getStudent(context, successCallback, errorCallback, id)
    }
}



