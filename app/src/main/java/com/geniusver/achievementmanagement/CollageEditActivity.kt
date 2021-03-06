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
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_collage_edit.*
import kotlinx.android.synthetic.main.edit_header.*

class CollageEditActivity : AppCompatActivity() {
    lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collage_edit)

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_cancel)
            setDisplayHomeAsUpEnabled(true)
            title = "Collage"
        }

        action = intent.getStringExtra(IntentKey.ACTION)

        if (action == IntentValue.Action.UPDATE) {
            val collage = intent.getSerializableExtra(IntentKey.ITEM) as Collage
            supportActionBar?.title = "collage: ${collage.id}"
            collage_name.setText(collage.name)
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
                    RequestCenter.CollageRequester.postCollage(Collage(0, collage_name.text.toString()), applicationContext,
                            { setResult(Activity.RESULT_OK); finish() }, {
                        AlertDialog.Builder(this).apply {
                            setMessage("Name already exists!!")
                            setPositiveButton("Ok", { _, _ -> Unit })
                        }.create().show()
                    })
                } else {
                    val collage = intent.getSerializableExtra(IntentKey.ITEM) as Collage
                    RequestCenter.CollageRequester.patchCollage(Collage(collage.id, collage_name.text.toString()), applicationContext,
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
