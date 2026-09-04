package net.ipip.ipdb;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class District {
    private Reader reader;

    public District(String name) throws IOException {
        this.reader = new Reader(name);
    }

    public District(InputStream in) throws IOException {
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

    public Map<String, String> findMap(String address, String language) throws IPFormatException, InvalidDatabaseException {
        String[] data = this.reader.find(address, language);
        if (data == null) {
            return null;
        }

        Map<String, String> m = new HashMap<>();

        String[] fields = this.reader.getSupportFields();

        for (int i = 0, l = data.length; i < l; i++) {
            m.put(fields[i], data[i]);
        }

        return m;
    }

    public Info findInfo(String address, String language) throws IPFormatException, InvalidDatabaseException {
        String[] data = this.reader.find(address, language);
        return data != null ? new Info(data) : null;

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

    public int buildTime() {
        return this.reader.getBuildUTCTime();
    }

    public static class Info {
        private final String[] data;

        public Info(String[] data) {
            this.data = data;
        }

        public String getCountryName() {
            return this.data[0];
        }

        public String getRegionName() {
            return this.data[1];
        }

        public String getCityName() {
            return this.data[2];
        }

        public String getDistrictName() {
            return this.data[3];
        }

        public String getChinaAdminCode() {
            return this.data[4];
        }

        public String getCoveringRadius() {
            return this.data[5];
        }

        public String getLatitude() {
            return this.data[7];
        }

        public String getLongitude() {
            return this.data[6];
        }

        @Override
        public String toString() {
            Map<String, String> json = new LinkedHashMap<>();
            json.put("country_name", this.getCountryName());
            json.put("region_name", this.getRegionName());
            json.put("city_name", this.getCityName());
            json.put("district_name", this.getDistrictName());
            json.put("china_admin_code", this.getChinaAdminCode());
            json.put("covering_radius", this.getCoveringRadius());
            json.put("latitude", this.getLatitude());
            json.put("longitude", this.getLongitude());

            return new Gson().toJson(json);
        }
    }
}