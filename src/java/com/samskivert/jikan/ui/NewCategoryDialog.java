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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.samskivert.jikan.Jikan;
import com.samskivert.jikan.data.Category;
import com.samskivert.util.StringUtil;

/**
 * Displays an interface allowing a new category to be created.
 */
public class NewCategoryDialog
{
    public NewCategoryDialog (Shell parent, CategoryItemList list)
    {
        _list = list;
        _shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        _shell.setText("Create new category");

        GridLayout gl = new GridLayout();
        gl.verticalSpacing = 0;
        gl.numColumns = 3;
        _shell.setLayout(gl);

        _text = new Text(_shell, SWT.SINGLE|SWT.BORDER);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.widthHint = 150;
        _text.setLayoutData(gd);

        Button create = new Button(_shell, 0);
        create.setText("Create");
        create.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                String name = _text.getText().trim();
                if (StringUtil.isBlank(name)) {
                    return;
                }
                String file = name.toLowerCase().replace("\\s", "_");
                Category cat = new Category(name, file);
                Jikan.store.createCategory(cat);
                _list.selectCategory(cat);
                _shell.dispose();
            }
        });

        Button cancel = new Button(_shell, 0);
        cancel.setText("Cancel");
        cancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected (SelectionEvent e) {
                _shell.dispose();
            }
        });

        _shell.pack();
        _shell.open();
    }

    protected CategoryItemList _list;
    protected Shell _shell;
    protected Text _text;
}
