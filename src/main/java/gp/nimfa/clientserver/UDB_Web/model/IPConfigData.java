package gp.nimfa.clientserver.UDB_Web.model;

public class IPConfigData
{
    private String ipAddress;
    private String subnetMask;

    public IPConfigData() {}

    public IPConfigData(String ipAddress, String subnetMask)
    {
        this.ipAddress = ipAddress;
        this.subnetMask = subnetMask;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getSubnetMask()
    {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask)
    {
        this.subnetMask = subnetMask;
    }
}

