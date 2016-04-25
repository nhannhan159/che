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
package org.eclipse.che.ide.ext.git.client.delete;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.git.gwt.client.GitServiceClient;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.workspace.Workspace;
import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.eclipse.che.ide.ext.git.client.outputconsole.GitOutputConsole;
import org.eclipse.che.ide.ext.git.client.outputconsole.GitOutputConsoleFactory;
import org.eclipse.che.ide.extension.machine.client.processes.ConsolesPanelPresenter;

import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;

/**
 * Delete repository command handler, performs deleting Git repository.
 *
 * @author Ann Zhuleva
 * @author Vlad Zhukovskyi
 */
@Singleton
public class DeleteRepositoryPresenter {
    public static final String DELETE_REPO_COMMAND_NAME = "Git delete repository";

    private final GitServiceClient        service;
    private final GitLocalizationConstant constant;
    private final ConsolesPanelPresenter  consolesPanelPresenter;
    private final AppContext              appContext;
    private final NotificationManager     notificationManager;
    private final GitOutputConsoleFactory gitOutputConsoleFactory;
    private final Workspace workspace;

    @Inject
    public DeleteRepositoryPresenter(GitServiceClient service,
                                     GitLocalizationConstant constant,
                                     GitOutputConsoleFactory gitOutputConsoleFactory,
                                     ConsolesPanelPresenter consolesPanelPresenter,
                                     AppContext appContext,
                                     NotificationManager notificationManager,
                                     Workspace workspace) {
        this.service = service;
        this.constant = constant;
        this.gitOutputConsoleFactory = gitOutputConsoleFactory;
        this.consolesPanelPresenter = consolesPanelPresenter;
        this.appContext = appContext;
        this.notificationManager = notificationManager;

        this.workspace = workspace;
    }

    /** Delete Git repository. */
    public void deleteRepository(final Project project) {
        final GitOutputConsole console = gitOutputConsoleFactory.create(DELETE_REPO_COMMAND_NAME);

        service.deleteRepository(appContext.getDevMachine(), project.getLocation()).then(new Operation<Void>() {
            @Override
            public void apply(Void ignored) throws OperationException {
                console.print(constant.deleteGitRepositorySuccess());
                consolesPanelPresenter.addCommandOutput(appContext.getDevMachineId(), console);
                notificationManager.notify(constant.deleteGitRepositorySuccess());

                project.synchronize();
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError error) throws OperationException {
                console.printError(error.getMessage());
                consolesPanelPresenter.addCommandOutput(appContext.getDevMachineId(), console);
                notificationManager.notify(constant.failedToDeleteRepository(), FAIL, true);
            }
        });
    }
}
