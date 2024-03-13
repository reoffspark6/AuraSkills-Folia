package dev.aurelium.auraskills.common.message;

import com.archyx.polyglot.Polyglot;
import com.archyx.polyglot.PolyglotProvider;
import com.archyx.polyglot.config.MessageReplacements;
import com.archyx.polyglot.config.PolyglotConfig;
import com.archyx.polyglot.config.PolyglotConfigBuilder;
import com.archyx.polyglot.lang.LangMessages;
import com.archyx.polyglot.lang.MessageManager;
import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.type.UnitMessage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Interface that provides messages for the plugin.
 */
public abstract class MessageProvider implements PolyglotProvider {

    private final AuraSkillsPlugin plugin;
    private final Polyglot polyglot;
    private final MessageManager manager;
    @Nullable
    private Locale defaultLanguage; // Lazy loaded by getDefaultLanguage
    private final Map<LocalizedKey, Component> componentCache = new HashMap<>();

    public MessageProvider(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        // Create replacement map for units
        Map<String, String> replace = new HashMap<>();
        replace.put("{mana_unit}", UnitMessage.MANA.getPath());
        replace.put("{xp_unit}", UnitMessage.XP.getPath());
        replace.put("{hp_unit}", UnitMessage.HP.getPath());

        PolyglotConfig config = new PolyglotConfigBuilder()
                .messageDirectory("messages")
                .messageFileName("messages_{language}.yml")
                .defaultLanguage("en")
                .providedLanguages(new String[] {"global", "en", "fr", "de", "es", "zh-CN", "zh-TW", "pt-BR", "it", "cs", "pl", "uk", "ko", "nl", "ja", "ru", "id", "vi"})
                .messageReplacements(new MessageReplacements(replace))
                .build();
        this.polyglot = new Polyglot(this, config);
        this.manager = this.polyglot.getMessageManager();
        this.defaultLanguage = null;
    }

    public abstract String applyFormatting(String message);

    public abstract String componentToString(Component component);

    public abstract Component stringToComponent(String message);

    public void loadMessages() {
        clearComponentCache();
        polyglot.getMessageManager().loadMessages();
    }

    public String get(MessageKey key, Locale locale) {
        // Check if the converted component is already cached
        LocalizedKey localizedKey = new LocalizedKey(key, locale);
        Component cached = componentCache.get(localizedKey);
        if (cached != null) {
            return componentToString(cached);
        }
        // Otherwise load and store to cache
        String message = manager.get(locale, convertKey(key));
        Component converted = stringToComponent(message);
        componentCache.put(localizedKey, converted);
        return componentToString(converted);
    }

    /**
     * Get a message as a raw string without any formatting applied
     *
     * @param key the message key
     * @param locale the locale to get in
     * @return the raw message
     */
    public String getRaw(MessageKey key, Locale locale) {
        return manager.get(locale, convertKey(key));
    }

    public String getSkillDisplayName(Skill skill, Locale locale) {
        String key = skill instanceof Skills ? skill.getId().getKey() : skill.getId().toString();
        return get(MessageKey.of("skills." + key + ".name"), locale);
    }

    public String getSkillDescription(Skill skill, Locale locale) {
        String key = skill instanceof Skills ? skill.getId().getKey() : skill.getId().toString();
        return get(MessageKey.of("skills." + key + ".desc"), locale);
    }

    public String getStatDisplayName(Stat stat, Locale locale) {
        String key = stat instanceof Stats ? stat.getId().getKey() : stat.getId().toString();
        return get(MessageKey.of("stats." + key + ".name"), locale);
    }

    public String getStatDescription(Stat stat, Locale locale) {
        String key = stat instanceof Stats ? stat.getId().getKey() : stat.getId().toString();
        return get(MessageKey.of("stats." + key + ".desc"), locale);
    }

    public String getStatColor(Stat stat, Locale locale) {
        String key = stat instanceof Stats ? stat.getId().getKey() : stat.getId().toString();
        return getRaw(MessageKey.of("stats." + key + ".color"), locale);
    }

    public String getStatSymbol(Stat stat, Locale locale) {
        String key = stat instanceof Stats ? stat.getId().getKey() : stat.getId().toString();
        return get(MessageKey.of("stats." + key + ".symbol"), locale);
    }

    public String getAbilityDisplayName(Ability ability, Locale locale) {
        String key = ability instanceof Abilities ? ability.getId().getKey() : ability.getId().toString();
        return get(MessageKey.of("abilities." + key + ".name"), locale);
    }

    public String getAbilityDescription(Ability ability, Locale locale) {
        String key = ability instanceof Abilities ? ability.getId().getKey() : ability.getId().toString();
        return get(MessageKey.of("abilities." + key + ".desc"), locale);
    }

    public String getAbilityInfo(Ability ability, Locale locale) {
        String key = ability instanceof Abilities ? ability.getId().getKey() : ability.getId().toString();
        return get(MessageKey.of("abilities." + key + ".info"), locale);
    }

    public String getManaAbilityDisplayName(ManaAbility ability, Locale locale) {
        String key = ability instanceof ManaAbilities ? ability.getId().getKey() : ability.getId().toString();
        return get(MessageKey.of("mana_abilities." + key + ".name"), locale);
    }

    public String getManaAbilityDescription(ManaAbility ability, Locale locale) {
        String key = ability instanceof ManaAbilities ? ability.getId().getKey() : ability.getId().toString();
        return get(MessageKey.of("mana_abilities." + key + ".desc"), locale);
    }

    public String getTraitDisplayName(Trait trait, Locale locale) {
        String key = trait instanceof Traits ? trait.getId().getKey() : trait.getId().toString();
        return get(MessageKey.of("traits." + key + ".name"), locale);
    }

    public void loadDefaultLanguageOption() {
        Locale locale = new Locale(plugin.configString(Option.DEFAULT_LANGUAGE));
        if (manager.getLoadedLanguages().contains(locale)) {
            defaultLanguage = locale;
        } else {
            defaultLanguage = manager.getDefaultLanguage();
        }
    }

    public Locale getDefaultLanguage() {
        if (defaultLanguage == null) {
            loadDefaultLanguageOption();
        }
        return defaultLanguage;
    }

    private com.archyx.polyglot.lang.MessageKey convertKey(MessageKey key) {
        return com.archyx.polyglot.lang.MessageKey.of(key.getPath());
    }

    private com.archyx.polyglot.lang.MessageKey convertKey(String path) {
        return com.archyx.polyglot.lang.MessageKey.of(path);
    }

    private void clearComponentCache() {
        componentCache.clear();
    }

    @Override
    public InputStream getResource(String path) {
        return plugin.getResource(path);
    }

    @Override
    public void saveResource(String path, boolean replace) {
        plugin.saveResource(path, replace);
    }

    @Override
    public File getDataFolder() {
        return plugin.getPluginFolder();
    }

    public boolean hasLocale(Locale locale) {
        return manager.getLoadedLanguages().contains(locale);
    }

    public Set<Locale> getLoadedLanguages() {
        return manager.getLoadedLanguages();
    }

    public List<String> getLanguageCodes() {
        return manager.getLanguageCodes();
    }

    public String getLanguageCode(Locale locale) {
        LangMessages messages = manager.getLangMessages(locale);
        if (messages != null) {
            return messages.getLanguageCode();
        }
        return locale.toLanguageTag();
    }

    @Override
    public void logInfo(String message) {
        plugin.logger().info(message);
    }

    @Override
    public void logWarn(String message) {
        plugin.logger().warn(message);
    }

    @Override
    public void logSevere(String message) {
        plugin.logger().severe(message);
    }
}
