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
public class CloneParams {
    private String remoteUri;
    private List<String> branchesToFetch;
    private String workingDir;
    private String remoteName;
    private int timeout;

    private CloneParams() {}

    public static CloneParams create() {
        return new CloneParams();
    }


    public String getRemoteUri() {
        return remoteUri;
    }

    public CloneParams withRemoteUri(String remoteUri) {
        this.remoteUri = remoteUri;
        return this;
    }

    public List<String> getBranchesToFetch() {
        return branchesToFetch;
    }

    public CloneParams withBranchesToFetch(List<String> branchesToFetch) {
        this.branchesToFetch = branchesToFetch;
        return this;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public CloneParams withWorkingDir(String workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public CloneParams withRemoteName(String remoteName) {
        this.remoteName = remoteName;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public CloneParams withTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
