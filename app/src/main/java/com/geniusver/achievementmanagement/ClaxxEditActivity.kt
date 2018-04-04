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
import kotlinx.android.synthetic.main.activity_claxx_edit.*
import kotlinx.android.synthetic.main.edit_header.*


class ClaxxEditActivity : AppCompatActivity() {
    lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claxx_edit)

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_cancel)
            setDisplayHomeAsUpEnabled(true)
            title = "Claxx"
        }

        action = intent.getStringExtra(IntentKey.ACTION)

        val claxx = intent.getSerializableExtra(IntentKey.ITEM) as Claxx

        if (action == IntentValue.Action.UPDATE) {
            supportActionBar?.title = "Claxx: ${claxx.id}"
        }

        if (claxx.major != null) {
            claxx_major.setText((claxx.major.id).toString())
        }
        claxx_name.setText(claxx.name)
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
            R.id.menu_ok -> checkMajor()
        }
        return super.onOptionsItemSelected(item)
    }

    fun checkMajor() {
        val majorId = claxx_major.text.toString().trim().toLongOrNull()
        if (majorId == null) {
            AlertDialog.Builder(this).apply {
                setMessage("major ID is not valid.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
            return
        }
        RequestCenter.MajorRequester.getMajor(this, ::sendClaxx, {
            AlertDialog.Builder(this).apply {
                setMessage("major ID: $majorId is not exist.")
                setPositiveButton("Ok", { _, _ -> Unit })
            }.create().show()
        }, id = majorId)
    }


    fun sendClaxx(major: Major) {
        if (action == IntentValue.Action.INSERT) {
            RequestCenter.ClaxxRequester.postClaxx(Claxx(0, claxx_name.text.toString(), major), applicationContext,
                    { setResult(Activity.RESULT_OK); finish() }, {
                AlertDialog.Builder(this).apply {
                    setMessage("Name already exists!!")
                    setPositiveButton("Ok", { _, _ -> Unit })
                }.create().show()
            })
        } else {
            val claxx = intent.getSerializableExtra(IntentKey.ITEM) as Claxx
            RequestCenter.ClaxxRequester.patchClaxx(Claxx(claxx.id, claxx_name.text.toString(), major), applicationContext,
                    { setResult(Activity.RESULT_OK); finish() }, {
                AlertDialog.Builder(this).apply {
                    setMessage("Name already exists!!")
                    setPositiveButton("Ok", { _, _ -> Unit })
                }.create().show()
            })
        }
    }

}
