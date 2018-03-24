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

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter
import com.davidecirillo.multichoicerecyclerview.MultiChoiceToolbar

/**
 * Created by GeniusV on 3/23/18.
 */

abstract class Data

open class ContentFragment<T : RecyclerView.ViewHolder> : Fragment() {
    lateinit var multiChoiceToolbar: MultiChoiceToolbar
    val queryTypeName = ""
    lateinit var mAdapter: BaseRecyclerViewAdapter<T>

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
                if (lastPosition > mAdapter.itemCount - 20) {
                    mAdapter.loadMore()
                }
            }
        })

        return rc
    }

    fun refresh() {
        mAdapter.refresh()
        val rc = view as RecyclerView
        rc.scrollToPosition(0)
    }


}


/**
 * override onBindViewHolder
 * override defaultItemViewClickListener
 */
abstract class BaseRecyclerViewAdapter<T : RecyclerView.ViewHolder>(context: Context, items: List<Data>, val listItemId: Int) : MultiChoiceAdapter<T>() {
    private val typedValue = TypedValue()
    private val background: Int
    private var values = ArrayList<Data>(items)
    private val page = 20
    private var offset = 0


    init {
        context.theme.resolveAttribute(R.attr.selectableItemBackground, typedValue, true)
        background = typedValue.resourceId
    }


    override fun getItemCount(): Int {
        return values.size
    }

    fun add(data: Data) {
        values.add(data)
        notifyDataSetChanged()
    }

    fun add(datas: List<Data>) {
        values.addAll(datas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): T {
        val view = LayoutInflater.from(parent?.context).inflate(listItemId, parent, false)
        view.setBackgroundResource(background)
        return newViewHolder(view)
    }

    abstract fun newViewHolder(view: View): T

    fun loadMore() {
        //todo http request
        // add
        notifyDataSetChanged()
    }

    fun refresh() {
        //todo http request
        notifyDataSetChanged()
    }

}

