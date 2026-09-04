package net.ipip.ipdb;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RiskTest {

    @Test
    public void testRisk() throws Exception {
        // 通用 Reader 能力验证：v6risk.ipdb 不在测试资源中，
        // 使用 database.ipdb 验证 Risk 包装类的加载与元数据接口。
        InputStream in = TestDb.open();
        Risk db = new Risk(in);

        assertNotNull(db.fields(), "fields should not be null");
        assertTrue(db.fields().contains("country_name"), "fields should contain country_name");
    }
}
