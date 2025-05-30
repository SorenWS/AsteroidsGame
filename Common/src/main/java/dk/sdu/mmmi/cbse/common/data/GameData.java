package dk.sdu.mmmi.cbse.common.data;

public class GameData {

    private int displayWidth = 900;
    private int displayHeight = 600;
    private final GameKeys keys = new GameKeys();
    private float delta;

    public GameKeys getKeys() { return keys; }
    public int getDisplayWidth() { return displayWidth; }
    public void setDisplayWidth(int w) { displayWidth = w; }

    public int getDisplayHeight() { return displayHeight; }
    public void setDisplayHeight(int h) { displayHeight = h; }

    public float getDelta() { return delta; }
    public void setDelta(float delta) { this.delta = delta; }
}
