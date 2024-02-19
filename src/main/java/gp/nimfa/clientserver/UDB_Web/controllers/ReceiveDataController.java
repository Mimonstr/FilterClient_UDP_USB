package gp.nimfa.clientserver.UDB_Web.controllers;

import gp.nimfa.clientserver.UDB_Web.UDPClientSocketHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.SocketException;

@Controller
public class ReceiveDataController
{
    private final UDPClientSocketHandler clientSocketHandler = new UDPClientSocketHandler();


    //Так работает!!!
//    @GetMapping("/receivedData")
//    public String receiveData(Model model) throws IOException
//    {
//        return "receivedData";
//    }

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

//import gp.nimfa.clientserver.UDB_Web.UDPClientSocketHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.io.IOException;
//
//@Controller
//public class ReceiveDataController
//{
//
//    private final UDPClientSocketHandler clientSocketHandler;
//
//    @Autowired
//    public ReceiveDataController(UDPClientSocketHandler clientSocketHandler)
//    {
//        this.clientSocketHandler = clientSocketHandler;
//    }
//
//    @GetMapping("/receivedData")
//    public String receivedData(Model model)
//    {
//        try
//        {
//            String receivedData = clientSocketHandler.receiveData();
//            if (receivedData != null)
//            {
//                model.addAttribute("receivedData", receivedData);
//            }
//            else
//            {
//                model.addAttribute("message", "No response received for the sent command.");
//            }
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            return "error";
//        }
//        return "receivedData";
//    }
//}
