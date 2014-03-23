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
package com.mousefeed.eclipse;

import static org.apache.commons.lang.Validate.notNull;

import com.mousefeed.client.collector.AbstractActionDesc;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.bindings.BindingManager;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * /** Generates {@link ActionDescImpl} from {@link CommandContributionItem}.
 * Pure strategy.
 * 
 * @author Andriy Palamarchuk
 * @author Rabea Gransberger (@rgransberger)
 */
class CommandActionDescGenerator {

    /**
     * Retrieves a command from a command contribution item.
     */
    private final CommandContributionItemCommandLocator locator = new CommandContributionItemCommandLocator();

    /**
     * The binding service used to retrieve bindings data.
     */
    private final IBindingService bindingService;

    /**
     * Constructor.
     */
    public CommandActionDescGenerator() {
        bindingService = (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);
    }

    /**
     * Generates action description from the command contribution item.
     * 
     * @param commandContributionItem
     *            the contribution item to generate an action description for.
     *            Not <code>null</code>.
     * @return the action description for the provided action. Never
     *         <code>null</code>.
     */
    public AbstractActionDesc generate(final CommandContributionItem commandContributionItem) {
        notNull(commandContributionItem);

        final Command command = locator.get(commandContributionItem);
        return generate(command, null);
    }

    public AbstractActionDesc generate(Command command, Point location) {
        final ActionDescImpl actionDesc = new ActionDescImpl();
        if (command == null) {
            return null;
        }
        try {
            actionDesc.setLabel(command.getName());
        } catch (final NotDefinedException e) {
            // should never happen
            throw new RuntimeException(e);
        }
        final String commandId = command.getId();
        actionDesc.setDef(commandId);
        actionDesc.setCaretLocation(location);

        BindingManager bindingManager = ((BindingService) bindingService).getBindingManager();

        Map<String, String> additional = new HashMap<String, String>();
        Scheme de = bindingManager.getActiveScheme();

        for (Scheme scheme : bindingManager.getDefinedSchemes()) {
            try {
                bindingManager.setActiveScheme(scheme);

                TriggerSequence bestActiveBindingFor = bindingManager.getBestActiveBindingFor(commandId);
                if (bestActiveBindingFor != null) {

                    additional.put(scheme.getId(), bestActiveBindingFor.format());
                }
            } catch (NotDefinedException e) {
                // should never happen
                throw new RuntimeException(e);
            }
        }
        try {
            bindingManager.setActiveScheme(de);
        } catch (NotDefinedException e) {
            // should never happen
            throw new RuntimeException(e);
        }

        final TriggerSequence binding = bindingService.getBestActiveBindingFor(commandId);
        if (binding != null) {
            actionDesc.setAccelerators(binding.format(), additional);
        }
        return actionDesc;
    }

}
