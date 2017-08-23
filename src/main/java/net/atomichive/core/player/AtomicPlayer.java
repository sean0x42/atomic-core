package net.atomichive.core.player;

import net.atomichive.core.Main;
import net.atomichive.core.util.ExperienceUtil;
import net.atomichive.core.util.ExpiringValue;
import net.atomichive.core.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Atomic Player
 * Tracks various additional things about players.
 */
public class AtomicPlayer {

    // Attributes
    private UUID identifier;
    private String username;
    private String displayName = null;
    private int level = 0;
    private int experience = 0;
    private Timestamp lastSeen;
    private int loginCount = 0;
    private short verbosity = 0;

    private transient Player lastMessageFrom = null;
    private transient ExpiringValue<Player> lastTeleportRequest;


    /**
     * Atomic Player constructor
     */
    public AtomicPlayer () {
        this(null, null);
    }


    /**
     * Atomic Player constructor
     *
     * @param identifier UUID of player.
     * @param username   Player's current username;
     */
    public AtomicPlayer (UUID identifier, String username) {

        this.identifier = identifier;
        this.username = username;
        this.lastSeen = Util.getCurrentTimestamp();

        int expiry = Main.getInstance().getBukkitConfig().getInt("teleport_request_expiry", 30);
        this.lastTeleportRequest = new ExpiringValue<>(expiry);

    }


    /**
     * @param player Bukkit player.
     * @return Whether the atomic player is the same as the bukkit player.
     */
    public boolean is (Player player) {
        return identifier.equals(player.getUniqueId());
    }


    /**
     * Gives experience to this player.
     *
     * @param experience Amount of experience to give.
     */
    public void giveExperience (int experience) {
        this.experience += experience;
        updateExperience();
    }


    /**
     * Determines if a level up is required, and performs
     * a level up if necessary.
     */
    public void updateExperience () {

        Player player = Bukkit.getPlayer(this.identifier);

        // Get required experience
        int required = ExperienceUtil.levelUpExperience(level);

        // Check if experience is high enough
        while (experience >= required) {

            // Perform level up
            level++;
            experience -= required;

            // Play effects if player is online
            if (player != null)
                playLevelUpEffects(player);

            required = ExperienceUtil.levelUpExperience(level);

        }

        // Update visually
        if (player != null) {
            player.setLevel(level);
            player.setExp(getExperienceFloat());
        }

    }


    /**
     * Shoots fireworks, plays sounds and makes a big
     * deal out of levelling up.
     *
     * @param player Player that levelled up.
     */
    private void playLevelUpEffects (Player player) {

        // Construct a new firework
        FireworkEffect effect = FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL)
                .trail(true)
                .withColor(Color.RED, Color.YELLOW, Color.ORANGE)
                .withFade(Color.BLUE)
                .build();

        Firework firework = (Firework) player.getWorld().spawnEntity(
                player.getLocation(),
                EntityType.FIREWORK
        );

        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        meta.setPower(0);
        firework.setFireworkMeta(meta);


        // Play sound effect
        // TODO Create some kind of level up sound
        player.getWorld().playSound(
                player.getLocation(),
                Sound.BLOCK_NOTE_HARP,
                1.5f,
                (float) Math.pow(2.0, ((double) 1 - 12.0) / 12.0)
        );

    }



	/*
        Getters and setters.
	 */

    public UUID getIdentifier () {
        return identifier;
    }

    public void setIdentifier (UUID identifier) {
        this.identifier = identifier;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getDisplayName () {
        return displayName;
    }

    public void setDisplayName (String displayName) {
        this.displayName = displayName;
    }

    public int getLevel () {
        return level;
    }

    public void setLevel (int level) {
        this.level = level;
    }

    public int getExperience () {
        return experience;
    }

    public void setExperience (int experience) {
        this.experience = experience;
    }

    public Timestamp getLastSeen () {
        return lastSeen;
    }

    public void setLastSeen (Timestamp lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getLoginCount () {
        return loginCount;
    }

    public void setLoginCount (int loginCount) {
        this.loginCount = loginCount;
    }

    public short getVerbosity () {
        return verbosity;
    }

    public void setVerbosity (short verbosity) {
        this.verbosity = verbosity;
    }

    public Player getLastMessageFrom () {
        return lastMessageFrom;
    }

    public void setLastMessageFrom (Player lastMessageFrom) {
        this.lastMessageFrom = lastMessageFrom;
    }

    public ExpiringValue<Player> getLastTeleportRequest () {
        return lastTeleportRequest;
    }

    public void incrementLoginCount () {
        loginCount++;
    }

    public float getExperienceFloat () {
        return Math.min(1.0f, Math.max(0.0f, experience / (float) ExperienceUtil.levelUpExperience(level)));
    }

    public void setExperienceFloat (float experience) {
        this.experience = (int) (experience * (float) ExperienceUtil.levelUpExperience(level));
    }

}
