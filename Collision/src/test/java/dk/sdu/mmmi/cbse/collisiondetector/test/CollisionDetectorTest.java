package dk.sdu.mmmi.cbse.collisiondetector.test;

import org.junit.jupiter.api.Test;
import dk.sdu.mmmi.cbse.collisionsystem.CollisionDetector;
import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.player.Player;

import static org.junit.jupiter.api.Assertions.*;

class CollisionDetectorTest {

    @Test
    void testPlayerAsteroidCollisionRemovesBoth() {
        GameData gameData = new GameData();
        World world = new World();

        // Create player and asteroid, positioned to collide
        Entity player = new Player();
        player.setX(100);
        player.setY(100);
        player.setRadius(10);

        Entity asteroid = new Asteroid();
        asteroid.setX(105);
        asteroid.setY(100);
        asteroid.setRadius(10);

        world.addEntity(player);
        world.addEntity(asteroid);

        CollisionDetector detector = new CollisionDetector();

        detector.process(gameData, world);

        assertFalse(world.getEntities().contains(player), "Player should be removed on collision.");
        assertFalse(world.getEntities().contains(asteroid), "Asteroid should be removed on collision.");
    }
}
