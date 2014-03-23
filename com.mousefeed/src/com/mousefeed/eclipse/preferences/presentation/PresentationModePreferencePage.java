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
package com.mousefeed.eclipse.preferences.presentation;

import com.mousefeed.client.Messages;
import com.mousefeed.eclipse.Activator;
import com.mousefeed.eclipse.preferences.PreferenceAccessor;
import com.mousefeed.eclipse.preferences.presentation.dialogs.ComboInputDialog;
import com.mousefeed.eclipse.preferences.presentation.viewers.ColorEditingSupport;
import com.mousefeed.eclipse.preferences.presentation.viewers.TextEditingSupport;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

/**
 * Provides control over how actions are invoked. Contains global as well as
 * action-specific settings.
 * 
 * @author Rabea Gransberger (@rgransberger)
 */
public class PresentationModePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    /*
     * Does not use field editors because field editors work for grid layout
     * only, but this page uses form layout.
     */

    /**
     * Provides messages text.
     */
    private static final Messages MESSAGES = new Messages(PresentationModePreferencePage.class);

    /**
     * Provides access to the plugin preferences.
     */
    private final PreferenceAccessor preferences = PreferenceAccessor.getInstance();

    private final IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getAdapter(
            IBindingService.class);

    private TableViewer viewer;

    private Button parenthesesButton;

    /**
     * Constructor.
     */
    public PresentationModePreferencePage() {
        super();
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(MESSAGES.get("description"));
    }

    // see base
    protected Control createContents(final Composite parent) {
        initializeDialogUnits(parent);

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));

        this.parenthesesButton = new Button(composite, SWT.CHECK);
        parenthesesButton.setText("Show Label in parentheses '()'");
        GridDataFactory.fillDefaults().span(2, 1).applyTo(parenthesesButton);

        this.viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.FULL_SELECTION | SWT.BORDER);

        createColumns(viewer);
        GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(viewer.getTable());

        // make lines and header visible
        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setInput(createInput());

        Button addButton = new Button(composite, SWT.PUSH);
        addButton.setText("Add");
        GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(addButton);

        final Button removeButton = new Button(composite, SWT.PUSH);
        removeButton.setText("Remove");
        GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(removeButton);

        // ###

        removeButton.setEnabled(false);

        this.parenthesesButton.setSelection(preferences.isUsePresentationModeParentheses());

        // ###

        parenthesesButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateParentheses(((Button) e.widget).getSelection());
            }
        });

        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addItem(viewer);
            }
        });
        removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeItem(viewer);
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                removeButton.setEnabled(!viewer.getSelection().isEmpty());
            }
        });

        Dialog.applyDialogFont(composite);
        return composite;
    }

    protected void updateParentheses(boolean selection) {
        this.preferences.setUsePresentationModeParentheses(selection);
    }

    protected void addItem(TableViewer viewer) {

        Scheme initialValue = bindingService.getActiveScheme();
        List<Scheme> input = Arrays.asList(bindingService.getDefinedSchemes());

        IBaseLabelProvider labelProvider = new LabelProvider() {
            @Override
            public String getText(Object element) {
                try {
                    return ((Scheme) element).getName();
                } catch (NotDefinedException e) {
                    Activator.getDefault().getLog()
                            .log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Could not get label for scheme", e));
                    return "(Undefined)";
                }
            }
        };
        ComboInputDialog<Scheme> comboInputDialog = new ComboInputDialog<Scheme>(getShell(), "Select Scheme",
                "Select the Scheme which should be added to the configuration", initialValue, input, labelProvider);
        if (comboInputDialog.open() == Window.OK) {
            Scheme value = comboInputDialog.getValue();
            SchemeConfig config = new SchemeConfig(value.getId());
            preferences.addPresentationModeSchemeConfigs(config);
            viewer.refresh();
            viewer.setSelection(new StructuredSelection(config), true);
        }

    }

    protected void removeItem(TableViewer viewer) {
        SchemeConfig firstElement = (SchemeConfig) ((StructuredSelection) viewer.getSelection()).getFirstElement();
        preferences.removePresentationModePreference(firstElement.getSchemeId());
        viewer.refresh();
    }

    private void createColumns(final TableViewer viewer) {
        final LocalResourceManager resManager = new LocalResourceManager(JFaceResources.getResources(),
                viewer.getTable());

        createTableViewerColumn(viewer, "Scheme", 150, new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                String schemeId = ((SchemeConfig) element).getSchemeId();
                Scheme scheme = bindingService.getScheme(schemeId);
                if (scheme != null && scheme.isDefined()) {
                    try {
                        return scheme.getName();
                    } catch (NotDefinedException e) {
                        // can not happen
                        e.printStackTrace();
                    }
                }
                return "Undefined (" + schemeId + ")";
            }
        }, null);
        createTableViewerColumn(viewer, "Label", 50, new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((SchemeConfig) element).getLabel();
            }
        }, new TextEditingSupport<SchemeConfig>(viewer, SchemeConfig.class) {

            @Override
            protected String getStringValue(SchemeConfig element) {
                return element.getLabel();
            }

            @Override
            protected void setStringValue(SchemeConfig element, String value) {
                element.setLabel(value);
                viewer.refresh();
            }

        });
        createTableViewerColumn(viewer, "Color", 150, new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return "";
            }

            @Override
            public Color getBackground(Object element) {
                return resManager.createColor(((SchemeConfig) element).getColorCode());
            }
        }, new ColorEditingSupport<SchemeConfig>(viewer, SchemeConfig.class) {

            @Override
            protected RGB getRGBValue(SchemeConfig element) {
                return element.getColorCode();
            }

            @Override
            protected void setRGBValue(SchemeConfig element, RGB value) {
                element.setColorCode(value);
                viewer.refresh();

            }
        });
    }

    private Collection<SchemeConfig> createInput() {
        return preferences.getPresentationModeSchemeConfigs();
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        preferences.restoreDefaultsPresentationModePreferences();
        viewer.refresh();
        this.parenthesesButton.setSelection(preferences.isUsePresentationModeParentheses());

    }

    @Override
    public boolean performOk() {
        preferences.savePresentationModePreferences();
        return super.performOk();
    }

    private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int width,
            CellLabelProvider labelProvider, EditingSupport editingSupport) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.CENTER);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(width);
        column.setResizable(true);
        column.setMoveable(false);
        viewerColumn.setLabelProvider(labelProvider);
        viewerColumn.setEditingSupport(editingSupport);
        return viewerColumn;
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
