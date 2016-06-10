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

import org.eclipse.che.api.core.model.machine.MachineConfig2;
import org.eclipse.che.api.core.model.workspace.Environment;
import org.eclipse.che.api.core.model.workspace.EnvironmentRecipe;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data object for {@link Environment}.
 *
 * @author Yevhenii Voevodin
 */
public class EnvironmentImpl implements Environment {

    private EnvironmentRecipeImpl              recipe;
    private Map<String, MachineConfig2Impl> machines;

    public EnvironmentImpl(EnvironmentRecipe recipe, Map<String, ? extends MachineConfig2> machines) {
        if (recipe != null) {
            this.recipe = new EnvironmentRecipeImpl(recipe);
        }
        this.machines = machines.entrySet()
                                .stream()
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                                          stringEntry ->  new MachineConfig2Impl(stringEntry.getValue())));
    }

    public EnvironmentImpl(Environment environment) {
        this(environment.getRecipe(),
             environment.getMachines());
    }


    @Override
    public EnvironmentRecipeImpl getRecipe() {
        return recipe;
    }

    public void setRecipe(EnvironmentRecipeImpl recipe) {
        this.recipe = recipe;
    }

    @Override
    public Map<String, MachineConfig2Impl> getMachines() {
        return machines;
    }

    public void setMachines(Map<String, MachineConfig2Impl> machines) {
        this.machines = machines;
    }
}
