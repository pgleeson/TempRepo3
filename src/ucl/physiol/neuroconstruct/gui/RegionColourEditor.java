/**
 *  neuroConstruct
 *  Software for developing large scale 3D networks of biologically realistic neurons
 * 
 *  Copyright (c) 2009 Padraig Gleeson
 *  UCL Department of Neuroscience, Physiology and Pharmacology
 *
 *  Development of this software was made possible with funding from the
 *  Medical Research Council and the Wellcome Trust
 *  
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package ucl.physiol.neuroconstruct.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;


/**
 * Implementation of AbstractCellEditor for region colour column
 *
 * @author Padraig Gleeson
 *  
 */



@SuppressWarnings("serial")
public class RegionColourEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
{
    Color currentColour;
    JButton button;
    JColorChooser colourChooser;
    JDialog dialog;

    public RegionColourEditor()
    {
        button = new JButton();
        button.setActionCommand("Edit");
        button.addActionListener(this);
        button.setBorderPainted(false);

        colourChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button,
                                            "Pick a colour for this 3D Region. Note: only regions with cells and non white regions are shown in 3D",
                                            true,
                                            colourChooser,
                                            this,
                                            null);
    }


    public void actionPerformed(ActionEvent e)
    {
        if ("Edit".equals(e.getActionCommand()))
        {
            button.setBackground(currentColour);
            colourChooser.setColor(currentColour);
            dialog.setVisible(true);

            fireEditingStopped();

        }
        else
        {
            currentColour = colourChooser.getColor();
        }
    }

    public Object getCellEditorValue()
    {
        return currentColour;
    }

    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column)
    {
        currentColour = (Color)value;
        return button;
    }
}
