package net.ipip.ipdb;

/**
 * IPv4 / IPv6 文本地址解析工具。
 *
 * <p>用于替代 JDK 内部 API {@code sun.net.util.IPAddressUtil}，
 * 语义与其保持一致：解析成功返回字节数组（IPv4 为 4 字节，IPv6 为 16 字节），
 * 格式非法返回 {@code null}，不抛异常。</p>
 */
final class IPFormat {

    private IPFormat() {
    }

    /**
     * 将点分十进制 IPv4 文本解析为 4 字节数组。
     *
     * @param ip1 IPv4 文本地址
     * @return 4 字节数组；格式非法返回 {@code null}
     */
    static byte[] textToNumericFormatV4(String ip1) {
        if (ip1 == null || ip1.isEmpty()) {
            return null;
        }

        String[] parts = ip1.split("\\.", -1);
        if (parts.length > 4) {
            return null;
        }

        // 与 InetAddress 一致：末段可以表示剩余的高位字节（如 "1.2.301"）
        int[] values = new int[4];
        int last = parts.length - 1;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            boolean tail = i == last && parts.length < 4;
            if (part.isEmpty() || (!tail && part.length() > 3) || (part.length() > 1 && part.charAt(0) == '0')) {
                return null;
            }
            int v = 0;
            for (int j = 0; j < part.length(); j++) {
                char c = part.charAt(j);
                if (c < '0' || c > '9') {
                    return null;
                }
                v = v * 10 + (c - '0');
            }
            int limit = tail ? (1 << (8 * (4 - i))) - 1 : 255;
            if (v > limit) {
                return null;
            }
            values[i] = v;
        }

        byte[] result = new byte[4];
        for (int i = 0; i < parts.length; i++) {
            if (i == last && parts.length < 4) {
                // 末段展开为剩余字节
                for (int j = 3; j >= i; j--) {
                    result[j] = (byte) (values[i] & 0xff);
                    values[i] >>>= 8;
                }
                break;
            }
            result[i] = (byte) values[i];
        }
        return result;
    }

    /**
     * 将冒分十六进制 IPv6 文本解析为 16 字节数组，支持 {@code ::} 压缩
     * 以及尾部内嵌 IPv4（如 {@code ::ffff:1.2.3.4}）。
     *
     * @param ip1 IPv6 文本地址
     * @return 16 字节数组；格式非法返回 {@code null}
     */
    static byte[] textToNumericFormatV6(String ip1) {
        if (ip1 == null || ip1.length() < 2) {
            return null;
        }

        byte[] result = new byte[16];
        int base = 0;
        int i = 0;
        int len = ip1.length();
        int doubleColon = -1;

        // 前导 "::"
        if (ip1.charAt(0) == ':') {
            if (ip1.charAt(1) != ':') {
                return null;
            }
            doubleColon = 0;
            i = 2;
            if (i == len) {
                return result; // 全零地址 "::"
            }
        }

        while (i < len) {
            // 每个段前（除首段外）应已消耗过冒号分隔符；此处解析一个段
            if (ip1.charAt(i) == ':') {
                return null;
            }

            // 尾部内嵌 IPv4（如 ::ffff:1.2.3.4），只允许出现在地址末尾
            int nextColon = ip1.indexOf(':', i);
            int dot = ip1.indexOf('.', i);
            if (dot >= 0 && (nextColon < 0 || dot < nextColon)) {
                if (nextColon >= 0) {
                    return null; // 内嵌 IPv4 后不能再有分段
                }
                byte[] v4bytes = textToNumericFormatV4(ip1.substring(i));
                if (v4bytes == null || base + 4 > 16) {
                    return null;
                }
                System.arraycopy(v4bytes, 0, result, base, 4);
                base += 4;
                i = len;
                continue;
            }

            int h = 0;
            int digits = 0;
            while (i < len) {
                char c = ip1.charAt(i);
                int d;
                if (c >= '0' && c <= '9') {
                    d = c - '0';
                } else if (c >= 'a' && c <= 'f') {
                    d = c - 'a' + 10;
                } else if (c >= 'A' && c <= 'F') {
                    d = c - 'A' + 10;
                } else {
                    break;
                }
                if (++digits > 4) {
                    return null;
                }
                h = (h << 4) | d;
                i++;
            }
            if (digits == 0 || base >= 16) {
                return null;
            }
            result[base++] = (byte) (h >> 8);
            result[base++] = (byte) h;

            if (i == len) {
                break;
            }
            // 段后必须是冒号：单冒号为分隔符，双冒号为压缩标记
            if (ip1.charAt(i) != ':') {
                return null;
            }
            if (i + 1 < len && ip1.charAt(i + 1) == ':') {
                if (doubleColon >= 0) {
                    return null;
                }
                doubleColon = base;
                i += 2;
                if (i == len) {
                    break;
                }
            } else {
                i++;
                if (i == len) {
                    return null; // 尾随单冒号非法
                }
            }
        }

        if (doubleColon < 0) {
            if (base != 16) {
                return null;
            }
            return result;
        }

        if (base > 14) {
            return null; // "::" 必须至少压缩一个非零段（8 段 + "::" 非法）
        }

        // 将 "::" 之后的段右移到数组尾部（降序复制避免覆盖未读源数据）
        for (int k = base - 1; k >= doubleColon; k--) {
            result[16 - base + k] = result[k];
        }
        // 清空 "::" 压缩留下的真空区
        for (int k = doubleColon; k < 16 - (base - doubleColon); k++) {
            result[k] = 0;
        }
        return result;
    }
}
