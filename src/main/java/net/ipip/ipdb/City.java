package net.ipip.ipdb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class City {
    private Reader reader;

    public City(String name) throws IOException {
        this.reader = new Reader(name);
    }

    public City(InputStream in) throws IOException {
        this.reader = new Reader(in);
    }

    public boolean reload(String name) {
        try {
            this.reader = new Reader(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String[] find(String addr, String language) throws IPFormatException, InvalidDatabaseException {
        return this.reader.find(addr, language);
    }

    public Map<String, String> findMap(String addr, String language) throws IPFormatException, InvalidDatabaseException {
        String[] data = this.reader.find(addr, language);
        if (data == null) {
            return null;
        }
        String[] fields = this.reader.getSupportFields();
        Map<String, String> m = new HashMap<>();
        for (int i = 0, l = data.length; i < l; i++) {
            m.put(fields[i], data[i]);
        }

        return m;
    }

    public Info findInfo(String addr, String language) throws IPFormatException, InvalidDatabaseException {
        Map<String, String> data = this.findMap(addr, language);
        return data != null ? new Info(data) : null;
    }

    public Info findCity(String addr, String language) throws IPFormatException, InvalidDatabaseException {
        Map<String, String> data = this.findMap(addr, language);
        return data != null ? new Info(data) : null;
    }

    public int buildTime() {
        return this.reader.getBuildUTCTime();
    }

    public boolean isIPv4() {
        return this.reader.isIPv4();
    }

    public boolean isIPv6() {
        return this.reader.isIPv6();
    }

    public String fields() {
        return Arrays.toString(this.reader.getSupportFields());
    }

    public String languages() {
        return this.reader.getSupportLanguages();
    }

    public static class Info {
        private final Map<String, String> map;

        private Info(Map<String, String> kv) {
            this.map = kv;
        }

        public String getCountryName() {
            if (!this.map.isEmpty()) {
                return this.map.get("country_name");
            }
            return null;
        }

        public String getRegionName() {
            if (!this.map.isEmpty()) {
                return this.map.get("region_name");
            } else {
                return null;
            }
        }

        public String getCityName() {
            if (!this.map.isEmpty()) {
                return this.map.get("city_name");
            } else {
                return null;
            }
        }

        public String getDistrictName() {
            if (!this.map.isEmpty()) {
                return this.map.get("district_name");
            } else {
                return null;
            }
        }

        public String getOwnerDomain() {
            if (!this.map.isEmpty()) {
                return this.map.get("owner_domain");
            } else {
                return null;
            }
        }

        public String getIspDomain() {
            if (!this.map.isEmpty()) {
                return this.map.get("isp_domain");
            } else {
                return null;
            }
        }

        public String getLatitude() {
            if (!this.map.isEmpty()) {
                return this.map.get("latitude");
            } else {
                return null;
            }
        }

        public String getLongitude() {
            if (!this.map.isEmpty()) {
                return this.map.get("longitude");
            } else {
                return null;
            }
        }

        public String getTimezone() {
            if (!this.map.isEmpty()) {
                return this.map.get("timezone");
            } else {
                return null;
            }
        }

        public String getUtcOffset() {
            if (!this.map.isEmpty()) {
                return this.map.get("utc_offset");
            } else {
                return null;
            }
        }

        public String getChinaRegionCode() {
            if (!this.map.isEmpty()) {
                return this.map.get("china_region_code");
            } else {
                return null;
            }
        }

        public String getChinaCityCode() {
            if (!this.map.isEmpty()) {
                return this.map.get("china_city_code");
            } else {
                return null;
            }
        }

        public String getChinaDistrictCode() {
            if (!this.map.isEmpty()) {
                return this.map.get("china_district_code");
            } else {
                return null;
            }
        }

        public String getChinaAdminCode() {
            if (!this.map.isEmpty()) {
                return this.map.get("china_admin_code");
            } else {
                return null;
            }
        }

        public String getIddCode() {
            if (!this.map.isEmpty()) {
                return this.map.get("idd_code");
            } else {
                return null;
            }
        }

        public String getCountryCode() {
            if (!this.map.isEmpty()) {
                return this.map.get("country_code");
            } else {
                return null;
            }
        }

        public String getContinentCode() {
            if (!this.map.isEmpty()) {
                return this.map.get("continent_code");
            } else {
                return null;
            }
        }

        public String getLine() {
            if (!this.map.isEmpty()) {
                return this.map.get("line");
            } else {
                return null;
            }
        }

        public String getIDC() {
            if (!this.map.isEmpty()) {
                return this.map.get("idc");
            } else {
                return null;
            }
        }

        public String getBaseStation() {
            if (!this.map.isEmpty()) {
                return this.map.get("base_station");
            } else {
                return null;
            }
        }

        public String getCountryCode3() {
            if (!this.map.isEmpty()) {
                return this.map.get("country_code3");
            } else {
                return null;
            }
        }

        public String getEuropeanUnion() {
            if (!this.map.isEmpty()) {
                return this.map.get("european_union");
            } else {
                return null;
            }
        }

        public String getCurrencyCode() {
            if (!this.map.isEmpty()) {
                return this.map.get("currency_code");
            } else {
                return null;
            }
        }

        public String getCurrencyName() {
            if (!this.map.isEmpty()) {
                return this.map.get("currency_name");
            } else {
                return null;
            }
        }

        public String getAnycast() {
            if (!this.map.isEmpty()) {
                return this.map.get("anycast");
            } else {
                return null;
            }
        }

        public String getRoute() {
            if (!this.map.isEmpty()) {
                return this.map.get("route");
            } else {
                return null;
            }
        }

        public String getASN() {
            if (!this.map.isEmpty()) {
                return this.map.get("asn");
            } else {
                return null;
            }
        }

        public String getAreaCode() {
            if (!this.map.isEmpty()) {
                return this.map.get("area_code");
            } else {
                return null;
            }
        }

        public String getUsageType() {
            if (!this.map.isEmpty()) {
                return this.map.get("usage_type");
            } else {
                return null;
            }
        }

        public District.Info getDistrictInfo() {
            if (!this.map.isEmpty()) {
                String str = this.map.get("district_info");
                if (str == null) {
                    return null;
                }
                Map<String, String> info = new Gson().fromJson(str, new TypeToken<Map<String, String>>() {
                }.getType());

                String[] data = new String[8];
                data[0] = this.getCountryName();
                data[1] = this.getRegionName();
                data[2] = this.getCityName();
                data[3] = info.get("district_name");
                data[4] = info.get("china_admin_code");
                data[5] = info.get("covering_radius");
                data[6] = info.get("longitude");
                data[7] = info.get("latitude");

                return new District.Info(data);
            }
            return null;
        }

        public AsnInfo[] getAsnInfos() {

            if (!this.map.isEmpty()) {
                String str = this.map.get("asn_info");
                if (str == null) {
                    return null;
                }
                if (!str.startsWith("[")) {
                    return null;
                }

                return new Gson().fromJson(str, AsnInfo[].class);
            }

            return null;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this.map);
        }
    }
}