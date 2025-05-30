package dk.sdu.mmmi.cbse.common.data;

public class GameKeys {

    private final boolean[] keys;
    private final boolean[] prevKeys;

    private static final int NUM_KEYS = 4;
    public static final int UP = 0, LEFT = 1, RIGHT = 2, SPACE = 3;

    public GameKeys() {
        keys = new boolean[NUM_KEYS];
        prevKeys = new boolean[NUM_KEYS];
    }

    public void update() {
        for (int i = 0; i < NUM_KEYS; i++) prevKeys[i] = keys[i];
    }

    public void setKey(int k, boolean pressed) { keys[k] = pressed; }
    public boolean isDown(int k) { return keys[k]; }
    public boolean isPressed(int k) { return keys[k] && !prevKeys[k]; }
}
