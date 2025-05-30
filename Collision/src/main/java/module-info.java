import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

module Collision {
    requires CommonAsteroids;
    requires CommonEnemy;
    requires CommonPlayer;
    requires Core;
    requires CommonBullet;
    requires Common;
    requires spring.context;
    requires spring.beans;

    provides IPostEntityProcessingService with dk.sdu.mmmi.cbse.collisionsystem.CollisionDetector;

    uses dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;

    opens dk.sdu.mmmi.cbse.collisionsystem to spring.core, spring.beans, spring.context;
}
