package ru.security.live.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
/**
 * @author sardor
 */
@SuppressLint("SimpleDateFormat")
class DateTool {
    companion object {
        const val ddMMyyyy = "dd.MM.yyyy"
        const val HHmm = "HH:mm"
        const val fullDateMs = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val fullDate2S = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"
        const val fullDate6S = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
        const val fullDate4S = "yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'"
        const val fullDate5S = "yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'"
        const val fullDateSec = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        const val fullDateMsNoZ = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        const val fullDate2SNoZ = "yyyy-MM-dd'T'HH:mm:ss.SS"
        const val fullDate6SNoZ = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        const val fullDate4SNoZ = "yyyy-MM-dd'T'HH:mm:ss.SSSS"
        const val fullDate5SNoZ = "yyyy-MM-dd'T'HH:mm:ss.SSSSS"
        const val fullDateSecNoZ = "yyyy-MM-dd'T'HH:mm:ss"


        fun format(calendar: Calendar, pattern: String): String {
            val date = Date(calendar.timeInMillis)
            val format = SimpleDateFormat(pattern)
            return format.format(date)
        }

        fun formatForRequest(calendar: Calendar, format: String = DateTool.fullDateMs, timeZone: TimeZone = TimeZone.getDefault()): String {
            val date = Date(calendar.timeInMillis)
            val simpleFormatter = SimpleDateFormat(format)
            simpleFormatter.timeZone = timeZone
            return simpleFormatter.format(date)
        }

        fun formatForRequest(timestamp: Long, format: String = DateTool.fullDateMs, timeZone: TimeZone = TimeZone.getDefault()): String {
            val date = Date(timestamp)
            val simpleFormatter = SimpleDateFormat(format)
            simpleFormatter.timeZone = timeZone
            return simpleFormatter.format(date)
        }

        fun getTimestamp(value: String, pattern: String): Long {
            LoggingTool.log("Parse $value as $pattern")
            val format = SimpleDateFormat(pattern)
            val date = format.parse(value)
            return date.time
        }

        fun getCalendar(value: String, pattern: String): Calendar {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = getTimestamp(value, pattern)
            return calendar
        }

        fun getDefaultFrom(): Calendar {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val weekMillis: Long = 60 * 1000 * 60 * 24 * 7
            calendar.timeInMillis = calendar.timeInMillis - weekMillis

            return calendar
        }

        fun getDefaultTill(): Calendar {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar
        }

        fun getAsCalendar(date: String, time: String): Calendar {

            val array = date.split(".")
            val day = array[0].toInt()

            // Calendar object works in Georgian calendar, January = 0, Feb =1, March = 2 etc
            // That's why we are subscribing from display month - 1
            val month = array[1].toInt() - 1

            val year = array[2].toInt()

            val array2 = time.split(":")
            val hour = array2[0].toInt()
            val minute = array2[1].toInt()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            return calendar
        }

        fun formatHour(timestamp: Long): String {
            val time = timestamp.times(1000)
            val date = Date(time)
            val format = SimpleDateFormat("HH:mm")
            return format.format(date)
        }

        fun getFullDate(timestamp: Long): String {
            val time = timestamp.times(1000)
            val date = Date(time)
            val format = SimpleDateFormat("dd MMMM, yyyy")
            return format.format(date)
        }

        fun getDate(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("dd MMMM")
            return format.format(date)
        }

        fun getTime(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("HH:mm")
            return format.format(date)
        }

        fun getTimeFromMillis(millis: Long): String {
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
        }

        fun isSame(time1: Long, time2: Long): Boolean {
            val date1 = Date(time1)
            val date2 = Date(time2)

            val calendar1 = Calendar.getInstance()
            calendar1.time = date1

            val calendar2 = Calendar.getInstance()
            calendar2.time = date2

            return calendar2.get(Calendar.DAY_OF_MONTH) == calendar1.get(Calendar.DAY_OF_MONTH)
        }


        fun isToday(time1: Long): Boolean {
            val date1 = Date(time1)

            val calendar1 = Calendar.getInstance()
            calendar1.time = date1

            return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == calendar1.get(Calendar.DAY_OF_MONTH)
        }

        fun isYesterday(time1: Long): Boolean {
            val date1 = Date(time1)

            val calendar1 = Calendar.getInstance()
            calendar1.time = date1

            return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1 == calendar1.get(Calendar.DAY_OF_MONTH)
        }

        @SuppressLint("SimpleDateFormat")
        fun getEventArchiveEnd(inputDateString: String): String {
            var date: Date? = null


            var newFormat = SimpleDateFormat(DateTool.fullDateMs)
            newFormat.timeZone = TimeZone.getTimeZone("UTC")
            date = try {
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDateSec)
                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDate2S)
                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDate4S)
                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDate5S)
                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDate6S)
                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            }
            val time = date.time + (minuteInMillis * 10)
            date.time = time

            return newFormat.format(date)
        }


        @SuppressLint("SimpleDateFormat")
        fun getEventArchiveStart(inputDateString: String): String {
            var date: Date? = null
            var newFormat = SimpleDateFormat(fullDateMs)
            var outputFormat = SimpleDateFormat(DateTool.fullDateMsNoZ, Locale.getDefault())
            date = try {
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDateSec)

                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDate2S)
                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDate4S)
                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDate5S)
                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            } catch (e: ParseException) {
                newFormat = SimpleDateFormat(DateTool.fullDate6S)
                newFormat.timeZone = TimeZone.getTimeZone("UTC")
                newFormat.parse(inputDateString)
            }
            val time = date.time - minuteInMillis
            date.time = time
            return newFormat.format(date)
        }


    }
}