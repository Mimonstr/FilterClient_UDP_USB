package gp.nimfa.clientserver.UDP;

import java.net.*;

public class UDPClientTestMessage
{
    public static void main(String[] args)
    {
        DatagramSocket socket = null;
        try
        {
            // Создаем сокет для отправки
            socket = new DatagramSocket();

            // IP адрес и порт сервера
            InetAddress serverAddress = InetAddress.getByName("192.168.0.103");
            int serverPort = 10001;

            // Отправляем первое значение "01"
            String message1 = "01";
            byte[] sendData = hexStringToByteArray(message1);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
            System.out.println("Отправлено значение: " + message1);

            // Ожидаем 3 секунды
            Thread.sleep(3000);

            // Отправляем второе значение "02"
            String message2 = "02";
            sendData = hexStringToByteArray(message2);
            sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);
            System.out.println("Отправлено значение: " + message2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (socket != null)
            {
                socket.close();
            }
        }
    }

    // Метод для преобразования строкового представления шестнадцатеричного числа в массив байтов
    public static byte[] hexStringToByteArray(String s)
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
}
