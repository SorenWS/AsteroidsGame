package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Interface for services that process entities during the game update cycle.
 * <p>
 * Typical uses include updating entity positions, handling input, or applying logic.
 */

public interface IEntityProcessingService {

    /**
     * Processes an individual entity, applying game logic based on the current state.
     *
     * @param gameData The current game data. Must not be {@code null}.
     * @param world The current game world. Must not be {@code null}.
     *
     * <p><b>Preconditions:</b>
     * <ul>
     *     <li>{@code gameData}, {@code world}, and {@code entity} must not be {@code null}.</li>
     *     <li>{@code entity} must be present in {@code world}.</li>
     * </ul>
     * </p>
     *
     * <p><b>Postconditions:</b>
     * <ul>
     *     <li>The entity's state may be updated (e.g., position, velocity, health).</li>
     *     <li>Game world state may be modified as a result of processing the entity.</li>
     * </ul>
     * </p>
     */
    void process(GameData gameData, World world);
}
