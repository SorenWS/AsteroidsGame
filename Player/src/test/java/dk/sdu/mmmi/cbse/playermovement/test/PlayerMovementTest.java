package dk.sdu.mmmi.cbse.playermovement.test;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerMovementTest {

    @Test
    void testMoveForward() {
        // Create player
        Entity player = new Player();
        player.setX(100);
        player.setY(100);
        player.setRotation(0);

        // Simulate "move forward"
        double changeX = Math.cos(Math.toRadians(player.getRotation()));
        double changeY = Math.sin(Math.toRadians(player.getRotation()));
        player.setX(player.getX() + changeX);
        player.setY(player.getY() + changeY);

        // Assert moved right
        assertEquals(101.0, player.getX(), 0.0001);
        assertEquals(100.0, player.getY(), 0.0001);
    }
}
