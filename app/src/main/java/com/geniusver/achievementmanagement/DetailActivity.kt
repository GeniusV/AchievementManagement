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
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.VolleyError
import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(), Identifiable {
    override val identifier: Int
        get() = 2


    val refreshList = ArrayList<() -> Unit>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            val type = intent.getStringExtra(IntentKey.TYPE)
            when (type) {
                "collage" ->
                    RequestCenter.CollageRequester.getCollage(this,
                            { collage -> intent.putExtra(IntentKey.ITEM, collage);setupDetail() }, ::showError,
                            id = query.toLongOrNull(), name = if (query.toLongOrNull() == null) query else "")
            }
        } else {
            setupDetail()
        }
    }

    fun setupDetail() {
        val type = intent.getStringExtra(IntentKey.TYPE)
        when (type) {
            "collage" -> {
                val item = intent.getSerializableExtra(IntentKey.ITEM) as Collage
                collapsing_toolbar.title = item.name
                detail.layoutManager = LinearLayoutManager(this)
                detail.adapter = CollageDetailAdapter(this, item).apply {
                    refreshList.add(this::refresh)
                }

                viewpaper.adapter = MyPagerAdapter(supportFragmentManager).apply {
                    addFragment(ContentFragment<MajorRecyclerAdapter.MajorViewHolder, Major>().apply {
                        mAdapter = MajorRecyclerAdapter(application).apply {
                            setMultiChoiceToolbar(newMultiChoiceToolbar())
                        }
                        refreshList.add(this::refresh)
                        enableEdit = true
                    }, "Major")
                }
            }

        }
        tabs.setupWithViewPager(viewpaper)
        refresh.setOnClickListener {
            refreshList.map { it() }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_edit -> {
                val type = intent.getStringExtra(IntentKey.TYPE)
                when (type) {
                    "collage" ->{
                        val collage = intent.getSerializableExtra(IntentKey.ITEM) as Collage
                        val intent = Intent(this, CollageEditActivity::class.java).apply {
                            putExtra(IntentKey.TYPE, "collage")
                            putExtra(IntentKey.ITEM, collage)
                            putExtra(IntentKey.ACTION, IntentValue.Action.UPDATE)
                        }
                        startActivityForResult(intent, identifier)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun newMultiChoiceToolbar(): MultiChoiceToolbar {
        return MultiChoiceToolbar.Builder(this, toolbar)
                .setTitles("test", "item selected")
                .setDefaultIcon(R.drawable.ic_back, { onBackPressed() }).build()
    }

    fun showError(volleyError: VolleyError) {
        Toast.makeText(applicationContext, "query error", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            identifier -> {
                if (resultCode == Activity.RESULT_OK) {
                    refresh.callOnClick()
                } else {
                    Toast.makeText(this, "response failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}