package net.ipip.ipdb;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseStationTest {

    @Test
    public void testBaseStation() throws Exception {
        // 通用 Reader 能力验证：base_station.ipdb 不在测试资源中，
        // 使用 database.ipdb 验证 BaseStation 包装类的加载与元数据接口。
        InputStream in = TestDb.open();
        BaseStation db = new BaseStation(in);

        assertTrue(db.buildTime() > 0, "build time should be positive");
        assertTrue(db.isIPv4(), "dataset supports ipv4");
        assertTrue(db.isIPv6(), "dataset supports ipv6");
        assertNotNull(db.fields(), "fields should not be null");
        assertTrue(db.languages().contains("CN"), "languages should contain CN");
    }
}
