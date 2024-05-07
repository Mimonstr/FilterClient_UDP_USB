package gp.nimfa.clientserver.UDB_Web.controllers;

import gp.nimfa.clientserver.UDB_Web.UDPClientSocketHandler;
import gp.nimfa.clientserver.UDB_Web.model.IPConfigData;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;



@Controller
public class ConnectionController
{
    private final UDPClientSocketHandler clientSocketHandler = new UDPClientSocketHandler();
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

    @PostMapping("/connect")
    public String connect(@RequestParam("ipAddress") String ipAddress, @RequestParam("port") int port, HttpSession session)
    {
        try
        {
            if(clientSocketHandler.connect(ipAddress, port))
            {
                session.setAttribute("ipAddress", ipAddress);
                session.setAttribute("port", port);
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

    @GetMapping("/sendData")
    public String sendDataForm()
    {
        return "sendData";
    }

    @PostMapping("/sendData")
    public String sendData(@RequestParam("leftCutoff") int leftCutoff,
                           @RequestParam("rightCutoff") int rightCutoff,
                           HttpSession session)
    {
        try
        {
            // Process the data or send it to the clientSocketHandler
            String ipAddress = (String) session.getAttribute("ipAddress");

            if(clientSocketHandler.isConnected(ipAddress))
            {
                clientSocketHandler.sendData(leftCutoff, rightCutoff);
                return "redirect:/receivedData";
            }
            else return "error";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "error";
        }
    }


    @GetMapping("/debug")
    public String debug()
    {
        return "debug";
    }
    @PostMapping("/debug")
    public String sendVoltages(@RequestParam("dacA") int dacA,
                               @RequestParam("dacB") int dacB,
                               @RequestParam("dacC") int dacC,
                               @RequestParam("dacD") int dacD,
                               HttpSession session)
    {
        try
        {
            String ipAddress = (String) session.getAttribute("ipAddress");
            if(clientSocketHandler.isConnected(ipAddress))
            {
                clientSocketHandler.sendData(dacA, dacB, dacC, dacD);
                return "redirect:/receivedData";
            }
            else return "error";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "error";
        }
    }
    @GetMapping("/disconnect")
    public String disconnect()
    {
        clientSocketHandler.disconnect();
        return "redirect:/";
    }
}


