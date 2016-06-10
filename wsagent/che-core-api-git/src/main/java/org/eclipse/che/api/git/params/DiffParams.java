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
public class DiffParams {
    private List<String> fileFilter;
    private DiffType type;
    private boolean noRenames;
    private int renameLimit;
    private String commitA;
    private String commitB;
    private boolean isCached;

    /** Type of diff output. */
    public enum DiffType {
        /** Only names of modified, added, deleted files. */
        NAME_ONLY("--name-only"),
        /**
         * Names staus of modified, added, deleted files.
         * <p/>
         * Example:
         * <p/>
         * <p/>
         * <pre>
         * D   README.txt
         * A   HOW-TO.txt
         * </pre>
         */
        NAME_STATUS("--name-status"),
        RAW("--raw");

        private final String value;

        private DiffType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private DiffParams() {}

    public static DiffParams create() {
        return new DiffParams();
    }

    public List<String> getFileFilter() {
        return fileFilter;
    }

    public DiffParams withFileFilter(List<String> fileFilter) {
        this.fileFilter = fileFilter;
        return this;
    }

    public DiffType getType() {
        return type;
    }

    public DiffParams withType(DiffType type) {
        this.type = type;
        return this;
    }

    public boolean isNoRenames() {
        return noRenames;
    }

    public DiffParams withNoRenames(boolean noRenames) {
        this.noRenames = noRenames;
        return this;
    }

    public int getRenameLimit() {
        return renameLimit;
    }

    public DiffParams withRenameLimit(int renameLimit) {
        this.renameLimit = renameLimit;
        return this;
    }

    public String getCommitA() {
        return commitA;
    }

    public DiffParams withCommitA(String commitA) {
        this.commitA = commitA;
        return this;
    }

    public String getCommitB() {
        return commitB;
    }

    public DiffParams withCommitB(String commitB) {
        this.commitB = commitB;
        return this;
    }

    public boolean isCached() {
        return isCached;
    }

    public DiffParams withCached(boolean cached) {
        isCached = cached;
        return this;
    }

}
