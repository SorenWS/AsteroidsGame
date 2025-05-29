package dk.sdu.mmmi.cbse;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.enemy.EnemySpaceship;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnemySpaceshipPlugin implements IGamePluginService {
    private final Random random = new Random();
    private ScheduledExecutorService scheduler;
    private static final int MAX_ENEMIES = 3;

    @Override
    public void start(GameData gameData, World world) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            long currentEnemies = world.getEntities(EnemySpaceship.class).spliterator().getExactSizeIfKnown();
            if (currentEnemies == -1) currentEnemies = world.getEntities(EnemySpaceship.class).stream().count();
            if (currentEnemies < MAX_ENEMIES) {
                Entity enemySpaceship = createEnemySpaceship(gameData);
                world.addEntity(enemySpaceship);
            }
        }, 0, 3 + random.nextInt(2), TimeUnit.SECONDS); // Spawn rate
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity enemy : world.getEntities(EnemySpaceship.class)) {
            world.removeEntity(enemy);
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    private Entity createEnemySpaceship(GameData gameData) {
        Entity enemySpaceship = new EnemySpaceship();
        enemySpaceship.setPolygonCoordinates(-5, -5, 10, 0, -5, 5);

        int width = gameData.getDisplayWidth();
        int height = gameData.getDisplayHeight();
        Random rand = new Random();

        // Pick a random edge to spawn
        int edge = rand.nextInt(4);
        double x = 0, y = 0;
        switch (edge) {
            case 0: // Top
                x = rand.nextDouble() * width;
                y = 0;
                break;
            case 1: // Right
                x = width;
                y = rand.nextDouble() * height;
                break;
            case 2: // Bottom
                x = rand.nextDouble() * width;
                y = height;
                break;
            case 3: // Left
                x = 0;
                y = rand.nextDouble() * height;
                break;
        }

        // Make the enemy aim toward the center when spawning
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double angle = Math.atan2(centerY - y, centerX - x);
        enemySpaceship.setRotation(Math.toDegrees(angle));

        enemySpaceship.setX(x);
        enemySpaceship.setY(y);
        enemySpaceship.setRadius(8);

        // Enemy color
        enemySpaceship.getProperties().put("color", "BLUE");

        return enemySpaceship;
    }
}
