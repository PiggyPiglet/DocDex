package me.piggypiglet.docdex.db.utils;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class MysqlUtils {
    private static final CharsetEncoder ENCODER = Charset.defaultCharset().newEncoder();

    private MysqlUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    /*
    I copied this from https://github.com/mysql/mysql-connector-j/blob/d64b664fa93e81296a377de031b8123a67e6def2/src/main/core-impl/java/com/mysql/cj/ClientPreparedQueryBindings.java#L730
     */
    @NotNull
    public static String escapeSql(@NotNull final String string) {
        final int length = string.length();
        final StringBuilder builder = new StringBuilder();

        //
        // Note: buf.append(char) is _faster_ than appending in blocks, because the block append requires a System.arraycopy().... go figure...
        // cool story mysql connector, i'll keep it in mind lol.

        // if these guys were so paranoid about performance why didn't they put finals in smh. pretty sure they do
        // something during compilation

        for (int i = 0; i < length; ++i) {
            final char character = string.charAt(i);

            switch (character) {
                case 0:
                    builder.append('\\');
                    builder.append('0');
                    break;

                case '\n':
                    builder.append('\\');
                    builder.append('n');
                    break;

                case '\r':
                    builder.append('\\');
                    builder.append('r');
                    break;

                case '\\':
                    builder.append('\\');
                    builder.append('\\');
                    break;

                case '\'':
                    builder.append('\\');
                    builder.append('\'');
                    break;

                // driver actually tests if the session is set to use ansi quoted identifiers, but I THINK that's just
                // for things like table names & columns, which don't need escaping anyway. So it should be safe to escape
                // all double quotes.
                case '"':
                    builder.append('\\');
                    builder.append('"');
                    break;

                case '\032':
                    builder.append('\\');
                    builder.append('Z');
                    break;

                case '\u00a5':
                case '\u20a9':
                    final CharBuffer charBuffer = CharBuffer.allocate(1);
                    final ByteBuffer byteBuffer = ByteBuffer.allocate(1);

                    charBuffer.put(character);
                    byteBuffer.position(0);

                    ENCODER.encode(charBuffer, byteBuffer, true);

                    if (byteBuffer.get(0) == '\\') {
                        builder.append('\\');
                    }

                    builder.append(character);
                    break;

                default:
                    builder.append(character);
            }
        }

        return builder.toString();
    }
}
