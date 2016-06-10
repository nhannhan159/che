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
package org.eclipse.che.ide.extension.machine.client.processes.consolescontainer;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;

/**
 * View of {@link ConsolesContainerPresenter}.
 *
 * @author Anna Shumilova
 * @author Roman Nikitenko
 */
public interface ConsolesContainerView extends View<ConsolesContainerView.ActionDelegate> {

    /**
     * Set view's title.
     *
     * @param title
     *         new title
     */
    void setTitle(String title);

    /** Shows or hides the given view. */
    void setVisible(boolean visible);

    SimplePanel getLeftContainer();
    SimplePanel getRighContainer();


    interface ActionDelegate extends BaseActionDelegate {

        /**
         * Will be called when user clicks 'Split Vertically' button
         *
         */
        void onSplitVerticallyClick();

        /**
         * Will be called when user clicks 'Split Horizontally' button
         *
         */
        void onSplitHorizontallyClick();
    }
}
