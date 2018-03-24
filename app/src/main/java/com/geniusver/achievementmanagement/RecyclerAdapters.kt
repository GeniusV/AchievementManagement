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
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.VolleyError
import com.bumptech.glide.Glide

/**
 * Created by GeniusV on 3/24/18.
 */

class StudentRecyclerAdapter(context: Context) : BaseRecyclerViewAdapter<StudentRecyclerAdapter.StudentViewHolder, Student>(context) {
    override fun queryData(page: Int, size: Int, successCallback: (List<Student>) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.getStudents(page, size, context, ::add, ::errorHandle)
    }

    override val listItemRecourse: Int

        get() = R.layout.student_list

    override fun newViewHolder(view: View): StudentViewHolder {
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder?, position: Int) {
        var mStudent = values[position] as Student
        holder?.apply {
            student = mStudent
            textView.text = mStudent.name
            Glide.with(holder.imageView.context)
                    .load(R.drawable.ic_student)
                    .fitCenter()
                    .into(holder.imageView)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun defaultItemViewClickListener(holder: StudentViewHolder?, position: Int): View.OnClickListener {
        return super.defaultItemViewClickListener(holder, position)
    }

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var student: Student? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.stu_name)
    }

}