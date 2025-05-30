package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.enemy.Enemy;

import java.util.Random;

public class BulletControlSystem implements IEntityProcessingService, BulletSPI {

    @Override
    public void process(GameData gameData, World world) {
        int width = gameData.getDisplayWidth();
        int height = gameData.getDisplayHeight();

        for (Entity bullet : world.getEntities(Bullet.class)) {
            double changeX = Math.cos(Math.toRadians(bullet.getRotation()));
            double changeY = Math.sin(Math.toRadians(bullet.getRotation()));
            bullet.setX(bullet.getX() + changeX * 6); // Faster laser
            bullet.setY(bullet.getY() + changeY * 6);

            // Remove bullet if out of bounds
            if (bullet.getX() < 0 || bullet.getX() > width || bullet.getY() < 0 || bullet.getY() > height) {
                world.removeEntity(bullet);
            }
        }
    }

    @Override
    public Entity createBullet(Entity shooter, GameData gameData) {
        Entity bullet = new Bullet();
        Random rand = new Random();

        // Slight spread when shooting
        double spread = (rand.nextDouble() * 2) - 1;
        double angle = shooter.getRotation() + spread;

        // Star wars "Laser bolt" shape
        bullet.setPolygonCoordinates(
                3, -1,   // top right
                3, 1,    // bottom right
                -3, 1,   // bottom left
                -3, -1   // top left
        );

        double changeX = Math.cos(Math.toRadians(angle));
        double changeY = Math.sin(Math.toRadians(angle));
        bullet.setX(shooter.getX() + changeX * 10);
        bullet.setY(shooter.getY() + changeY * 10);
        bullet.setRotation(angle);
        bullet.setRadius(2); //bullet width

        // Bullet color: green for enemy, red for player
        if (shooter instanceof Enemy) {
            bullet.getProperties().put("color", "#22ff22"); // green
        } else {
            bullet.getProperties().put("color", "#ff3333"); // red
        }

        return bullet;
    }
}
