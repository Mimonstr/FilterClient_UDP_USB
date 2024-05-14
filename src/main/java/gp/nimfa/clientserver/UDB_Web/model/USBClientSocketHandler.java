package gp.nimfa.clientserver.UDB_Web.model;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import java.io.IOException;

public class USBClientSocketHandler
{

    private SerialPort serialPort;
    private boolean isConnected;

    public boolean connect(String portName)
    {
        serialPort = new SerialPort(portName);
        try
        {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            isConnected = true;
            return true;
        }
        catch (SerialPortException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() throws SerialPortException
    {
        if (serialPort != null && serialPort.isOpened())
        {
            serialPort.closePort();
            isConnected = false;
        }
    }

    // Метод для отправки данных по COM-порту
    //Частоты
    public void sendData(int leftFreq, int rightFreq) throws IOException, SerialPortException
    {
        if (serialPort != null && isConnected)
        {
            byte[] sendData = Message.messagePackaging(leftFreq, rightFreq);
            serialPort.writeBytes(sendData);
        }
    }
    //Debug
    public void sendData(int dacA, int dacB, int dacC, int dacD) throws IOException, SerialPortException
    {
        if (serialPort != null && isConnected)
        {
            byte[] sendData = Message.messageVolt(dacA, dacB, dacC, dacD);
            serialPort.writeBytes(sendData);
        }
    }


    public boolean isConnected()
    {
        return isConnected;
    }

    //Не работает как будто ждет ответа
    public boolean pingDevice(String portName)
    {
        // Создаем новый экземпляр SerialPort для указанного порта
        SerialPort serialPort = new SerialPort(portName);
        try
        {
            // Открываем порт
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // Отправляем команду "ping"
            serialPort.writeString("ping");

            // Ждем ответа в течение 2 секунд
            String response = serialPort.readString(2000);

            // Проверяем, получен ли ожидаемый ответ "OK"
            return "OK".equals(response.trim());

        }
        catch (SerialPortException e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            // Важно закрыть порт после использования
            try
            {
                serialPort.closePort();
            }
            catch (SerialPortException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static String[] getAllSerialPorts()
    {
        return SerialPortList.getPortNames();
    }
    public static String[] getAvailableSerialPorts()
    {
        String[] allPorts = SerialPortList.getPortNames();
        String[] availablePorts = new String[allPorts.length];
        int count = 0;

        for (String portName : allPorts) {
            SerialPort port = new SerialPort(portName);
            try {
                port.openPort();
                port.closePort();
                availablePorts[count++] = portName;
            } catch (SerialPortException e) {
                // Порт занят или не доступен
            }
        }

        // Создаем новый массив, чтобы избавиться от нулевых элементов (пустых слотов)
        String[] result = new String[count];
        System.arraycopy(availablePorts, 0, result, 0, count);
        return result;
    }
}
