import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

module EnemySpaceship {
    requires CommonBullet;
    requires CommonEnemy;
    requires CommonPlayer;
    requires Common;
    uses dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
    provides IGamePluginService with dk.sdu.mmmi.cbse.EnemySpaceshipPlugin;
    provides IEntityProcessingService with dk.sdu.mmmi.cbse.EnemySpaceshipControlSystem;

}