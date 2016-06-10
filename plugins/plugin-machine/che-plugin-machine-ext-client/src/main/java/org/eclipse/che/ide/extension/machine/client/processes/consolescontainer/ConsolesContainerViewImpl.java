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
package org.eclipse.che.ide.extension.machine.client.processes.consolescontainer;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.api.theme.Style;
import org.eclipse.che.ide.extension.machine.client.MachineResources;
import org.eclipse.che.ide.util.loging.Log;

/**
 * Implementation of {@link ConsolesContainerView}.
 *
 * @author Roman Nikitenko
 */
@Singleton
public class ConsolesContainerViewImpl extends BaseView<ConsolesContainerView.ActionDelegate> implements ConsolesContainerView, RequiresResize {

    interface ProcessesViewImplUiBinder extends UiBinder<Widget, ConsolesContainerViewImpl> {
    }

    @UiField(provided = true)
    MachineResources machineResources;

    @UiField
    SplitLayoutPanel splitLayoutPanel;

    @UiField
    SimplePanel leftPanel;

    @UiField
    SimplePanel rightPanel;

    @Inject
    public ConsolesContainerViewImpl(org.eclipse.che.ide.Resources resources,
                                     MachineResources machineResources,
                                     PartStackUIResources partStackUIResources,
                                     ProcessesViewImplUiBinder uiBinder) {
        super(partStackUIResources);

        this.machineResources = machineResources;

        setContentWidget(uiBinder.createAndBindUi(this));

        tuneSplitter();
    }

    /**
     * Improves splitter visibility.
     */
    private void tuneSplitter() {
        NodeList<Node> nodes = splitLayoutPanel.getElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.getItem(i);
            if (node.hasChildNodes()) {
                Element el = node.getFirstChild().cast();
                if ("gwt-SplitLayoutPanel-HDragger".equals(el.getClassName())) {
                    tuneSplitter(el);
                    return;
                }
            }
        }
    }

    /**
     * Tunes splitter. Makes it wider and adds double border to seem rich.
     *
     * @param el
     *         element to tune
     */
    private void tuneSplitter(Element el) {
        /** Add Z-Index to move the splitter on the top and make content visible */
        el.getParentElement().getStyle().setProperty("zIndex", "1000");
        el.getParentElement().getStyle().setProperty("overflow", "visible");

        /** Tune splitter catch panel */
        el.getStyle().setProperty("boxSizing", "border-box");
        el.getStyle().setProperty("width", "5px");
        el.getStyle().setProperty("overflow", "hidden");
        el.getStyle().setProperty("marginLeft", "-3px");
        el.getStyle().setProperty("backgroundColor", "transparent");

        /** Add small border */
        DivElement smallBorder = Document.get().createDivElement();
        smallBorder.getStyle().setProperty("position", "absolute");
        smallBorder.getStyle().setProperty("width", "1px");
        smallBorder.getStyle().setProperty("height", "100%");
        smallBorder.getStyle().setProperty("left", "3px");
        smallBorder.getStyle().setProperty("top", "0px");
        smallBorder.getStyle().setProperty("backgroundColor", Style.getSplitterSmallBorderColor());
        el.appendChild(smallBorder);

        /** Add large border */
        DivElement largeBorder = Document.get().createDivElement();
        largeBorder.getStyle().setProperty("position", "absolute");
        largeBorder.getStyle().setProperty("width", "2px");
        largeBorder.getStyle().setProperty("height", "100%");
        largeBorder.getStyle().setProperty("left", "1px");
        largeBorder.getStyle().setProperty("top", "0px");
        largeBorder.getStyle().setProperty("opacity", "0.4");
        largeBorder.getStyle().setProperty("backgroundColor", Style.getSplitterLargeBorderColor());
        el.appendChild(largeBorder);
    }

    @Override
    public SimplePanel getLeftContainer() {

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                int width = splitLayoutPanel.getOffsetWidth() / 2;
                splitLayoutPanel.setWidgetMinSize(leftPanel, width);
                Log.error(getClass(), "== width " + width);
//                splitLayoutPanel.getCgetWidgetContainerElement(leftPanel).getStyle().setWidth(width,
//                                                                                          com.google.gwt.dom.client.Style.Unit.PX);
            }
        });
        return leftPanel;
    }

    @Override
    public SimplePanel getRightContainer() {
//        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
//            @Override
//            public void execute() {
//                int width = splitLayoutPanel.getOffsetWidth()/2;
//                Log.error(getClass(), "== width " + width);
//                rightPanel.setWidth(String.valueOf(width) + "px");
//            }
//        });
        return rightPanel;
    }

    @Override
    protected void focusView() {
        getElement().focus();
    }

    @Override
    public void onResize() {
        onResizePanel(leftPanel);
        onResizePanel(rightPanel);
    }

    private void onResizePanel(SimplePanel panel) {
        Widget widget = panel.getWidget();
        if (widget instanceof RequiresResize) {
            ((RequiresResize)widget).onResize();
        }
    }
}
