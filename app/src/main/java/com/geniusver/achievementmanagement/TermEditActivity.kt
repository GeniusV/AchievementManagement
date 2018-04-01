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
import kotlinx.android.synthetic.main.edit_header.*
import kotlinx.android.synthetic.main.activity_term_edit.*

class TermEditActivity : AppCompatActivity() {
    lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term_edit)

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_cancel)
            setDisplayHomeAsUpEnabled(true)
            title = "term"
        }

        action = intent.getStringExtra(IntentKey.ACTION)

        if (action == IntentValue.Action.UPDATE) {
            val term = intent.getSerializableExtra(IntentKey.ITEM) as Term
            supportActionBar?.title = "term: ${term.id}"
            term_value.setText(term.value)
        }

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
            R.id.menu_ok -> {
                if (action == IntentValue.Action.INSERT) {
                    RequestCenter.TermRequester.postTerm(Term(0, term_value.text.toString()), applicationContext,
                            { setResult(Activity.RESULT_OK); finish() }, {
                        AlertDialog.Builder(this).apply {
                            setMessage("Name already exists!!")
                            setPositiveButton("Ok", { _, _ -> Unit })
                        }.create().show()
                    })
                } else {
                    val term = intent.getSerializableExtra(IntentKey.ITEM) as Term
                    RequestCenter.TermRequester.patchTerm(Term(term.id, term_value.text.toString()), applicationContext,
                            { setResult(Activity.RESULT_OK); finish() }, {
                        AlertDialog.Builder(this).apply {
                            setMessage("Name already exists!!")
                            setPositiveButton("Ok", { _, _ -> Unit })
                        }.create().show()
                    })
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
