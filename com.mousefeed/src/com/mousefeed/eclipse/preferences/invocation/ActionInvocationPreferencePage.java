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
package com.mousefeed.eclipse.preferences.invocation;

import com.mousefeed.eclipse.preferences.PreferenceAccessor;

import com.mousefeed.client.Messages;
import com.mousefeed.eclipse.Activator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Provides control over how actions are invoked. Contains global as well as
 * action-specific settings.
 * 
 * @author Andriy Palamarchuk
 * @author Robert Wloch
 */
public class ActionInvocationPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {
    /*
     * Does not use field editors because field editors work for grid layout
     * only, but this page uses form layout.
     */

    /**
     * Provides messages text.
     */
    private static final Messages MESSAGES = new Messages(
            ActionInvocationPreferencePage.class);

    /**
     * Provides access to the plugin preferences.
     */
    private final PreferenceAccessor preferences = PreferenceAccessor
            .getInstance();

    /**
     * Creates UI for action-specific invocation settings.
     */
    private ActionInvocationModeControl actionModeControl;

    /**
     * Constructor.
     */
    public ActionInvocationPreferencePage() {
        super();
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(MESSAGES.get("description"));
    }

    // see base
    protected Control createContents(final Composite parent) {
        initializeDialogUnits(parent);

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());

        actionModeControl = createActionModeControl(composite);
        GridDataFactory.fillDefaults().applyTo(actionModeControl);

        Dialog.applyDialogFont(composite);
        return composite;
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        actionModeControl.clearActionSettings();
    }

    @Override
    public boolean performOk() {
        preferences.setActionsOnWrongInvocationMode(actionModeControl
                .getActionModes());
        return super.performOk();
    }

    /**
     * Does not do anything.
     * 
     * @param workbench
     *            not used.
     */
    public void init(final IWorkbench workbench) {
    }

    /**
     * Creates the action-specific invocation settings UI control.
     * 
     * @param composite
     *            the container. Assumed not <code>null</code>.
     * @return the action invocation control. Not <code>null</code>.
     */
    private ActionInvocationModeControl createActionModeControl(
            final Composite composite) {
        return new ActionInvocationModeControl(composite);
    }

}
