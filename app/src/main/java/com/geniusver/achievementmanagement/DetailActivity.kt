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
import android.content.Entity
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

    val entityMap = HashMap<String, () -> Data>()

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
                "major" ->
                    RequestCenter.MajorRequester.getMajor(this,
                            { major -> intent.putExtra(IntentKey.ITEM, major);setupDetail() }, ::showError,
                            id = query.toLongOrNull(), name = if (query.toLongOrNull() == null) query else "")
                "course" ->
                    RequestCenter.CourseRequester.getCourse(this,
                            { course -> intent.putExtra(IntentKey.ITEM, course);setupDetail() }, ::showError,
                            id = query.toLongOrNull(), name = if (query.toLongOrNull() == null) query else "")
                "claxx" ->
                    RequestCenter.ClaxxRequester.getClaxx(this,
                            { claxx -> intent.putExtra(IntentKey.ITEM, claxx);setupDetail() }, ::showError,
                            id = query.toLongOrNull(), name = if (query.toLongOrNull() == null) query else "")
                "student" ->
                    RequestCenter.StudentRequester.getStudent(this,
                            { student -> intent.putExtra(IntentKey.ITEM, student);setupDetail() }, ::showError,
                            id = query.toLongOrNull(), name = if (query.toLongOrNull() == null) query else "")
                "term" ->
                    RequestCenter.TermRequester.getTerm(this,
                            { term ->
                                val intent = Intent(this, TermEditActivity::class.java)
                                intent.putExtra(IntentKey.ITEM, term)
                                intent.putExtra(IntentKey.TYPE, "term")
                                intent.putExtra(IntentKey.ACTION, IntentValue.Action.UPDATE)
                                startActivity(intent)
                            }, ::showError,
                            id = query.toLongOrNull(), value = if (query.toLongOrNull() == null) query else "")
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
                collapsing_toolbar.title = "Collage - ${item.name}"
                detail.layoutManager = LinearLayoutManager(this)
                detail.adapter = CollageDetailAdapter(this, item).apply {
                    refreshList.add(this::refresh)
                    entityMap["collage"] = this::entity
                }

                viewpaper.adapter = MyPagerAdapter(supportFragmentManager).apply {
                    addFragment(ContentFragment<MajorRecyclerAdapter.MajorViewHolder, Major>().apply {
                        mAdapter = MajorRecyclerAdapter(application, item).apply {
                            setMultiChoiceToolbar(newMultiChoiceToolbar())
                        }
                        refreshList.add(this::refresh)
                        enableEdit = true
                    }, "Major")
                    addFragment(ContentFragment<CourseRecyclerAdapter.CourseViewHolder, Course>().apply {
                        mAdapter = CourseRecyclerAdapter(application, item).apply {
                            setMultiChoiceToolbar(newMultiChoiceToolbar())
                        }
                        refreshList.add(this::refresh)
                        enableEdit = true
                    }, "Course")
                }
            }
            "major" -> {
                val item = intent.getSerializableExtra(IntentKey.ITEM) as Major
                collapsing_toolbar.title = "Major - ${item.name}"
                detail.layoutManager = LinearLayoutManager(this)
                detail.adapter = MajorDetailAdapter(this, item).apply {
                    refreshList.add(this::refresh)
                    entityMap["major"] = this::entity
                }

                viewpaper.adapter = MyPagerAdapter(supportFragmentManager).apply {
                    addFragment(ContentFragment<ClaxxRecyclerAdapter.ClaxxViewHolder, Claxx>().apply {
                        mAdapter = ClaxxRecyclerAdapter(application, item).apply {
                            setMultiChoiceToolbar(newMultiChoiceToolbar())
                        }
                        refreshList.add(this::refresh)
                        enableEdit = true
                    }, "Claxx")
                }
            }

            "course" -> {
                val item = intent.getSerializableExtra(IntentKey.ITEM) as Course
                collapsing_toolbar.title = "Course - ${item.name}"
                detail.layoutManager = LinearLayoutManager(this)
                detail.adapter = CourseDetailAdapter(this, item).apply {
                    refreshList.add(this::refresh)
                    entityMap["course"] = this::entity
                }
                //todo
            }
            "claxx" -> {
                val item = intent.getSerializableExtra(IntentKey.ITEM) as Claxx
                collapsing_toolbar.title = "Claxx - ${item.name}"
                detail.layoutManager = LinearLayoutManager(this)
                detail.adapter = ClaxxDetailAdapter(this, item).apply {
                    refreshList.add(this::refresh)
                    entityMap["claxx"] == this::entity
                }
                viewpaper.adapter = MyPagerAdapter(supportFragmentManager).apply {
                    addFragment(ContentFragment<StudentRecyclerAdapter.StudentViewHolder, Student>().apply {
                        mAdapter = StudentRecyclerAdapter(application, item).apply {
                            setMultiChoiceToolbar(newMultiChoiceToolbar())
                        }
                        refreshList.add(this::refresh)
                        enableEdit = true
                    }, "Student")
                }

            }
            "student" -> {
                val item = intent.getSerializableExtra(IntentKey.ITEM) as Student
                collapsing_toolbar.title = "Student - ${item.name}"
                detail.layoutManager = LinearLayoutManager(this)
                detail.adapter = StudentDetailAdapter(this, item).apply {
                    refreshList.add(this::refresh)
                    entityMap["student"] = this::entity
                }

                viewpaper.adapter = MyPagerAdapter(supportFragmentManager).apply {
                    addFragment(ContentFragment<ScoreRecyclerAdapter.ScoreViewHolder, Score>().apply {
                        mAdapter = ScoreRecyclerAdapter(applicationContext, student = item, displayMode = Score.COURSE).apply {
                            setMultiChoiceToolbar(newMultiChoiceToolbar())
                        }
                        refreshList.add(this::refresh)
                        enableEdit = true
                    }, "Course")
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
                    "collage" -> {
                        val intent = Intent(this, CollageEditActivity::class.java).apply {
                            putExtra(IntentKey.TYPE, "collage")
                            putExtra(IntentKey.ITEM, entityMap["collage"]?.invoke())
                            putExtra(IntentKey.ACTION, IntentValue.Action.UPDATE)
                        }
                        startActivityForResult(intent, identifier)
                    }
                    "major" -> {
                        val intent = Intent(this, MajorEditActivity::class.java).apply {
                            putExtra(IntentKey.TYPE, "major")
                            putExtra(IntentKey.ITEM, entityMap["major"]?.invoke())
                            putExtra(IntentKey.ACTION, IntentValue.Action.UPDATE)
                        }
                        startActivityForResult(intent, identifier)
                    }
                    "course" -> {
                        val intent = Intent(this, CourseEditActivity::class.java).apply {
                            putExtra(IntentKey.TYPE, "course")
                            putExtra(IntentKey.ITEM, entityMap["course"]?.invoke())
                            putExtra(IntentKey.ACTION, IntentValue.Action.UPDATE)
                        }
                        startActivityForResult(intent, identifier)
                    }
                    "claxx" -> {
                        val intent = Intent(this, ClaxxEditActivity::class.java).apply {
                            putExtra(IntentKey.TYPE, "claxx")
                            putExtra(IntentKey.ITEM, entityMap["claxx"]?.invoke())
                            putExtra(IntentKey.ACTION, IntentValue.Action.UPDATE)
                        }
                        startActivityForResult(intent, identifier)
                    }
                    "student" -> {
                        val intent = Intent(this, StudentEditActivity::class.java).apply {
                            putExtra(IntentKey.TYPE, "student")
                            putExtra(IntentKey.ITEM, entityMap["student"]?.invoke())
                            putExtra(IntentKey.ACTION, IntentValue.Action.UPDATE)
                        }
                        startActivityForResult(intent, identifier)
                    }
                }
            }
            R.id.menu_add -> {
                val type = getTabString()
                var addIntent = Intent()
                when (type) {
                    "collage" -> addIntent = Intent(this, CollageEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                    }
                    "major" -> addIntent = Intent(this, MajorEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                        putExtra(IntentKey.ITEM, Major(0, "", intent.getSerializableExtra(IntentKey.ITEM) as Collage))
                    }
                    "course" -> addIntent = Intent(this, CourseEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                        putExtra(IntentKey.ITEM, Course(0, "", intent.getSerializableExtra(IntentKey.ITEM) as Collage))
                    }
                    "claxx" -> addIntent = Intent(this, ClaxxEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                        putExtra(IntentKey.ITEM, Claxx(0, "", intent.getSerializableExtra(IntentKey.ITEM) as Major))
                    }
                    "student" -> addIntent = Intent(this, StudentEditActivity::class.java).apply {
                        putExtra(IntentKey.ACTION, IntentValue.Action.INSERT)
                        putExtra(IntentKey.ITEM, Student(0, "", intent.getSerializableExtra(IntentKey.ITEM) as Claxx))
                    }
                }
                startActivityForResult(addIntent, identifier)
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


    override fun startActivity(intent: Intent?) {
        if (Intent.ACTION_SEARCH == intent?.action) {
            intent.putExtra(IntentKey.TYPE, getTabString())
        }
        super.startActivity(intent)
    }
}
