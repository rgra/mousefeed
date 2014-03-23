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
package com.mousefeed.client.collector;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.Validate.isTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.graphics.Point;

/**
 * Information about an action invoked by the user.
 * 
 * @author Andriy Palamarchuk
 * @author Rabea Gransberger (@rgransberger)
 */
public abstract class AbstractActionDesc {

    public static String DEFAULT_ACCELERATOR_KEY = "default";

    /**
     * @see #getLabel()
     */
    private String label;

    private final Map<String, String> accelerators = new HashMap<String, String>();

    private Point caretLocation;

    /**
     * The id of the user action.
     * 
     * @return the string identifying the action invoked by the user. The
     *         default behavior is to return {@link #getLabel()}. Subclasses
     *         should provide better values. Never blank.
     */
    public String getId() {
        return getLabel();
    }

    /**
     * The action human-readable label.
     * 
     * @return the label. Should not be blank after initialized. The '&'
     *         characters are removed from the original value.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     *            the new label. Not blank.
     * @see #getLabel()
     */
    public void setLabel(final String label) {
        isTrue(isNotBlank(label));
        this.label = label.replace("&", "");
    }

    /**
     * Indicates whether the action has a keyboard shortcut.
     * 
     * @return <code>true</code> if the action description has keyboard
     *         shortcut.
     */
    public boolean hasAccelerator() {
        return !accelerators.isEmpty();
    }

    /**
     * @param accelerator
     *            the new shortcut value. Can be <code>null</code>.
     * @see #getAccelerator()
     */
    public void setAccelerator(final String accelerator) {
        this.accelerators.put(DEFAULT_ACCELERATOR_KEY, accelerator);
    }

    public void setAccelerators(String accelerator,
            Map<String, String> accelerators) {
        setAccelerator(accelerator);
        this.accelerators.putAll(accelerators);

    }

    public Map<String, String> getAccelerators() {
        return Collections.unmodifiableMap(accelerators);
    }

    public Point getCaretLocation() {
        return caretLocation;
    }

    public void setCaretLocation(Point location) {
        this.caretLocation = location;
    }
}
