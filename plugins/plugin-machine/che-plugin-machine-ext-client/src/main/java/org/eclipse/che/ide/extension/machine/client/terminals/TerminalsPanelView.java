/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.machine.client.terminals;

import com.google.gwt.user.client.ui.IsWidget;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import org.eclipse.che.ide.extension.machine.client.processes.ProcessTreeNode;

import javax.validation.constraints.NotNull;

/**
 * View of {@link org.eclipse.che.ide.extension.machine.client.processes.ConsolesPanelPresenter}.
 *
 * @author Anna Shumilova
 * @author Roman Nikitenko
 */
public interface TerminalsPanelView extends View<TerminalsPanelView.ActionDelegate> {

    /** Shows or hides the given view. */
    void setVisible(boolean visible);

    /** Add process widget */
    void addProcessWidget(String processId, IsWidget widget);

    /** Add process node */
    void addProcessNode(@NotNull ProcessTreeNode node);

    /** Remove process node */
    void removeProcessNode(@NotNull ProcessTreeNode node);

    /**
     * Set process data to be displayed.
     *
     * @param root
     *         data which will be displayed
     */
    void setProcessesData(@NotNull ProcessTreeNode root);

    /** Select given process node */
    void selectNode(ProcessTreeNode node);

    /** Displays output for process with given ID */
    void showProcessOutput(String processId);

    /** Hides output for process with given ID */
    void hideProcessOutput(String processId);

    /** Removes all process widgets from the view */
    void clear();

    /** Returns index for node with given ID */
    int getNodeIndex(String processId);

    /** Returns node by given index */
    ProcessTreeNode getNodeByIndex(@NotNull int index);

    /** Returns node by given ID */
    ProcessTreeNode getNodeById(@NotNull String nodeId);

    interface ActionDelegate {

        /**
         * Will be called when user clicks 'Add new terminal' button
         *
         * @param machineId
         *         id of machine in which the terminal will be added
         */
        void onAddTerminal(@NotNull String machineId);

        /**
         * Will be called when user clicks 'Close' button
         *
         * @param node
         *         terminal node to close
         */
        void onCloseTerminal(@NotNull ProcessTreeNode node);

        /**
         * Perform actions when tree node is selected.
         *
         * @param node
         *         selected tree node
         */
        void onTreeNodeSelected(@NotNull ProcessTreeNode node);

    }
}
