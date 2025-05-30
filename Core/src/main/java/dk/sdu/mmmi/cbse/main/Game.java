package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.web.client.RestTemplate;

public class Game {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();
    private Text scoreText = new Text(10, 40, "Score: 0"); // Below asteroid count
    private int localScore = 0;
    private final String scoringServiceUrl = "http://localhost:8080/score";

    private final List<IGamePluginService> gamePluginServices;
    private final List<IEntityProcessingService> entityProcessingServiceList;
    private final List<IPostEntityProcessingService> postEntityProcessingServices;

    private static final int STAR_COUNT = 100;

    private final RestTemplate restTemplate;

    public Game(List<IGamePluginService> gamePluginServices,
                List<IEntityProcessingService> entityProcessingServiceList,
                List<IPostEntityProcessingService> postEntityProcessingServices,
                RestTemplate restTemplate) {
        this.gamePluginServices = gamePluginServices;
        this.entityProcessingServiceList = entityProcessingServiceList;
        this.postEntityProcessingServices = postEntityProcessingServices;
        this.restTemplate = restTemplate;
    }

    public void start(Stage window) {
        // Background stars
        Random rand = new Random();
        for (int i = 0; i < STAR_COUNT; i++) {
            double x = rand.nextDouble() * gameData.getDisplayWidth();
            double y = rand.nextDouble() * gameData.getDisplayHeight();
            Circle star = new Circle(x, y, 1, Color.WHITE);
            star.setMouseTransparent(true);
            gameWindow.getChildren().add(star);
        }

        // UI text for destroyed asteroids
        Text destroyedAsteroidsText = new Text(10, 20, "Destroyed asteroids");
        destroyedAsteroidsText.setFill(Color.WHITE);
        gameWindow.getChildren().add(destroyedAsteroidsText);

        // Score text
        scoreText.setFill(Color.WHITE);
        fetchScoreFromMicroservice();
        gameWindow.getChildren().add(scoreText);

        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        Scene scene = new Scene(gameWindow);
        scene.setFill(Color.BLACK);

        // Keyboard input handling
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) gameData.getKeys().setKey(GameKeys.LEFT, true);
            if (event.getCode().equals(KeyCode.RIGHT)) gameData.getKeys().setKey(GameKeys.RIGHT, true);
            if (event.getCode().equals(KeyCode.UP)) gameData.getKeys().setKey(GameKeys.UP, true);
            if (event.getCode().equals(KeyCode.SPACE)) gameData.getKeys().setKey(GameKeys.SPACE, true);
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) gameData.getKeys().setKey(GameKeys.LEFT, false);
            if (event.getCode().equals(KeyCode.RIGHT)) gameData.getKeys().setKey(GameKeys.RIGHT, false);
            if (event.getCode().equals(KeyCode.UP)) gameData.getKeys().setKey(GameKeys.UP, false);
            if (event.getCode().equals(KeyCode.SPACE)) gameData.getKeys().setKey(GameKeys.SPACE, false);
        });

        // Start all game plugins
        for (IGamePluginService plugin : gamePluginServices) {
            plugin.start(gameData, world);
        }

        // Add all entities' polygons for the first time
        for (Entity entity : world.getEntities()) {
            Polygon polygon = new Polygon(entity.getPolygonCoordinates());
            polygons.put(entity, polygon);
            gameWindow.getChildren().add(polygon);
        }

        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();
    }


    public void render() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastFrameTime = 0;

            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }
                float delta = (now - lastFrameTime) / 1_000_000_000f;
                lastFrameTime = now;
                gameData.setDelta(delta);

                update();
                draw();
                gameData.getKeys().update();
            }
        };
        timer.start();
    }

    private void update() {
        for (IEntityProcessingService proc : entityProcessingServiceList) {
            proc.process(gameData, world);
        }
        for (IPostEntityProcessingService postProc : postEntityProcessingServices) {
            postProc.process(gameData, world);
        }
    }

    private void draw() {
        // Remove polygons for entities that no longer exist
        polygons.keySet().removeIf(entity -> {
            if (!world.getEntities().contains(entity)) {
                Polygon removedPolygon = polygons.get(entity);
                gameWindow.getChildren().remove(removedPolygon);
                // Remove associated healthbar if it exists
                javafx.scene.shape.Rectangle oldBar = (javafx.scene.shape.Rectangle) entity.getProperties().get("healthbar");
                if (oldBar != null) {
                    gameWindow.getChildren().remove(oldBar);
                }
                return true;
            }
            return false;
        });

        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity);
            if (polygon == null) {
                polygon = new Polygon(entity.getPolygonCoordinates());
                polygons.put(entity, polygon);
                gameWindow.getChildren().add(polygon);
            }

            // PLAYER ship: color, flashing, healthbar
            if (entity.getClass().getSimpleName().toLowerCase().contains("player")) {
                Object hitTimerObj = entity.getProperties().get("hitTimer");
                boolean isFlashing = false;
                if (hitTimerObj instanceof Long) {
                    long hitTimer = (Long) hitTimerObj;
                    if (System.currentTimeMillis() < hitTimer) isFlashing = true;
                }
                if (isFlashing) {
                    polygon.setFill(Color.ORANGE);
                } else {
                    Object colorObj = entity.getProperties().get("color");
                    if (colorObj instanceof String) {
                        polygon.setFill(Color.web((String) colorObj));
                    } else {
                        polygon.setFill(Color.GRAY);
                    }
                }

                // Healthbar logic (assumes health 0-5)
                int health = ((Number) entity.getProperties().getOrDefault("health", 5)).intValue();
                double barWidth = 24.0;
                double barHeight = 4.0;
                double healthPercent = Math.max(health, 0) / 5.0;

                Color barColor;
                if (health >= 4) barColor = Color.LIMEGREEN;
                else if (health >= 2) barColor = Color.GOLD;
                else barColor = Color.RED;

                javafx.scene.shape.Rectangle bar = (javafx.scene.shape.Rectangle) entity.getProperties().get("healthbar");
                if (bar == null) {
                    bar = new javafx.scene.shape.Rectangle(barWidth, barHeight, barColor);
                    bar.setStroke(Color.WHITE);
                    bar.setStrokeWidth(0.7);
                    bar.setArcWidth(3);
                    bar.setArcHeight(3);
                    gameWindow.getChildren().add(bar);
                    entity.getProperties().put("healthbar", bar);
                }
                bar.setFill(barColor);
                bar.setWidth(barWidth * healthPercent);
                bar.setTranslateX(entity.getX() - barWidth / 2);
                bar.setTranslateY(entity.getY() - 20);
                bar.setVisible(health > 0);

            } else {
                Object colorObj = entity.getProperties().get("color");
                if (colorObj instanceof String) {
                    polygon.setFill(Color.web((String) colorObj));
                } else {
                    polygon.setFill(Color.GRAY);
                }
            }
            polygon.setTranslateX(entity.getX());
            polygon.setTranslateY(entity.getY());
            polygon.setRotate(entity.getRotation());
        }
    }

    private void fetchScoreFromMicroservice() {
        try {
            ScoreResponse response = restTemplate.getForObject(scoringServiceUrl, ScoreResponse.class);
            if (response != null) {
                localScore = response.getScore();
                scoreText.setText("Score: " + localScore);
            }
        } catch (Exception e) {
            scoreText.setText("Score: N/A");
        }
    }

    public void increaseScore(int amount) {
        localScore += amount;
        try {
            ScoreResponse req = new ScoreResponse();
            req.setScore(localScore);
            restTemplate.postForObject(scoringServiceUrl, req, ScoreResponse.class);
            scoreText.setText("Score: " + localScore);
        } catch (Exception e) {
            scoreText.setText("Score: ?");
        }
    }
}
