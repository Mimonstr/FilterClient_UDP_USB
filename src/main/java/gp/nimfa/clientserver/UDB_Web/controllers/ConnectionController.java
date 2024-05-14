package gp.nimfa.clientserver.UDB_Web.controllers;

import gp.nimfa.clientserver.UDB_Web.model.UDPClientSocketHandler;
import gp.nimfa.clientserver.UDB_Web.model.IPConfigData;
import gp.nimfa.clientserver.UDB_Web.model.USBClientSocketHandler;
import jakarta.servlet.http.HttpSession;
import jssc.SerialPortException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Controller
public class ConnectionController
{
    private final UDPClientSocketHandler udpClientSocketHandler = new UDPClientSocketHandler();
    private final USBClientSocketHandler usbClientSocketHandler = new USBClientSocketHandler();

    private static boolean USBConnect = false;
    private static boolean UDPConnect = false;

    private final IPConfigData ipConfigData = new IPConfigData();

    private static final String CONFIG_FILE_PATH = "src/main/resources/config.ini";
    @GetMapping("/")
    public String index(Model model)
    {
        List<String> ipAddresses = ipConfigData.getAllIPAddresses();
        model.addAttribute("ipAddresses", ipAddresses);

        List<String> macAddresses = ipConfigData.getAllMACAddresses();
        model.addAttribute("macAddresses", macAddresses);

        List<String> macDescriptions = ipConfigData.getAllMACDescriptions();
        model.addAttribute("macDescriptions", macDescriptions);

        String ipAddress = ipConfigData.loadIpAddress();
        model.addAttribute("ipAddress", ipAddress);

        List<String> availablePorts = List.of(USBClientSocketHandler.getAvailableSerialPorts()); // Метод, который возвращает список доступных портов
        model.addAttribute("availablePorts", availablePorts);

        // Возвращаем имя представления (HTML-страницы)
        return "index";
    }

    @PostMapping("/edit-config")
    public String editConfig(@RequestParam("configFileContent") String newConfigContent)
    {
        try (FileWriter writer = new FileWriter(CONFIG_FILE_PATH))
        {
            writer.write(newConfigContent);
        } catch (IOException e)
        {
            e.printStackTrace();
            // Обработка ошибки записи в файл
        }
        return "redirect:/";
    }

    @PostMapping("/connectUDP")
    public String connectUDP(@RequestParam("ipAddress") String ipAddress, @RequestParam("port") int port, HttpSession session)
    {
        try
        {
            if(udpClientSocketHandler.connect(ipAddress, port))
            {
                session.setAttribute("ipAddress", ipAddress);
                session.setAttribute("port", port);
                UDPConnect = true;
                return "redirect:/sendData";
            }
            else return "redirect:/";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "error";
        }
    }

    @PostMapping("/connectUSB")
    public String connectUSB(@RequestParam("ComPort") String comPort, HttpSession session)
    {

        try
        {
            usbClientSocketHandler.connect(comPort);
            session.setAttribute("comPort", comPort);
            USBConnect = true;
            return "redirect:/sendData";
        }
        catch (Exception e)
        {
            return "error";
        }
    }

    @GetMapping("/sendData")
    public String sendDataForm()
    {
        return "sendData";
    }


    @PostMapping("/sendData")
    public void sendData(@RequestParam("leftCutoff") int leftCutoff,
                         @RequestParam("rightCutoff") int rightCutoff,
                         HttpSession session,
                         Model model) {
        try {
            if (UDPConnect)
            {
                String ipAddress = (String) session.getAttribute("ipAddress");

                if (udpClientSocketHandler.isConnected(ipAddress))
                {
                    udpClientSocketHandler.sendData(leftCutoff, rightCutoff);
                    model.addAttribute("message", "Данные успешно отправлены!");
                }
                else
                {
                    model.addAttribute("errorMessage", "Ошибка: устройство не подключено");
                }
            }
            else if (USBConnect)
            {
                String comPort = (String) session.getAttribute("comPort");
                if (usbClientSocketHandler.isConnected())
                {
                    // Отправка данных
                    usbClientSocketHandler.sendData(leftCutoff, rightCutoff);
                    model.addAttribute("message", "Данные успешно отправлены!");
                }
                else
                {
                    model.addAttribute("errorMessage", "Ошибка: устройство не подключено");
                }
            }
        }
        catch (Exception e)
        {
            model.addAttribute("errorMessage", "Произошла ошибка при отправке данных");
        }
    }


    @PostMapping("/debug")
    public void sendVoltages(@RequestParam("dacA") int dacA,
                               @RequestParam("dacB") int dacB,
                               @RequestParam("dacC") int dacC,
                               @RequestParam("dacD") int dacD,
                               HttpSession session,
                               Model model)
    {
        try
        {
            if(UDPConnect)
            {
                String ipAddress = (String) session.getAttribute("ipAddress");
                if (udpClientSocketHandler.isConnected(ipAddress))
                {
                    udpClientSocketHandler.sendData(dacA, dacB, dacC, dacD);
                    model.addAttribute("message", "Данные успешно отправлены!");
                }
                else
                {
                    model.addAttribute("errorMessage", "Ошибка: устройство не подключено");
                }
            }
            else if (USBConnect)
            {
                String comPort = (String) session.getAttribute("comPort");
                if(usbClientSocketHandler.isConnected())
                {
                    usbClientSocketHandler.sendData(dacA, dacB, dacC, dacD);
                    model.addAttribute("message", "Данные успешно отправлены!");
                }
                else
                {
                    model.addAttribute("errorMessage", "Ошибка: устройство не подключено");
                }
            }
        }
        catch (Exception e)
        {
            model.addAttribute("errorMessage", "Произошла ошибка при отправке данных");
        }
    }


    @GetMapping("/disconnect")
    public String disconnect()
    {
        if(UDPConnect)
        {
            udpClientSocketHandler.disconnect();
            UDPConnect = false;
        }
        else if (USBConnect)
        {
            try
            {
                usbClientSocketHandler.disconnect();
            }
            catch (SerialPortException e)
            {
                return "error";
            }
            USBConnect = false;
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage()
    {
        return "login"; // Возвращает страницу с формой входа
    }

    @PostMapping("/login")
    public String login(@RequestParam String password, HttpSession session)
    {
        // Проверка пароля (в данном случае пароль "password")
        if ("password".equals(password))
        {
            session.setAttribute("loggedIn", true); // Устанавливаем флаг в сессии
            return "redirect:/debug"; // Перенаправляем на страницу отладки
        }
        else
        {
            return "login"; // Если пароль неверный, снова отображаем страницу входа
        }
    }

    @GetMapping("/debug")
    public String debugPage(HttpSession session)
    {
        // Проверка наличия флага в сессии (пользователь должен быть авторизован)
        if (session.getAttribute("loggedIn") != null)
        {
            return "debug"; // Возвращает страницу отладки
        }
        else
        {
            return "redirect:/login"; // Если пользователь не авторизован, перенаправляет на страницу входа
        }
    }
}


