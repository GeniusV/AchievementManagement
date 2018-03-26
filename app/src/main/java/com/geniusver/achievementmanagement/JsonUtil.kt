/*
 * Modified work Copyright (c) 2018 GeniusV
 * Original work Copyright http://www.cnblogs.com/Jason-Jan/p/7262515.html
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

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by GeniusV on 3/21/18.
 *
 * This is the JsonUtil to read json easier. All methods had not tested yet.
 */
class JsonUtil {
    companion object {

        fun get_key_string(key: String, jsonString: String): String {
            var str: String = ""

            try {

                val jsonObj: JSONObject = JSONObject(jsonString)
                str = jsonObj.getString(key)


            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return str
        }

        fun get_key_boolean(key: String, jsonString: String): Boolean {
            var str: Boolean = true
            try {
                val jsonObj: JSONObject = JSONObject(jsonString)
                str = jsonObj.getBoolean(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return str
        }

        fun get_key_int(key: String, jsonString: String): Int {
            var str: Int = 0
            try {
                val jsonObj: JSONObject = JSONObject(jsonString)
                str = jsonObj.getInt(key)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return str
        }


        fun getList(key: String, jsonString: String): List<String> {
            val list = ArrayList<String>()
            try {
                val jsonObject = JSONObject(jsonString)
                val jsonArray = jsonObject.getJSONArray(key)
                for (i in 0..jsonArray.length() - 1) {
                    val msg = jsonArray.getString(i)
                    list.add(msg)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return list
        }


        fun getListMap(key: String, jsonString: String): List<Map<String, Any>> {
            val list = ArrayList<Map<String, Any>>()
            try {
                val jsonObject = JSONObject(jsonString)
                val jsonArray = jsonObject.getJSONArray(key)
                for (i in 0..jsonArray.length() - 1) {
                    val jsonObject2 = jsonArray.getJSONObject(i)
                    val map = HashMap<String, Any>()
                    val iterator = jsonObject2.keys()
                    while (iterator.hasNext()) {
                        val json_key = iterator.next()
                        var json_value: Any? = jsonObject2.get(json_key)
                        if (json_value == null) {
                            json_value = ""
                        }
                        map.put(json_key, json_value)
                    }
                    list.add(map)
                }
            } catch (e: JSONException) {

                e.printStackTrace()
            }
            return list
        }

    }

}