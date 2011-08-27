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
 * The class encodes char array  into byte stream in big-endian format.
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
public class UTF_16BE_Writer extends com.sun.cldc.i18n.StreamWriter {

    /**
     * Write a portion of an array of characters.
     *
     * @param cbuf Array of characters
     * @param off  Offset from which to start writing characters
     * @param len  Number of characters to write
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(char cbuf[], int off, int len) throws IOException {
        final int outputSize = 2; // Always write 2 encoded bytes
        byte[] outputByte = new byte[outputSize];
        char inputChar;
        int count = 0;

        while (count < len) {
            inputChar = cbuf[off + count];
            outputByte[0] = (byte) ((inputChar >> 8) & 0xff);
            outputByte[1] = (byte) (inputChar & 0xff);
            out.write(outputByte, 0, outputSize);
            count++;
        }
    }

    /**
     * Get the size in bytes of an array of chars.
     *
     * @param array  Source buffer
     * @param offset Offset at which to start counting bytes
     * @param length number of chars to use for counting
     *
     * @return number of bytes that would be occupied
     */
    public int sizeOf(char[] array, int offset, int length) {
        return length * 2;
    }
}