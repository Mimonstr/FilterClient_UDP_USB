package gp.nimfa.clientserver;

import gp.nimfa.clientserver.UDB_Web.model.USBClientSocketHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ClientServerApplication extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(ClientServerApplication.class);
    }
    public static void main(String[] args)
    {
        SpringApplication.run(ClientServerApplication.class, args);

        USBClientSocketHandler clientHandler = new USBClientSocketHandler();

        String portName = "COM1"; // Укажите соответствующий порт

//        boolean isDeviceAvailable = clientHandler.pingDevice(portName);
//        if (isDeviceAvailable) {
//            System.out.println("Устройство доступно");
//        } else {
//            System.out.println("Устройство недоступно");
//        }

//        USBClientSocketHandler usbClientSocketHandler = new USBClientSocketHandler();
//        System.out.println("connect ? - " + usbClientSocketHandler.connect("COM4"));
//        System.out.println("ping... " + usbClientSocketHandler.pingDevice("COM4"));
//        usbClientSocketHandler.sendData("Hello123");
//        System.out.println(usbClientSocketHandler.receiveData());
    }
}
