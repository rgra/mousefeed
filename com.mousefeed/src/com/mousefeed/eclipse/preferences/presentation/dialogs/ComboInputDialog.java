package com.mousefeed.eclipse.preferences.presentation.dialogs;

import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author Rabea Gransberger (@rgransberger)
 */
public class ComboInputDialog<T> extends Dialog {

    private final String title;

    private final String message;

    private final List<T> input;

    private final IBaseLabelProvider labelProvider;

    private T value;

    private ComboViewer viewer;

    public ComboInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, T initialValue, List<T> input,
            IBaseLabelProvider labelProvider) {
        super(parentShell);
        this.title = dialogTitle;
        this.message = dialogMessage;
        this.value = initialValue;
        this.input = input;
        this.labelProvider = labelProvider;
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = (T) ((StructuredSelection) viewer.getSelection()).getFirstElement();
        } else {
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets
     * .Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
     * .swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        // do this here because setting the text will set enablement on the ok
        // button
        viewer.getControl().setFocus();
        if (value != null) {
            viewer.setSelection(new StructuredSelection(value), true);
        }
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
                    | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }

        this.viewer = new ComboViewer(composite, SWT.BORDER | SWT.READ_ONLY);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setLabelProvider(labelProvider);
        viewer.setInput(input);

        applyDialogFont(composite);
        return composite;
    }

    /**
     * Returns the string typed into this input dialog.
     * 
     * @return the input string
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the style bits that should be used for the input text field.
     * Defaults to a single line entry. Subclasses may override.
     * 
     * @return the integer style bits that should be used when creating the
     *         input text
     * 
     * @since 3.4
     */
    protected int getInputTextStyle() {
        return SWT.SINGLE | SWT.BORDER;
    }

}
