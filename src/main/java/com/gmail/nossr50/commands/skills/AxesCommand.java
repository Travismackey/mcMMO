package com.gmail.nossr50.commands.skills;

import java.util.ArrayList;
import java.util.List;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.datatypes.skills.SubSkill;
import com.gmail.nossr50.util.SkillTextComponentFactory;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.axes.Axes;
import com.gmail.nossr50.util.Permissions;

public class AxesCommand extends SkillCommand {
    private String critChance;
    private String critChanceLucky;
    private double axeMasteryDamage;
    private double impactDamage;
    private String skullSplitterLength;
    private String skullSplitterLengthEndurance;

    private boolean canSkullSplitter;
    private boolean canCritical;
    private boolean canAxeMastery;
    private boolean canImpact;
    private boolean canGreaterImpact;

    public AxesCommand() {
        super(PrimarySkill.AXES);
    }

    @Override
    protected void dataCalculations(Player player, float skillValue, boolean isLucky) {
        // ARMOR IMPACT
        if (canImpact) {
            impactDamage = 1 + (skillValue / Axes.impactIncreaseLevel);
        }

        // SKULL SPLITTER
        if (canSkullSplitter) {
            String[] skullSplitterStrings = calculateLengthDisplayValues(player, skillValue);
            skullSplitterLength = skullSplitterStrings[0];
            skullSplitterLengthEndurance = skullSplitterStrings[1];
        }

        // CRITICAL HIT
        if (canCritical) {
            String[] criticalHitStrings = calculateAbilityDisplayValues(skillValue, SubSkill.AXES_CRITICAL_STRIKES, isLucky);
            critChance = criticalHitStrings[0];
            critChanceLucky = criticalHitStrings[1];
        }

        // AXE MASTERY
        if (canAxeMastery) {
            axeMasteryDamage = Axes.getAxeMasteryBonusDamage(player);
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canSkullSplitter = Permissions.skullSplitter(player);
        canCritical = Permissions.isSubSkillEnabled(player, SubSkill.AXES_CRITICAL_STRIKES);
        canAxeMastery = Permissions.isSubSkillEnabled(player, SubSkill.AXES_AXE_MASTERY);
        canImpact = Permissions.isSubSkillEnabled(player, SubSkill.AXES_ARMOR_IMPACT);
        canGreaterImpact = Permissions.isSubSkillEnabled(player, SubSkill.AXES_GREATER_IMPACT);
    }

    @Override
    protected List<String> effectsDisplay() {
        List<String> messages = new ArrayList<String>();

        if (canSkullSplitter) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.0"), LocaleLoader.getString("Axes.Effect.1")));
        }

        if (canCritical) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.2"), LocaleLoader.getString("Axes.Effect.3")));
        }

        if (canAxeMastery) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.4"), LocaleLoader.getString("Axes.Effect.5")));
        }

        if (canImpact) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.6"), LocaleLoader.getString("Axes.Effect.7")));
        }

        if (canGreaterImpact) {
            messages.add(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Axes.Effect.8"), LocaleLoader.getString("Axes.Effect.9")));
        }

        return messages;
    }

    @Override
    protected List<String> statsDisplay(Player player, float skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<String>();

        if (canAxeMastery) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Axes.Ability.Bonus.0"), LocaleLoader.getString("Axes.Ability.Bonus.1", axeMasteryDamage)));
        }

        if (canImpact) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Axes.Ability.Bonus.2"), LocaleLoader.getString("Axes.Ability.Bonus.3", impactDamage)));
        }

        if (canGreaterImpact) {
            messages.add(LocaleLoader.getString("Ability.Generic.Template", LocaleLoader.getString("Axes.Ability.Bonus.4"), LocaleLoader.getString("Axes.Ability.Bonus.5", Axes.greaterImpactBonusDamage)));
        }

        if (canCritical) {
            messages.add(LocaleLoader.getString("Axes.Combat.CritChance", critChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", critChanceLucky) : ""));
        }

        if (canSkullSplitter) {
            messages.add(LocaleLoader.getString("Axes.Combat.SS.Length", skullSplitterLength) + (hasEndurance ? LocaleLoader.getString("Perks.ActivationTime.Bonus", skullSplitterLengthEndurance) : ""));
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        SkillTextComponentFactory.getSubSkillTextComponents(player, textComponents, PrimarySkill.AXES);

        return textComponents;
    }
}
