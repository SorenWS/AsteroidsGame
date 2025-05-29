package dk.sdu.mmmi.cbse.collisionsystem;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;
import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.enemy.Enemy;
import dk.sdu.mmmi.cbse.common.player.Player;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import java.util.ServiceLoader;

public class CollisionDetector implements IPostEntityProcessingService {

    private final ServiceLoader<IAsteroidSplitter> splitterLoader = ServiceLoader.load(IAsteroidSplitter.class);

    @Override
    public void process(GameData gameData, World world) {
        java.util.Set<Entity> toRemove = new java.util.HashSet<>();

        for (Entity entity1 : world.getEntities()) {
            for (Entity entity2 : world.getEntities()) {
                if (entity1 == entity2 || entity1.getID().equals(entity2.getID())) continue;
                // only check each pair once so player doesn't take double damage etc
                if (entity1.getID().compareTo(entity2.getID()) > 0) continue;

                if (collides(entity1, entity2)) {
                    boolean entity1IsAsteroid = entity1 instanceof Asteroid;
                    boolean entity2IsAsteroid = entity2 instanceof Asteroid;
                    boolean entity1IsBullet = entity1 instanceof Bullet;
                    boolean entity2IsBullet = entity2 instanceof Bullet;
                    boolean entity1IsEnemy = entity1 instanceof Enemy;
                    boolean entity2IsEnemy = entity2 instanceof Enemy;
                    boolean entity1IsPlayer = entity1 instanceof Player;
                    boolean entity2IsPlayer = entity2 instanceof Player;

                    // ignore asteroids colliding with each other, enemies colliding with each other, etc.
                    if ((entity1IsAsteroid && entity2IsAsteroid) ||
                            (entity1IsEnemy && entity2IsEnemy) ||
                            (entity1IsEnemy && entity2IsAsteroid) ||
                            (entity2IsEnemy && entity1IsAsteroid)) {
                        continue;
                    }

                    // Bullet hits Asteroid
                    if (entity1IsAsteroid && entity2IsBullet) {
                        for (IAsteroidSplitter splitter : splitterLoader) {
                            splitter.createAsteroids(entity1, world);
                        }
                        toRemove.add(entity1);
                        toRemove.add(entity2);
                    } else if (entity2IsAsteroid && entity1IsBullet) {
                        for (IAsteroidSplitter splitter : splitterLoader) {
                            splitter.createAsteroids(entity2, world);
                        }
                        toRemove.add(entity2);
                        toRemove.add(entity1);
                    }
                    // enemy bullet hits player, only do damage once per collision!
                    else if (entity1IsPlayer && entity2IsBullet) {
                        boolean fromEnemy = Boolean.TRUE.equals(entity2.getProperties().get("fromEnemy"));
                        if (fromEnemy) {
                            // player takes dmg
                            Integer health = (Integer) entity1.getProperties().getOrDefault("health", 5);
                            health = Math.max(health - 1, 0);
                            entity1.getProperties().put("health", health);
                            entity1.getProperties().put("hitTimer", System.currentTimeMillis() + 150);
                            if (health <= 0) {
                                toRemove.add(entity1); // player dead
                            }
                            toRemove.add(entity2); // remove bullet
                            continue;
                        }
                    }
                    // (reverse direction already handled above)
                    // everything else just dies (player hits asteroid, etc)
                    else {
                        toRemove.add(entity1);
                        toRemove.add(entity2);
                    }
                }
            }
        }

        // do the actual removals
        for (Entity e : toRemove) {
            world.removeEntity(e);
        }
    }

    private boolean collides(Entity entity1, Entity entity2) {
        // really basic circle collision
        float dx = (float) entity1.getX() - (float) entity2.getX();
        float dy = (float) entity1.getY() - (float) entity2.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < (entity1.getRadius() + entity2.getRadius());
    }
}
