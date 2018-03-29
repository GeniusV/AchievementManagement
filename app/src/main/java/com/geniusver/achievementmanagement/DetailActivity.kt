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

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.android.volley.VolleyError
import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {


    val refreshList = ArrayList<() -> Unit>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if (Intent.ACTION_SEARCH == intent.action) {
            var query = intent.getStringExtra(SearchManager.QUERY)
            var type = intent.getStringExtra("type")
            when (type) {
                "collage" ->{
                    RequestCenter.CollageRequester.getCollage(query.toLong(), this, {collage -> intent.putExtra("item", collage);setupDetail() }, ::showError)
                }
            }
        } else {
            setupDetail()
        }
    }

    fun setupDetail() {
        val type = intent.getStringExtra("type")


        if (Intent.ACTION_SEARCH == intent.action) {
            var query = intent.getStringExtra(SearchManager.QUERY)
        }


        when (type) {
            "collage" -> {
                val item = intent.getSerializableExtra("item") as Collage
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
                    }, "Major")
                }
            }

        }
        tabs.setupWithViewPager(viewpaper)
        refresh.setOnClickListener {
            refreshList.map { it() }
        }
    }


    fun newMultiChoiceToolbar(): MultiChoiceToolbar {
        return MultiChoiceToolbar.Builder(this, toolbar)
                .setTitles("test", "item selected")
                .setDefaultIcon(R.drawable.ic_back, { onBackPressed() }).build()
    }

    fun showError(volleyError: VolleyError){
        Toast.makeText(applicationContext, "query error", Toast.LENGTH_SHORT).show()
    }
}
