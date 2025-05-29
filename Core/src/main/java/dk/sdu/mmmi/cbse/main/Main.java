package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import static java.util.stream.Collectors.toList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();
    private long lastFrameTime = 0;
    private Collection<? extends IGamePluginService> plugins;
    private Collection<? extends IEntityProcessingService> entityProcessors;
    private Collection<? extends IPostEntityProcessingService> postProcessors;
    private static final int STAR_COUNT = 100; //for background




    public static void main(String[] args) {
        launch(Main.class);
    }

    @Override
    public void start(Stage window) throws Exception {
        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());

        // Add static white stars to background
        final int STAR_COUNT = 100;
        Random rand = new Random();
        for (int i = 0; i < STAR_COUNT; i++) {
            double x = rand.nextDouble() * gameData.getDisplayWidth();
            double y = rand.nextDouble() * gameData.getDisplayHeight();
            Circle star = new Circle(x, y, 1, Color.WHITE); // Tiny star
            star.setMouseTransparent(true); // So stars never eat input
            gameWindow.getChildren().add(star);
        }

        // UI text (on top of stars, but under polygons)
        Text text = new Text(10, 20, "Destroyed asteroids: 0");
        gameWindow.getChildren().add(text);

        Scene scene = new Scene(gameWindow);
        scene.setFill(Color.BLACK);

        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) {
                gameData.getKeys().setKey(GameKeys.LEFT, true);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                gameData.getKeys().setKey(GameKeys.RIGHT, true);
            }
            if (event.getCode().equals(KeyCode.UP)) {
                gameData.getKeys().setKey(GameKeys.UP, true);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, true);
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) {
                gameData.getKeys().setKey(GameKeys.LEFT, false);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                gameData.getKeys().setKey(GameKeys.RIGHT, false);
            }
            if (event.getCode().equals(KeyCode.UP)) {
                gameData.getKeys().setKey(GameKeys.UP, false);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, false);
            }
        });

        // Load all services once and cache them
        plugins = ServiceLoader.load(IGamePluginService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
        entityProcessors = ServiceLoader.load(IEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList());
        postProcessors = ServiceLoader.load(IPostEntityProcessingService.class).stream().map(ServiceLoader.Provider::get).collect(toList());

        for (IGamePluginService iGamePlugin : plugins) {
            iGamePlugin.start(gameData, world);
        }

        for (Entity entity : world.getEntities()) {
            Polygon polygon = new Polygon(entity.getPolygonCoordinates());
            polygons.put(entity, polygon);
            gameWindow.getChildren().add(polygon);
        }
        render();
        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();
    }


    private void render() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }
                // Calculate delta in seconds
                float delta = (now - lastFrameTime) / 1_000_000_000f;
                lastFrameTime = now;
                gameData.setDelta(delta);

                update();
                draw();
                gameData.getKeys().update();
            }


        }.start();
    }

    private void update() {
        for (IEntityProcessingService entityProcessorService : entityProcessors) {
            entityProcessorService.process(gameData, world);
        }
        for (IPostEntityProcessingService postEntityProcessorService : postProcessors) {
            postEntityProcessorService.process(gameData, world);
        }


        // Check for asteroids
        long numAsteroids = java.util.stream.StreamSupport.stream(world.getEntities().spliterator(), false)
                .filter(e -> e.getClass().getSimpleName().toLowerCase().contains("asteroid"))
                .count();

        if (numAsteroids == 0) {
            // Use ServiceLoader to call "nextWave" on the asteroid plugin
            for (IGamePluginService iGamePlugin : plugins) {
                if (iGamePlugin.getClass().getSimpleName().toLowerCase().contains("asteroid")) {
                    try {
                        iGamePlugin.getClass().getMethod("nextWave", GameData.class, World.class)
                                .invoke(iGamePlugin, gameData, world);
                    } catch (Exception e) {

                    }
                    break;
                }
            }

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

        // Draw and update polygons for all active entities
        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity);
            if (polygon == null) {
                polygon = new Polygon(entity.getPolygonCoordinates());
                polygons.put(entity, polygon);
                gameWindow.getChildren().add(polygon);
            }

            if (entity.getClass().getSimpleName().toLowerCase().contains("player")) {
                // Flash orange if hit
                Object hitTimerObj = entity.getProperties().get("hitTimer");
                boolean isFlashing = false;
                if (hitTimerObj instanceof Long) {
                    long hitTimer = (Long) hitTimerObj;
                    if (System.currentTimeMillis() < hitTimer) {
                        isFlashing = true;
                    }
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

                // Healthbar
                int health = ((Number) entity.getProperties().getOrDefault("health", 5)).intValue();
                double barWidth = 24.0;
                double barHeight = 4.0;
                double healthPercent = Math.max(health, 0) / 5.0;

                // Choose color
                Color barColor;
                if (health >= 4) {
                    barColor = Color.LIMEGREEN;
                } else if (health >= 2) {
                    barColor = Color.GOLD;
                } else {
                    barColor = Color.RED;
                }

                // Create or update healthbar
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
                // Other entities
                Object colorObj = entity.getProperties().get("color");
                if (colorObj instanceof String) {
                    polygon.setFill(Color.web((String) colorObj));
                } else {
                    polygon.setFill(Color.GRAY); // Fallback color
                }
            }

            polygon.setTranslateX(entity.getX());
            polygon.setTranslateY(entity.getY());
            polygon.setRotate(entity.getRotation());
        }
    }
}
