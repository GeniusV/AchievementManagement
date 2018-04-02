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

import android.app.Activity
import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_score_edit.*
import kotlinx.android.synthetic.main.edit_header.*

class ScoreEditActivity : AppCompatActivity() {
    lateinit var action: String
    lateinit var mScore: Score

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_edit)

        setSupportActionBar(toolbar)


        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_cancel)
            setDisplayHomeAsUpEnabled(true)
            title = "Score"
        }

        action = intent.getStringExtra(IntentKey.ACTION)
        val score = intent.getSerializableExtra(IntentKey.ITEM) as Score
        mScore = score

        if (action == IntentValue.Action.UPDATE) {
            supportActionBar?.title = "score: ${score.id}"
            score_value.setText(score.value.toString())
        }


        if(score.student != null) score_student.setText(score.student?.id.toString())
        if(score.course != null) score_course.setText(score.course?.id.toString())
        if(score.term != null) score_term.setText(score.term?.id.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed(); return true
            }
            R.id.menu_ok -> checkCascade()
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    fun checkCascade(){
        checkStudent()
    }

    fun checkStudent(){
        val studentId = score_student.text.toString().trim().toLongOrNull()
        if (studentId == null) {
            AlertDialog.Builder(this).apply {
                setMessage("Student ID is not valid.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
            return
        }
        mScore.student = Student(studentId, "", null)
        RequestCenter.StudentRequester.getStudent(this, ::checkCourse, {
            AlertDialog.Builder(this).apply {
                setMessage("Student ID: $studentId is not exist.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
        }, id = studentId)
    }

    fun checkCourse(student: Student){
        val courseId = score_course.text.toString().trim().toLongOrNull()
        if (courseId == null) {
            AlertDialog.Builder(this).apply {
                setMessage("Course ID is not valid.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
            return
        }
        mScore.course = Course(courseId, "", null)
        RequestCenter.CourseRequester.getCourse(this, ::checkTerm, {
            AlertDialog.Builder(this).apply {
                setMessage("Course ID: $courseId is not exist.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
        }, id = courseId)

    }

    fun checkTerm(course: Course){
        val termId = score_term.text.toString().trim().toLongOrNull()
        if (termId == null) {
            AlertDialog.Builder(this).apply {
                setMessage("Term ID is not valid.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
            return
        }
        mScore.course = Course(termId, "", null)
        RequestCenter.TermRequester.getTerm(this, ::sendScore, {
            AlertDialog.Builder(this).apply {
                setMessage("Term ID: $termId is not exist.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
        }, id = termId)
    }


    fun sendScore(term: Term) {
        val scoreValue = score_value.text.toString().toIntOrNull()
        if (scoreValue == null) {
            AlertDialog.Builder(this).apply {
                setMessage("Score value is not valid.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
            return
        }
        mScore.value = scoreValue
        if (action == IntentValue.Action.INSERT) {
            RequestCenter.ScoreRequester.postScore(mScore, applicationContext,
                    { setResult(Activity.RESULT_OK); finish() }, {
                AlertDialog.Builder(this).apply {
                    setMessage(it.message)
                    setPositiveButton("Ok", { _, _ -> Unit })
                }.create().show()
            })
        } else {
            val score = intent.getSerializableExtra(IntentKey.ITEM) as Score
            RequestCenter.ScoreRequester.patchScore(Score(score.id, scoreValue), applicationContext,
                    { setResult(Activity.RESULT_OK); finish() }, {
                AlertDialog.Builder(this).apply {
                    setMessage(it.message)
                    setPositiveButton("Ok", { _, _ -> Unit })
                }.create().show()
            })
        }
    }
}
