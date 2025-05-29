package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Interface for services that perform post-processing on entities after the main update cycle.
 * <p>
 * Typical uses include collision resolution, state synchronization, or cleanup tasks.
 */
public interface IPostEntityProcessingService {
    /**
     * Performs post-processing on an individual entity, after all main processing has occurred.
     *
     * @param gameData The current game data. Must not be {@code null}.
     * @param world The current game world. Must not be {@code null}.
     *
     * <p><b>Preconditions:</b>
     * <ul>
     *     <li>{@code gameData}, {@code world}, and {@code entity} must not be {@code null}.</li>
     *     <li>{@code entity} must be present in {@code world}.</li>
     *     <li>Main entity processing has been completed for this update cycle.</li>
     * </ul>
     * </p>
     *
     * <p><b>Postconditions:</b>
     * <ul>
     *     <li>The entity's state may be further updated based on post-processing logic.</li>
     *     <li>World state may be synchronized or cleaned up.</li>
     * </ul>
     * </p>
     */
    void process(GameData gameData, World world);
}
