package com.mousefeed.eclipse.preferences.main.editors;

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author Rabea Gransberger (@rgransberger)
 */
public class SpinnerFieldEditor extends IntegerFieldEditor {

    public SpinnerFieldEditor(String name, String labelText, Composite parent, int min, int max, int increment,
            int pageIncrement) {
        super(name, labelText, parent);
        setValidRange(min, max);
    }

}