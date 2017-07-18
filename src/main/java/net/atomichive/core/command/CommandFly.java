package net.atomichive.core.command;

import net.atomichive.core.exception.CommandException;
import net.atomichive.core.exception.PermissionException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command Fly
 * Toggles whether a player can fly.
 */
public class CommandFly extends BaseCommand {


    // Successful output format
    private final static String FORMAT = "Flight %s.";


    /**
     * Fly command constructor
     */
    public CommandFly () {
        super(
                "fly",
                "Toggles whether a player can fly.",
                "/fly [player]",
                "atomic-core.fly",
                false,
                0
        );
    }


    /**
     * Run
     * Toggles a players flight state.
     * @param sender The object that sent the command.
     * @param label  The exact command label typed by the user.
     * @param args   Any command arguments.
     * @throws CommandException if an error occurs.
     * @throws PermissionException if the user doesn't have
     *                                    appropriate permissions.
     */
    @Override
    public boolean run (CommandSender sender, String label, String[] args) throws CommandException, PermissionException {

        // If the sender is not a player, ensure more than one arg is entered.
        if (args.length == 0 && !(sender instanceof Player)) {
            throw new CommandException("Only players can change their own flight status.");
        }


        if (args.length == 0) {

            // Update the senders flight status
            Player player = (Player) sender;
            toggleFlight(player);

        } else if (args.length == 1) {

            // Ensure the user has permission
            if (!sender.hasPermission("atomic-core.fly.others")) {
                throw new PermissionException();
            }

            // Retrieve the target player
            Player target = Bukkit.getPlayer(args[0]);

            // Ensure target exists
            if (target == null)
                throw new CommandException("The player " + args[0] + " could not be found.");

            toggleFlight(target);

            // Update the command sender
            sender.sendMessage(target.getDisplayName() + "'s " + getOutputString(target).toLowerCase());

        }

        return true;

    }


    /**
     * Toggle flight
     * Toggles the flying status of a particular player.
     * @param player Player whose flight status should be toggled.
     */
    private void toggleFlight (Player player) {

        // Get flight status
        boolean canFly = player.getAllowFlight();

        player.setFallDistance(0f);
        player.setFlying(false);
        player.setAllowFlight(!canFly);

        // Update the player on their new flight status
        player.sendMessage(getOutputString(player));

    }


    /**
     * Get output string
     * Constructs a string which details a user's flight status.
     * @param player Player whose flight status will be printed.
     * @return A string which details whether a player can fly or not.
     */
    private String getOutputString (Player player) {

        // Get whether flight is enabled or disabled, and colour a string
        String status = player.getAllowFlight() ?
                ChatColor.GREEN + "enabled" :
                ChatColor.RED + "disabled";

        // Ensure that the colour is reset.
        status += ChatColor.RESET;

        return String.format(FORMAT, status);

    }

}