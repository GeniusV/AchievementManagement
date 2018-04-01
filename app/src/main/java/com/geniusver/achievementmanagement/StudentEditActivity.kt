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
import kotlinx.android.synthetic.main.activity_student_edit.*
import kotlinx.android.synthetic.main.edit_header.*

class StudentEditActivity : AppCompatActivity() {
    lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_edit)

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_cancel)
            setDisplayHomeAsUpEnabled(true)
            title = "Student"
        }

        action = intent.getStringExtra(IntentKey.ACTION)

        val student = intent.getSerializableExtra(IntentKey.ITEM) as Student

        if (action == IntentValue.Action.UPDATE) {
            supportActionBar?.title = "Student: ${student.id}"
        }

        if (student.claxx != null) {
            student_claxx.setText((student.claxx.id).toString())
        }
        student_name.setText(student.name)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed(); return true
            }
            R.id.menu_ok -> checkClaxx()
        }
        return super.onOptionsItemSelected(item)
    }

    fun checkClaxx() {
        val claxxId = student_claxx.text.toString().trim().toLongOrNull()
        if (claxxId == null) {
            AlertDialog.Builder(this).apply {
                setMessage("claxx ID is not valid.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
        }
        RequestCenter.ClaxxRequester.getClaxx(this, ::sendStudent, {
            AlertDialog.Builder(this).apply {
                setMessage("claxx ID: $claxxId is not exist.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
        }, id = claxxId)
    }


    fun sendStudent(claxx: Claxx) {
        if (action == IntentValue.Action.INSERT) {
            RequestCenter.StudentRequester.postStudent(Student(0, student_name.text.toString(), claxx), applicationContext,
                    { setResult(Activity.RESULT_OK); finish() }, {
                AlertDialog.Builder(this).apply {
                    setMessage("Name already exists!!")
                    setPositiveButton("Ok", { _, _ -> Unit })
                }.create().show()
            })
        } else {
            val student = intent.getSerializableExtra(IntentKey.ITEM) as Student
            RequestCenter.StudentRequester.patchStudent(Student(student.id, student_name.text.toString(), claxx), applicationContext,
                    { setResult(Activity.RESULT_OK); finish() }, {
                AlertDialog.Builder(this).apply {
                    setMessage("Name already exists!!")
                    setPositiveButton("Ok", { _, _ -> Unit })
                }.create().show()
            })
        }
    }

}
