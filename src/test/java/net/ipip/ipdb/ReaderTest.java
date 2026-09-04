package net.ipip.ipdb;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReaderTest {

    @Test
    public void testReader() throws Exception {
        InputStream in = TestDb.open();
        Reader reader = new Reader(in);

        assertTrue(reader.getBuildUTCTime() > 0, "build utc time should be positive");
        assertTrue(reader.isIPv4(), "database.ipdb should support ipv4");
        assertTrue(reader.isIPv6(), "database.ipdb should support ipv6");

        String[] fields = reader.getSupportFields();
        assertNotNull(fields);
        assertTrue(fields.length > 0, "fields should not be empty");

        assertNotNull(reader.getSupportLanguages());
        assertTrue(reader.getSupportLanguages().contains("CN"), "languages should contain CN");

        assertNotNull(reader.find("114.114.114.114", "CN"), "known ipv4 should be found");
    }

    @Test
    public void testIPv6Lookup() throws Exception {
        InputStream in = TestDb.open();
        Reader reader = new Reader(in);

        // 压缩/全形/内嵌 IPv4 形式的 IPv6 均不应抛格式异常；
        // 未精确命中的地址会落到默认路由节点，返回空字段行或 null
        reader.find("2001:db8::1", "CN");
        reader.find("0000:0000:0000:0000:0000:ffff:0101:0101", "CN");
        reader.find("::ffff:1.1.1.1", "CN");
        String[] row = reader.find("::", "CN");
        assertTrue(row == null || row.length == reader.getSupportFields().length,
                "row must be null or full field width");
    }

    @Test
    public void testBadFormat() throws Exception {
        InputStream in = TestDb.open();
        Reader reader = new Reader(in);

        assertThrows(IPFormatException.class, () -> reader.find("999.999.999.999", "CN"), "out of range ipv4");
        assertThrows(IPFormatException.class, () -> reader.find("1.2.3.4.5", "CN"), "too many groups");
        assertThrows(IPFormatException.class, () -> reader.find("12345:::2", "CN"), "bad ipv6");
        assertThrows(IPFormatException.class, () -> reader.find("not-an-ip", "CN"), "not an ip");
        assertNull(reader.find("1.1.1.1", "XX"), "unknown language should return null");
    }
}
