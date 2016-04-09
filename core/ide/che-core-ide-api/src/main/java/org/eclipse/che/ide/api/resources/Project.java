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
package org.eclipse.che.ide.api.resources;

import com.google.common.annotations.Beta;

import org.eclipse.che.api.core.model.project.ProjectConfig;
import org.eclipse.che.api.project.shared.dto.SourceEstimation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.resources.marker.Marker;
import org.eclipse.che.ide.api.workspace.Workspace;

import java.util.List;

/**
 * An object that represents client side project.
 * <p/>
 * Features of projects include:
 * <ul>
 * <li>A project collects together a set of files and folders.</li>
 * <li>A project's location controls where the project's resources are stored in the local file system.</li>
 * </ul>
 * Project also extends {@link ProjectConfig} which contains the meta-data required to define a project.
 * <p/>
 * To get list of currently of all loaded projects in the IDE, use {@link Workspace#getProjects()}
 * <p/>
 * Note. This interface is not intended to be implemented by clients.
 *
 * @author Vlad Zhukovskyi
 * @see Workspace#getProjects()
 * @since 4.0.0-RC14
 */
@Beta
public interface Project extends Container, ProjectConfig {

    /**
     * Changes this project resource to match the given configuration provided by the request.
     * Request contains all necessary data for manipulating with the project.
     * <p/>
     * Example of usage:
     * <pre>
     *     ProjectConfig config = ... ;
     *     Project project = ... ;
     *
     *     Project.ProjectRequest updateRequest = project.update()
     *                                                   .withBody(config);
     *
     *     Promise<Project> updatedProject = updateRequest.send();
     * </pre>
     * <p/>
     * Fires {@link ResourceChangedEvent} with the following {@link ResourceDelta}:
     * Delta kind: {@link ResourceDelta#UPDATED}.
     * Updated resource provided by {@link ResourceDelta#getResource()}
     * <p/>
     * Note. Calling this method doesn't update the project immediately. To complete request
     * method {@link ProjectRequest#send()} should be called.
     *
     * @return the request to update the project
     * @see ProjectRequest
     * @see ProjectRequest#send()
     * @since 4.0.0-RC14
     */
    ProjectRequest update();

    /**
     * Check whether current project has problems. Problem project calculates in a runtime, so it is not affects stored
     * configuration on the server. To find out the reasons why project has problems, following code snippet may be helpful:
     * <p/>
     * Example of usage:
     * <pre>
     *     Project project = ... ;
     *     if (project.isProblem()) {
     *         Marker problemMarker = getMarker(ProblemProjectMarker.PROBLEM_PROJECT).get();
     *
     *         String message = String.valueOf(problemMarker.getAttribute(Marker.MESSAGE));
     *     }
     * </pre>
     *
     * @return {@code true} if current project has problems, otherwise {@code false}
     * @see ProblemProjectMarker
     * @since 4.0.0-RC14
     */
    boolean isProblem();

    /**
     * Resolve possible project types for current {@link Project}.
     * <p/>
     * These source estimations may be useful for automatically project type detection.
     * <p/>
     * Source estimation provides possible project type and attributes that this project type can provide.
     * Based on this information, current project may be configured in correct way.
     *
     * @return the {@link Promise} with source estimations
     * @since 4.0.0-RC14
     */
    Promise<List<SourceEstimation>> resolve();

    /**
     * Marker that describe problematic project.
     *
     * @see #isProblem()
     * @since 4.0.0-RC14
     */
    @Beta
    class ProblemProjectMarker implements Marker {

        /**
         * Marker type, which should be used when marker requests.
         *
         * @see Resource#getMarker(String)
         */
        public static final String PROBLEM_PROJECT = "problemProjectMarker";

        /** {@inheritDoc} */
        @Override
        public String getType() {
            return PROBLEM_PROJECT;
        }
    }

    /**
     * Base interface for project update operation.
     *
     * @see Project#update()
     * @since 4.0.0-RC14
     */
    @Beta
    interface ProjectRequest extends Resource.Request<Project, ProjectConfig> {
        @Override
        Request<Project, ProjectConfig> withBody(ProjectConfig object);

        @Override
        ProjectConfig getBody();
    }
}
