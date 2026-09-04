package net.ipip.ipdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Risk {
    private Reader reader;

    public Risk(String name) throws IOException {
        this.reader = new Reader(name);
    }

    public Risk(InputStream in) throws IOException {
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

    public Map<String, String> findMap(String addr, String language) throws IPFormatException, InvalidDatabaseException {
        String[] data = this.reader.find(addr, language);
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

    public Info findInfo(String addr) throws IPFormatException, InvalidDatabaseException {
        Map<String, String> data = this.findMap(addr, "CN");
        return data != null ? new Info(data) : null;

    }

    public String fields() {
        return Arrays.toString(this.reader.getSupportFields());
    }

    public static class Info {
        private final Map<String, String> data;

        public Info(Map<String, String> map) {
            this.data = map;
        }

        public int getScore() {
            String s = this.data.get("score");
            return s != null ? Integer.parseInt(s) : 0;
        }

        public String getBehavior() {
            String s = this.data.get("behavior");
            return s != null ? s : "";
        }

        public String getCountryCode() {
            String s = this.data.get("country_code");
            return s != null ? s : "";
        }
    }
}