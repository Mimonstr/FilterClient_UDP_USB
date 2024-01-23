package gp.nimfa.clientserver.controllers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@RestController
@RequestMapping("/greet")
public class GreetController
{

    @GetMapping("/hello")
    public String greet()
    {
        try
        {
            return startServer(6666);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Error occurred";
        }
    }

    private String startServer(int port) throws IOException
    {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        )
        {
            String greeting = in.readLine();
            if ("hello server".equals(greeting))
            {
                return "hello client";
            }
            else
            {
                return "unrecognised greeting";
            }
        }
    }
}
