package gp.nimfa.clientserver.UDB_Web.model;

import gp.nimfa.clientserver.UDB_Web.UDPClientSocketHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPConfigData
{
    public static List<String> getAllIPAddresses()
    {
        List<String> ipAddresses = new ArrayList<>();
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback())
                {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements())
                    {
                        InetAddress address = addresses.nextElement();
                        if (address instanceof Inet4Address)
                        {
                            ipAddresses.add(address.getHostAddress());
                        }
                    }
                }
            }
        } catch (SocketException e)
        {
            e.printStackTrace();
        }
        return ipAddresses;
    }

    //Получаем список всех mac-адресов
    public static List<String> getAllMACAddresses()
    {
        List<String> macAddresses = new ArrayList<>();
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null)
                {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++)
                    {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    macAddresses.add(sb.toString());
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        return macAddresses;
    }

    public static List<String> getAllMACDescriptions()
    {
        List<String> descriptions = new ArrayList<>();
        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null)
                {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++)
                    {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    String description = networkInterface.getDisplayName(); // Описание интерфейса
                    descriptions.add(description);
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        return descriptions;
    }

    public static String loadIpAddress()
    {
        try (InputStream inputStream = UDPClientSocketHandler.class.getClassLoader().getResourceAsStream("config.ini");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            String line;
            String ipAddress = null;

            // Чтение файла построчно
            while ((line = reader.readLine()) != null)
            {
                // Поиск строки, содержащей IP-адрес
                Pattern pattern = Pattern.compile("ip-адрес:\\s*(\\S+)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find())
                {
                    ipAddress = matcher.group(1);
                    break; // Выходим из цикла, если нашли IP-адрес
                }
            }

            return ipAddress;

        }
        catch (Exception e)
        {
            return "";
        }
    }
}

