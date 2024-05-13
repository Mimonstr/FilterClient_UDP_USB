package gp.nimfa.clientserver.UDB_Web.model;


import jssc.SerialPort;

import jssc.SerialPortException;

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

    public void disconnect()
    {
        if (serialPort != null && serialPort.isOpened())
        {
            try
            {
                serialPort.closePort();
                isConnected = false;
            }
            catch (SerialPortException e)
            {
                e.printStackTrace();
            }
        }
    }

    // Метод для отправки данных по COM-порту
    public void sendData(String data)
    {
        try
        {
            serialPort.writeBytes(data.getBytes());
        }
        catch (SerialPortException e)
        {
            e.printStackTrace();
        }
    }

    // Метод для приема данных по COM-порту
    public byte[] receiveData()
    {
        try
        {
            return serialPort.readBytes();
        }
        catch (SerialPortException e)
        {
            e.printStackTrace();
            return null;
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
}
