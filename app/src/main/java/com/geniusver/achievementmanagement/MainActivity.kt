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
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Identifiable {
    override val identifier: Int
        get() = 1

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
            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.ClaxxRecyclerAdapter.ClaxxViewHolder, com.geniusver.achievementmanagement.Claxx>().apply {
                mAdapter = com.geniusver.achievementmanagement.ClaxxRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Claxx")
            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.StudentRecyclerAdapter.StudentViewHolder, com.geniusver.achievementmanagement.Student>().apply {
                mAdapter = com.geniusver.achievementmanagement.StudentRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Student")
            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.TermRecyclerAdapter.TermViewHolder, com.geniusver.achievementmanagement.Term>().apply {
                mAdapter = com.geniusver.achievementmanagement.TermRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Term")
            addFragment(com.geniusver.achievementmanagement.ContentFragment<com.geniusver.achievementmanagement.ScoreRecyclerAdapter.ScoreViewHolder, com.geniusver.achievementmanagement.Score>().apply {
                mAdapter = com.geniusver.achievementmanagement.ScoreRecyclerAdapter(applicationContext).apply { setMultiChoiceToolbar(newMultiChoiceToolbar()) }
                refreshList.add(this::refresh)
            }, "Score")
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


    override fun startActivity(intent: Intent?) {
        if (Intent.ACTION_SEARCH == intent?.action) {
            intent.putExtra(IntentKey.TYPE, getTabString())
        }
        super.startActivity(intent)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_add -> {
                val type = getTabString()
                var addIntent = Intent()
                when (type) {
                    "collage" -> addIntent = Intent(this, CollageEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                    }
                    "major" -> addIntent = Intent(this, MajorEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                        putExtra(IntentKey.ITEM, Major(0, "", null))
                    }
                    "course" -> addIntent = Intent(this, CourseEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                        putExtra(IntentKey.ITEM, Course(0, "", null))
                    }
                    "claxx" -> addIntent = Intent(this, ClaxxEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                        putExtra(IntentKey.ITEM, Claxx(0, "", null))
                    }
                    "student" -> addIntent = Intent(this, StudentEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                        putExtra(IntentKey.ITEM, Student(0, "", null))
                    }
                    "term" -> addIntent = Intent(this, TermEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                    }
                    "score" -> addIntent = Intent(this, ScoreEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                        putExtra(IntentKey.ITEM, Score(0, 0, ""))
                    }
                }
                startActivityForResult(addIntent, identifier)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
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
