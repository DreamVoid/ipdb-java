package net.ipip.ipdb;

import com.google.gson.annotations.SerializedName;

public class AsnInfo {
    @SerializedName("asn")
    public int ASN;

    @SerializedName("reg")
    public String Registry;

    @SerializedName("cc")
    public String Country;

    @SerializedName("net")
    public String NetName;

    @SerializedName("org")
    public String OrgName;
}
