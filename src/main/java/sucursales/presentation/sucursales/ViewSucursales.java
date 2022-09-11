package sucursales.presentation.sucursales;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class ViewSucursales implements Observer {

    private JPanel panel;
    private JTextField direccionFld;
    private JButton buscarFld;
    private JLabel direccionLbl;
    private JButton agregarFld;
    private JTable sucursalesFld;

    ControllerSucursales controllerSucursales;
    ModelSucursales modelSucursales;

    public ViewSucursales() {
        buscarFld.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controllerSucursales.buscar(direccionFld.getText());
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setController(ControllerSucursales controllerSucursales) {
        this.controllerSucursales = controllerSucursales;
    }

    public void setModel(ModelSucursales modelSucursales) {
        this.modelSucursales = modelSucursales;
        modelSucursales.addObserver(this);
    }

    @Override
    public void update(Observable updatedModel, Object parametros) {
        int[] cols = {TableModelSucursales.CODIGO, TableModelSucursales.REFERENCIA, TableModelSucursales.DIRECCION, TableModelSucursales.ZONAJE};
        sucursalesFld.setModel(new TableModelSucursales(cols, modelSucursales.getSucursales()));
        sucursalesFld.setRowHeight(30);
        this.panel.revalidate();
    }

}