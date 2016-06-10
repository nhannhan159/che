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
import org.eclipse.che.api.core.model.machine.ServerConf;
import org.eclipse.che.api.machine.server.model.impl.ServerConfImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alexander Garagatyi
 */
public class MachineConfig2Impl implements MachineConfig2 {
    private List<String> agents;
    private Map<String, ServerConfImpl> servers;

    public MachineConfig2Impl(List<String> agents,
                              Map<String, ServerConfImpl> servers) {
        this.agents = agents;
        this.servers = servers;
    }

    public MachineConfig2Impl(MachineConfig2 config) {
        this(config.getAgents(), config.getServers()
                                       .entrySet()
                                       .stream()
                                       .collect(Collectors.toMap(Map.Entry::getKey,
                                                                 entry -> new ServerConfImpl(entry.getValue()))));
    }

    @Override
    public List<String> getAgents() {
        return null;
    }

    @Override
    public Map<String, ? extends ServerConf> getServers() {
        return null;
    }
}
