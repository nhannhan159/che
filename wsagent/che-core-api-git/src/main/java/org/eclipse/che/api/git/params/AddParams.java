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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Arguments holder .
 *
 * @author Igor Vinokur
 */
public class AddParams {

    private Map<String, String> attributes;
    private List<String>        filePattern;
    private boolean             isUpdate;

    /** Default file pattern that will be used if {@link #filePattern} is not set. All content of working tree will be added in index. */
    public List<String> DEFAULT_PATTERN = new ArrayList<>(Collections.singletonList("."));

    private AddParams() {}

    public static AddParams create() {
        return new AddParams();
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public AddParams withAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    /** @return files to add content from */
    public List<String> getFilepattern() {
        return filePattern;
    }

    public AddParams withFilePattern(List<String> filePattern) {
        this.filePattern = filePattern;
        return this;
    }

    /**
     * @return if <code>true</code> than never stage new files, but stage modified new contents of tracked files. It will remove files from
     * the index if the corresponding files in the working tree have been removed. If <code>false</code> then new files and
     * modified
     * files added to the index.
     */
    public boolean isUpdate() {
        return isUpdate;
    }

    public AddParams withUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
        return this;
    }
}
