package net.ipip.ipdb;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CityTest {

    private static City cityDb;

    @BeforeAll
    public static void setUp() throws Exception {
        InputStream in = TestDb.open();
        cityDb = new City(in);
    }

    @Test
    public void find() throws Exception {
        City.Info info = cityDb.findCity("114.114.114.114", "CN");
        assertNotNull(info, "known ipv4 should be found");
        assertNotNull(info.getCountryName(), "country_name should not be null");
        assertFalse(info.getCountryName().isEmpty(), "country_name should not be empty");
        assertNotNull(info.toString());
    }

    @Test
    public void findMap() throws Exception {
        Map<String, String> data = cityDb.findMap("114.114.114.114", "CN");
        assertNotNull(data, "findMap should return a map for known ipv4");
        assertTrue(data.containsKey("country_name"), "map should contain field country_name");
        assertTrue(data.containsKey("isp_domain"), "map should contain field isp_domain");

        // database.ipdb 数据集不包含 asn_info / district_info 字段
        City.Info info = cityDb.findInfo("114.114.114.114", "CN");
        assertNotNull(info);
        assertNull(info.getAsnInfos(), "asn_info is not present in this dataset");
        assertNull(info.getDistrictInfo(), "district_info is not present in this dataset");
    }

    @Test
    public void metadata() {
        assertTrue(cityDb.buildTime() > 0, "build time should be positive");
        assertTrue(cityDb.isIPv4(), "dataset supports ipv4");
        assertTrue(cityDb.isIPv6(), "dataset supports ipv6");
        assertTrue(cityDb.fields().contains("country_name"), "fields should contain country_name");
        assertTrue(cityDb.languages().contains("CN"), "languages should contain CN");
    }
}
