package net.atomichive.core.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;

/**
 * Atomic Llama
 */
public class AtomicLlama extends AtomicAbstractHorse {


    private boolean isCarryingChest;
    private int strength;
    private String color;


    /**
     * Init
     * Set config values.
     * @param attributes Entity Config from entities.json.
     */
    @Override
    public void init (EntityAttributes attributes) {

        isCarryingChest = attributes.getBoolean("is_carrying_chest", false);
        strength = attributes.getInt("strength", -1);
        color = attributes.getString("color", null);

        super.init(attributes);

    }


    /**
     * Spawn
     * Generates a new entity, and places it in the world.
     * @param location to spawn entity.
     * @return Spawned entity.
     */
    @Override
    public Entity spawn (Location location) {
        return spawn(location, EntityType.LLAMA);
    }


    /**
     * Apply attributes
     * Applies everything defined in config to the entity.
     * @param entity Entity to edit.
     * @return Modified entity.
     */
    @Override
    public Entity applyAttributes (Entity entity) {

        // Cast
        Llama llama = (Llama) entity;

        // Apply
        llama.setCarryingChest(this.isCarryingChest);

        if (this.strength != -1) {
            // Clamp value between 1 and 5
            this.strength = Math.max(1, Math.min(5, this.strength));
            llama.setStrength(this.strength);
        }

        if (this.color != null) {
            Llama.Color llamaColor = getColor(this.color);
            if (llamaColor != null)
                llama.setColor(llamaColor);
        }

        return super.applyAttributes(llama);
    }


    private Llama.Color getColor (String color) {
        try {
            return Llama.Color.valueOf(color);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}