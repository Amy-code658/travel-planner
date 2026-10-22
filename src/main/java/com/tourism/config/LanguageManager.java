package com.tourism.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton managing application language preferences and localization notifications.
 */
public class LanguageManager {

    private static LanguageManager instance;

    public enum Language {
        ENGLISH("en", "🇺🇸 English"),
        SPANISH("es", "🇪🇸 Español"),
        FRENCH("fr", "🇫🇷 Français"),
        GERMAN("de", "🇩🇪 Deutsch"),
        HINDI("hi", "🇮🇳 हिन्दी");

        private final String code;
        private final String displayName;

        Language(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private Language currentLanguage = Language.ENGLISH;
    private final List<Runnable> listeners = new ArrayList<>();

    private LanguageManager() {}

    public static synchronized LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public void setLanguage(Language language) {
        if (language != null && language != this.currentLanguage) {
            this.currentLanguage = language;
            notifyListeners();
        }
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable r : listeners) {
            try {
                r.run();
            } catch (Exception ignored) {}
        }
    }
}
