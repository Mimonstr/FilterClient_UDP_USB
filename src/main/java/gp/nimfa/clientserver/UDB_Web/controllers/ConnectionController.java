package gp.nimfa.clientserver.UDB_Web.controllers;

import gp.nimfa.clientserver.UDB_Web.UDPClientSocketHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.SocketException;

@Controller
public class ConnectionController
{
    private final UDPClientSocketHandler clientSocketHandler = new UDPClientSocketHandler();


    @GetMapping("/")
    public String index()
    {
        return "index";
    }

    @PostMapping("/connect")
    public String connect(@RequestParam("ipAddress") String ipAddress, @RequestParam("port") int port)
    {
        try
        {
            clientSocketHandler.connect(ipAddress, port);
            return "redirect:/sendData";
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
    public String sendData(@RequestParam("data") String data)
    {
        try
        {
            clientSocketHandler.sendData(data);
            return "redirect:/receivedData";
        }
        catch (IOException e)
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


