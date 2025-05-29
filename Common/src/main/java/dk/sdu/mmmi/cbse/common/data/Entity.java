package dk.sdu.mmmi.cbse.common.data;

import java.io.Serializable;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class Entity implements Serializable {

    private final UUID ID = UUID.randomUUID();

    private double[] polygonCoordinates;
    private double x;
    private double y;
    private double rotation;
    private float radius;
    private float dx;
    private float dy;
    private final Map<String, Object> properties = new HashMap<>();



    public String getID() {
        return ID.toString();
    }


    public void setPolygonCoordinates(double... coordinates ) {
        this.polygonCoordinates = coordinates;
    }

    public double[] getPolygonCoordinates() {
        return polygonCoordinates;
    }


    public void setX(double x) {
        this.x =x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return this.radius;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDx() {
        return dx;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public float getDy() {
        return dy;
    }

    public String getId() {
        return ID.toString();
    }
    public Map<String, Object> getProperties() {
        return properties;
    }


}