package ru.security.live.util

import java.util.concurrent.Executors

/**
 * @author sardor
 */
val ioExectuor = Executors.newFixedThreadPool(2)
val worker = Executors.newSingleThreadExecutor()