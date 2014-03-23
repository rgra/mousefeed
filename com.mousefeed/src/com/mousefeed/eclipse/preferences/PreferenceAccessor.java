/*
 * Copyright (C) Heavy Lifting Software 2007, Robert Wloch 2012.
 *
 * This file is part of MouseFeed.
 *
 * MouseFeed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MouseFeed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MouseFeed.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mousefeed.eclipse.preferences;

import static com.mousefeed.eclipse.preferences.PreferenceConstants.P_CONFIGURE_KEYBOARD_SHORTCUT_ENABLED;
import static com.mousefeed.eclipse.preferences.PreferenceConstants.P_CONFIGURE_KEYBOARD_SHORTCUT_THRESHOLD;
import static com.mousefeed.eclipse.preferences.PreferenceConstants.P_DEFAULT_ON_WRONG_INVOCATION_MODE;
import static com.mousefeed.eclipse.preferences.PreferenceConstants.P_INVOCATION_CONTROL_ENABLED;
import static com.mousefeed.eclipse.preferences.PreferenceConstants.P_NAG_CLOSE_TIMEOUT;
import static com.mousefeed.eclipse.preferences.PreferenceConstants.P_NAG_LISTENER_TIMEOUT;
import static org.apache.commons.lang.Validate.notNull;

import com.mousefeed.client.OnWrongInvocationMode;
import com.mousefeed.eclipse.Activator;
import com.mousefeed.eclipse.preferences.invocation.ActionOnWrongInvocationMode;
import com.mousefeed.eclipse.preferences.presentation.SchemeConfig;
import java.util.Collection;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Provides access to the plugin preferences. Singleton.
 * 
 * @author Andriy Palamarchuk
 * @author Robert Wloch
 * @author Rabea Gransberger (@rgransberger)
 */
public class PreferenceAccessor {

    /**
     * The singleton instance.
     */
    private static final PreferenceAccessor INSTANCE = new PreferenceAccessor();

    private final ActionsOnWrongInvocationModePreferences actionsOnWrongInvocationMode = new ActionsOnWrongInvocationModePreferences();

    private final PresentationModePreferences presentationModePreferences = new PresentationModePreferences();

    /**
     * Creates new preference accessor. Because this class is a singleton, the
     * constructor normally should not be used. It is exposed for testing
     * purposes only.
     */
    PreferenceAccessor() {
        actionsOnWrongInvocationMode.load();
        presentationModePreferences.load();
    }

    /**
     * The singleton instance.
     * 
     * @return the singleton instance. Never <code>null</code>.
     */
    public static PreferenceAccessor getInstance() {
        return INSTANCE;
    }

    /**
     * Whether invocation control is enabled preference. The preference
     * indicates whether to help to learn the desired way to invoke actions
     * 
     * @return current preference value whether invocation control is enabled.
     */
    public boolean isInvocationControlEnabled() {
        return getPreferenceStore().getBoolean(P_INVOCATION_CONTROL_ENABLED);
    }

    /**
     * @param invocationControlEnabled
     *            the new value for the setting returned by
     *            {@link #isInvocationControlEnabled()}.
     * @see #isInvocationControlEnabled()
     */
    public void storeInvocationControlEnabled(final boolean invocationControlEnabled) {
        getPreferenceStore().setValue(P_INVOCATION_CONTROL_ENABLED, invocationControlEnabled);
    }

    public int getNagPopupCloseTimeout() {
        return getPreferenceStore().getInt(P_NAG_CLOSE_TIMEOUT);
    }

    public int getNagPopupListenerTimeout() {
        return getPreferenceStore().getInt(P_NAG_LISTENER_TIMEOUT);
    }

    /**
     * Whether keyboard shortcut configuration is enabled preference. The
     * preference indicates whether to show the Keys preference page for often
     * used actions without a shortcut.
     * 
     * @return current preference value whether keyboard shortcut configuration
     *         is enabled.
     */
    public boolean isConfigureKeyboardShortcutEnabled() {
        return getPreferenceStore().getBoolean(P_CONFIGURE_KEYBOARD_SHORTCUT_ENABLED);
    }

    /**
     * @param configureKeyboardShortcutEnabled
     *            the new value for the setting returned by
     *            {@link #isConfigureKeyboardShortcutEnabled()}.
     * @see #isConfigureKeyboardShortcutEnabled()
     */
    public void storeConfigureKeyboardShortcutEnabled(final boolean configureKeyboardShortcutEnabled) {
        getPreferenceStore().setValue(P_CONFIGURE_KEYBOARD_SHORTCUT_ENABLED, configureKeyboardShortcutEnabled);
    }

    /**
     * The default preference of the threshold for the action invocation counter
     * above which keyboard shortcut configuration is enabled.
     * 
     * @return the global configure keyboard shortcut threshold preference.
     * @see PreferenceConstants#P_CONFIGURE_KEYBOARD_SHORTCUT_THRESHOLD
     */
    public int getConfigureKeyboardShortcutThreshold() {
        return getPreferenceStore().getInt(P_CONFIGURE_KEYBOARD_SHORTCUT_THRESHOLD);
    }

    /**
     * @param configureKeyboardShortcutThreshold
     *            the new value for the setting returned by
     *            {@link #getConfigureKeyboardShortcutThreshold()}.
     * @see #getConfigureKeyboardShortcutThreshold()
     */
    public void storeConfigureKeyboardShortcutThreshold(final int configureKeyboardShortcutThreshold) {
        getPreferenceStore().setValue(P_CONFIGURE_KEYBOARD_SHORTCUT_THRESHOLD, configureKeyboardShortcutThreshold);
    }

    /**
     * The default preference what to do by default on wrong invocation mode.
     * 
     * @return the global invocation mode preference. Never <code>null</code>.
     * @see PreferenceConstants#P_DEFAULT_ON_WRONG_INVOCATION_MODE
     */
    public OnWrongInvocationMode getOnWrongInvocationMode() {
        final String stored = getPreferenceStore().getString(P_DEFAULT_ON_WRONG_INVOCATION_MODE);
        return stored == null ? OnWrongInvocationMode.DEFAULT : OnWrongInvocationMode.valueOf(stored);
    }

    /**
     * The preference what to do on wrong invocation mode for the specified
     * action.
     * 
     * @param actionId
     *            the id of the action get preferences for. Not
     *            <code>null</code>.
     * @return the invocation mode preference. <code>null</code> if there is no
     *         action-specific setting. In this case use the default preference
     *         value.
     */
    public OnWrongInvocationMode getOnWrongInvocationMode(final String actionId) {
        notNull(actionId);
        final ActionOnWrongInvocationMode mode = actionsOnWrongInvocationMode.get(actionId);
        return mode == null ? null : mode.getOnWrongInvocationMode();
    }

    /**
     * Saves action-specific on wrong invocation mode settings.
     * 
     * @param settings
     *            the new value. Not <code>null</code>.
     */
    public void setOnWrongInvocationMode(final ActionOnWrongInvocationMode settings) {
        notNull(settings);
        actionsOnWrongInvocationMode.add(settings);

    }

    /**
     * Returns action-specific wrong invocation mode handling for all actions.
     * 
     * @return the action-specific settings. Read-only. The collection nor its
     *         elements should not be changed. Note, the collection and the
     *         objects in it should be cloned for using for any significant
     *         period of time. Never <code>null</code>, can be empty if no
     *         action-specific settings were defined. All objects in the
     *         collection have unique ids.
     */
    public Collection<ActionOnWrongInvocationMode> getActionsOnWrongInvocationMode() {
        return actionsOnWrongInvocationMode.values();
    }

    /**
     * Replaces the action-specific settings.
     * 
     * @param settings
     *            the new settings. Not <code>null</code>. Changes to the data
     *            passed to this method won't affect the values stored in this
     *            class.
     * @see #getActionsOnWrongInvocationMode()
     */
    public void setActionsOnWrongInvocationMode(final Collection<ActionOnWrongInvocationMode> settings) {
        this.actionsOnWrongInvocationMode.save(settings);
    }

    public Collection<SchemeConfig> getPresentationModeSchemeConfigs() {
        return presentationModePreferences.values();
    }

    public void setUsePresentationModeParentheses(boolean selection) {
        presentationModePreferences.setUseParentheses(selection);
    }

    public boolean isUsePresentationModeParentheses() {
        return presentationModePreferences.isUseParentheses();
    }

    public void removePresentationModePreference(String schemeId) {
        this.presentationModePreferences.remove(schemeId);
    }

    public void addPresentationModeSchemeConfigs(SchemeConfig config) {
        this.presentationModePreferences.add(config);
    }

    public void restoreDefaultsPresentationModePreferences() {
        this.presentationModePreferences.defaults();
    }

    public void savePresentationModePreferences() {
        this.presentationModePreferences.save();
    }

    /**
     * Removes action-specific on wrong invocation mode setting. After calling
     * this method when the action with the specified id is handled using the
     * default settings.
     * 
     * @param actionId
     *            the action id. not <code>null</code>.
     */
    public void removeOnWrongInvocaitonMode(final String actionId) {
        notNull(actionId);
        actionsOnWrongInvocationMode.remove(actionId);

    }

    /**
     * Stores the provided preference.
     * 
     * @param value
     *            the new value. Not <code>null</code>.
     */
    public void storeOnWrongInvocationMode(final OnWrongInvocationMode value) {
        notNull(value);
        getPreferenceStore().setValue(P_DEFAULT_ON_WRONG_INVOCATION_MODE, value.name());
    }

    /**
     * The plugin preference store.
     * 
     * @return never <code>null</code>.
     */
    private IPreferenceStore getPreferenceStore() {
        return Activator.getDefault().getPreferenceStore();
    }

}
