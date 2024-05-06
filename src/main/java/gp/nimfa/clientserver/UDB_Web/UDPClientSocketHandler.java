package gp.nimfa.clientserver.UDB_Web;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UDPClientSocketHandler
{

    private static DatagramSocket socket;

    static
    {
        try
        {
            socket = new DatagramSocket();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private InetAddress serverAddress;
    private int serverPort;
    private boolean isConnected;

    public boolean ping(String ipAddress)
    {
        try
        {
            InetAddress address = InetAddress.getByName(ipAddress);
            return address.isReachable(2000); // 5000 мс таймаут
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean connect(String ipAddress, int port) throws IOException
    {
        //Нужно добавить!!!! После нажатия кнопки дисконект сокет закрывается
        // и не открывается после нажатия на кнопку конект(исправлено)
        socket = new DatagramSocket();
        if(ping(ipAddress))
        {
            serverAddress = InetAddress.getByName(ipAddress);
            serverPort = port;
            //this.ipAddress = ipAddress;
            isConnected = true;
            return true;
        }
        else
        {
            isConnected = false;
            return false;
        }

    }

    public void disconnect()
    {
        if (socket != null)
        {
            socket.close();//Могу не закрывать
            //Сокет можно сразу открыть(исправлено)
            isConnected = false;
        }
    }

    public boolean isConnected(String ipAddress)
    {
        return ping(ipAddress);
    }

    public void sendData(String data) throws IOException
    {
        byte[] sendData = hexStringToByteArray(data);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
        socket.send(sendPacket);
    }
    //Отправка частот
    public void sendData(int leftFreq, int rightFreq) throws IOException
    {
        if (socket != null && isConnected)
        {
            byte[] sendData = messagePackaging(leftFreq, rightFreq);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
        }
        else
        {
            System.out.println("Error");
        }
    }

    //Отправка напряжений на каждый канал
    public void sendData(int dacA, int dacB, int dacC, int dacD) throws IOException
    {
        if (socket != null && isConnected)
        {
            byte[] sendData = messageVolt(dacA, dacB, dacC, dacD);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
        }
        else
        {
            System.out.println("Error");
        }
    }

    //Попытка 1:
    public String receiveData() throws IOException
    {
        socket.setSoTimeout(500);
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length, serverAddress, serverPort);
        String response = "Посылка отправлена успешно!)";
        try
        {
            socket.receive(receivePacket);
            response = new String(receivePacket.getData(), 0, receivePacket.getLength());
        }
        catch (SocketTimeoutException e)
        {
            return response;
        }
        return response;
    }

    private static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static byte[] messagePackaging(int leftFreq, int rightFreq)
    {
        double a1 = 0.01716;
        double b1 = 0.003807;
        double c1 = -1.811e+06;
        double d1 = -0.01376;

        double U1_pol = a1 * Math.exp(b1 * leftFreq) + c1 * Math.exp(d1 * leftFreq);
        U1_pol = Math.round(U1_pol * 10000.0) / 10000.0;
        System.out.println("A = " + U1_pol);

        double a2 = 0.006056;
        double b2 = 0.003645;
        double c2 = -1.302e+08;
        double d2 = -0.01334;

        double U2_pol = a2 * Math.exp(b2 * rightFreq) + c2 * Math.exp(d2 * rightFreq);
        U2_pol = Math.round(U2_pol * 10000.0) / 10000.0;
        System.out.println("B = " + U2_pol);

        double DA = 0;
        double DB = 0;
        double Vref = 5;
        double VoutA = U1_pol;
        double VoutB = U2_pol;
        double n = 12;

        if (VoutA < 0) VoutA = 0;
        if (VoutA > 5) VoutA = 5;
        DA = (VoutA * Math.pow(2, n)) / Vref;
        if (DA < 0) DA = 0;
        if (DA > Math.pow(2, n)) DA = Math.pow(2, n);
        int daca = (int) Math.floor(DA);//short

        if (U1_pol < 0) U1_pol = 0;
        //numericUpDown_DAC_chA.Value = Convert.ToDecimal(U1_pol * 1000);
        int dacaHigh = (daca >> 8);
        int dacaLow = ((daca << 24) >> 24);
        byte daca_High = (byte) (dacaHigh & 0xFF);
        byte daca_Low = (byte) (dacaLow & 0xFF);

        //B
        if (VoutB < 0) VoutB = 0;
        if (VoutB > 5) VoutB = 5;
        DB = (VoutB * Math.pow(2, n)) / Vref;
        if (DB < 0) DB = 0;
        if (DB > Math.pow(2, n)) DB = Math.pow(2, n);
        int dacb = (int)Math.floor(DB);

        if (U2_pol < 0) U2_pol = 0;
        //numericUpDown_DAC_chB.Value = Convert.ToDecimal(U2_pol * 1000);
        System.out.println("VoutA = " + VoutA);
        System.out.println("VoutB = " + VoutB);
        int dacbHigh = (dacb >> 8);
        int dacbLow = ((dacb << 24) >> 24);
        byte dacb_High = (byte) (dacbHigh & 0xFF);
        byte dacb_Low = (byte) (dacbLow & 0xFF);

        //ChC and ChD
        //...
        byte dacc_High = (byte) (dacaHigh & 0xFF);
        byte dacc_Low = (byte) (dacaLow & 0xFF);

        byte dacd_High = (byte) (dacbHigh & 0xFF);
        byte dacd_Low = (byte) (dacbLow & 0xFF);

        /*Write data*/
        byte[] dataWrite = new byte[]
                { 0x0A, 0x0B, 0x0C,
                 daca_High, daca_Low,
                 dacb_High, dacb_Low,
                 dacc_High, dacc_Low,
                 dacd_High, dacd_Low
                };
        return dataWrite;
    }

    //Вводим значение напряжения на каждом канале в мВ
    // И получаем это напряжение на каждом канале
    private static byte[] messageVolt(int dacA, int dacB, int dacC, int dacD)
    {
        //ChA
        int Voltage_DACA = dacA;

        int dacaHigh = (Voltage_DACA >> 8);
        int dacaLow = ((Voltage_DACA << 24) >> 24);

        byte daca_High = (byte) (dacaHigh & 0xFF);
        byte daca_Low = (byte) (dacaLow & 0xFF);

        /*ChB*/
        int Voltage_DACB = dacB;

        int dacbHigh = (Voltage_DACB >> 8);
        int dacbLow = ((Voltage_DACB << 24) >> 24);

        byte dacb_High = (byte) (dacbHigh & 0xFF);
        byte dacb_Low = (byte) (dacbLow & 0xFF);

        //ChC
        int Voltage_DACC = dacC;

        int daccHigh = (Voltage_DACC >> 8);
        int daccLow = ((Voltage_DACC << 24) >> 24);

        byte dacc_High = (byte) (daccHigh & 0xFF);
        byte dacc_Low = (byte) (daccLow & 0xFF);
        //ChD
        int Voltage_DACD = dacD;

        int dacdHigh = (Voltage_DACD >> 8);
        int dacdLow = ((Voltage_DACD << 24) >> 24);

        byte dacd_High = (byte) (dacdHigh & 0xFF);
        byte dacd_Low = (byte) (dacdLow & 0xFF);

        /*Write data*/
        byte[] dataWrite = new byte[]
           { 0x0A, 0x0B, 0x0C,
            daca_High, daca_Low,
            dacb_High, dacb_Low,
            dacc_High, dacc_Low,
            dacd_High, dacd_Low
           };

        return dataWrite;
    }

    //Получаем ip-адреса
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

    public DatagramSocket getSocket()
    {
        return socket;
    }

    public InetAddress getServerAddress()
    {
        return serverAddress;
    }

    public int getServerPort()
    {
        return serverPort;
    }
}
