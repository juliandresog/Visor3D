/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.joarchitectus.visor3d;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.vecmath.Matrix4d;
import net.joarchitectus.visor3d.icp.ListenerAvance;
import net.joarchitectus.visor3d.jogl.Canvas;
import net.joarchitectus.visor3d.jogl.Reader;
import net.joarchitectus.visor3d.util.CSVUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author josorio
 */
public class MainJFrame extends javax.swing.JFrame {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(MainJFrame.class);

    /**
     * Canvas for OpenGL to draw on.
     */
    private GLCanvas canvas;
    /**
     * Default width of the rendered window.
     */
    private static final int CANVAS_WIDTH = 800;

    /**
     * Default height of the rendered window.
     */
    private static final int CANVAS_HEIGHT = 600;

    /**
     * Target FPS for the animator.
     */
    private static final int FPS = 60;

    private FPSAnimator animator;

//    private JCheckBoxList checkBoxList;
//    private DefaultListModel<JCheckBox> modelCheck;
    private Box boxCheck;

    //Historico ICP
    private List<Map> historicoICP;

    /**
     * Creates new form MainJFrame
     */
    public MainJFrame() {
        // Create the OpenGL rendering canvas and configure it
        //getting the capabilities object of GL2 profile
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        canvas = new Canvas(capabilities);
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // Create a animator that drives canvas' display() at the specified FPS.
        animator = new FPSAnimator(canvas, FPS, true);

        // Create the top-level container frame
        this.getContentPane().add(canvas);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Use a dedicate thread to run the stop() to ensure that the
                // animator stops before program exits.
                new Thread() {
                    @Override
                    public void run() {
                        if (animator != null && animator.isStarted()) {
                            animator.stop();
                        }
                        System.exit(0);
                    }
                }.start();
            }
        });

        initComponents();
        jMenuBarMain.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        boxCheck = Box.createVerticalBox();
//        modelCheck = new DefaultListModel<JCheckBox>();
//        checkBoxList = new JCheckBoxList(modelCheck);

        JoCheckBox inicial = new JoCheckBox("Conejo", null);
        inicial.addActionListener(crearActionCheck());
//        modelCheck.addElement(inicial);
        boxCheck.add(inicial);

        scrollXList.setViewportView(boxCheck);
        //scrollXList.add(box);

        //this.setTitle(TITLE);
        //this.pack();
        this.setVisible(true);

        // Start animation loop
        animator.start();
    }

    /**
     * Para ocultar o mostrar objetos 3D segun check
     *
     * @return
     */
    private ActionListener crearActionCheck() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    JoCheckBox abstractButton = (JoCheckBox) actionEvent.getSource();
                    boolean selected = abstractButton.getModel().isSelected();
                    //log.info("cambio: "+selected);
                    Boolean raiz = abstractButton.setObjeto3DVisible(selected);
                    ((Canvas) canvas).refrescar(raiz, selected, abstractButton.isForICP());
                } catch (Exception ex) {
                    log.error("Error", ex);
                }
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooserPyl = new javax.swing.JFileChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        cbKDTree = new javax.swing.JCheckBox();
        cbKDTreeWeka = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        scrollXList = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtEstado = new javax.swing.JTextArea();
        barraProgreso = new javax.swing.JProgressBar();
        labelEstado = new javax.swing.JLabel();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuAbrir = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menuBun000 = new javax.swing.JMenuItem();
        menuBun045 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Visor 3D PLY - Julian A Osorio G");
        setPreferredSize(new java.awt.Dimension(924, 700));
        setResizable(false);

        jToolBar1.setRollover(true);

        jLabel2.setText("Julián Andrés Osorio G.");
        jToolBar1.add(jLabel2);
        jToolBar1.add(jSeparator1);

        cbKDTree.setText("KD-Tree Java");
        cbKDTree.setFocusable(false);
        cbKDTree.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cbKDTree.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cbKDTree.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(cbKDTree);

        cbKDTreeWeka.setText("KD-Tree Weka");
        cbKDTreeWeka.setFocusable(false);
        cbKDTreeWeka.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cbKDTreeWeka.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cbKDTreeWeka.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(cbKDTreeWeka);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jPanel1.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jPanel1.add(scrollXList);

        txtEstado.setColumns(20);
        txtEstado.setRows(5);
        txtEstado.setText("Observaciones");
        jScrollPane1.setViewportView(txtEstado);

        jPanel1.add(jScrollPane1);
        jPanel1.add(barraProgreso);

        labelEstado.setText("Progreso");
        jPanel1.add(labelEstado);

        getContentPane().add(jPanel1, java.awt.BorderLayout.LINE_END);

        jMenu1.setText("Menu");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });

        jMenuAbrir.setText("Limpiar y Abrir");
        jMenuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAbrirActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuAbrir);

        jMenuItem1.setText("Agregar");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Limpiar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Ejecutar ICP");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBarMain.add(jMenu1);

        jMenu2.setText("Bunny");

        menuBun000.setText("Bun000");
        menuBun000.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBun000ActionPerformed(evt);
            }
        });
        jMenu2.add(menuBun000);

        menuBun045.setText("Bun045");
        menuBun045.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBun045ActionPerformed(evt);
            }
        });
        jMenu2.add(menuBun045);

        jMenuBarMain.add(jMenu2);

        setJMenuBar(jMenuBarMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        // TODO add your handling code here:
        //Abrir otro archivo
        //((Canvas)canvas).cargarArchivo("/media/DATOS/Universidad/Proyectos/horse.ply");


    }//GEN-LAST:event_jMenu1MouseClicked

    private void jMenuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAbrirActionPerformed
        // TODO add your handling code here:
        ((Canvas) canvas).limpiar(false);
        boxCheck.removeAll();
        boxCheck.doLayout();
        txtEstado.setText("");

        jFileChooserPyl.setDialogTitle("Select a ply file");
        jFileChooserPyl.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PLY file", "ply");
        jFileChooserPyl.addChoosableFileFilter(filter);

        int returnValue = jFileChooserPyl.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jFileChooserPyl.getSelectedFile().isDirectory()) {
                System.out.println("You selected the directory: " + jFileChooserPyl.getSelectedFile());
            } else {
                Reader objeto3D = ((Canvas) canvas).cargarArchivo(jFileChooserPyl.getSelectedFile().getPath());
                JoCheckBox chckObjeto3D = new JoCheckBox(jFileChooserPyl.getSelectedFile().getName(), objeto3D);
                chckObjeto3D.addActionListener(crearActionCheck());
                boxCheck.add(chckObjeto3D);
                boxCheck.doLayout();
            }
        }
    }//GEN-LAST:event_jMenuAbrirActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here: Agregar
        jFileChooserPyl.setDialogTitle("Select a ply file");
        jFileChooserPyl.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PLY file", "ply");
        jFileChooserPyl.addChoosableFileFilter(filter);

        int returnValue = jFileChooserPyl.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jFileChooserPyl.getSelectedFile().isDirectory()) {
                System.out.println("You selected the directory: " + jFileChooserPyl.getSelectedFile());
            } else {
                Reader objeto3D = ((Canvas) canvas).agregarArchivo(jFileChooserPyl.getSelectedFile().getPath());
                JoCheckBox chckObjeto3D = new JoCheckBox(jFileChooserPyl.getSelectedFile().getName(), objeto3D);
                chckObjeto3D.addActionListener(crearActionCheck());
                boxCheck.add(chckObjeto3D);
                boxCheck.doLayout();
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here: Limpiar
        ((Canvas) canvas).limpiar(true);
        boxCheck.removeAll();
        boxCheck.repaint();
        boxCheck.doLayout();

        txtEstado.setText("");
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO Ejecutar ICP
        log.warn("Ejecutando ICP...");
        labelEstado.setText("Ejecutando ICP");

//        ((Canvas) canvas).ejecutarICP(new ListenerAvance() {
//            @Override
//            public void avance(String text) {
//                labelEstado.setText(text);
//                labelEstado.revalidate();
//                labelEstado.repaint();
//            }
//            
//            @Override
//            public void avance(int iteracion) {
//                
//            }
//            
//            @Override
//            public void matrixAvance(Matrix4d matrix4d, int iteracion) {
//                
//            }
//        });
        //start the SwingWorker outside the EDT
        MySwingWorker worker = new MySwingWorker(barraProgreso, labelEstado);
        worker.execute();
        JoCheckBox chckObjeto3D = new JoCheckBox("Resultado ICP", null);
        chckObjeto3D.forICP(true);
        chckObjeto3D.addActionListener(crearActionCheck());
        boxCheck.add(chckObjeto3D);
        boxCheck.doLayout();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void menuBun000ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBun000ActionPerformed
        // TODO add your handling code here:
        Reader objeto3D = ((Canvas) canvas).agregarArchivo(getClass().getClassLoader().getResourceAsStream("bunny/bun000.ply"));
        JoCheckBox chckObjeto3D = new JoCheckBox("bun000", objeto3D);
        chckObjeto3D.addActionListener(crearActionCheck());
        boxCheck.add(chckObjeto3D);
        boxCheck.doLayout();

        txtEstado.setText(txtEstado.getText() + "\nbun000 tiene " + objeto3D.vertex_len + " puntos");
    }//GEN-LAST:event_menuBun000ActionPerformed

    private void menuBun045ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBun045ActionPerformed
        // TODO add your handling code here:
        Reader objeto3D = ((Canvas) canvas).agregarArchivo(getClass().getClassLoader().getResourceAsStream("bunny/bun045.ply"));
        JoCheckBox chckObjeto3D = new JoCheckBox("bun045", objeto3D);
        chckObjeto3D.addActionListener(crearActionCheck());
        boxCheck.add(chckObjeto3D);
        boxCheck.doLayout();

        txtEstado.setText(txtEstado.getText() + "\nbun045 tiene " + objeto3D.vertex_len + " puntos");
    }//GEN-LAST:event_menuBun045ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            // Set cross-platform Java L&F (also called "Metal")
//            UIManager.setLookAndFeel(
//                    UIManager.getCrossPlatformLookAndFeelClassName());

            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new MainJFrame().setVisible(true);
//            }
//        });
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Run the constructor
                new MainJFrame();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraProgreso;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JCheckBox cbKDTree;
    private javax.swing.JCheckBox cbKDTreeWeka;
    private javax.swing.JFileChooser jFileChooserPyl;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuAbrir;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labelEstado;
    private javax.swing.JMenuItem menuBun000;
    private javax.swing.JMenuItem menuBun045;
    private javax.swing.JScrollPane scrollXList;
    private javax.swing.JTextArea txtEstado;
    // End of variables declaration//GEN-END:variables

    /**
     * Worker
     */
    private class MySwingWorker extends SwingWorker<String, Double> {

        private final JProgressBar fProgressBar;
        private final JLabel fLabel;
        private long inicio;

        private MySwingWorker(JProgressBar aProgressBar, JLabel aLabel) {
            fProgressBar = aProgressBar;
            fLabel = aLabel;
        }

        @Override
        protected String doInBackground() throws Exception {
            inicio = System.currentTimeMillis();
            historicoICP = new ArrayList<>();

            ((Canvas) canvas).ejecutarICP(new ListenerAvance() {
                @Override
                public void avance(String text) {
                    fLabel.setText(text);
                }

                @Override
                public void avance(int iteracion) {
                    //log.info("IT: " + iteracion);
                    publish(new Double(iteracion) / 10);
                }

                @Override
                public void matrixAvance(Matrix4d matrix4d, int iteracion) {

                }

                @Override
                public void addHistorico(Map registro) {
                    historicoICP.add(registro);
                }

            }, cbKDTree.isSelected(), cbKDTreeWeka.isSelected());

            return "Finished";
        }

        @Override
        protected void process(List<Double> aDoubles) {
            //log.info("IT_B: " + aDoubles);
            //update the percentage of the progress bar that is done
            int amount = fProgressBar.getMaximum() - fProgressBar.getMinimum();
            fProgressBar.setValue((int) (fProgressBar.getMinimum() + (amount * aDoubles.get(aDoubles.size() - 1))));
        }

        @Override
        protected void done() {
            try {
                fLabel.setText(get());

                double tiempo = new Double(System.currentTimeMillis() - inicio) / 1000.0;
                txtEstado.setText(txtEstado.getText() + "\nEl modelo ICP terminó\n"
                        + "Tiempo: " + tiempo + "seg");
                
                if (historicoICP != null && !historicoICP.isEmpty()) {
                    Map ultRegistro = historicoICP.get(historicoICP.size()-1);
                    txtEstado.setText(txtEstado.getText() + 
                            "\nIteraciones: "+ultRegistro.get("iteracion")+
                            "\nUlt error: "+ultRegistro.get("error")+
                            "\nMuestra puntos: "+ultRegistro.get("muestra_puntos")+
                            "\n"
                    );
                }

                //crearCSVHistoricoICP();
            } catch (Exception e) {
                log.error("Error", e);
            }
        }
    }

    /**
     * Crear archivo csv con historico de ICP
     */
    private void crearCSVHistoricoICP() throws IOException {
        if (historicoICP != null && !historicoICP.isEmpty()) {
            String carpetaSalida="";
            String csvFile = carpetaSalida + "csv_icp_" + new Date().getTime() + ".csv";
            FileWriter writer = new FileWriter(csvFile);
            CSVUtils.writeLine(writer, Arrays.asList("Iteracion", "Error", "MuestraPtos"), ',', '"');
            
            for (Map map : historicoICP) {
                CSVUtils.writeLine(writer, Arrays.asList(map.get("iteracion")+"",map.get("error")+"",map.get("muestra_puntos")+""), ',', '"');
            }
            
            writer.flush();
            writer.close();
            
            log.warn("Se crea archivo: {}",csvFile);
        }
    }

}
