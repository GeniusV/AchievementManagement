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
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError

/**
 * Created by GeniusV on 3/24/18.
 */

class StudentRecyclerAdapter(context: Context) : BaseRecyclerViewAdapter<StudentRecyclerAdapter.StudentViewHolder, Student>(context) {
    override fun performDelete(data: List<Student>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryData(page: Int, size: Int, successCallback: (List<Student>) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.StudentRequester.getStudents(page, size, context, ::add, ::errorHandle)
    }

    override fun newViewHolder(view: View): StudentViewHolder {
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder?, position: Int) {
        var mStudent = values[position] as Student
        holder?.apply {
            student = mStudent
            textView.text = mStudent.name
            imageView.setImageResource(R.drawable.ic_student)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun defaultItemViewClickListener(holder: StudentViewHolder?, position: Int): View.OnClickListener {
        return View.OnClickListener {
            Toast.makeText(context, "student clicked", Toast.LENGTH_SHORT).show()
        }
    }

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var student: Student? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.name)
    }
}


class CollageRecyclerAdapter(context: Context) : BaseRecyclerViewAdapter<CollageRecyclerAdapter.CollageViewHolder, Collage>(context) {
    init {
        refresh()
    }

    override fun performDelete(data: List<Collage>) {
        RequestCenter.CollageRequester.deleteCollages(data, context, ::deleteSuccessHandle, ::errorHandle)
    }

    override fun queryData(page: Int, size: Int, successCallback: (List<Collage>) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.CollageRequester.getCollages(page, size, context, ::add, ::errorHandle)
    }


    override fun newViewHolder(view: View): CollageViewHolder {
        return CollageViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollageViewHolder?, position: Int) {
        var mCollage = values[position] as Collage
        holder?.apply {
            collage = mCollage
            textView.text = mCollage.name
            imageView.setImageResource(R.drawable.ic_collage)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun defaultItemViewClickListener(holder: CollageViewHolder?, position: Int): View.OnClickListener {
        return View.OnClickListener {
            val context = holder?.view?.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(IntentKey.ITEM, holder?.collage)
            intent.putExtra(IntentKey.TYPE, "collage")
            context?.startActivity(intent)
        }
    }

//    override fun deleteSelectedData() {
//        val collages = selectedItemList.map {
//            values[it]
//        }
//    }


    class CollageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var collage: Collage? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.name)
    }
}

class MajorRecyclerAdapter(context: Context, var collage: Collage? = null) : BaseRecyclerViewAdapter<MajorRecyclerAdapter.MajorViewHolder, Major>(context) {

    init {
        refresh()
    }

    override fun performDelete(data: List<Major>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryData(page: Int, size: Int, successCallback: (List<Major>) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.MajorRequester.getMajors(page, size, context, ::add, ::errorHandle, collage)
    }


    override fun newViewHolder(view: View): MajorViewHolder {
        return MajorViewHolder(view)
    }

    override fun onBindViewHolder(holder: MajorViewHolder?, position: Int) {
        var mMajor = values[position] as Major
        holder?.apply {
            major = mMajor
            textView.text = mMajor.name
            imageView.setImageResource(R.drawable.ic_major)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun defaultItemViewClickListener(holder: MajorViewHolder?, position: Int): View.OnClickListener {
        return View.OnClickListener {
            Toast.makeText(context, "major clicked", Toast.LENGTH_SHORT).show()
        }
    }

    class MajorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var major: Major? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.name)
    }
}