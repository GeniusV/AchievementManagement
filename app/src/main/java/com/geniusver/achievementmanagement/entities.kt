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

/**
 * Created by GeniusV on 3/24/18.
 */

data class Student(val id: Long, val name: String, val claxx: Claxx?, var password:String = "") : Data()

data class Claxx(val id: Long, val name: String, val major: Major?) : Data()

data class Course(val id: Long, val name: String, val collage: Collage?) : Data()

data class Major(val id: Long, val name: String, val collage: Collage?) : Data()

class Score(val id: Long, var value: Int ) : Data() {
    constructor(id: Long, value: Int, student: Student, course: Course, term: Term) : this(id, value)

    var student: Student? = null


    var course: Course? = null


    var term: Term? = null

    companion object {
        const val FULL = "full"
        const val STUDENT = "student"
        const val COURSE = "course"
        const val TERM = "term"
    }


    fun getFullName(): String {
        val fieldList = ArrayList<String>()
        if (student != null) fieldList.add(student!!.name)
        if (course != null) fieldList.add(course!!.name)
        if (term != null) fieldList.add(term!!.value)
        return fieldList.joinToString(" - ")
    }

    fun getName(mode: String = FULL, displayValue: Boolean = false): String {
        var displayString = ""
        when (mode) {
            STUDENT -> if (student != null) displayString = student!!.name else ""
            COURSE -> if (course != null) displayString =  course!!.name else ""
            TERM -> if(term != null) displayString = term!!.value else ""
            else -> displayString = getFullName()
        }
        return if(displayValue) "$displayString - $value" else displayString
    }
}

data class Term(val id: Long, val value: String) : Data()

data class Collage(val id: Long, val name: String) : Data()

data class Teacher(val id: Long, val name: String, val password: String): Data()