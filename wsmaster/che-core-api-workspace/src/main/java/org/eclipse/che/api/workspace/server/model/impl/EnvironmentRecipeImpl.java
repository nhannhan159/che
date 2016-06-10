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
package org.eclipse.che.api.workspace.server.model.impl;

import org.eclipse.che.api.core.model.workspace.EnvironmentRecipe;

/**
 * @author Alexander Garagatyi
 */
public class EnvironmentRecipeImpl implements EnvironmentRecipe {
    private String type;
    private String contentType;
    private String content;
    private String location;

    public EnvironmentRecipeImpl(String type, String contentType, String content, String location) {
        this.type = type;
        this.contentType = contentType;
        this.content = content;
        this.location = location;
    }

    public EnvironmentRecipeImpl(EnvironmentRecipe recipe) {
        this(recipe.getType(), recipe.getContentType(), recipe.getContent(), recipe.getLocation());
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
