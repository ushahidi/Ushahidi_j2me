/*
 *
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

package com.sun.cldc.i18n.j2me;

import java.io.*;

/**
 * The class encode char[] into byte stream in big-endian format.
 * Each char requires two bytes to encode it.
 *
 * Note:
 * The Unicode standard allows for characters
 * whose representation requires more than 16 bit.
 * Characters whose code points are greater
 * than U+FFFF are called supplementary characters.
 * Since String represents a string in the UTF-16 format
 * in which supplementary characters are represented by surrogate pairs,
 * so a supplementary character uses two positions in a String.
 * Therefore, UTF_16BE_Writer doesn't care about supplementary characters,
 * it treats them as usual characters.
 */
public class UTF_16BE_Reader extends com.sun.cldc.i18n.StreamReader {

    /**
     * Read a block of characters.
     *
     * @param cbuf output buffer for converted characters read
     * @param off  initial offset into the provided buffer
     * @param len  length of characters in the buffer
     * @return the number of converted characters
     * @exception IOException is thrown if the input stream
     * could not be read for the raw unconverted character
     */
    public int read(char cbuf[], int off, int len) throws IOException {
        int count = 0;
        int currentChar = 0;
        int b1, b2;

        while (count < len) {
            if ((b1 = in.read()) == -1) {
                break;
            }

            if ((b2 = in.read()) == -1) {
                break;
            }

            currentChar = (b1 << 8) & 0xff00;
            currentChar |= b2 & 0xff;

            cbuf[off + count] = (char) currentChar;
            count++;
        }

        return count;
    }

    /**
     * Get the size in chars of an array of bytes.
     *
     * Note:
     * This method is only used by our internal Helper class in the method
     * byteToCharArray to know how much to allocate before using a
     * reader. If we encounter bad encoding we should return a count
     * that includes that character so the reader will throw an IOException
     *
     * @param array  Source buffer
     * @param offset Offset at which to start counting characters
     * @param length number of bytes to use for counting
     *
     * @return number of characters that would be converted
     */
    public int sizeOf(byte[] array, int offset, int length) {
        return length / 2;
    }
}