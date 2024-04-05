package gp.nimfa.clientserver.UDB_Web.controllers;
import gp.nimfa.clientserver.UDB_Web.model.IPConfigData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/ipconfig")
public class IPConfigController
{

    @GetMapping("")
    public String ipConfigAll(Model model)
    {
        try
        {
            // Выполняем команду ipconfig /all
            Process process = Runtime.getRuntime().exec("ipconfig /all");

            // Получаем вывод команды
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
            //Pattern ipPattern = Pattern.compile("IPv4 Address\\.+: (.+)");
            //Pattern ipPattern = Pattern.compile("IPv4 Address[\\. ]+: ([0-9\\.]+)");
            //Pattern maskPattern = Pattern.compile("Subnet Mask\\.+: (.+)");
            //Pattern maskPattern = Pattern.compile("Subnet Mask[\\. ]+: ([0-9\\.]+)");

            Pattern ipPattern = Pattern.compile("IPv4[^0-9]*([0-9\\.]+)");
            Pattern maskPattern = Pattern.compile("Маска подсети . . . . . . . . . . . . : ([0-9\\.]+)");

            Matcher ipMatcher = ipPattern.matcher(output.toString());
            Matcher maskMatcher = maskPattern.matcher(output.toString());

            String ipAddress = ipMatcher.find() ? ipMatcher.group(1) : "IP-адрес не найден";
            String subnetMask = maskMatcher.find() ? maskMatcher.group(1) : "Маска подсети не найдена";

            // Создаем объект модели и передаем его в представление
            IPConfigData ipConfigData = new IPConfigData(ipAddress, subnetMask);
            model.addAttribute("ipConfigData", ipConfigData);

            // Передаем результаты в модель
            // model.addAttribute("ipAddress", ipAddress);
            //model.addAttribute("subnetMask", subnetMask);

            // Возвращаем имя представления (HTML-страницы)
            return "IpConfig";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            model.addAttribute("ipAddress", "Ошибка выполнения команды ipconfig /all");
            model.addAttribute("subnetMask", "");
            return "index";
        }
    }
}

