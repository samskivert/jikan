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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.samskivert.util.StringUtil;

/**
 * Displays a UI asking for a username and password.
 */
public abstract class GetCredsDialog
{
    public GetCredsDialog (Shell parent, String info)
    {
        final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("Login");

        GridLayout gl = new GridLayout();
        gl.numColumns = 2;
        shell.setLayout(gl);

        Label ilabel = new Label(shell, SWT.LEFT);
        ilabel.setText(info);
        ilabel.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, true, false, 2, 1));

        Label ulabel = new Label(shell, SWT.LEFT);
        ulabel.setText("Username:");
        ulabel.setLayoutData(new GridData(SWT.RIGHT, SWT.DEFAULT, false, false));

        final Text utext = new Text(shell, SWT.SINGLE);
        utext.setText(getDefaultUsername());
        GridData gd = new GridData(SWT.DEFAULT, SWT.DEFAULT, true, false);
        gd.widthHint = 150;
        utext.setLayoutData(gd);

        Label plabel = new Label(shell, SWT.LEFT);
        plabel.setText("Password:");
        plabel.setLayoutData(new GridData(SWT.RIGHT, SWT.DEFAULT, false, false));

        final Text ptext = new Text(shell, SWT.SINGLE | SWT.PASSWORD);
        gd = new GridData(SWT.DEFAULT, SWT.DEFAULT, true, false);
        gd.widthHint = 150;
        ptext.setLayoutData(gd);

        Button cancel = new Button(shell, 0);
        cancel.setText("Cancel");

        Button logon = new Button(shell, 0);
        logon.setText("Logon");
        logon.setLayoutData(new GridData(SWT.RIGHT, SWT.DEFAULT, false, false));

        cancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                shell.dispose();
            }
        });

        SelectionAdapter onLogon = new SelectionAdapter() {
            public void widgetDefaultSelected (SelectionEvent e) {
                widgetSelected(e);
            }
            public void widgetSelected (SelectionEvent e) {
                String username = utext.getText().trim();
                String password = ptext.getText().trim();
                if (StringUtil.isBlank(username) || StringUtil.isBlank(password)) {
                    return;
                }
                onLogon(username, password);
                shell.dispose();
            }
        };
        ptext.addSelectionListener(onLogon);
        logon.addSelectionListener(onLogon);

        shell.pack();
        shell.open();

        if (utext.getText().length() > 0) {
            ptext.forceFocus();
        }
    }

    protected String getDefaultUsername ()
    {
        return "";
    }

    protected abstract void onLogon (String username, String password);
}
