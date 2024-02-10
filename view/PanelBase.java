package view;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.naming.InvalidNameException;
import javax.swing.*;

public abstract class PanelBase extends JPanel implements ActionListener
{
    protected MyWindow _window;

    protected final int _rows;
    protected final int _cols;

    public PanelBase(MyWindow window, int rows, int cols)
    {
        this._window = window;
        this._rows = rows;
        this._cols = cols;
        this.setLayout(new GridLayout(this._rows, this._cols));
    }

    /** some button was clicked */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        try 
        {
            this._buttonClicked(e);
        } 
        catch (InvalidNameException | InvalidAttributeValueException | 
            AttributeNotFoundException | IOException | InterruptedException e1) 
        {
            e1.printStackTrace();
        }
    }

    protected abstract void _buttonClicked(ActionEvent e) 
        throws  InvalidNameException, InvalidAttributeValueException, 
                IOException, AttributeNotFoundException, InterruptedException;
    public abstract void repaintPanel();
}