package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Interface for game plugins that can be started and stopped dynamically.
 * <p>
 * Implementations of this interface provide modular functionality to the game
 * (such as spawning entities or registering systems) and can be added or removed
 * at runtime.
 */

public interface IGamePluginService {
    /**
     * Initializes and activates the plugin, registering entities or systems as needed.
     *
     * @param gameData The current game data. Must not be {@code null}.
     * @param world The game world to which the plugin will add entities or modify state. Must not be {@code null}.
     *
     * <p><b>Preconditions:</b>
     * <ul>
     *     <li>{@code gameData} and {@code world} must not be {@code null}.</li>
     *     <li>The plugin must not already be started.</li>
     * </ul>
     * </p>
     *
     * <p><b>Postconditions:</b>
     * <ul>
     *     <li>The plugin's entities, systems, or functionality are registered in the world.</li>
     *     <li>Game state may be modified according to the plugin's purpose.</li>
     * </ul>
     * </p>
     */

    void start(GameData gameData, World world);

    /**
     * Deactivates the plugin, unregistering entities or systems as needed.
     *
     * @param gameData The current game data. Must not be {@code null}.
     * @param world The game world from which the plugin will remove entities or revert state. Must not be {@code null}.
     *
     * <p><b>Preconditions:</b>
     * <ul>
     *     <li>{@code gameData} and {@code world} must not be {@code null}.</li>
     *     <li>The plugin must currently be active.</li>
     * </ul>
     * </p>
     *
     * <p><b>Postconditions:</b>
     * <ul>
     *     <li>The plugin's entities, systems, or functionality are removed or deactivated in the world.</li>
     *     <li>Game state may be reverted or cleaned up according to the plugin's requirements.</li>
     * </ul>
     * </p>
     */

    void stop(GameData gameData, World world);
}
