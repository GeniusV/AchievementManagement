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
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter
import java.io.Serializable

/**
 * Created by GeniusV on 3/23/18.
 */

abstract class Data : Serializable

class IntentKey {

    companion object {
        const val TYPE = "type"
        const val ITEM = "item"
        const val ACTION = "action"
    }
}

class IntentValue {

    class Action {
        companion object {
            const val UPDATE = "update"
            const val INSERT = "insert"
        }
    }

}

/**
 * ContentFragment is responsible for the following features:
 *  - Invoke deleteSelectedItem() of adapter extended from BaseRecyclerViewAdapter*
 *  - Control the edit, add, delete, search button visibility when enable multiChoice mode.
 *  - Set search type when switch the tab.
 *
 * @see BaseRecyclerViewAdapter
 * @param T The ViewHolder type should be implemented
 * @param K The Data type to display
 */
open class ContentFragment<T : RecyclerView.ViewHolder, K : Data> : Fragment() {
    lateinit var mAdapter: BaseRecyclerViewAdapter<T, K>
    var enableEdit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rc = inflater?.inflate(R.layout.fragment_list, container, false) as RecyclerView
        rc.apply {
            layoutManager = LinearLayoutManager(rc.context)
            adapter = mAdapter
        }

        rc.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = rc.layoutManager as LinearLayoutManager
                var lastPosition = manager.findLastVisibleItemPosition()
                // load more data when left items is less than 20
                if (lastPosition > mAdapter.itemCount - 20) {
                    mAdapter.loadMore()
                }
            }
        })
        return rc
    }

    fun refresh() {
        mAdapter.refresh()
//        val rc = view as RecyclerView
//        rc.scrollToPosition(0)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.options_menu, menu)
        inflater?.inflate(R.menu.search_menu, menu)
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView
        val tabs = activity.findViewById<TabLayout>(R.id.tabs)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        searchView.queryHint = "Search " + tabs.getTabAt(tabs.selectedTabPosition)?.text as String
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                searchView.queryHint = "Search " + tabs.getTabAt(tabs.selectedTabPosition)?.text as String
            }

        })

        if (enableEdit) menu.findItem(R.id.menu_edit)?.isVisible = true
        mAdapter.setMultiChoiceSelectionListener(object : MultiChoiceAdapter.Listener {
            override fun OnDeselectAll(itemSelectedCount: Int, allItemCount: Int) {
                menu.findItem(R.id.menu_trash)?.isVisible = false
                menu.findItem(R.id.menu_add)?.isVisible = true
                menu.findItem(R.id.search)?.isVisible = true
                menu.findItem(R.id.menu_edit)?.isVisible = true
            }

            override fun OnSelectAll(itemSelectedCount: Int, allItemCount: Int) {
            }

            override fun OnItemSelected(selectedPosition: Int, itemSelectedCount: Int, allItemCount: Int) {
                menu.findItem(R.id.menu_trash)?.isVisible = true
                menu.findItem(R.id.menu_add)?.isVisible = false
                menu.findItem(R.id.menu_edit)?.isVisible = false
                menu.findItem(R.id.search)?.isVisible = false

            }

            override fun OnItemDeselected(deselectedPosition: Int, itemSelectedCount: Int, allItemCount: Int) {
                if (itemSelectedCount == 0) {
                    menu.findItem(R.id.menu_trash)?.isVisible = false
                    menu.findItem(R.id.menu_add)?.isVisible = true
                    menu.findItem(R.id.menu_edit)?.isVisible = true
                    menu.findItem(R.id.search)?.isVisible = true
                }
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_trash -> mAdapter.deleteSelectedData()
        }
        return super.onOptionsItemSelected(item)
    }


}


/**
 * This class implements most basic logic.
 * You need to implement all abstract methods and following methods
 * override onBindViewHolder
 * override defaultItemViewClickListener
 *
 * @param T The ViewHolder type should be implemented
 * @param K The Data type to display
 */
abstract class BaseRecyclerViewAdapter<T : RecyclerView.ViewHolder, K : Data>(val context: Context) : MultiChoiceAdapter<T>() {
    protected val typedValue = TypedValue()
    protected val background: Int
    protected var values = ArrayList<K>()
    protected val size = 20
    protected var page = 0


    init {
        context.theme.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
        background = typedValue.resourceId
    }


    override fun getItemCount(): Int {
        return values.size
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): T {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.base_list, parent, false)
        view.setBackgroundResource(background)
        return newViewHolder(view)
    }

    /**
     * Implement the method to return a T type ViewHolder
     */
    abstract fun newViewHolder(view: View): T


    fun loadMore() {
        queryData(page + 1, size)
        page++
    }

    fun refresh() {
        values = ArrayList()
        queryData(0)
        page = 0
    }

    protected fun add(datas: List<K>) {
        values.addAll(datas)
        notifyDataSetChanged()
    }

    /**
     * if request success will call add(datas: List<Data>), else print error
     */
    abstract fun queryData(page: Int = 0, size: Int = 20, successCallback: (List<K>) -> Unit = ::add, errorCallback: (VolleyError) -> Unit = ::errorHandle)

    protected fun errorHandle(e: VolleyError) {
        Log.e("RecyclerViewAdapter", e.toString(), e)
        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
    }

    protected fun deleteSuccessHandle() {
        Toast.makeText(context, "Delete Success!!", Toast.LENGTH_SHORT).show()
        refresh()
    }

    fun deleteSelectedData() {
        val selectedData = selectedItemList.map {
            values[it]
        }
        deselectAll()
        performDelete(selectedData)
    }


    abstract fun performDelete(data: List<K>)
}

class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragments = ArrayList<Fragment>()
    private val fragmentTitles = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitles.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitles[position]
    }
}

/**
 * Display detail message of K
 */
abstract class DetailAdapter<K : Data>(val context: Context, var entity: K) : RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {
    protected val typedValue = TypedValue()
    protected val background: Int
    abstract val id: Long
    protected lateinit var values: List<DetailData>

    init {
        context.theme.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
        background = typedValue.resourceId
        generateList()
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.detail_list, parent, false)
        view.setBackgroundResource(background)
        view.setOnClickListener(defaultItemViewClickListener(view))
        return DetailViewHolder(view)
    }

    protected open fun defaultItemViewClickListener(view: View): View.OnClickListener {
        return View.OnClickListener { }
    }


    fun replaceDetail(data: K) {
        entity = data
        generateList()
    }

    abstract fun generateList()

    abstract fun queryDetail(successCallback: (K) -> Unit = ::replaceDetail, errorCallback: (VolleyError) -> Unit = ::errorHandle)

    protected fun errorHandle(e: VolleyError) {
        Log.e("DetailAdapter", e.toString(), e)
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }

    open fun refresh() {
        queryDetail()
    }

    override fun onBindViewHolder(holder: DetailViewHolder?, position: Int) {
        holder?.apply {
            textView.text = values[position].string
            imageView.visibility = if (values[position].isGoEnable) View.VISIBLE else View.GONE
        }
    }

    class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view
        var textView = view.findViewById<TextView>(R.id.detail_text)
        var imageView = view.findViewById<ImageView>(R.id.detail_icon)

    }

    override fun getItemCount(): Int {
        return values.size
    }

    data class DetailData(val string: String, val isGoEnable: Boolean)

}

interface Identifiable {
    val identifier: Int
}


