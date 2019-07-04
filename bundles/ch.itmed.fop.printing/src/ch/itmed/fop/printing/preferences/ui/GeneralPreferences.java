package ch.itmed.fop.printing.preferences.ui;

import java.util.function.Consumer;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.util.SWTHelper;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.resources.Messages;
import ch.rgw.io.Settings;

public final class GeneralPreferences extends PreferencePage implements IWorkbenchPreferencePage {
	private String[] tableCols = { Messages.GeneralPreferences_Document, Messages.GeneralPreferences_Printer,
			Messages.GeneralPreferences_PaperFormat, Messages.GeneralPreferences_XslTemplate,
			Messages.GeneralPreferences_TextOrientation, Messages.GeneralPreferences_SettingScope };

	private static Table table;

	public GeneralPreferences() {
	}

	@Override
	public void init(IWorkbench arg0) {
		// Auto-generated method stub
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));

		table = new Table(ret, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		for (int i = 0; i < tableCols.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(tableCols[i]);
		}

		createTableContent();

		for (int i = 0; i < tableCols.length; i++) {
			table.getColumn(i).pack();
		}

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				TemplatePreferencesDialog dialog = new TemplatePreferencesDialog(getShell(), table.getSelectionIndex());
				dialog.open();
				updateTableRow(table.getSelectionIndex(),
						PreferenceConstants.getDocumentName(table.getSelectionIndex()));
			}
		});

		table.setLayoutData(SWTHelper.getFillGridData(2, true, 1, true));
		table.setSize(table.computeSize(SWT.DEFAULT, 200));

		return ret;
	}

	@Override
	public boolean performOk() {
		return super.performOk();
	}

	private static void updateTableRow(int rowIndex, String docName) {
		TableItem item = table.getItem(rowIndex);
		Settings settingsStore;

		if (CoreHub.localCfg.get(PreferenceConstants.getDocPreferenceConstant(docName, 12), true)) {
			settingsStore = CoreHub.globalCfg;
		} else {
			settingsStore = CoreHub.localCfg;
		}

		item.setText(1, settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 0), ""));

		if (settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 4), false)) {
			item.setText(2, Messages.GeneralPreferences_Custom);
		} else {
			item.setText(2, settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 3), ""));
		}

		if (settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 2), false)) {
			item.setText(3, Messages.GeneralPreferences_Custom);
		} else {
			item.setText(3, Messages.GeneralPreferences_Default);
		}

		if (settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 7), "0").equals("90")) {
			item.setText(4, Messages.GeneralPreferences_OrientationPortrait);
		} else {
			item.setText(4, Messages.GeneralPreferences_OrientationLandscape);
		}

		// TODO: Resize the cell
		// table.setSize(table.computeSize(SWT.DEFAULT, 200)); // Make sure the table
		// size will be adjusted
	}

	private void createTableContent() {
		PreferenceConstants.getDocumentNames().forEach(new TableConsumer());
	}

	private static class TableConsumer implements Consumer<String> {

		@Override
		public void accept(String s) {
			TableItem item = new TableItem(table, SWT.NONE);
			Settings settingsStore;
			if (CoreHub.localCfg.get(PreferenceConstants.getDocPreferenceConstant(s, 12), true)) {
				settingsStore = CoreHub.globalCfg;
			} else {
				settingsStore = CoreHub.localCfg;
			}

			item.setText(0, Messages.getDocumentName(table.getItemCount() - 1));
			item.setText(1, settingsStore.get(PreferenceConstants.getDocPreferenceConstant(s, 0), ""));

			if (settingsStore.get(PreferenceConstants.getDocPreferenceConstant(s, 4), false)) {
				item.setText(2, Messages.GeneralPreferences_Custom);
			} else {
				item.setText(2, settingsStore.get(PreferenceConstants.getDocPreferenceConstant(s, 3), ""));
			}

			if (settingsStore.get(PreferenceConstants.getDocPreferenceConstant(s, 2), false)) {
				item.setText(3, Messages.GeneralPreferences_Custom);
			} else {
				item.setText(3, Messages.GeneralPreferences_Default);
			}

			if (settingsStore.get(PreferenceConstants.getDocPreferenceConstant(s, 7), "").equals("90")) {
				item.setText(4, Messages.GeneralPreferences_OrientationPortrait);
			} else {
				item.setText(4, Messages.GeneralPreferences_OrientationLandscape);
			}

			TableEditor editor = new TableEditor(table);
			Button button = new Button(table, SWT.CHECK);
			button.setSelection(CoreHub.localCfg.get(PreferenceConstants.getDocPreferenceConstant(s, 12), true));
			button.pack();
			editor.minimumWidth = button.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(button, item, 5);

			button.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					CoreHub.localCfg.set(PreferenceConstants.getDocPreferenceConstant(s, 12), button.getSelection());
					updateTableRow(table.indexOf(item), s);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
		}

	}
}
