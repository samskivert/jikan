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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

/**
 * Displays text in a label and allows that text to be edited if the label
 * is clicked upon.
 */
public class EditableLabel extends Composite
{
    public EditableLabel (Composite parent, String text)
    {
        super(parent, 0);
        addMouseListener(_mlistener);
        setLayout(_layout);

        _label = new Label(this, 0);
        _label.addMouseListener(_mlistener);
        _text = new Text(this, SWT.SINGLE);
        _text.addKeyListener(_klistener);
        _text.setVisible(false);
        _text.addFocusListener(_flistener);

        setText(text);
    }

    public String getText ()
    {
        return _label.getText();
    }

    public void setText (String text)
    {
        _label.setText(text);
        _text.setText(text);
    }

    protected void textUpdated (String text)
    {
    }

    protected void startEdit ()
    {
        if (_label.isVisible()) {
            _label.setVisible(false);
            _text.setText(_label.getText());
            _text.setVisible(true);
            _text.forceFocus();
        }
    }

    protected void commitEdit ()
    {
        if (_text.isVisible()) {
            String text = _text.getText();
            _label.setText(text);
            _text.setVisible(false);
            _label.setVisible(true);
            textUpdated(text);
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
            Rectangle area = composite.getClientArea();
            _text.setBounds(area);
            // center the text label vertically
            Point lsize = _label.computeSize(
                SWT.DEFAULT, SWT.DEFAULT, flushCache);
            _label.setBounds(area.x, area.y + (area.height-lsize.y)/2,
                             area.width, lsize.y);
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
            if (e.button == 1) {
                startEdit();
            }
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

    protected FocusListener _flistener = new FocusListener() {
        public void focusGained (FocusEvent e) {
            // nada
        }
        public void focusLost (FocusEvent e) {
            commitEdit();
        }
    };

    protected Label _label;
    protected Text _text;
}
