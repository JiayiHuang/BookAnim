package com.example.hello

import java.io.Serializable

/**
 * <pre>
 *     @author Jiun
 *     date   :2023/04/21/12:12
 *     desc   : description
 *     version:
 * </pre>
 */
data class Quadruple<out A, out B, out C, out D>(
    val left: A, val top: B, val width: C, val height: D
) : Serializable {
    override fun toString(): String = "($left, $top, $width, $height)"
}

public fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(left, top, width, height)
