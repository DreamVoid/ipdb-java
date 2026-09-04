package net.ipip.ipdb;

import com.google.gson.Gson;
import me.dreamvoid.ipdb.AddressNotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Reader {
    private int fileSize;
    private int nodeCount;

    private MetaData meta;
    private byte[] data;

    private int v4offset;

    public Reader(String name) throws IOException {
        this(new File(name));
    }

    private Reader(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            this.init(this.readAllAsStream(in));
        }
    }

    public Reader(InputStream in) throws IOException {
        try {
            this.init(this.readAllAsStream(in));
        } finally {
            in.close();
        }
    }

    protected byte[] readAllAsStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    protected void init(byte[] data) throws InvalidDatabaseException {
        this.data = data;
        this.fileSize = data.length;
        if (this.fileSize < 5) {
            throw new InvalidDatabaseException("database file size error");
        }

        long metaLength = bytesToLong(
                this.data[0],
                this.data[1],
                this.data[2],
                this.data[3]
        );

        try {
            int end = (int) metaLength + 4;
            byte[] metaBytes = Arrays.copyOfRange(this.data, 4, end);

            MetaData meta = new Gson().fromJson(new String(metaBytes, StandardCharsets.UTF_8), MetaData.class);
            if (meta == null || meta.nodeCount < 0 || meta.Languages == null || meta.Fields == null) {
                throw new InvalidDatabaseException("database meta error");
            }
            this.nodeCount = meta.nodeCount;
            this.meta = meta;
        } catch (Exception e) {
            throw new InvalidDatabaseException(e);
        }

        if ((meta.totalSize + (int) metaLength + 4) != this.data.length) {
            throw new InvalidDatabaseException("database file size error");
        }

        this.data = Arrays.copyOfRange(this.data, (int) metaLength + 4, this.fileSize);

        /* for ipv4 */
        if (0x01 == (this.meta.IPVersion & 0x01)) {
            int node = 0;
            for (int i = 0; i < 96 && node < this.nodeCount; i++) {
                if (i >= 80) {
                    node = this.readNode(node, 1);
                } else {
                    node = this.readNode(node, 0);
                }
            }

            this.v4offset = node;
        }
    }

    public String[] find(String addr, String language) throws IPFormatException, InvalidDatabaseException {

        Integer lang = this.meta.Languages.get(language);
        if (lang == null) {
            return null;
        }
        int off = lang;

        byte[] ipv;

        if (addr.contains(":")) {
            ipv = IPFormat.textToNumericFormatV6(addr);
            if (ipv == null) {
                throw new IPFormatException("ipv6 format error");
            }
            if ((this.meta.IPVersion & 0x02) != 0x02) {
                throw new IPFormatException("no support ipv6");
            }

        } else if (addr.indexOf(".") > 0) {
            ipv = IPFormat.textToNumericFormatV4(addr);
            if (ipv == null) {
                throw new IPFormatException("ipv4 format error");
            }
            if ((this.meta.IPVersion & 0x01) != 0x01) {
                throw new IPFormatException("no support ipv4");
            }
        } else {
            throw new IPFormatException("ip format error");
        }

        try {
            int node = this.findNode(ipv);
            final String data = this.resolve(node);
            return Arrays.copyOfRange(data.split("\t", this.meta.Fields.length * this.meta.Languages.size()), off, off + this.meta.Fields.length);
        } catch (AddressNotFoundException nfe) {
            return null;
        }
    }

    private int findNode(byte[] binary) throws AddressNotFoundException {

        int node = 0;

        final int bit = binary.length * 8;

        if (bit == 32) {
            node = this.v4offset;
        }

        for (int i = 0; i < bit; i++) {
            if (node > this.nodeCount) {
                break;
            }

            node = this.readNode(node, 1 & ((0xFF & binary[i / 8]) >> 7 - (i % 8)));
        }

        if (node > this.nodeCount) {
            return node;
        }

        throw new AddressNotFoundException("ip not found");
    }

    private String resolve(int node) throws InvalidDatabaseException {
        final int resolved = node - this.nodeCount + this.nodeCount * 8;
        if (resolved >= this.fileSize) {
            throw new InvalidDatabaseException("database resolve error");
        }

        byte b = 0;
        int size = (int) bytesToLong(
                b,
                b,
                this.data[resolved],
                this.data[resolved + 1]
        );

        if (this.data.length < (resolved + 2 + size)) {
            throw new InvalidDatabaseException("database resolve error");
        }

        return new String(this.data, resolved + 2, size, StandardCharsets.UTF_8);
    }

    private int readNode(int node, int index) {
        int off = node * 8 + index * 4;

        return (int) bytesToLong(
                this.data[off],
                this.data[off + 1],
                this.data[off + 2],
                this.data[off + 3]
        );
    }

    private static long bytesToLong(byte a, byte b, byte c, byte d) {
        return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
    }

    private static long int2long(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }

    public boolean isIPv4() {
        return (this.meta.IPVersion & 0x01) == 0x01;
    }

    public boolean isIPv6() {
        return (this.meta.IPVersion & 0x02) == 0x02;
    }

    public int getBuildUTCTime() {
        return this.meta.Build;
    }

    public String[] getSupportFields() {
        return Arrays.copyOf(this.meta.Fields, this.meta.Fields.length);
    }

    public String getSupportLanguages() {
        return this.meta.Languages.keySet().toString();
    }
}
