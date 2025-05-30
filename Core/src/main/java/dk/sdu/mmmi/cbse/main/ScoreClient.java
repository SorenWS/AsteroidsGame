package dk.sdu.mmmi.cbse.main;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ScoreClient {
    private final RestTemplate restTemplate;

    @Autowired
    public ScoreClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void addScore(int value) {
        try {
            // Call endpoint (you may need to implement this endpoint in your microservice)
            restTemplate.postForObject("http://localhost:8080/score/add?value=" + value, null, Void.class);
        } catch (Exception e) {
            System.out.println("Failed to add score: " + e.getMessage());
        }
    }
}
