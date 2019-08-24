/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import net.joarchitectus.visor3d.jogl.Reader;

/**
 *
 * @author josorio
 */
public class JoCheckBox extends JCheckBox {//implements ListCellRenderer {

    private Reader objeto3D;
    private boolean forICP=false;
    
    /**
     * Constructor
     * @param string 
     */
    public JoCheckBox(String string, Reader objeto3D) {
        super(string);
        setSelected(true);
        this.objeto3D = objeto3D;
    }

    public Reader getObjeto3D() {
        return objeto3D;
    }

    protected Boolean setObjeto3DVisible(boolean selected) {
        if(objeto3D!=null){
            objeto3D.setVisible(selected);
            return selected;
        }
        return null;
    }

    protected void forICP(boolean forICP) {
        this.forICP = forICP;
    }

    public boolean isForICP() {
        return forICP;
    }
    
//    public Component getListCellRendererComponent(JList list, Object value, int index, 
//            boolean isSelected, boolean cellHasFocus) {
//
//        setComponentOrientation(list.getComponentOrientation());
//        setFont(list.getFont());
//        setBackground(list.getBackground());
//        setForeground(list.getForeground());
//        setSelected(isSelected);
//        setEnabled(list.isEnabled());
//
//        setText(value == null ? "" : value.toString());  
//
//        return this;
//    }
    
}
