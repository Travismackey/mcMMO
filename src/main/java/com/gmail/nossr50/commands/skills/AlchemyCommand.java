package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.datatypes.skills.SubSkill;
import com.gmail.nossr50.util.SkillTextComponentFactory;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.alchemy.Alchemy.Tier;
import com.gmail.nossr50.skills.alchemy.AlchemyManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;

public class AlchemyCommand extends SkillCommand {
    private String brewSpeed;
    private String brewSpeedLucky;

    private int    tier;
    private int    ingredientCount;
    private String ingredientList;

    private boolean canCatalysis;
    private boolean canConcoctions;

    public AlchemyCommand() {
        super(PrimarySkill.ALCHEMY);
    }

    protected String[] calculateAbilityDisplayValues(Player player, boolean isLucky) {
        AlchemyManager alchemyManager = UserManager.getPlayer(player).getAlchemyManager();
        String[] displayValues = new String[2];

        displayValues[0] = decimal.format(alchemyManager.calculateBrewSpeed(false)) + "x";
        displayValues[1] = isLucky ? decimal.format(alchemyManager.calculateBrewSpeed(true)) + "x" : null;

        return displayValues;
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // ALCHEMY_CATALYSIS
        if (canCatalysis) {
            String[] catalysisStrings = calculateAbilityDisplayValues(player, isLucky);
            brewSpeed = catalysisStrings[0];
            brewSpeedLucky = catalysisStrings[1];
        }

        // ALCHEMY_CONCOCTIONS
        if (canConcoctions) {
            AlchemyManager alchemyManager = UserManager.getPlayer(player).getAlchemyManager();
            tier = alchemyManager.getTier();
            ingredientCount = alchemyManager.getIngredients().size();
            ingredientList = alchemyManager.getIngredientList();
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canCatalysis = Permissions.isSubSkillEnabled(player, SubSkill.ALCHEMY_CATALYSIS);
        canConcoctions = Permissions.isSubSkillEnabled(player, SubSkill.ALCHEMY_CONCOCTIONS);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canCatalysis) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Alchemy.Effect.0"), LocaleLoader.getString("Alchemy.Effect.1")));
        }

        if (canConcoctions) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Alchemy.Effect.2"), LocaleLoader.getString("Alchemy.Effect.3")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canCatalysis) {
            int unlockLevel = AdvancedConfig.getInstance().getCatalysisUnlockLevel();

            if (skillValue < unlockLevel) {
                messages.add(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Alchemy.Ability.Locked.0", unlockLevel)));
            }
            else {
                messages.add(LocaleLoader.getString("Alchemy.Catalysis.Speed", brewSpeed) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", brewSpeedLucky) : ""));
            }
        }

        if (canConcoctions) {
            messages.add(LocaleLoader.getString("Alchemy.Concoctions.Rank", tier, Tier.values().length));
            messages.add(LocaleLoader.getString("Alchemy.Concoctions.Ingredients", ingredientCount, ingredientList));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        SkillTextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkill.ALCHEMY);

        return textComponents;
    }
}
