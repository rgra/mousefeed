package com.mousefeed.eclipse.preferences.presentation.viewers;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

/**
 * 
 * @author Rabea Gransberger (@rgransberger)
 */
public abstract class TextEditingSupport<T> extends EditingSupport {

    private final TextCellEditor editor;

    private final Class<T> clazz;

    public TextEditingSupport(TableViewer viewer, Class<T> clazz) {
        super(viewer);
        this.clazz = clazz;
        this.editor = new TextCellEditor(viewer.getTable(), SWT.NONE);
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return editor;
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected final Object getValue(Object element) {
        return getStringValue(clazz.cast(element));
    }

    @Override
    protected final void setValue(Object element, Object value) {
        setStringValue(clazz.cast(element), (String) value);

    }

    protected abstract String getStringValue(T element);

    protected abstract void setStringValue(T element, String value);

}