package dk.sdu.mmmi.cbse.common.data;

import java.io.Serializable;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class Entity implements Serializable {

    private final String id = UUID.randomUUID().toString();
    private double[] polygonCoordinates;
    private double x, y, rotation;
    private float radius, dx, dy;
    private final Map<String, Object> properties = new HashMap<>();

    public String getID() { return id; }

    public void setPolygonCoordinates(double... coordinates) {
        this.polygonCoordinates = coordinates;
    }

    public double[] getPolygonCoordinates() { return polygonCoordinates; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; }

    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }

    public float getDx() { return dx; }
    public void setDx(float dx) { this.dx = dx; }

    public float getDy() { return dy; }
    public void setDy(float dy) { this.dy = dy; }

    public Map<String, Object> getProperties() { return properties; }
}
