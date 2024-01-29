package gp.nimfa.clientserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ClientServerApplicationTests
{

    @Test
    void contextLoads()
    {
    }
    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws IOException
    {
        GreetClient client = new GreetClient();
        //client.startConnection("127.0.0.1", 2030);
        client.startConnection("localhost", 2030);
        String response = client.sendMessage("hello server");
        assertEquals("hello client", response);
    }

}
