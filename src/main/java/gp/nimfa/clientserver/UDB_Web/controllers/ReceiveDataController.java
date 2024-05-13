package gp.nimfa.clientserver.UDB_Web.controllers;

import gp.nimfa.clientserver.UDB_Web.model.UDPClientSocketHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class ReceiveDataController
{
    private final UDPClientSocketHandler clientSocketHandler = new UDPClientSocketHandler();

    @GetMapping("/receivedData")
    public String receiveData(Model model)
    {
        try
        {
            String receivedData = clientSocketHandler.receiveData();
            model.addAttribute("receivedData", receivedData);
            return "receivedData";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "error";
        }
    }

}

