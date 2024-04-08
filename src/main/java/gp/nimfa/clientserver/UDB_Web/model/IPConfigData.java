package gp.nimfa.clientserver.UDB_Web.model;

public class IPConfigData
{
    private String ipAddress;
    private String subnetMask;
    private String description;
    private String macAddress;

    public IPConfigData() {}

    public IPConfigData(String ipAddress, String subnetMask, String description, String macAddress)
    {
        this.ipAddress = ipAddress;
        this.subnetMask = subnetMask;
        this.description = description;
        this.macAddress = macAddress;
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
    }
}

