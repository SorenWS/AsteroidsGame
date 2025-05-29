import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

module Collision {
    requires CommonAsteroids;
    requires CommonBullet;
    requires CommonEnemy;
    requires CommonPlayer;
    requires Common;

    provides IPostEntityProcessingService with dk.sdu.mmmi.cbse.collisionsystem.CollisionDetector;

    uses dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;
}