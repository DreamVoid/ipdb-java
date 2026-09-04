package net.ipip.ipdb;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IPFormatTest {

    @Test
    public void validV4() {
        assertArrayEquals(new byte[]{1, 2, 3, 4}, IPFormat.textToNumericFormatV4("1.2.3.4"));
        assertArrayEquals(new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255},
                IPFormat.textToNumericFormatV4("255.255.255.255"));
        assertArrayEquals(new byte[]{0, 0, 0, 1}, IPFormat.textToNumericFormatV4("0.0.0.1"));
        // InetAddress 风格：末段承载剩余高位字节
        assertArrayEquals(new byte[]{1, 2, 1, 45}, IPFormat.textToNumericFormatV4("1.2.301"));
        assertArrayEquals(new byte[]{1, 2, 0, 3}, IPFormat.textToNumericFormatV4("1.2.3"));
    }

    @Test
    public void invalidV4() {
        assertNull(IPFormat.textToNumericFormatV4(null));
        assertNull(IPFormat.textToNumericFormatV4(""));
        assertNull(IPFormat.textToNumericFormatV4("999.999.999.999"));
        assertNull(IPFormat.textToNumericFormatV4("1.2.3.4.5"));
        assertNull(IPFormat.textToNumericFormatV4("1.2.3.4."));
        assertNull(IPFormat.textToNumericFormatV4("010.1.1.1"));   // 前导零
        assertNull(IPFormat.textToNumericFormatV4("1.2.3.999"));
        assertNull(IPFormat.textToNumericFormatV4("1.2.3.a"));
    }

    @Test
    public void validV6() {
        assertArrayEquals(new byte[16], IPFormat.textToNumericFormatV6("::"));
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (byte) 0xff, (byte) 0xff, 1, 2, 3, 4},
                IPFormat.textToNumericFormatV6("::ffff:1.2.3.4"));

        byte[] expect = new byte[16];
        expect[0] = 0x20;
        expect[1] = 0x01;
        expect[2] = 0x0d;
        expect[3] = (byte) 0xb8;
        expect[15] = 0x01;
        assertArrayEquals(expect, IPFormat.textToNumericFormatV6("2001:db8::1"));

        assertArrayEquals(expect, IPFormat.textToNumericFormatV6("2001:0db8:0000:0000:0000:0000:0000:0001"));
        assertArrayEquals(new byte[]{0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8},
                IPFormat.textToNumericFormatV6("1:2:3:4:5:6:7:8"));
    }

    @Test
    public void invalidV6() {
        assertNull(IPFormat.textToNumericFormatV6(null));
        assertNull(IPFormat.textToNumericFormatV6(":1:2:3:4:5:6:7:8"));
        assertNull(IPFormat.textToNumericFormatV6("1:2:3:4:5:6:7:8:9"));
        assertNull(IPFormat.textToNumericFormatV6("1::2::3"));
        assertNull(IPFormat.textToNumericFormatV6("12345::"));
        assertNull(IPFormat.textToNumericFormatV6("1:2:3:4:5:6:7"));
        assertNull(IPFormat.textToNumericFormatV6("1:2:3:4:5:6:7:8:"));
        assertNull(IPFormat.textToNumericFormatV6("gggg::1"));
        assertNull(IPFormat.textToNumericFormatV6("1:2:3:4:5:6:1.2.3.4.5"));
        assertNull(IPFormat.textToNumericFormatV6("1:2:3:4:5:6:7:1.2.3.4"));
    }

    @Test
    public void v4EmbeddedUsesV4Semantics() {
        // 内嵌 IPv4 段非法时整体非法
        assertNull(IPFormat.textToNumericFormatV6("::ffff:999.1.1.1"));
        // "::" 压缩后总段数超过 8 段应非法
        assertNull(IPFormat.textToNumericFormatV6("1:2:3:4:5:6:7:8::"));
        assertTrue16(IPFormat.textToNumericFormatV6("::2:3:4:5:6:7:8"));
    }

    private static void assertTrue16(byte[] b) {
        org.junit.jupiter.api.Assertions.assertTrue(b != null && b.length == 16, "expected 16 bytes: " + Arrays.toString(b));
    }
}
