package net.ipip.ipdb;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DistrictTest {

    @Test
    public void testDistrict() throws Exception {
        // 通用 Reader 能力验证：database.ipdb 为城市库，可加载并解析行数据；
        // 专门的 china_district.ipdb 不在测试资源中，故不断言行政区专有字段。
        InputStream in = TestDb.open();
        District db = new District(in);

        assertTrue(db.buildTime() > 0, "build time should be positive");
        assertTrue(db.isIPv4(), "dataset supports ipv4");
        assertTrue(db.fields().contains("country_name"), "fields should contain country_name");
        assertTrue(db.languages().contains("CN"), "languages should contain CN");

        District.Info info = db.findInfo("114.114.114.114", "CN");
        assertNotNull(info, "city database rows are still resolvable as DistrictInfo");
        assertNotNull(info.getCountryName(), "country_name should be resolvable");
    }
}
