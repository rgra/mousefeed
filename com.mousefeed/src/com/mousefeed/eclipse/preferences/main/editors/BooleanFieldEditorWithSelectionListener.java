package com.mousefeed.eclipse.preferences.main.editors;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author Rabea Gransberger (@rgransberger)
 */
public class BooleanFieldEditorWithSelectionListener extends BooleanFieldEditor {

    private final Composite parent;

    public BooleanFieldEditorWithSelectionListener(String name, String label, Composite parent) {
        super(name, label, parent);
        this.parent = parent;
    }

    public BooleanFieldEditorWithSelectionListener(String name, String labelText, int style,
            Composite parent) {
        super(name, labelText, style, parent);
        this.parent = parent;
    }

    public void addSelectionListener(SelectionListener listener) {
        super.getChangeControl(parent).addSelectionListener(listener);
    }
}
