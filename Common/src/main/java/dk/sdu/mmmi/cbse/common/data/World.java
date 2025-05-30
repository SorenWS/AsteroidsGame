package dk.sdu.mmmi.cbse.common.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Keeps track of all active game entities
public class World {

    private final Map<String, Entity> entityMap = new ConcurrentHashMap<>();

    public String addEntity(Entity entity) {
        entityMap.put(entity.getID(), entity);
        return entity.getID();
    }

    public void removeEntity(Entity entity) {
        entityMap.remove(entity.getID());
    }

    public Collection<Entity> getEntities() {
        return entityMap.values();
    }

    // Returns all entities of specific types
    @SafeVarargs
    public final <E extends Entity> List<Entity> getEntities(Class<E>... types) {
        List<Entity> matches = new ArrayList<>();
        for (Entity e : getEntities()) {
            for (Class<E> type : types) {
                if (type.equals(e.getClass())) {
                    matches.add(e);
                    break;
                }
            }
        }
        return matches;
    }
}
