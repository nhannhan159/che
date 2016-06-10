/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *   SAP           - implementation
 *******************************************************************************/
package org.eclipse.che.api.git.params;

import java.util.List;

/**
 * Arguments holder .
 *
 * @author Igor Vinokur
 */
public class CheckoutParams {
    private String name;
    private String startPoint;
    private String trackBranch;
    private List<String> filesToCheckout;
    private boolean isCreateNew;
    private boolean noTrack;

    private CheckoutParams() {}

    public static CheckoutParams create() {
        return new CheckoutParams();
    }

    /** @return name of branch to checkout */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CheckoutParams withName(String name) {
        this.name = name;
        return this;
    }

    /** @return name of a commit at which to start the new branch. If <code>null</code> the HEAD will be used */
    public String getStartPoint() {
        return startPoint;
    }

    public CheckoutParams withStartPoint(String startPoint) {
        this.startPoint = startPoint;
        return this;
    }

    /**
     * @return if <code>true</code> then create a new branch named {@link #name} and start it at {@link #startPoint} or to the HEAD if
     *         {@link #startPoint} is not set. If <code>false</code> and there is no branch with name {@link #name} corresponding exception
     *         will be thrown
     */
    public boolean isCreateNew() {
        return isCreateNew;
    }

    public CheckoutParams withCreateNew(boolean isCreateNew) {
        this.isCreateNew = isCreateNew;
        return this;
    }

    /** @return name of branch that will be tracked */
    public String getTrackBranch() {
        return trackBranch;
    }

    public CheckoutParams withTrackBranch(String trackBranch) {
        this.trackBranch = trackBranch;
        return this;
    }

    /** @return list of files to checkout */
    public List<String> getFiles() {
        return filesToCheckout;
    }

    public CheckoutParams withFiles(List<String> filesToCheckout) {
        this.filesToCheckout = filesToCheckout;
        return this;
    }

    /** @return indicates whether --no-track option should be applied during checkout */
    public boolean isNoTrack() {
        return noTrack;
    }

    public CheckoutParams withNoTrack(boolean noTrack) {
        this.noTrack = noTrack;
        return this;
    }
}
