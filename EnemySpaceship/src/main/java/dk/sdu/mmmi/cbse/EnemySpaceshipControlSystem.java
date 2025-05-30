package dk.sdu.mmmi.cbse;

import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.enemy.EnemySpaceship;
import dk.sdu.mmmi.cbse.common.player.Player;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class EnemySpaceshipControlSystem implements IEntityProcessingService {

    private static final double ROTATION_SPEED = 2.5;
    private static final double MOVE_SPEED = 1.4;
    private static final double FIRE_CONE_DEGREES = 20;
    private static final double FIRE_RATE = 0.5;
    private static final double FIRE_RANGE_FACTOR = 0.5;

    private final Map<String, Double> lastShotTime = new HashMap<>();
    private final Random rand = new Random();

    @Override
    public void process(GameData gameData, World world) {
        Entity player = world.getEntities(Player.class).stream().findFirst().orElse(null);
        if (player == null) return;

        double playerX = player.getX();
        double playerY = player.getY();
        double centerX = gameData.getDisplayWidth() / 2.0;
        double centerY = gameData.getDisplayHeight() / 2.0;
        double maxDist = Math.hypot(gameData.getDisplayWidth(), gameData.getDisplayHeight()) * FIRE_RANGE_FACTOR;

        for (Entity enemy : world.getEntities(EnemySpaceship.class)) {
            // AI state machine
            String id = enemy.getID();
            String state = (String) enemy.getProperties().getOrDefault("state", "HUNT");
            double stateEndTime = (double) enemy.getProperties().getOrDefault("stateEndTime", 0.0);
            double now = System.currentTimeMillis() / 1000.0;

            // Switch state if timer is up
            if (now > stateEndTime) {
                if (state.equals("HUNT")) {
                    state = "ORBIT";
                } else {
                    state = "HUNT";
                }
                double duration = 1.5 + rand.nextDouble() * 2.5; // 1.5–4.0s
                enemy.getProperties().put("state", state);
                enemy.getProperties().put("stateEndTime", now + duration);
            }

            // Behavior based on state
            double dx, dy, targetAngle;
            if (state.equals("HUNT")) {
                dx = playerX - enemy.getX();
                dy = playerY - enemy.getY();
                targetAngle = Math.toDegrees(Math.atan2(dy, dx));
            } else {
                // Orbit around center
                double angleToCenter = Math.atan2(centerY - enemy.getY(), centerX - enemy.getX());
                targetAngle = Math.toDegrees(angleToCenter) + 90;
            }

            // Rotate toward target
            double currentAngle = enemy.getRotation();
            double angleDiff = (targetAngle - currentAngle + 180) % 360 - 180;
            if (Math.abs(angleDiff) < ROTATION_SPEED) {
                enemy.setRotation(targetAngle);
            } else {
                enemy.setRotation(currentAngle + Math.signum(angleDiff) * ROTATION_SPEED);
            }

            // Move forward / thrust
            double moveX = Math.cos(Math.toRadians(enemy.getRotation()));
            double moveY = Math.sin(Math.toRadians(enemy.getRotation()));
            enemy.setX(enemy.getX() + moveX * MOVE_SPEED);
            enemy.setY(enemy.getY() + moveY * MOVE_SPEED);

            // wrap screen
            if (enemy.getX() < 0) enemy.setX(gameData.getDisplayWidth() - 1);
            if (enemy.getX() > gameData.getDisplayWidth()) enemy.setX(1);
            if (enemy.getY() < 0) enemy.setY(gameData.getDisplayHeight() - 1);
            if (enemy.getY() > gameData.getDisplayHeight()) enemy.setY(1);

            // attack only in hunt mode
            if (state.equals("HUNT")) {
                double distToPlayer = Math.hypot(playerX - enemy.getX(), playerY - enemy.getY());
                double facingAngle = enemy.getRotation();
                double toPlayerAngle = Math.toDegrees(Math.atan2(playerY - enemy.getY(), playerX - enemy.getX()));
                double diffToTarget = ((toPlayerAngle - facingAngle + 180) % 360) - 180;
                if (Math.abs(diffToTarget) <= FIRE_CONE_DEGREES / 2 && distToPlayer < maxDist) {
                    double lastShot = lastShotTime.getOrDefault(id, 0.0);
                    if (now - lastShot > FIRE_RATE) {
                        getBulletSPIs().stream().findFirst().ifPresent(
                                spi -> {
                                    Entity bullet = spi.createBullet(enemy, gameData);
                                    bullet.getProperties().put("color", "#33FF33"); // bullet colour
                                    bullet.getProperties().put("fromEnemy", true);  // tag as enemy bullet
                                    world.addEntity(bullet);
                                    lastShotTime.put(id, now);
                                }
                        );
                    }
                }
            }
        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
