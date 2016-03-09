/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.runtime.tests;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.logging.ui.LogLevels;

public abstract class AbstractContextMenuTest extends UITest {

	/**
	 * The test component should be capable of standard runtime operations (start, stop, logging, etc). It should also
	 * have one BULKIO output port that produces data.
	 */
	protected abstract ComponentDescription getTestComponent();

	private ComponentDescription testComponent = getTestComponent();

	/**
	 * Perform all necessary setup to get the appropriate diagram open.
	 */
	protected abstract RHBotGefEditor launchDiagram();

	/**
	 * Ensure the "standard" runtime context menu items are present and appear to do something.
	 * IDE-661 Start/stop
	 * IDE-662 Plotting
	 * IDE-663 Play port
	 * IDE-664 Display SRI
	 * IDE-665 Monitor port
	 * IDE-666 Snapshot
	 * IDE-667 Data list
	 * IDE-1325 Log level
	 */
	@Test
	public void standardRuntimeContextMenuOptions() {
		RHBotGefEditor editor = launchDiagram();
		String componentName = testComponent.getShortName(1);
		String portName = testComponent.getOutPort(0);

		// Start
		DiagramTestUtils.startComponentFromDiagram(editor, componentName);

		// Test Log Levels
		DiagramTestUtils.changeLogLevelFromDiagram(editor, componentName, LogLevels.TRACE);
		DiagramTestUtils.confirmLogLevelFromDiagram(editor, componentName, LogLevels.TRACE);

		DiagramTestUtils.changeLogLevelFromDiagram(editor, componentName, LogLevels.FATAL);
		DiagramTestUtils.confirmLogLevelFromDiagram(editor, componentName, LogLevels.FATAL);

		// Test plot context menu
		editor.setFocus();
		DiagramTestUtils.plotPortDataOnComponentPort(editor, componentName, portName);
		SWTBotView plotView = ViewUtils.getPlotView(bot);
		plotView.close();

		// Test data list context menu
		DiagramTestUtils.displayDataListViewOnComponentPort(editor, componentName, portName);
		ViewUtils.waitUntilDataListViewDisplays(bot);
		ViewUtils.startAquireOnDataListView(bot);
		ViewUtils.waitUntilDataListViewPopulates(bot);
		SWTBotView dataListView = ViewUtils.getDataListView(bot);
		dataListView.close();

		// Test monitor ports context menu
		DiagramTestUtils.displayPortMonitorViewOnUsesPort(editor, componentName, portName);
		ViewUtils.waitUntilPortMonitorViewPopulates(bot, componentName);
		SWTBotView monitorView = ViewUtils.getPortMonitorView(bot);
		monitorView.close();

		// Test SRI view context menu
		DiagramTestUtils.displaySRIDataOnComponentPort(editor, componentName, portName);
		ViewUtils.waitUntilSRIViewPopulates(bot);
		SWTBotView sriView = ViewUtils.getSRIView(bot);
		Assert.assertEquals("Expected streamID property in row 0", "streamID: ", sriView.bot().tree().cell(0, "Property: "));
		String streamID = sriView.bot().tree().cell(0, "Value: ");
		Assert.assertNotNull(streamID);
		Assert.assertTrue(!streamID.isEmpty());
		sriView.close();

		// Test audio/play port context menu
		DiagramTestUtils.playPortDataOnComponentPort(editor, componentName, portName);
		ViewUtils.waitUntilAudioViewPopulates(bot);
		SWTBotView audioView = ViewUtils.getAudioView(bot);
		String item = audioView.bot().list().getItems()[0];
		Assert.assertTrue(testComponent.getShortName() + " not found in Audio Port Playback", item.matches(componentName + ".*"));
		audioView.close();

		// Test snapshot context menu
		DiagramTestUtils.displaySnapshotDialogOnComponentPort(editor, componentName, portName);
		ViewUtils.waitUntilSnapshotDialogDisplays(bot);
		SWTBotShell snapshotDialog = ViewUtils.getSnapshotDialog(bot);
		Assert.assertNotNull(snapshotDialog);
		snapshotDialog.close();

		// Stop
		DiagramTestUtils.stopComponentFromDiagram(editor, componentName);
	}

	/**
	 * Tests that undo does not work after certain context menu operations.
	 * IDE-1038 Start and stop should not be undoable
	 * IDE-1065 Shouldn't be able to undo from initial diagram load, start/stop, various state transitions
	 */
	@Test
	public void noUndoForStartStopAndTransitions() {
		RHBotGefEditor editor = launchDiagram();
		String componentName = testComponent.getShortName(1);

		Assert.assertFalse("Undo menu is enabled", MenuUtils.isUndoDisabled(bot));

		// Perform start -> stop -> start, so we don't have to worry about initial state
		DiagramTestUtils.startComponentFromDiagram(editor, componentName);
		DiagramTestUtils.waitForComponentState(bot, editor, componentName, ComponentState.STARTED);
		Assert.assertFalse("Editor dirty after starting a resource", editor.isDirty());
		Assert.assertFalse("Undo menu is enabled after starting a resource", MenuUtils.isUndoDisabled(bot));

		DiagramTestUtils.stopComponentFromDiagram(editor, componentName);
		DiagramTestUtils.waitForComponentState(bot, editor, componentName, ComponentState.STOPPED);
		Assert.assertFalse("Editor dirty after stopping a resource", editor.isDirty());
		Assert.assertFalse("Undo menu is enabled after stopping a resource", MenuUtils.isUndoDisabled(bot));

		DiagramTestUtils.startComponentFromDiagram(editor, componentName);
		DiagramTestUtils.waitForComponentState(bot, editor, componentName, ComponentState.STARTED);
		Assert.assertFalse("Editor dirty after starting a resource", editor.isDirty());
		Assert.assertFalse("Undo menu is enabled after starting a resource", MenuUtils.isUndoDisabled(bot));
	}

	/**
	 * Tests that certain context menu options are <b>not</b> present in the context menu.
	 * Depends on sub-class implementing {@link #getAbsentContextMenuOptions()}.
	 */
	@Test
	public void absentContextMenuOptions() {
		RHBotGefEditor editor = launchDiagram();

		SWTBotGefEditPart editPart = editor.getEditPart(testComponent.getShortName(1));
		editPart.select();
		List<String> removedContextOptions = getAbsentContextMenuOptions();
		for (String contextOption : removedContextOptions) {
			try {
				editor.clickContextMenu(contextOption);
				Assert.fail(); // The only way to get here is if the undesired context menu option appears
			} catch (WidgetNotFoundException e) {
				Assert.assertEquals(contextOption, e.getMessage());
			}
		}
	}

	/**
	 * IDE-961 No "Delete" option for runtime
	 * IDE-1196 "Show Console" should not be present for domain objects
	 * @return
	 */
	protected List<String> getAbsentContextMenuOptions() {
		return Arrays.asList("Delete", "Release", "Terminate", "Show Console");
	}
}
