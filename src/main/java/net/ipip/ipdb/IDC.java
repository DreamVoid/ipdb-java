package net.ipip.ipdb;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class IDC {
    private Reader reader;

    public IDC(String name) throws IOException {
        this.reader = new Reader(name);
    }

    public IDC(InputStream stream) throws IOException {
        this.reader = new Reader(stream);
    }

    public boolean reload(String name) {
        try {
            this.reader = new Reader(name);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public String[] find(String address, String language) throws IPFormatException, InvalidDatabaseException {
        return this.reader.find(address, language);
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
        if (data == null) {
            return null;
        }
        return new Info(data);
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

    public int buildTime() {
        return this.reader.getBuildUTCTime();
    }

    public String languages() {
        return this.reader.getSupportLanguages();
    }

    public static class Info {
        private final String[] data;

        private Info(String[] data) {
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

        public String getOwnerDomain() {
            return this.data[3];
        }

        public String getIspDomain() {
            return this.data[4];
        }

        public String getIDC() {
            return this.data[5];
        }

        @Override
        public String toString() {
            Map<String, String> json = new LinkedHashMap<>();
            json.put("country_name", this.getCountryName());
            json.put("region_name", this.getRegionName());
            json.put("city_name", this.getCityName());
            json.put("owner_domain", this.getOwnerDomain());
            json.put("isp_domain", this.getIspDomain());
            json.put("idc", this.getIDC());

            return new Gson().toJson(json);
        }
    }
}