package ru.security.live.util

import android.annotation.SuppressLint
import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
/**
 * @author sardor
 */
const val minuteInMillis = 60 * 1000

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false, initView: View.() -> Unit): View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot).apply { this.initView() }

fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String) {
    if (context != null)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun parseTime(from: String, to: String): String {
    return "(${reformatDate(from)}) - (${reformatDate(to)})"
}


fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}


//2018-12-11T15:08:40.629581Z
@SuppressLint("SimpleDateFormat")
fun reformatDate(inputDateString: String,
                 inputDateFormat: SimpleDateFormat = SimpleDateFormat(DateTool.fullDateMs)): String {
    var date: Date? = null
    inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    date = try {
        inputDateFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDateSec)
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate2S)
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate4S)
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate5S)
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate6S)
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    }
    val outputDateFormat = SimpleDateFormat("dd.MM.yyyy  HH:mm", Locale.getDefault())
    outputDateFormat.timeZone = TimeZone.getDefault()
    val formatted = outputDateFormat.format(date)
    return formatted

}


fun getLocalDate(inputDateString: String): Date {
    val inputDateFormat = SimpleDateFormat(DateTool.fullDateMs)
    var date: Date? = null
    inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    var outputFormat = SimpleDateFormat(DateTool.fullDateMsNoZ, Locale.getDefault())
    date = try {
        inputDateFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDateSec)
        outputFormat = SimpleDateFormat(DateTool.fullDateSecNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate2S)
        outputFormat = SimpleDateFormat(DateTool.fullDate2SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate4S)
        outputFormat = SimpleDateFormat(DateTool.fullDate4SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate5S)
        outputFormat = SimpleDateFormat(DateTool.fullDate5SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate6S)
        outputFormat = SimpleDateFormat(DateTool.fullDate6SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    }
    outputFormat.timeZone = TimeZone.getDefault()
    val formatted = outputFormat.format(date)
    LoggingTool.log("LocalDate: $inputDateString, formatted: $formatted")
    val resultDate = outputFormat.parse(formatted)
    return resultDate
}

//2018-12-11T15:08:40.629581Z
@SuppressLint("SimpleDateFormat")
fun getEventArchiveStart(inputDateString: String,
                         inputDateFormat: SimpleDateFormat = SimpleDateFormat(DateTool.fullDateMs),
                         localTimeZone: TimeZone = TimeZone.getTimeZone("UTC")): String {
    var date: Date? = null
    inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    var outputFormat = SimpleDateFormat(DateTool.fullDateMsNoZ, Locale.getDefault())
    date = try {
        inputDateFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDateSec)
        outputFormat = SimpleDateFormat(DateTool.fullDateSecNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate2S)
        outputFormat = SimpleDateFormat(DateTool.fullDate2SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate4S)
        outputFormat = SimpleDateFormat(DateTool.fullDate4SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate5S)
        outputFormat = SimpleDateFormat(DateTool.fullDate5SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate6S)
        outputFormat = SimpleDateFormat(DateTool.fullDate6SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    }
    val time = date.time - minuteInMillis
    date.time = time
    outputFormat.timeZone = TimeZone.getDefault()
    val result = outputFormat.format(date)
    return result
}

//2018-12-11T15:08:40.629581Z
@SuppressLint("SimpleDateFormat")
fun getEventArchiveStartTime(inputDateString: String,
                             inputDateFormat: SimpleDateFormat = SimpleDateFormat(DateTool.fullDateMs))
        : Long {
    var date: Date? = null
    inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    var outputFormat = SimpleDateFormat(DateTool.fullDateMsNoZ, Locale.getDefault())
    date = try {
        inputDateFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDateSec)
        outputFormat = SimpleDateFormat(DateTool.fullDateSecNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate2S)
        outputFormat = SimpleDateFormat(DateTool.fullDate2SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate4S)
        outputFormat = SimpleDateFormat(DateTool.fullDate4SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate5S)
        outputFormat = SimpleDateFormat(DateTool.fullDate5SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate6S)
        outputFormat = SimpleDateFormat(DateTool.fullDate6SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    }
    outputFormat.timeZone = TimeZone.getDefault()
    val formatted = outputFormat.format(date)
    val resultDate = outputFormat.parse(formatted)

    return resultDate.time - minuteInMillis
}

//2018-12-11T15:08:40.629581Z
@SuppressLint("SimpleDateFormat")
fun getEventArchiveEnd(inputDateString: String,
                       inputDateFormat: SimpleDateFormat = SimpleDateFormat(DateTool.fullDateMs),
                       localTimeZone: TimeZone = TimeZone.getDefault()): String {
    var date: Date? = null
    inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

    var outputFormat = SimpleDateFormat(DateTool.fullDateMsNoZ, Locale.getDefault())

    date = try {
        inputDateFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDateSec)
        outputFormat = SimpleDateFormat(DateTool.fullDateSecNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate2S)
        outputFormat = SimpleDateFormat(DateTool.fullDate2SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate4S)
        outputFormat = SimpleDateFormat(DateTool.fullDate4SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate5S)
        outputFormat = SimpleDateFormat(DateTool.fullDate5SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate6S)
        outputFormat = SimpleDateFormat(DateTool.fullDate6SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    }
    val time = date.time + (minuteInMillis * 10)
    date.time = time

    outputFormat.timeZone = TimeZone.getDefault()
    val formatted = outputFormat.format(date)
    val resultDate = outputFormat.parse(formatted)

    return outputFormat.format(resultDate)
}

//2018-12-11T15:08:40.629581Z
@SuppressLint("SimpleDateFormat")
fun getEventArchiveEndTime(inputDateString: String,
                           inputDateFormat: SimpleDateFormat = SimpleDateFormat(DateTool.fullDateMs)): Long {
    var date: Date? = null
    inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

    var outputFormat = SimpleDateFormat(DateTool.fullDateMsNoZ, Locale.getDefault())
    date = try {
        inputDateFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDateSec)
        outputFormat = SimpleDateFormat(DateTool.fullDateSecNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate2S)
        outputFormat = SimpleDateFormat(DateTool.fullDate2SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate4S)
        outputFormat = SimpleDateFormat(DateTool.fullDate4SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate5S)
        outputFormat = SimpleDateFormat(DateTool.fullDate5SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate6S)
        outputFormat = SimpleDateFormat(DateTool.fullDate6SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    }
    outputFormat.timeZone = TimeZone.getDefault()
    val formatted = outputFormat.format(date)
    val resultDate = outputFormat.parse(formatted)
    return resultDate.time + (minuteInMillis * 10)
}


@SuppressLint("SimpleDateFormat")
fun getCalendarFromTextDate(inputDateString: String,
                            inputDateFormat: SimpleDateFormat = SimpleDateFormat(DateTool.fullDateMs)): Calendar {
    var date: Date? = null
    inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    var outputFormat = SimpleDateFormat(DateTool.fullDateMsNoZ, Locale.getDefault())
    date = try {
        inputDateFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDateSec)
        outputFormat = SimpleDateFormat(DateTool.fullDateSecNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate2S)
        outputFormat = SimpleDateFormat(DateTool.fullDate2SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate4S)
        outputFormat = SimpleDateFormat(DateTool.fullDate4SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate5S)
        outputFormat = SimpleDateFormat(DateTool.fullDate5SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    } catch (e: ParseException) {
        val newFormat = SimpleDateFormat(DateTool.fullDate6S)
        outputFormat = SimpleDateFormat(DateTool.fullDate6SNoZ, Locale.getDefault())
        newFormat.timeZone = TimeZone.getTimeZone("UTC")
        newFormat.parse(inputDateString)
    }

    outputFormat.timeZone = TimeZone.getDefault()
    val formatted = outputFormat.format(date)
    val resultDate = outputFormat.parse(formatted)

    val c = Calendar.getInstance()
    c.timeInMillis = resultDate!!.time
    return c
}
