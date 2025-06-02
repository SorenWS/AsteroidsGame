package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AsteroidPlugin implements IGamePluginService {

    private static final int MAX_ASTEROIDS = 20;
    private static final double INITIAL_SPAWN_INTERVAL = 3.0;
    private static final double MIN_SPAWN_INTERVAL = 0.8;
    private ScheduledExecutorService scheduler;
    private double currentSpawnInterval = INITIAL_SPAWN_INTERVAL;
    private int spawnCount = 0;

    @Override
    public void start(GameData gameData, World world) {
        scheduleSpawner(gameData, world);
    }

    private void scheduleSpawner(GameData gameData, World world) {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("Asteroid-Spawner");
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            long numAsteroids = java.util.stream.StreamSupport.stream(world.getEntities().spliterator(), false)
                    .filter(e -> e instanceof Asteroid)
                    .count();

            if (numAsteroids < MAX_ASTEROIDS) {
                Entity asteroid = createAsteroid(gameData);
                world.addEntity(asteroid);
            }

            spawnCount++;
            if (spawnCount % 5 == 0 && currentSpawnInterval > MIN_SPAWN_INTERVAL) {
                currentSpawnInterval -= 0.2;
                restartSpawner(gameData, world); // Reschedule with faster interval
            }
        }, 0, (long)(currentSpawnInterval * 1000), TimeUnit.MILLISECONDS);
    }

    private void restartSpawner(GameData gameData, World world) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        scheduleSpawner(gameData, world);
    }

    @Override
    public void stop(GameData gameData, World world) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            world.removeEntity(asteroid);
        }
    }

    private Entity createAsteroid(GameData gameData) {
        Entity asteroid = new Asteroid();
        Random rand = new Random();

        int width = gameData.getDisplayWidth();
        int height = gameData.getDisplayHeight();

        // Pick a random edge
        int edge = rand.nextInt(4);
        double x = 0, y = 0;
        switch (edge) {
            case 0: x = rand.nextDouble() * width; y = 0; break;
            case 1: x = width; y = rand.nextDouble() * height; break;
            case 2: x = rand.nextDouble() * width; y = height; break;
            case 3: x = 0; y = rand.nextDouble() * height; break;
        }

        // Aim at center
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double angle = Math.atan2(centerY - y, centerX - x);

        double speed = 40 + rand.nextDouble() * 60;
        double dx = Math.cos(angle) * speed;
        double dy = Math.sin(angle) * speed;

        int size = 10 + rand.nextInt(12); // Size range
        asteroid.setPolygonCoordinates(size, -size, -size, -size, -size, size, size, size);
        asteroid.setX(x);
        asteroid.setY(y);
        asteroid.setDx((float) dx);
        asteroid.setDy((float) dy);
        asteroid.setRadius(size);
        asteroid.setRotation(Math.toDegrees(angle));

        // Set asteroid to a random gray color
        int gray = 120 + rand.nextInt(40);
        String grayHex = String.format("#%02x%02x%02x", gray, gray, gray);
        asteroid.getProperties().put("color", grayHex);

        return asteroid;
    }
}
