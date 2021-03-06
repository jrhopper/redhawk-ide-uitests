/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.ui.tests.projectCreation;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractCreationWizardTest extends UITest {

	private SWTBotShell wizardShell;
	private SWTBot wizardBot;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@BeforeClass
	public static void setupPyDev() throws Exception {
		StandardTestActions.configurePyDev();
	}

	protected SWTBotShell getWizardShell() {
		return wizardShell;
	}

	protected SWTBot getWizardBot() {
		return wizardBot;
	}

	@Before
	@Override
	public void before() throws Exception {
		super.before();

		bot.menu("File").menu("New").menu("Project...").click();
		wizardShell = bot.shell("New Project");

		// On el7, SWTBot tests seem to reach a point where the new project wizard is no longer activated when it opens
		if (!wizardShell.isActive()) {
			wizardShell.setFocus();
		}

		wizardBot = wizardShell.bot();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", getProjectType()));
		treeItem.select();
		wizardBot.button("Next >").click();
	}

	protected abstract String getProjectType();

	@Test
	public void testNonDefaultLocation() throws IOException {
		bot.textWithLabel("&Project name:").setText("ProjectName");
		bot.checkBox("Use default location").click();

		bot.textWithLabel("&Location:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		File createdFolder = folder.newFolder("ProjectName");
		bot.textWithLabel("&Location:").setText(createdFolder.getAbsolutePath());
		bot.button("Finish").click();

		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("ProjectName");
		IPath location = project.getLocation();
		Assert.assertEquals(createdFolder.getAbsolutePath(), location.toOSString());
	}

	protected String getBaseFilename(String projectName) {
		String[] segments = projectName.split("\\.");
		return segments[segments.length - 1];
	}

}
