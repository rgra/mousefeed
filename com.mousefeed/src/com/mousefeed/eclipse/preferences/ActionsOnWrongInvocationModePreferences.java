package com.mousefeed.eclipse.preferences;

import com.mousefeed.client.OnWrongInvocationMode;
import com.mousefeed.eclipse.Activator;
import com.mousefeed.eclipse.preferences.invocation.ActionOnWrongInvocationMode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * 
 * @author Rabea Gransberger (@rgransberger)
 */
class ActionsOnWrongInvocationModePreferences {

    /**
     * Name of the file to store action-specific behavior when the actions are
     * invoked wrong way.
     */
    private static final String ACTIONS_WRONG_INVOCATION_MODE_FILE = "actionsWrongInvocationMode.xml";

    /**
     * The root document for the action-specific wrong invocation mode
     * preferences.
     */
    private static final String TAG_ACTIONS_WRONG_INVOCATION_MODE = "actionsWrongInvocationMode";

    /**
     * The action tag name.
     */
    private static final String TAG_ACTION = "action";

    /**
     * The action id tag name.
     */
    private static final String TAG_ACTION_ID = "id";

    /**
     * The action label tag name.
     */
    private static final String TAG_ACTION_LABEL = "label";

    /**
     * The on wrong invocation mode handling approach.
     */
    private static final String TAG_ON_WRONG_INVOCATION_MODE = "onWrongInvocationMode";

    /**
     * Actions on wrong invocation mode settings. Keys - action ids, values -
     * the settings.
     */
    private final Map<String, ActionOnWrongInvocationMode> actionsOnWrongMode = new HashMap<String, ActionOnWrongInvocationMode>();

    /**
     * Loads preferences for the {@link #getOnWrongInvocationMode(String)}.
     */
    void load() {
        final File file = getActionsWrongInvocationModeFile();
        if (!file.exists() || file.length() == 0) {
            // the file not initialized yet
            return;
        }
        Reader reader = null;
        try {
            reader = new FileReader(file);
            final XMLMemento memento = XMLMemento.createReadRoot(reader);
            loadActionsOnWrongInvocationMode(memento);
        } catch (final FileNotFoundException ignore) {
            // the file does not exist yet
        } catch (final WorkbenchException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
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
    public void save(final Collection<ActionOnWrongInvocationMode> settings) {
        this.actionsOnWrongMode.clear();
        for (ActionOnWrongInvocationMode mode : settings) {
            final ActionOnWrongInvocationMode clone;
            try {
                clone = (ActionOnWrongInvocationMode) mode.clone();
            } catch (final CloneNotSupportedException e) {
                throw new RuntimeException();
            }
            this.actionsOnWrongMode.put(clone.getId(), clone);
        }
        saveActionsOnWrongInvocationMode();
    }

    public Collection<ActionOnWrongInvocationMode> values() {
        return actionsOnWrongMode.values();
    }

    public void add(ActionOnWrongInvocationMode settings) {
        this.actionsOnWrongMode.put(settings.getId(), settings);
        saveActionsOnWrongInvocationMode();
    }

    public ActionOnWrongInvocationMode get(String actionId) {
        return this.actionsOnWrongMode.get(actionId);
    }

    public void remove(String actionId) {
        this.actionsOnWrongMode.remove(actionId);
        saveActionsOnWrongInvocationMode();
    }

    /**
     * Loads action-specific invocation handling.
     * 
     * @param memento
     *            the memento to load the data from. Assumed not
     *            <code>null</code>.
     * @see #loadActionsOnWrongInvocationMode()
     */
    private void loadActionsOnWrongInvocationMode(final XMLMemento memento) {
        actionsOnWrongMode.clear();
        final IMemento[] children = memento.getChildren(TAG_ACTION);
        for (IMemento child : children) {
            final ActionOnWrongInvocationMode mode = new ActionOnWrongInvocationMode();
            mode.setLabel(child.getString(TAG_ACTION_LABEL));
            mode.setId(child.getString(TAG_ACTION_ID));
            mode.setOnWrongInvocationMode(OnWrongInvocationMode.valueOf(child.getString(TAG_ON_WRONG_INVOCATION_MODE)));
            actionsOnWrongMode.put(mode.getId(), mode);
        }
    }

    /**
     * Saves settings loaded by {@link #loadActionsOnWrongInvocationMode()}.
     */
    private void saveActionsOnWrongInvocationMode() {
        final XMLMemento memento = createActionsOnWrongInvocationModeMemento();
        Writer writer = null;
        try {
            writer = new FileWriter(getActionsWrongInvocationModeFile());
            memento.save(writer);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Generates XML memento with the actions wrong invocation mode data.
     * 
     * @return the memento. Never <code>null</code>.
     */
    private XMLMemento createActionsOnWrongInvocationModeMemento() {
        final XMLMemento memento = XMLMemento.createWriteRoot(TAG_ACTIONS_WRONG_INVOCATION_MODE);
        for (ActionOnWrongInvocationMode val : actionsOnWrongMode.values()) {
            final IMemento actionMemento = memento.createChild(TAG_ACTION);
            actionMemento.putString(TAG_ACTION_ID, val.getId());
            actionMemento.putString(TAG_ACTION_LABEL, val.getLabel());
            actionMemento.putString(TAG_ON_WRONG_INVOCATION_MODE, val.getOnWrongInvocationMode().name());
        }
        return memento;
    }

    /**
     * File storing action-specific preferences for action invocation mode.
     * 
     * @return the file for action-specific preferences when the actions are
     *         invoked with a wrong invocation mode.
     */
    File getActionsWrongInvocationModeFile() {
        if (Activator.getDefault() == null) {
            return new File("nonexisting");
        }
        return Activator.getDefault().getStateLocation().append(ACTIONS_WRONG_INVOCATION_MODE_FILE).toFile();
    }

}