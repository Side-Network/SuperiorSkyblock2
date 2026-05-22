package com.bgsoftware.superiorskyblock.island.role;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.handlers.RolesManager;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.core.Manager;
import com.bgsoftware.superiorskyblock.core.errors.ManagerLoadException;
import com.bgsoftware.superiorskyblock.island.role.container.RolesContainer;
import com.google.common.base.Preconditions;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class RolesManagerImpl extends Manager implements RolesManager {

    private static final int ALT_ROLE_INDEX = -3;
    private static final int GUEST_ROLE_INDEX = -2;
    private static final int COOP_ROLE_INDEX = -1;
    private final RolesContainer rolesContainer;
    private int lastRole = Integer.MIN_VALUE;
    @Nullable
    private SPlayerRole altRole;

    public RolesManagerImpl(SuperiorSkyblockPlugin plugin, RolesContainer rolesContainer) {
        super(plugin);
        this.rolesContainer = rolesContainer;
    }

    @Override
    public void loadData() throws ManagerLoadException {
        this.rolesContainer.clearRoles();
        this.altRole = null;

        ConfigurationSection rolesSection = plugin.getSettings().getIslandRoles().getSection();

        ConfigurationSection guestSection = rolesSection.getConfigurationSection("guest");

        if (guestSection == null)
            throw new ManagerLoadException("Missing \"guest\" section for island roles", ManagerLoadException.ErrorLevel.SERVER_SHUTDOWN);

        ConfigurationSection coopSection = rolesSection.getConfigurationSection("coop");

        if (coopSection == null)
            throw new ManagerLoadException("Missing \"coop\" section for island roles", ManagerLoadException.ErrorLevel.SERVER_SHUTDOWN);

        SPlayerRole guestsRole = loadRole(guestSection, GUEST_ROLE_INDEX, null);

        if (plugin.getSettings().isAltMembers()) {
            ConfigurationSection altSection = rolesSection.getConfigurationSection("alt");
            if (altSection != null)
                this.altRole = loadRole(altSection, ALT_ROLE_INDEX, guestsRole, true);
        }

        SPlayerRole coopRole = loadRole(coopSection, COOP_ROLE_INDEX, guestsRole);

        ConfigurationSection laddersSection = rolesSection.getConfigurationSection("ladder");

        if (laddersSection == null)
            throw new ManagerLoadException("Missing \"ladder\" section for island roles", ManagerLoadException.ErrorLevel.SERVER_SHUTDOWN);

        List<ConfigurationSection> rolesByWeight = new LinkedList<>();
        for (String roleSectionName : laddersSection.getKeys(false))
            rolesByWeight.add(laddersSection.getConfigurationSection(roleSectionName));
        rolesByWeight.sort(Comparator.comparingInt(o -> o.getInt("weight", -1)));

        SPlayerRole previousRole = coopRole;
        for (ConfigurationSection roleSection : rolesByWeight)
            previousRole = loadRole(roleSection, previousRole.getWeight() + 1, previousRole);

        SPlayerRole.refreshRoles();
    }

    @Override
    @Nullable
    public PlayerRole getPlayerRole(int index) {
        return this.rolesContainer.getPlayerRole(index);
    }

    @Override
    @Nullable
    public PlayerRole getPlayerRoleFromId(int id) {
        return this.rolesContainer.getPlayerRoleFromId(id);
    }

    @Override
    public PlayerRole getPlayerRole(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");
        return this.rolesContainer.getPlayerRole(name);
    }

    @Override
    public PlayerRole getDefaultRole() {
        return getPlayerRole(0);
    }

    @Override
    public PlayerRole getLastRole() {
        return getPlayerRole(lastRole);
    }

    @Override
    public PlayerRole getGuestRole() {
        return getPlayerRole(GUEST_ROLE_INDEX);
    }

    @Override
    public PlayerRole getCoopRole() {
        return getPlayerRole(COOP_ROLE_INDEX);
    }

    @Override
    @Nullable
    public PlayerRole getAltRole() {
        return this.altRole;
    }

    @Override
    public List<PlayerRole> getRoles() {
        return this.rolesContainer.getRoles();
    }

    private SPlayerRole loadRole(ConfigurationSection section, int expectedWeight, SPlayerRole previousRole) throws ManagerLoadException {
        return loadRole(section, expectedWeight, previousRole, false);
    }

    private SPlayerRole loadRole(ConfigurationSection section, int expectedWeight, SPlayerRole previousRole,
                                   boolean altRole) throws ManagerLoadException {
        int weight = section.getInt("weight", expectedWeight);

        if (weight != expectedWeight)
            throw new ManagerLoadException("The role \"" + section.getName() + "\" has an unexpected weight: " +
                    weight + ", expected: " + expectedWeight, ManagerLoadException.ErrorLevel.SERVER_SHUTDOWN);

        int id = section.getInt("id", weight);
        String name = section.getString("name");
        String displayName = section.getString("display-name");

        SPlayerRole playerRole = new SPlayerRole(name, displayName, id, weight, section.getStringList("permissions"), previousRole, altRole);

        this.rolesContainer.addPlayerRole(playerRole);

        if (weight > lastRole)
            lastRole = weight;

        return playerRole;
    }

}
