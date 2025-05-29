package dk.sdu.mmmi.cbse.playersystem;

import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.player.Player;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class PlayerControlSystem implements IEntityProcessingService {

    private final Map<String, Long> lastShotTime = new HashMap<>();
    private static final long SHOT_COOLDOWN_NANOS = 80_000_000L; // rate of fire

    @Override
    public void process(GameData gameData, World world) {
        long now = System.nanoTime();
        for (Entity player : world.getEntities(Player.class)) {
            // Rotation and movement
            if (gameData.getKeys().isDown(GameKeys.LEFT)) {
                player.setRotation(player.getRotation() - 5);
            }
            if (gameData.getKeys().isDown(GameKeys.RIGHT)) {
                player.setRotation(player.getRotation() + 5);
            }
            if (gameData.getKeys().isDown(GameKeys.UP)) {
                double changeX = Math.cos(Math.toRadians(player.getRotation()));
                double changeY = Math.sin(Math.toRadians(player.getRotation()));
                player.setX(player.getX() + changeX);
                player.setY(player.getY() + changeY);
            }

            // Fire logic with cooldown
            String id = player.getID();
            long lastShot = lastShotTime.getOrDefault(id, 0L);
            if (gameData.getKeys().isDown(GameKeys.SPACE) && (now - lastShot > SHOT_COOLDOWN_NANOS)) {

                getBulletSPIs().stream().findFirst().ifPresent(
                        spi -> {
                            world.addEntity(spi.createBullet(player, gameData));
                            lastShotTime.put(id, now);
                        }
                );
            }

            // Make player reenter map on opposite side when going out of bounds
            if (player.getX() < 0) player.setX(1);
            if (player.getX() > gameData.getDisplayWidth()) player.setX(gameData.getDisplayWidth() - 1);
            if (player.getY() < 0) player.setY(1);
            if (player.getY() > gameData.getDisplayHeight()) player.setY(gameData.getDisplayHeight() - 1);
        }
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
