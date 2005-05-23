//
// Jikan - an application for managing your time
// Copyright (C) 2005 Michael Bayne
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import com.samskivert.jikan.data.Item;

/**
 * Displays a single item and allows it to be edited if it is clicked
 * upon.
 */
public class ItemWidget extends Composite
{
    public ItemWidget (Composite parent, Item item)
    {
        super(parent, 0);
        addMouseListener(_mlistener);
        setLayout(_layout);

        _item = item;

        _label = new Label(this, 0);
        _label.addMouseListener(_mlistener);
        _text = new Text(this, SWT.SINGLE);
        _text.addKeyListener(_klistener);
        _text.setVisible(false);

        _label.setText(item.getText());
        _text.setText(item.getText());
    }

    protected void startEdit ()
    {
        if (_label.isVisible()) {
            _label.setVisible(false);
            _text.setText(_item.getText());
            _text.setVisible(true);
            _text.forceFocus();
        }
    }

    protected void commitEdit ()
    {
        if (_text.isVisible()) {
            _item.setText(_text.getText());
            _label.setText(_item.getText());
            _text.setVisible(false);
            _label.setVisible(true);
        }
    }

    protected void abortEdit ()
    {
        if (_text.isVisible()) {
            _text.setVisible(false);
            _label.setVisible(true);
        }
    }

    protected Layout _layout = new Layout() {
        protected Point computeSize (
            Composite composite, int wHint, int hHint, boolean flushCache) {
            return _text.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
        }

        protected void layout (Composite composite, boolean flushCache) {
            // TODO: center the text label vertically
            _label.setBounds(composite.getClientArea());
            _text.setBounds(composite.getClientArea());
        }
    };

    protected MouseListener _mlistener = new MouseListener() {
        public void mouseDoubleClick (MouseEvent e) {
            // nada
        }
        public void mouseUp (MouseEvent e) {
            // nada
        }
        public void mouseDown (MouseEvent e) {
            startEdit();
        }
    };

    protected KeyListener _klistener = new KeyListener() {
        public void keyPressed (KeyEvent e) {
            switch (e.keyCode) {
            case SWT.ESC:
                abortEdit();
                break;

            case SWT.CR:
                commitEdit();
                break;
            }
        }
        public void keyReleased (KeyEvent e) {
            // nada
        }
    };

    protected Label _label;
    protected Text _text;
    protected Item _item;
}
