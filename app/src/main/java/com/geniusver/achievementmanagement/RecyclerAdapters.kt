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
import com.android.volley.VolleyError
import com.geniusver.achievementmanagement.R.id.refresh

/**
 * Created by GeniusV on 3/24/18.
 */

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
        RequestCenter.MajorRequester.deleteMajors(data, context, ::deleteSuccessHandle, ::errorHandle)
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
            val context = holder?.view?.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(IntentKey.ITEM, holder?.major)
            intent.putExtra(IntentKey.TYPE, "major")
            context?.startActivity(intent)
        }
    }

    class MajorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var major: Major? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.name)
    }
}

class CourseRecyclerAdapter(context: Context, var collage: Collage? = null) : BaseRecyclerViewAdapter<CourseRecyclerAdapter.CourseViewHolder, Course>(context) {

    init {
        refresh()
    }

    override fun performDelete(data: List<Course>) {
        RequestCenter.CourseRequester.deleteCourses(data, context, ::deleteSuccessHandle, ::errorHandle)
    }

    override fun queryData(page: Int, size: Int, successCallback: (List<Course>) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.CourseRequester.getCourses(page, size, context, ::add, ::errorHandle, collage)
    }


    override fun newViewHolder(view: View): CourseViewHolder {
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder?, position: Int) {
        var mCourse = values[position] as Course
        holder?.apply {
            course = mCourse
            textView.text = mCourse.name
            imageView.setImageResource(R.drawable.ic_course)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun defaultItemViewClickListener(holder: CourseViewHolder?, position: Int): View.OnClickListener {
        return View.OnClickListener {
            val context = holder?.view?.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(IntentKey.ITEM, holder?.course)
            intent.putExtra(IntentKey.TYPE, "course")
            context?.startActivity(intent)
        }
    }

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var course: Course? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.name)
    }
}

class ClaxxRecyclerAdapter(context: Context, var major: Major? = null) : BaseRecyclerViewAdapter<ClaxxRecyclerAdapter.ClaxxViewHolder, Claxx>(context) {

    init {
        refresh()
    }

    override fun performDelete(data: List<Claxx>) {
        RequestCenter.ClaxxRequester.deleteClaxxs(data, context, ::deleteSuccessHandle, ::errorHandle)
    }

    override fun queryData(page: Int, size: Int, successCallback: (List<Claxx>) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.ClaxxRequester.getClaxxs(page, size, context, ::add, ::errorHandle, major)
    }


    override fun newViewHolder(view: View): ClaxxViewHolder {
        return ClaxxViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClaxxViewHolder?, position: Int) {
        var mClaxx = values[position] as Claxx
        holder?.apply {
            claxx = mClaxx
            textView.text = mClaxx.name
            imageView.setImageResource(R.drawable.ic_claxx)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun defaultItemViewClickListener(holder: ClaxxViewHolder?, position: Int): View.OnClickListener {
        return View.OnClickListener {
            val context = holder?.view?.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(IntentKey.ITEM, holder?.claxx)
            intent.putExtra(IntentKey.TYPE, "claxx")
            context?.startActivity(intent)
        }
    }

    class ClaxxViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var claxx: Claxx? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.name)
    }
}

class StudentRecyclerAdapter(context: Context, var claxx: Claxx? = null) : BaseRecyclerViewAdapter<StudentRecyclerAdapter.StudentViewHolder, Student>(context) {

    init {
        refresh()
    }

    override fun performDelete(data: List<Student>) {
        RequestCenter.StudentRequester.deleteStudents(data, context, ::deleteSuccessHandle, ::errorHandle)
    }

    override fun queryData(page: Int, size: Int, successCallback: (List<Student>) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.StudentRequester.getStudents(page, size, context, ::add, ::errorHandle, claxx)
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
            val context = holder?.view?.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(IntentKey.ITEM, holder?.student)
            intent.putExtra(IntentKey.TYPE, "student")
            context?.startActivity(intent)
        }
    }

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var student: Student? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.name)
    }
}

class TermRecyclerAdapter(context: Context) : BaseRecyclerViewAdapter<TermRecyclerAdapter.TermViewHolder, Term>(context) {
    init {
        refresh()
    }

    override fun performDelete(data: List<Term>) {
        RequestCenter.TermRequester.deleteTerms(data, context, ::deleteSuccessHandle, ::errorHandle)
    }

    override fun queryData(page: Int, size: Int, successCallback: (List<Term>) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.TermRequester.getTerms(page, size, context, ::add, ::errorHandle)
    }


    override fun newViewHolder(view: View): TermViewHolder {
        return TermViewHolder(view)
    }

    override fun onBindViewHolder(holder: TermViewHolder?, position: Int) {
        var mTerm = values[position] as Term
        holder?.apply {
            term = mTerm
            textView.text = mTerm.value
            imageView.setImageResource(R.drawable.ic_term)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun defaultItemViewClickListener(holder: TermViewHolder?, position: Int): View.OnClickListener {
        return View.OnClickListener {
            val context = holder?.view?.context
            val intent = Intent(context, TermEditActivity::class.java)
            intent.putExtra(IntentKey.ITEM, holder?.term)
            intent.putExtra(IntentKey.TYPE, "term")
            intent.putExtra(IntentKey.ACTION, IntentValue.Action.UPDATE)
            context?.startActivity(intent)
        }
    }


    class TermViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var term: Term? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.name)
    }
}

class ScoreRecyclerAdapter(context: Context, var student: Student? = null, var course: Course? = null, var term: Term? = null, val displayMode: String = Score.FULL , val final: Boolean = false) : BaseRecyclerViewAdapter<ScoreRecyclerAdapter.ScoreViewHolder, Score>(context) {

    init {
        refresh()
    }

    override fun performDelete(data: List<Score>) {
        RequestCenter.ScoreRequester.deleteScores(data, context, ::deleteSuccessHandle, ::errorHandle)
    }

    override fun queryData(page: Int, size: Int, successCallback: (List<Score>) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.ScoreRequester.getScores(page, size, context, ::queryCascadeData, ::errorHandle, student, course, term)
    }

    fun queryCascadeData(scoreList: List<Score>){
        scoreList.forEachIndexed{
            index, score -> RequestCenter.ScoreRequester.getScoreCascade(score, index, ::updateScoreCascade, ::errorHandle, context)
        }
    }

    fun updateScoreCascade(score: Score, position: Int){
        for (item in values) {
            if(displayMode == Score.STUDENT && item.student?.id == score.student?.id) return
            if (displayMode == Score.COURSE && item.course?.id == score.course?.id) return
            if(displayMode == Score.TERM && item.term?.id == score.term?.id) return
        }
        values.add(score)
        notifyItemChanged(values.lastIndex)
    }

    override fun newViewHolder(view: View): ScoreViewHolder {
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder?, position: Int) {
        var mScore = values[position] as Score
        holder?.apply {
            score = mScore
            textView.text = mScore.getName(displayMode)
            imageView.setImageResource(R.drawable.ic_score)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun defaultItemViewClickListener(holder: ScoreViewHolder?, position: Int): View.OnClickListener {
        return View.OnClickListener {
            if (final) {
                val context = holder?.view?.context
                val intent = Intent(context, ScoreEditActivity::class.java)
                intent.putExtra(IntentKey.ITEM, holder?.score)
                intent.putExtra(IntentKey.TYPE, "score")
                intent.putExtra(IntentKey.ACTION, IntentValue.Action.UPDATE)
                context?.startActivity(intent)
            }
            else{

            }
        }
    }

    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var score: Score? = null
        var imageView = view.findViewById<ImageView>(R.id.avater)
        var textView = view.findViewById<TextView>(R.id.name)
    }
}



