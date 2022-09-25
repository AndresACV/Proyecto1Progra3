package sucursales.presentation.sucursal;

import sucursales.Application;
import sucursales.logic.Empleado;
import sucursales.logic.Sucursal;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class View implements Observer {

    private JPanel panel;
    private JTextField codigoFld;
    private JTextField referenciaFld;

    private JButton guardarFld;
    private JButton cancelarFld;

    private JLabel codigoLbl;
    private JLabel referenciaLbl;
    private JLabel direccionLbl;
    private JLabel zonajeLbl;
    private JTextField direccionFld;
    private JTextField zonajeFld;
    private JFrame window;

    private JLabel mapaLabel;
    private Image mapa;

    private JLabel sucursalLabel;
    private Image sucursal;

    private JLabel sucursalSelLabel;
    private Image sucursalSel;

    private Graphics g;
    private  BufferedImage result;

    private int x = 0;
    private int y = 0;

    public View() throws IOException {
        guardarFld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clean();
                if (validate()) {
                    Sucursal n = null;
                    try {
                        n = take();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        controller.guardar(n);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, ex.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        cancelarFld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.hide();
                result = new BufferedImage(400,400, BufferedImage.TYPE_INT_ARGB);
                g = result.getGraphics();
                g.drawImage(mapa, 10, 10, mapaLabel);
                mapaLabel.setIcon(new ImageIcon(result));
            }
        }
        );
        mapaLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                result = new BufferedImage(400,400, BufferedImage.TYPE_INT_ARGB);
                g = result.getGraphics();
                g.drawImage(mapa, 10, 10, mapaLabel);
                mapaLabel.setIcon(new ImageIcon(result));

                x = e.getX();
                y = e.getY();

                g.drawImage(sucursal,e.getX() - 15, e.getY() - 31, mapaLabel);
                mapaLabel.setIcon(new ImageIcon(result));
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    sucursales.presentation.sucursal.Controller controller;
    sucursales.presentation.sucursal.Model model;

    public void setController(sucursales.presentation.sucursal.Controller controller) {
        this.controller = controller;
    }

    public void setModel(sucursales.presentation.sucursal.Model model) {
        this.model = model;
        model.addObserver(this);
    }

    @Override
    public void update(Observable updatedModel, Object parametros) {
        Sucursal current = model.getCurrent();
            this.codigoFld.setEnabled(model.getModo() == Application.MODO_AGREGAR);
            this.codigoFld.setText(current.getCodigo());
            referenciaFld.setText(current.getReferencia());
            direccionFld.setText(current.getDireccion());
            if(String.valueOf(current.getPorcentajeZonaje()).equals("0.0")){
                zonajeFld.setText("");
            }
            else{
                zonajeFld.setText(String.valueOf(current.getPorcentajeZonaje()));
            }

            g.drawImage(sucursal, current.getX(), current.getY(), mapaLabel);
            mapaLabel.setIcon(new ImageIcon(result));

            this.panel.validate();
    }


    public Sucursal take() {
        Sucursal s = new Sucursal();
        s.setCodigo(codigoFld.getText());
        s.setReferencia(referenciaFld.getText());
        s.setDireccion(direccionFld.getText());
        s.setPorcentajeZonaje(Double.parseDouble(zonajeFld.getText()));
        s.setX(x);
        s.setY(y);
        return s;
    }

    public void clean(){
        codigoLbl.setBorder(new EmptyBorder(0, 0, 2, 0));
        referenciaLbl.setBorder(new EmptyBorder(0, 0, 2, 0));
        direccionLbl.setBorder(new EmptyBorder(0, 0, 2, 0));
        zonajeLbl.setBorder(new EmptyBorder(0, 0, 2, 0));
    }
    private boolean validate() {
        boolean valid = true;
        String mensajeError = "";
        int concatenaciones = 0;

        if (codigoFld.getText().isEmpty()) {
            valid = false;
            codigoLbl.setBorder(Application.BORDER_ERROR);
            mensajeError += "Codigo requerido. "; concatenaciones++;
        } else if(!codigoFld.getText().matches("[0-9]+")){
            valid = false;
            codigoLbl.setBorder(Application.BORDER_ERROR);
            mensajeError += "Codigo debe ser numerico. ";        }
        else {
            codigoFld.setBorder(null);
            codigoFld.setToolTipText(null);
        }

        if (referenciaFld.getText().length() == 0) {
            valid = false;
            referenciaLbl.setBorder(Application.BORDER_ERROR);
            mensajeError += "Referencia requerida. "; concatenaciones++;
        } else if(!referenciaFld.getText().matches("^[a-z\\sA-Z]+$")){
            valid = false;
            referenciaLbl.setBorder(Application.BORDER_ERROR);
            mensajeError += "Referencia no puede ser numerica. ";
        } else {
            referenciaFld.setBorder(null);
            referenciaFld.setToolTipText(null);
        }
        if (direccionFld.getText().isEmpty()) {
            valid = false;
            direccionLbl.setBorder(Application.BORDER_ERROR);
            mensajeError += "Direccion requerida. "; concatenaciones++;
        } else {
            codigoLbl.setBorder(null);
            codigoLbl.setToolTipText(null);
        }

        if (zonajeFld.getText().isEmpty()) {
            valid = false;
            zonajeLbl.setBorder(Application.BORDER_ERROR);
            mensajeError += "Zonaje requerido. "; concatenaciones++;
        } else if(!zonajeFld.getText().matches("^[0-9]+\\.?[0-9]*$")) {
            valid = false;
            zonajeLbl.setBorder(Application.BORDER_ERROR);
            mensajeError += "Zonaje debe ser numerico. ";
        } else {
            codigoLbl.setBorder(null);
            codigoLbl.setToolTipText(null);
        }
        if(concatenaciones == 4){
            JOptionPane.showMessageDialog(panel, "Todos los campos son requeridos","ERROR",JOptionPane.ERROR_MESSAGE);
        } else if(!mensajeError.equals("")){
            JOptionPane.showMessageDialog(panel, mensajeError,"ERROR",JOptionPane.ERROR_MESSAGE);
        }
        return valid;
    }

    private void createUIComponents() throws IOException {
        // TODO: place custom component creation code here
        mapaLabel = new JLabel();
        mapa = ImageIO.read(new File("src/main/resources/MapCR.png"));
        mapa = mapa.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        result = new BufferedImage(400,400, BufferedImage.TYPE_INT_ARGB);
        g = result.getGraphics();
        g.drawImage(mapa, 10, 10, mapaLabel);
        mapaLabel.setIcon(new ImageIcon(result));

        sucursalLabel = new JLabel();
        sucursal = ImageIO.read(new File("src/main/resources/Sucursal.png"));
        sucursalLabel.setIcon(new ImageIcon(sucursal));

        sucursalSelLabel = new JLabel();
        sucursalSel = ImageIO.read(new File("src/main/resources/SucursalSel.png"));
        sucursalSelLabel.setIcon(new ImageIcon(sucursalSel));

        //        Sucursal sucursal = new Sucursal("006", "Limon", "Limon, Cahuita, Playa Negra", 4.0);
//        JLabel labelPrueba = new JLabel();
//        labelPrueba.putClientProperty(sucursal.getReferencia(), sucursal);
//        Sucursal x = (Sucursal) labelPrueba.getClientProperty("Limon");

        //  mapaLabel.putClientProperty(sucursal.getReferencia(), sucursal);
        //  mapaLabel.getClientProperty(sucursal.getReferencia());

//        g.drawImage(mapa, 30, 40,mapaLabel);
//        mapaLabel.setIcon(new ImageIcon(result));
    }
}