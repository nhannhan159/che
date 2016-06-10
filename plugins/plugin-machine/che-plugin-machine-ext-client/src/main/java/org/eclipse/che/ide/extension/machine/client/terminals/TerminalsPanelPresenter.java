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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.api.machine.MachineServiceClient;
import org.eclipse.che.ide.api.machine.events.DevMachineStateEvent;
import org.eclipse.che.api.machine.shared.dto.CommandDto;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.machine.shared.dto.MachineProcessDto;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.workspace.event.WorkspaceStoppedEvent;
import org.eclipse.che.ide.api.workspace.event.WorkspaceStoppedHandler;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.outputconsole.OutputConsole;
import org.eclipse.che.ide.api.parts.HasView;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.extension.machine.client.command.CommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.CommandType;
import org.eclipse.che.ide.extension.machine.client.command.CommandTypeRegistry;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.inject.factories.TerminalFactory;
import org.eclipse.che.ide.extension.machine.client.machine.Machine;
import org.eclipse.che.ide.extension.machine.client.machine.MachineStateEvent;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.CommandConsoleFactory;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.CommandOutputConsole;
import org.eclipse.che.ide.extension.machine.client.outputspanel.console.DefaultOutputConsole;
import org.eclipse.che.ide.extension.machine.client.perspective.terminal.TerminalPresenter;
import org.eclipse.che.ide.api.dialogs.ConfirmCallback;
import org.eclipse.che.ide.api.dialogs.DialogFactory;
import org.eclipse.che.ide.extension.machine.client.processes.ProcessTreeNode;
import org.eclipse.che.ide.util.loging.Log;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.api.notification.StatusNotification.DisplayMode.FLOAT_MODE;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;
import static org.eclipse.che.ide.extension.machine.client.perspective.terminal.TerminalPresenter.TerminalStateListener;
import static org.eclipse.che.ide.extension.machine.client.processes.ProcessTreeNode.ProcessNodeType.COMMAND_NODE;
import static org.eclipse.che.ide.extension.machine.client.processes.ProcessTreeNode.ProcessNodeType.MACHINE_NODE;
import static org.eclipse.che.ide.extension.machine.client.processes.ProcessTreeNode.ProcessNodeType.ROOT_NODE;
import static org.eclipse.che.ide.extension.machine.client.processes.ProcessTreeNode.ProcessNodeType.TERMINAL_NODE;

/**
 * Presenter for managing additional panel with terminals in the consoles panel.
 *
 * @author Roman Nikitenko
 */
public class TerminalsPanelPresenter implements TerminalsPanelView.ActionDelegate,
                                                HasView,
                                                WorkspaceStoppedHandler,
                                                MachineStateEvent.Handler {

    private static final String DEFAULT_TERMINAL_NAME = "Terminal";

    private final EntityFactory                entityFactory;
    private final TerminalFactory              terminalFactory;
    private final NotificationManager          notificationManager;
    private final MachineLocalizationConstant  localizationConstant;
    private final TerminalsPanelView            view;
    private final MachineResources             resources;
    private final AppContext                   appContext;
    private final MachineServiceClient         machineService;
    private final Map<String, ProcessTreeNode> machineNodes;

    final List<ProcessTreeNode>          rootChildren;
    final Map<String, TerminalPresenter> terminals;

    ProcessTreeNode rootNode;
    ProcessTreeNode selectedTreeNode;

    @Inject
    public TerminalsPanelPresenter(TerminalsPanelView view,
                                   EventBus eventBus,
                                   EntityFactory entityFactory,
                                   TerminalFactory terminalFactory,
                                   NotificationManager notificationManager,
                                   MachineLocalizationConstant localizationConstant,
                                   MachineServiceClient machineService,
                                   MachineResources resources,
                                   AppContext appContext) {
        this.view = view;
        this.terminalFactory = terminalFactory;
        this.notificationManager = notificationManager;
        this.localizationConstant = localizationConstant;
        this.resources = resources;
        this.entityFactory = entityFactory;
        this.appContext = appContext;
        this.machineService = machineService;

        this.rootChildren = new ArrayList<>();
        this.terminals = new HashMap<>();
        this.machineNodes = new HashMap<>();

//        this.view.setDelegate(this);

        eventBus.addHandler(DevMachineStateEvent.TYPE, new DevMachineStateEvent.Handler() {
            @Override
            public void onDevMachineStarted(DevMachineStateEvent event) {
                fetchMachines();
            }

            @Override
            public void onDevMachineDestroyed(DevMachineStateEvent event) {
            }
        });

        eventBus.addHandler(WorkspaceStoppedEvent.TYPE, this);
        eventBus.addHandler(MachineStateEvent.TYPE, this);

        fetchMachines();
    }


    @Override
    public View getView() {
        return view;
    }

    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    public void go(AcceptsOneWidget container) {
        Log.error(getClass(), "=== " + container.getClass());
        container.setWidget(view);
    }

    @Override
    public void onMachineCreating(MachineStateEvent event) {
    }

    @Override
    public void onMachineRunning(MachineStateEvent event) {
        machineService.getMachine(event.getMachineId()).then(new Operation<MachineDto>() {
            @Override
            public void apply(MachineDto machine) throws OperationException {
                addMachineToConsoles(machine);
            }
        });
    }

    @Override
    public void onMachineDestroyed(MachineStateEvent event) {
        String destroyedMachineId = event.getMachineId();

        ProcessTreeNode destroyedMachineNode = machineNodes.get(destroyedMachineId);
        if (destroyedMachineNode == null) {
            return;
        }

        rootChildren.remove(destroyedMachineNode);
        onCloseTerminal(destroyedMachineNode);

        view.setProcessesData(rootNode);
    }

    /** Get the list of all available machines. */
    public void fetchMachines() {
        String workspaceId = appContext.getWorkspaceId();

        machineService.getMachines(workspaceId).then(new Operation<List<MachineDto>>() {
            @Override
            public void apply(List<MachineDto> machines) throws OperationException {
                rootNode = new ProcessTreeNode(ROOT_NODE, null, null, null, rootChildren);

                MachineDto devMachine = getDevMachine(machines);
                addMachineToConsoles(devMachine);

                machines.remove(devMachine);

                for (MachineDto machine : machines) {
                    addMachineToConsoles(machine);
                }
            }
        });
    }

    private MachineDto getDevMachine(List<MachineDto> machines) {
        for (MachineDto machine : machines) {
            if (machine.getConfig().isDev()) {
                return machine;
            }
        }

        throw new IllegalArgumentException("Dev machine can not be null");
    }

    private void addMachineToConsoles(MachineDto machine) {
        List<ProcessTreeNode> processTreeNodes = new ArrayList<ProcessTreeNode>();
        ProcessTreeNode machineNode = new ProcessTreeNode(MACHINE_NODE, rootNode, machine, null, processTreeNodes);
        machineNode.setRunning(true);

        if (rootChildren.contains(machineNode)) {
            rootChildren.remove(machineNode);
        }

        rootChildren.add(machineNode);
        view.setProcessesData(rootNode);

        String machineId = machine.getId();

        machineNodes.put(machineId, machineNode);
        Log.error(getClass(), "=== addMachineToConsoles");
    }

    /**
     * Opens new terminal for the selected machine.
     */
    public void newTerminal() {
        Log.error(getClass(), "=== newTerminal");
        if (selectedTreeNode == null) {
            if (appContext.getDevMachine() != null) {
                onAddTerminal(appContext.getDevMachine().getId());
            }
            return;
        }

        if (selectedTreeNode.getType() == MACHINE_NODE) {
            onAddTerminal(selectedTreeNode.getId());
        } else {
            if (selectedTreeNode.getParent() != null &&
                selectedTreeNode.getParent().getType() == MACHINE_NODE) {
                onAddTerminal(appContext.getDevMachine().getId());
            }
        }
    }

    /**
     * Adds new terminal to the additional terminal panel
     *
     * @param machineId
     *         id of machine in which the terminal will be added
     */
    @Override
    public void onAddTerminal(@NotNull final String machineId) {
        Log.error(getClass(), "=== onAddTerminal");
        machineService.getMachine(machineId).then(new Operation<MachineDto>() {
            @Override
            public void apply(MachineDto arg) throws OperationException {
                Machine machine = entityFactory.createMachine(arg);
                final ProcessTreeNode machineTreeNode = findProcessTreeNodeById(machineId);

                if (machineTreeNode == null) {
                    notificationManager.notify(localizationConstant.failedToConnectTheTerminal(),
                                               localizationConstant.machineNotFound(machineId), FAIL, FLOAT_MODE);
                    Log.error(getClass(), localizationConstant.machineNotFound(machineId));
                    return;
                }

                final TerminalPresenter newTerminal = terminalFactory.create(machine);
                final IsWidget terminalWidget = newTerminal.getView();
                final String terminalName = getUniqueTerminalName(machineTreeNode);
                final ProcessTreeNode terminalNode =
                        new ProcessTreeNode(TERMINAL_NODE, machineTreeNode, terminalName, resources.terminalTreeIcon(), null);
                addChildToMachineNode(terminalNode, machineTreeNode);

                final String terminalId = terminalNode.getId();
                terminals.put(terminalId, newTerminal);
                view.addProcessNode(terminalNode);
                view.addProcessWidget(terminalId, terminalWidget);

                newTerminal.setVisible(true);
                newTerminal.connect();
                newTerminal.setListener(new TerminalStateListener() {
                    @Override
                    public void onExit() {
                        onStopProcess(terminalNode);
                        terminals.remove(terminalId);
                    }
                });
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                notificationManager.notify(localizationConstant.failedToFindMachine(machineId));
            }
        });
    }

    @Override
    public void onCloseTerminal(@NotNull ProcessTreeNode node) {
        String terminalId = node.getId();
        if (terminals.containsKey(terminalId)) {
            onStopProcess(node);
            terminals.get(terminalId).stopTerminal();
            terminals.remove(terminalId);
        }
    }

    @Override
    public void onTreeNodeSelected(@NotNull ProcessTreeNode node) {
        selectedTreeNode = node;

        view.showProcessOutput(node.getId());
    }

    private void onStopProcess(@NotNull ProcessTreeNode node) {
        String processId = node.getId();
        ProcessTreeNode parentNode = node.getParent();

        int processIndex = view.getNodeIndex(processId);
        if (processIndex < 0) {
            return;
        }

        int countWidgets = terminals.size();
        if (countWidgets == 1) {
            view.hideProcessOutput(processId);
            removeChildFromMachineNode(node, parentNode);
            return;
        }

        int neighborIndex = processIndex > 0 ? processIndex - 1 : processIndex + 1;
        ProcessTreeNode neighborNode = view.getNodeByIndex(neighborIndex);
        String neighborNodeId = neighborNode.getId();

        removeChildFromMachineNode(node, parentNode);
        view.selectNode(neighborNode);
        view.showProcessOutput(neighborNodeId);
        view.hideProcessOutput(processId);
    }


    private void addChildToMachineNode(ProcessTreeNode childNode, ProcessTreeNode machineTreeNode) {
        machineTreeNode.getChildren().add(childNode);
        view.setProcessesData(rootNode);
        view.selectNode(childNode);
    }

    private void removeChildFromMachineNode(ProcessTreeNode childNode, ProcessTreeNode machineTreeNode) {
        view.removeProcessNode(childNode);
        machineTreeNode.getChildren().remove(childNode);
        view.setProcessesData(rootNode);
    }

    private ProcessTreeNode findProcessTreeNodeById(@NotNull String id) {
        for (ProcessTreeNode processTreeNode : rootNode.getChildren()) {
            if (id.equals(processTreeNode.getId())) {
                return processTreeNode;
            }
        }
        return null;
    }

    private String getUniqueTerminalName(ProcessTreeNode machineNode) {
        String terminalName = DEFAULT_TERMINAL_NAME;
        if (!isTerminalNameExist(machineNode, terminalName)) {
            return DEFAULT_TERMINAL_NAME;
        }

        int counter = 2;
        do {
            terminalName = localizationConstant.viewProcessesTerminalNodeTitle(String.valueOf(counter));
            counter++;
        } while (isTerminalNameExist(machineNode, terminalName));
        return terminalName;
    }

    private boolean isTerminalNameExist(ProcessTreeNode machineNode, String terminalName) {
        for (ProcessTreeNode node : machineNode.getChildren()) {
            if (TERMINAL_NODE == node.getType() && node.getName().equals(terminalName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onWorkspaceStopped(WorkspaceStoppedEvent event) {
        for (ProcessTreeNode processTreeNode : rootNode.getChildren()) {
            if (processTreeNode.getType() == MACHINE_NODE) {
                onCloseTerminal(processTreeNode);
            }
        }

        rootNode.getChildren().clear();
        rootChildren.clear();

        view.clear();
        view.selectNode(null);
        view.setProcessesData(rootNode);
    }
}
