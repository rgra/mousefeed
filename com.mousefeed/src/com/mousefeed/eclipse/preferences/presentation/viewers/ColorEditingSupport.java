package com.mousefeed.eclipse.preferences.presentation.viewers;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * 
 * @author Rabea Gransberger (@rgransberger)
 */
public abstract class ColorEditingSupport<T> extends EditingSupport {

    private final ColorCellEditor editor;

    private final Class<T> clazz;

    public ColorEditingSupport(TableViewer viewer, Class<T> clazz) {
        super(viewer);
        this.clazz = clazz;
        this.editor = new ColorCellEditor(viewer.getTable(), SWT.NONE);
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
        return getRGBValue(clazz.cast(element));
    }

    @Override
    protected final void setValue(Object element, Object value) {
        setRGBValue(clazz.cast(element), (RGB) value);

    }

    protected abstract RGB getRGBValue(T element);

    protected abstract void setRGBValue(T element, RGB value);

}