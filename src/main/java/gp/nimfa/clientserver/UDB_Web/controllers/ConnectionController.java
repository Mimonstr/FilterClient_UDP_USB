package gp.nimfa.clientserver.UDB_Web.controllers;

import gp.nimfa.clientserver.UDB_Web.UDPClientSocketHandler;
import gp.nimfa.clientserver.UDB_Web.model.IPConfigData;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class ConnectionController
{
    private final UDPClientSocketHandler clientSocketHandler = new UDPClientSocketHandler();

    @GetMapping("/")
    public String index(Model model)
    {
        try
        {
            // Выполняем команду ipconfig /all
            Process process = Runtime.getRuntime().exec("ipconfig /all");

            // Получаем вывод команды
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "Cp866"));
            StringBuilder output = new StringBuilder();
            String line;

            // Считываем вывод команды построчно
            while ((line = reader.readLine()) != null)
            {
                output.append(line).append("\n");
            }

            // Закрываем ресурсы
            reader.close();

            // Используем регулярные выражения для поиска IP-адреса и маски подсети
            Pattern ipPattern = Pattern.compile("IPv4[^0-9]*([0-9\\.]+)");
            Pattern maskPattern = Pattern.compile("Маска подсети[\\. ]+: ([0-9\\.]+)");
            Pattern descriptionPattern = Pattern.compile("Описание[\\. ]+: (.+)");
            Pattern macAddressPattern = Pattern.compile("Физический адрес[\\. ]+: (.+)");

            Matcher ipMatcher = ipPattern.matcher(output.toString());
            Matcher maskMatcher = maskPattern.matcher(output.toString());
            Matcher descriptionMatcher = descriptionPattern.matcher(output.toString());
            Matcher macAddressMatcher = macAddressPattern.matcher(output.toString());

            String ipAddress = ipMatcher.find() ? ipMatcher.group(1) : "IP-адрес не найден";
            String subnetMask = maskMatcher.find() ? maskMatcher.group(1) : "Маска подсети не найдена";
            String description = descriptionMatcher.find() ? descriptionMatcher.group(1) : "Описание не найдено";
            String macAddress = macAddressMatcher.find() ? macAddressMatcher.group(1) : "Описание не найдено";

            // Создаем объект модели и передаем его в представление
            IPConfigData ipConfigData = new IPConfigData(ipAddress, subnetMask, description, macAddress);
            model.addAttribute("ipConfigData", ipConfigData);

            // Возвращаем имя представления (HTML-страницы)
            return "index";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            model.addAttribute("ipAddress", "Ошибка выполнения команды ipconfig /all");
            model.addAttribute("subnetMask", "");
            return "index";
        }
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
            else return "error";
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


