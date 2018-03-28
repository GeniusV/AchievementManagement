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

import android.content.Context
import com.android.volley.VolleyError

/**
 * Created by GeniusV on 3/28/18.
 */
class CollageDetailAdapter(context: Context, val collage: Collage) : DetailAdapter<Collage>(context, collage) {
    override val id: Long
        get() = collage.id

    override fun generateList() {
        values = listOf(
                DetailAdapter.DetailData("ID: " + entity.id, false),
                DetailAdapter.DetailData("Name: " + entity.name, false)
        )
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun queryDetail(successCallback: (Collage) -> Unit, errorCallback: (VolleyError) -> Unit) {
        RequestCenter.CollageRequester.getCollage(id, context, successCallback, errorCallback)
    }
}