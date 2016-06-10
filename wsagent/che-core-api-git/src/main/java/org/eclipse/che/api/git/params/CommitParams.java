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
public class CommitParams {
    private List<String> files;
    private String message;
    private boolean isAll;
    private boolean isAmend;


    private CommitParams() {}

    public static CommitParams create() {
        return new CommitParams();
    }

    public List<String> getFiles() {
        return files;
    }

    public CommitParams withFiles(List<String> files) {
        this.files = files;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CommitParams withMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isAll() {
        return isAll;
    }

    public CommitParams withAll(boolean all) {
        isAll = all;
        return this;
    }

    public boolean isAmend() {
        return isAmend;
    }

    public CommitParams withAmend(boolean amend) {
        isAmend = amend;
        return this;
    }
}
