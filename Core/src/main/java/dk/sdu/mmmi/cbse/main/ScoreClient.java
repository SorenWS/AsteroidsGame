package dk.sdu.mmmi.cbse.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class ScoreClient {

    private final RestTemplate restTemplate;
    private final String scoringServiceUrl = "http://localhost:8080/score";

    @Autowired
    public ScoreClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void addScore(int amount) {
        try {
            restTemplate.postForObject(scoringServiceUrl + "?amount=" + amount, null, Void.class);
        } catch (Exception e) {
            System.out.println("[WARN] Could not update score via microservice: " + e.getMessage());
        }
    }

    public int getScore() {
        try {
            Map response = restTemplate.getForObject("http://localhost:8080/score", Map.class);
            if (response != null && response.get("score") != null) {
                return ((Number)response.get("score")).intValue();
            }
        } catch (Exception e) {
            System.out.println("[WARN] Could not read score from microservice: " + e.getMessage());
        }
        return -1;
    }
}
