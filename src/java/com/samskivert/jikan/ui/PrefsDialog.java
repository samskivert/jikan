//
// Jikan - an application for managing your time
// Copyright (C) 2005-2009 Michael Bayne
// 
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2 of the License, or (at your
// option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

package com.samskivert.jikan.ui;

import java.io.File;

import com.samskivert.jikan.Jikan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * Displays user preferences and allows them to be configured.
 */
public class PrefsDialog
{
    public PrefsDialog (Display display)
    {
        _display = display;
        _shell = new Shell(display, SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL);
        _shell.setText("Preferences");

        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        _shell.setLayout(gl);

        TabFolder tabs = new TabFolder(_shell, SWT.BORDER);
        createDataDirTab(tabs);

        Button close = new Button(_shell, SWT.NONE);
        close.setText("Close");
        close.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                _shell.dispose();
            }
        });
        close.setLayoutData(new GridData(SWT.RIGHT, SWT.DEFAULT, false, false));

        _shell.pack();
        _shell.open();
    }

    public void run ()
    {
	while (!_shell.isDisposed()) {
            if (!_display.readAndDispatch()) {
                _display.sleep();
            }
	}
    }

    protected void createDataDirTab (TabFolder tabs)
    {
        Composite pane = new Composite(tabs, SWT.NONE);
        GridLayout gl = new GridLayout();
        gl.numColumns = 2;
        pane.setLayout(gl);

        Label label = new Label(pane, SWT.LEFT);
        label.setText("Where to store your data?");
        label.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, true, false, 2, 1));

        String dir = Jikan.prefs.get("datadir", getDataDir(System.getProperty("user.home")));
        final Text text = new Text(pane, SWT.SINGLE|SWT.BORDER);
        text.setText(dir);
        text.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 1, 1));
        text.addModifyListener(new ModifyListener() {
            public void modifyText (ModifyEvent event) {
                Jikan.prefs.put("datadir", text.getText().trim());
            }
        });

        Button change = new Button(pane, SWT.NONE);
        change.setText("Change");
        change.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                DirectoryDialog chooser = new DirectoryDialog(_shell);
                chooser.setText("Change data directory");
                chooser.setMessage(
                    "A 'Jikan' directory will be created in the directory you choose.");
                // chooser.setFileName(text.getText());
                String dir = chooser.open();
                if (dir != null) {
                    text.setText(getDataDir(dir));
                }
            }
        });

        Label tip = new Label(pane, SWT.LEFT|SWT.WRAP);
        tip.setText("Tip: Put your data in your Dropbox folder to sync it " +
                    "between multiple computers.");
        tip.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, true, false, 2, 1));
        tip.setFont(Jikan.config.getFont(Jikan.config.ICON_FONT));

        TabItem tab = new TabItem(tabs, SWT.NONE);
        tab.setText("Data");
        tab.setControl(pane);
    }

    protected String getDataDir (String parent)
    {
        return parent + File.separator + "Jikan";
    }

    protected Display _display;
    protected Shell _shell;
}
