package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.layout.PagedMenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;
import com.bgsoftware.superiorskyblock.api.missions.IMissionsHolder;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.missions.MissionCategory;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.AbstractPagedMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.MissionsPagedObjectButton;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractPagedMenuView;
import com.google.common.base.Preconditions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MenuMissionsCategory extends AbstractPagedMenu<MenuMissionsCategory.View, MenuMissionsCategory.Args, Mission<?>> {

    private final boolean sortByCompletion;
    private final boolean removeCompleted;
    private SuperiorPlayer target = null;

    private MenuMissionsCategory(MenuParseResult<View> parseResult, boolean sortByCompletion, boolean removeCompleted) {
        super(MenuIdentifiers.MENU_MISSIONS_CATEGORY, parseResult, false);
        this.sortByCompletion = sortByCompletion;
        this.removeCompleted = removeCompleted;
    }

    @Override
    protected View createViewInternal(SuperiorPlayer superiorPlayer, Args args,
                                      @Nullable MenuView<?, ?> previousMenuView) {
        return new View(superiorPlayer, superiorPlayer, previousMenuView, this, args);
    }

    protected View createViewInternal(SuperiorPlayer superiorPlayer, SuperiorPlayer target, Args args,
                                      @Nullable MenuView<?, ?> previousMenuView) {
        return new View(superiorPlayer, target, previousMenuView, this, args);
    }

    public void refreshViews(MissionCategory missionCategory) {
        refreshViews(view -> missionCategory.equals(view.missionCategory));
    }

    @Nullable
    public static MenuMissionsCategory createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("missions-category.yml", null,
                new MissionsPagedObjectButton.Builder());

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        PagedMenuLayout.Builder<View, Mission<?>> patternBuilder = (PagedMenuLayout.Builder<View, Mission<?>>) menuParseResult.getLayoutBuilder();

        boolean sortByCompletion = cfg.getBoolean("sort-by-completion", false);
        boolean removeCompleted = cfg.getBoolean("remove-completed", false);

        ConfigurationSection soundsSection = cfg.getConfigurationSection("sounds");
        if (soundsSection != null) {
            for (char slotChar : cfg.getString("slots", "").toCharArray()) {
                ConfigurationSection soundSection = soundsSection.getConfigurationSection(slotChar + "");

                if (soundSection == null)
                    continue;

                GameSound completedSound = MenuParserImpl.getInstance().getSound(soundSection.getConfigurationSection("completed"));
                GameSound notCompletedSound = MenuParserImpl.getInstance().getSound(soundSection.getConfigurationSection("not-completed"));
                GameSound canCompleteSound = MenuParserImpl.getInstance().getSound(soundSection.getConfigurationSection("can-complete"));

                patternBuilder.setPagedObjectSlots(menuPatternSlots.getSlots(slotChar), new MissionsPagedObjectButton.Builder()
                        .setCompletedSound(completedSound)
                        .setNotCompletedSound(notCompletedSound)
                        .setCanCompleteSound(canCompleteSound));
            }
        }

        return new MenuMissionsCategory(menuParseResult, sortByCompletion, removeCompleted);
    }

    public final CompletableFuture<MenuMissionsCategory.View> createMissionView(SuperiorPlayer superiorPlayer, SuperiorPlayer target, Args args, @Nullable MenuView<?, ?> previousMenu) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkState(superiorPlayer.isOnline(), "Cannot create view for offline player: " + superiorPlayer.getName());
        Preconditions.checkNotNull(args, "args parameter cannot be null.");
        this.target = target;
        MenuMissionsCategory.View view = createViewInternal(superiorPlayer, target, args, previousMenu);
        addView(view);
        return refreshView(view);
    }

    public static class Args implements ViewArgs {

        private final MissionCategory missionCategory;

        public Args(MissionCategory missionCategory) {
            this.missionCategory = missionCategory;
        }

    }

    public class View extends AbstractPagedMenuView<View, Args, Mission<?>> {

        private final MissionCategory missionCategory;
        private final List<Mission<?>> missions;
        private final SuperiorPlayer target;

        View(SuperiorPlayer inventoryViewer, SuperiorPlayer target, @Nullable MenuView<?, ?> previousMenuView,
             Menu<View, Args> menu, Args args) {
            super(inventoryViewer, previousMenuView, menu);

            this.missionCategory = args.missionCategory;
            this.target = target;

            if (inventoryViewer == null) {
                this.missions = Collections.emptyList();
            } else {
                SequentialListBuilder<Mission<?>> listBuilder = new SequentialListBuilder<>();

                if (sortByCompletion)
                    listBuilder.sorted(Comparator.comparingInt(this::getCompletionStatus));

                this.missions = listBuilder
                        .filter(mission -> plugin.getMissions().canDisplayMission(mission, target, removeCompleted))
                        .build(args.missionCategory.getMissions());
            }
        }

        @Override
        public String replaceTitle(String title) {
            return title.replace("{0}", missionCategory.getName());
        }

        @Override
        protected List<Mission<?>> requestObjects() {
            return missions;
        }

        private int getCompletionStatus(Mission<?> mission) {
            IMissionsHolder missionsHolder = mission.getIslandMission() ? target.getIsland() : target;
            return missionsHolder == null ? 0 :
                    !missionsHolder.canCompleteMissionAgain(mission) ? 2 :
                            plugin.getMissions().canComplete(target, mission) ? 1 : 0;
        }

        public SuperiorPlayer getTarget() {
            return target;
        }
    }

}
