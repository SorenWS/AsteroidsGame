package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class AsteroidProcessor implements IEntityProcessingService {

    private static final float BASE_SPEED = 0.3f;
    private static final float MAX_SIZE = 45f;

    @Override
    public void process(GameData gameData, World world) {
        float width = gameData.getDisplayWidth();
        float height = gameData.getDisplayHeight();

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            float radius = asteroid.getRadius();
            float speed = BASE_SPEED * (MAX_SIZE / radius);

            double changeX = Math.cos(Math.toRadians(asteroid.getRotation()));
            double changeY = Math.sin(Math.toRadians(asteroid.getRotation()));

            asteroid.setX(asteroid.getX() + changeX * speed);
            asteroid.setY(asteroid.getY() + changeY * speed);

            // Wrap horizontally
            if (asteroid.getX() < 0) asteroid.setX(asteroid.getX() + width);
            if (asteroid.getX() > width) asteroid.setX(asteroid.getX() - width);
            // Wrap vertically
            if (asteroid.getY() < 0) asteroid.setY(asteroid.getY() + height);
            if (asteroid.getY() > height) asteroid.setY(asteroid.getY() - height);
        }
    }
}
