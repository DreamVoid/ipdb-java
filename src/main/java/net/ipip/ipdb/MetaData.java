package net.ipip.ipdb;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * @author IPIP.net
 */
public class MetaData {
    @SerializedName("build")
    public int Build;
    @SerializedName("ip_version")
    public int IPVersion;
    @SerializedName("node_count")
    public int nodeCount;
    @SerializedName("languages")
    public Map<String, Integer> Languages;
    @SerializedName("fields")
    public String[] Fields;
    @SerializedName("total_size")
    public int totalSize;
}