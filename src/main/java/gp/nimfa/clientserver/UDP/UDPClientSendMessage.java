package gp.nimfa.clientserver.UDP;

import java.net.*;
import java.util.Scanner;

public class UDPClientSendMessage
{
    public static void main(String[] args)
    {
        DatagramSocket socket = null;
        Scanner scanner = new Scanner(System.in);
        try
        {
            // Создаем сокет для отправки
            socket = new DatagramSocket();

            // IP адрес и порт сервера
            InetAddress serverAddress = InetAddress.getByName("192.168.0.103");
            int serverPort = 10001;

            while (true)
            {
                // Вводим данные для отправки
                System.out.print("Введите данные для отправки (в шестнадцатеричном формате, или 'end' для завершения): ");
                String message = scanner.nextLine();

                if (message.equals("end"))
                {
                    break; // Завершаем цикл при вводе слова 'end'
                }

                // Преобразуем введенные данные в массив байтов
                byte[] sendData = hexStringToByteArray(message);

                // Отправляем данные
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                socket.send(sendPacket);
                System.out.println("Отправлено значение: " + message);
            }

            System.out.println("Удалось подключиться к серверу.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Не удалось подключиться к серверу.");
        }
        finally
        {
            if (socket != null)
            {
                socket.close();
            }
            scanner.close();
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
