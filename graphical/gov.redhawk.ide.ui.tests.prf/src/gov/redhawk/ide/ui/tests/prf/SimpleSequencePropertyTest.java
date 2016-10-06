/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.ui.tests.prf;

import gov.redhawk.ide.swtbot.StandardTestActions;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Assert;
import org.junit.Test;

public class SimpleSequencePropertyTest extends AbstractPropertyTest {

	@Test
	public void testValues() throws CoreException {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		// Start with type selected as string
		addValue(editorBot, "true");
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "a", false);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "true", false);
		assertFormValid();
		addValue(editorBot, "true");
		assertFormValid();
		clearValues();

		setType("char", "");
		addValue(editorBot, "1");
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "abc", false);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1", false);
		assertFormValid();
		addValue(editorBot, "1");
		assertFormValid();
		clearValues();

		setType("double (64-bit)", "");
		editorBot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "al", false);
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1", false);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("-1.1");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("float (32-bit)", "complex");
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1+j10.1");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1.1", false);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1.1+jjak", false);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1.1", false);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1.1+j10.1", false);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1+j10.1");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("long (32-bit)", "");
		addValue(editorBot, "-11");
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1.1", false);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-11", false);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("-11");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("longlong (64-bit)", "");
		addValue(editorBot, "-11");
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1.1", false);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-11", false);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("-11");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("short (16-bit)", "complex");
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-11-j2");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1", false);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1+100iada", false);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-11-j2", false);
		assertFormValid();
		addValue(editorBot, "-11-j2");
		assertFormValid();
		clearValues();

		setType("ulong (32-bit)", "");
		addValue(editorBot, "11");
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1", false);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "11", false);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("11");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("ulonglong (64-bit)", "");
		addValue(editorBot, "11");
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1", false);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "11", false);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("11");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("ushort (16-bit)", "complex");
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("11+j2");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1", false);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1+j1ada", false);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "11", false);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "11+j2", false);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("11+j2");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("objref", "");
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("1");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("string", "");
		addValue(editorBot, "abcd");
		assertFormValid();
		addValue(editorBot, "efg");
		assertFormValid();
		clearValues();
	}

	private void setType(String type, String complex) {
		editorBot.comboBoxWithLabel("Type*:").setSelection(type);
		editorBot.comboBoxWithLabel("Type*:", 1).setSelection(complex);
	}

	private void addValue(SWTBot bot, String text) {
		final int startingRows = bot.table().rowCount();
		bot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText(text);
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				int endingRows = bot.table().rowCount();
				return (startingRows + 1) == endingRows;
			}

			@Override
			public String getFailureMessage() {
				return "Value was not added to table";
			}
		});
	}

	private void clearValues() {
		SWTBotTable valuesTable = editorBot.tableWithLabel("Values:");
		if (valuesTable.rowCount() > 0) {
			for (int i = 0; i <= valuesTable.rowCount(); i++) {
				valuesTable.select(0);
				SWTBotButton removeButton = editorBot.button("Remove", 1);
				if (removeButton.isEnabled()) {
					editorBot.button("Remove", 1).click();
				} else {
					break;
				}
			}
		}
	}

	@Override
	protected void createType() {
		editorBot.button("Add Sequence").click();
	}

	@Override
	public void testEnum() throws CoreException {
		// Override to do nothing since Simple Sequences do not have enums
	}

	@Test
	public void testKind() throws IOException {
		assertFormValid();

		testKind(false, false);
	}
}
