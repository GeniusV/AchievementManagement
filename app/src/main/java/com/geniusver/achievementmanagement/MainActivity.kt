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

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.StudentRecyclerAdapter.StudentViewHolder, com.geniusver.achievementmanagement.Student>().apply {
                mAdapter = com.geniusver.achievementmanagement.StudentRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Student")
            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.CollageRecyclerAdapter.CollageViewHolder, com.geniusver.achievementmanagement.Collage>().apply {
                mAdapter = com.geniusver.achievementmanagement.CollageRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Collage")
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

}
