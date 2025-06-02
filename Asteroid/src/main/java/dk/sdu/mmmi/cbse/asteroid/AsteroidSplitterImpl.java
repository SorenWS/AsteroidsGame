package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.World;

public class AsteroidSplitterImpl implements IAsteroidSplitter {

    @Override
    public void createAsteroids(Entity original, World world) {
        float newRadius = original.getRadius() / 2;
        if (newRadius < 8) return;

        double baseAngle = Math.atan2(original.getDy(), original.getDx());

        // Creates two fragments, that continue in the original movement direction offset by +- 20 degrees
        double[] scatterAngles = {
                baseAngle + Math.toRadians(20),
                baseAngle - Math.toRadians(20)
        };

        for (double angle : scatterAngles) {
            Asteroid fragment = new Asteroid();
            fragment.setRadius(newRadius);
            double distance = original.getRadius() + newRadius + 2;

            fragment.setX(original.getX() + Math.cos(angle) * distance);
            fragment.setY(original.getY() + Math.sin(angle) * distance);
            fragment.setRotation(Math.toDegrees(angle));
            fragment.setPolygonCoordinates(
                    newRadius, -newRadius, -newRadius, -newRadius, -newRadius, newRadius, newRadius, newRadius
            );

            // Set the velocity to "scatter" outward, so fragments don't go "inwards"
            double speed = Math.hypot(original.getDx(), original.getDy()) * (0.7 + Math.random() * 0.6);
            fragment.setDx((float) (Math.cos(angle) * speed));
            fragment.setDy((float) (Math.sin(angle) * speed));

            world.addEntity(fragment);
        }
    }
}
