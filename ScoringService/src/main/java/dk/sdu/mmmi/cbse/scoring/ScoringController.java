package dk.sdu.mmmi.cbse.scoring;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
public class ScoringController {

    private int score = 0;

    @GetMapping("/score")
    public Map<String, Integer> getScore() {
        return Collections.singletonMap("score", score);
    }

    @PostMapping("/score")
    public void setScore(@RequestBody Map<String, Integer> payload) {
        this.score = payload.getOrDefault("score", 0);
    }

    @PostMapping("/score/increment")
    public void incrementScore(@RequestParam(value = "amount", defaultValue = "1") int amount) {
        this.score += amount;
    }
    @PostMapping("/score/add")
    public void addScore(@RequestParam int value) {
        score += value;
    }

}
