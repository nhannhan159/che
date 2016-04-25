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
package org.eclipse.che.ide.ext.java.client.documentation;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.ext.java.client.resource.JavaSourceFolderMarker;
import org.eclipse.che.ide.ext.java.client.util.JavaUtil;
import org.eclipse.che.ide.jseditor.client.position.PositionConverter;
import org.eclipse.che.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import org.eclipse.che.ide.util.loging.Log;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class QuickDocPresenter implements QuickDocumentation, QuickDocView.ActionDelegate {


    private QuickDocView view;
    private AppContext   appContext;
    private EditorAgent editorAgent;

    @Inject
    public QuickDocPresenter(QuickDocView view,
                             AppContext appContext,
                             EditorAgent editorAgent) {
        this.view = view;
        this.appContext = appContext;
        this.editorAgent = editorAgent;
    }

    @Override
    public void showDocumentation() {
        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
        if (activeEditor == null) {
            return;
        }

        if (!(activeEditor instanceof EmbeddedTextEditorPresenter)) {
            Log.error(getClass(), "Quick Document support only EmbeddedTextEditorPresenter as editor");
            return;
        }

        EmbeddedTextEditorPresenter editor = ((EmbeddedTextEditorPresenter)activeEditor);
        int offset = editor.getCursorOffset();
        final PositionConverter.PixelCoordinates coordinates = editor.getPositionConverter().offsetToPixel(offset);

        final Resource resource = appContext.getResource();

        if (resource != null) {
            final Project project = resource.getRelatedProject();

            final Optional<Resource> srcFolder = resource.getParentWithMarker(JavaSourceFolderMarker.ID);

            if (!srcFolder.isPresent()) {
                return;
            }

            final String fqn = JavaUtil.resolveFQN((Container)srcFolder.get(), resource);

            view.show(appContext.getDevMachine().getWsAgentBaseUrl() + "/jdt/" + appContext.getDevMachine().getId() + "/javadoc/find?fqn=" + fqn + "&projectpath=" +
                      project.getLocation() + "&offset=" + offset, coordinates.getX(), coordinates.getY());
        }


    }

    @Override
    public void onCloseView() {
    }
}
