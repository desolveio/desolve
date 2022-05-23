package io.desolve.parser.container

import java.io.InputStream
import java.io.OutputStream

data class DataStream(
    val inputStream: InputStream,
    val outputStream: OutputStream,
    val errorStream: InputStream,
    val wait: () -> Unit
)