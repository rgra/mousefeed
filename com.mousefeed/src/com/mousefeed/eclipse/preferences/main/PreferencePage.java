/*
 * Copyright (C) Heavy Lifting Software 2007.
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
package com.mousefeed.eclipse.preferences.main;

import com.mousefeed.client.Messages;
import com.mousefeed.client.OnWrongInvocationMode;
import com.mousefeed.eclipse.Activator;
import com.mousefeed.eclipse.preferences.PreferenceConstants;
import com.mousefeed.eclipse.preferences.main.editors.BooleanFieldEditorWithSelectionListener;
import com.mousefeed.eclipse.preferences.main.editors.SpinnerFieldEditor;
import java.util.concurrent.TimeUnit;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Main MouseFeed preferences page.
 *
 * @author Andriy Palamarchuk
 * @author Rabea Gransberger (@rgransberger)
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {



    /**
     * Provides messages text.
     */
    private static final Messages MESSAGES = new Messages(PreferencePage.class);

    /**
     * Creates new preference page.
     */
    public PreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(MESSAGES.get("description"));
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    @Override
    public void createFieldEditors() {
        addEditor(new BooleanFieldEditorWithSelectionListener(PreferenceConstants.P_INVOCATION_CONTROL_ENABLED,
                MESSAGES.get("field.invocationControlEnabledCheckbox.label"), getFieldEditorParent()));
        addEditor(new ComboFieldEditor(PreferenceConstants.P_DEFAULT_ON_WRONG_INVOCATION_MODE,
                MESSAGES.get("field.defaultOnWrongInvocationMode.label"), createInvocationModeInput(),
                getFieldEditorParent()));

        GridDataFactory.fillDefaults().span(2, 1).applyTo(new Label(getFieldEditorParent(), SWT.NONE));
        addEditor(new BooleanFieldEditor(PreferenceConstants.P_CONFIGURE_KEYBOARD_SHORTCUT_ENABLED,
                MESSAGES.get("field.configureKeyboardShortcutCheckbox.label"), getFieldEditorParent()));
        addEditor(new SpinnerFieldEditor(PreferenceConstants.P_CONFIGURE_KEYBOARD_SHORTCUT_THRESHOLD,
                MESSAGES.get("field.configureKeyboardShortcutThreshold.label"), getFieldEditorParent(), 0, 10, 1, 1));

        GridDataFactory.fillDefaults().span(2, 1).applyTo(new Label(getFieldEditorParent(), SWT.NONE));
        addEditor(new SpinnerFieldEditor(PreferenceConstants.P_NAG_CLOSE_TIMEOUT,
                MESSAGES.get("field.nagCloseTimeout.label"), getFieldEditorParent(), 0,
                (int) TimeUnit.SECONDS.toMillis(60), 1, 1));
        addEditor(new SpinnerFieldEditor(PreferenceConstants.P_NAG_LISTENER_TIMEOUT,
                MESSAGES.get("field.nagListenerTimeout.label"), getFieldEditorParent(), 0,
                (int) TimeUnit.SECONDS.toMillis(60), 1, 1));
    }

    private <T extends FieldEditor> T addEditor(T fieldEditor) {
        super.addField(fieldEditor);
        return fieldEditor;
    }

    private String[][] createInvocationModeInput() {
        String[][] entryNamesAndValues = new String[OnWrongInvocationMode.values().length][2];

        for (int i = 0; i < OnWrongInvocationMode.values().length; i++) {
            OnWrongInvocationMode x = OnWrongInvocationMode.values()[i];
            entryNamesAndValues[i] = new String[] { x.getLabel(), x.name() };
        }
        return entryNamesAndValues;
    }

    /**
     * Does not do anything.
     * 
     * @param workbench
     *            not used.
     */
    public void init(final IWorkbench workbench) {
    }
}