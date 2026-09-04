package net.ipip.ipdb;

import java.io.IOException;
import java.io.InputStream;

/**
 * 测试辅助：从 classpath 加载测试数据集 database.ipdb。
 */
final class TestDb {

    static final String RESOURCE = "/database.ipdb";

    private TestDb() {
    }

    static InputStream open() throws IOException {
        InputStream in = TestDb.class.getResourceAsStream(RESOURCE);
        if (in == null) {
            throw new IOException("test dataset not found on classpath: " + RESOURCE);
        }
        return in;
    }
}
