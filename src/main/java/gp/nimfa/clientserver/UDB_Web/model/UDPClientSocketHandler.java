package gp.nimfa.clientserver.UDB_Web.model;

import gp.nimfa.clientserver.UDB_Web.model.Message;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;

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


    //Отправка частот
    public void sendData(int leftFreq, int rightFreq) throws IOException
    {
        if (socket != null && isConnected)
        {
            byte[] sendData = Message.messagePackaging(leftFreq, rightFreq);
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
            byte[] sendData = Message.messageVolt(dacA, dacB, dacC, dacD);
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
