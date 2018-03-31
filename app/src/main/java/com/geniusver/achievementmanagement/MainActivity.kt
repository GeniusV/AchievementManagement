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
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Identifiable {
    override val identifier: Int
        get() = 1

    var currentTab = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        val refreshList = ArrayList<() -> Unit>()

        viewpaper.adapter = MyPagerAdapter(supportFragmentManager).apply {

            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.CollageRecyclerAdapter.CollageViewHolder, com.geniusver.achievementmanagement.Collage>().apply {
                mAdapter = com.geniusver.achievementmanagement.CollageRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Collage")
            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.MajorRecyclerAdapter.MajorViewHolder, com.geniusver.achievementmanagement.Major>().apply {
                mAdapter = com.geniusver.achievementmanagement.MajorRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Major")
            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.CourseRecyclerAdapter.CourseViewHolder, com.geniusver.achievementmanagement.Course>().apply {
                mAdapter = com.geniusver.achievementmanagement.CourseRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Course")
            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.StudentRecyclerAdapter.StudentViewHolder, com.geniusver.achievementmanagement.Student>().apply {
                mAdapter = com.geniusver.achievementmanagement.StudentRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Student")
        }


        tabs.setupWithViewPager(viewpaper)

        refresh.setOnClickListener {
            refreshList.map { it() }
        }

    }

    fun newMultiChoiceToolbar(): MultiChoiceToolbar {
        return MultiChoiceToolbar.Builder(this, toolbar)
                .setTitles(toolbar.title.toString(), "item selected")
                .setDefaultIcon(R.drawable.ic_menu, {}).build()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Search " + tabs.getTabAt(tabs.selectedTabPosition)?.text as String
        currentTab = tabs.getTabAt(0)?.text.toString()
        tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tabs.getTabAt(tabs.selectedTabPosition)?.text as String
                searchView.queryHint = "Search " + tabs.getTabAt(tabs.selectedTabPosition)?.text as String
            }

        })

        return true
    }

    override fun startActivity(intent: Intent?) {
        if (Intent.ACTION_SEARCH == intent?.action) {
            intent.putExtra(IntentKey.TYPE, getTabString())
        }
        super.startActivity(intent)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_add ->{
                appendOnClick(this, getTabString())
            }
        }
        return super.onOptionsItemSelected(item)
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

    fun getTabString(): String {
        val raw = tabs.getTabAt(tabs.selectedTabPosition)?.text as String
        return raw.toLowerCase()
    }

}
