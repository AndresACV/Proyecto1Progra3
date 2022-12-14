package sucursales.logic;

import sucursales.data.Data;
import sucursales.data.XmlPersister;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Service {

    private static Service instance;
    private static XmlPersister persister;
    private Data data;

    public static Service instance() {
        if (instance == null) {
            instance = new Service();
            persister = new XmlPersister("data.xml");
        }
        return instance;
    }

    private Service(){
        try{ data = XmlPersister.instance().load(); }
        catch(Exception e){ data = new Data(); }
    }

    public Data getData() {
        return data;
    }

    public void store(){
        try { XmlPersister.instance().store(data); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public List<Empleado> empleadosSearch(String filtro) {
        return data.getEmpleados().stream()
                .filter(e -> e.getNombre().contains(filtro))
                .sorted(Comparator.comparing(Empleado::getCedula))
                .collect(Collectors.toList());
    }

    public List<Sucursal> sucursalesSearch(String filtro) {
        return data.getSucursales().stream()
                .filter(e -> e.getReferencia().contains(filtro))
                .sorted(Comparator.comparing(Sucursal::getReferencia))
                .collect(Collectors.toList());
    }

    public Empleado empleadoGet(String cedula) { return data.getEmpleados().stream().filter(e -> e.getCedula().equals(cedula)).findFirst().orElse(null); }
    public Sucursal sucursalGet(String referencia) { return data.getSucursales().stream().filter(e -> e.getReferencia().equals(referencia)).findFirst().orElse(null); }

    public void agregarEmpleado(Empleado empleado) throws Exception {
        Empleado result = data.getEmpleados().stream().filter(e -> e.getCedula().equals(empleado.getCedula())).findFirst().orElse(null);
        if (result == null) {
            data.getEmpleados().add(empleado);
            this.store();
        } else {
            throw new Exception("Empleado ya existe");
        }
    }

    public void agregarSucursal(Sucursal sucursal) throws Exception {
        Sucursal result = data.getSucursales().stream().filter(e -> e.getCodigo().equals(sucursal.getCodigo())).findFirst().orElse(null);
        if (result == null) {
            data.getSucursales().add(sucursal);
            this.store();
        } else {
            throw new Exception("Sucursal ya existe");
        }
    }

    public void empleadoUpdate(Empleado empleado) throws Exception {
        Empleado result;
        try {
            result = this.empleadoGet(empleado.cedula);
            data.getEmpleados().remove(result);
            data.getEmpleados().add(empleado);
            this.store();
        } catch (Exception e) {
            throw new Exception("Empleado no existe");
        }
    }

    public void sucursalUpdate(Sucursal sucursal) throws Exception {
        Sucursal result;
        try {
            result = this.sucursalGet(sucursal.referencia);
            data.getSucursales().remove(result);
            data.getSucursales().add(sucursal);
            this.store();
        } catch (Exception e) {
            throw new Exception("Sucursal no existe");
        }
    }

    public List<Empleado> eliminarEmpleado(String nombre) throws Exception {
        for (int i = 0; i < data.getEmpleados().size(); i++) {
            if (Objects.equals(data.getEmpleados().get(i).getNombre(), nombre)) {
                data.getEmpleados().remove(i);
                this.store();
                return data.getEmpleados();
            }
        }
        throw new Exception("Empleado no existe");
    }

    public List<Sucursal> eliminarSucursal(String referencia) throws Exception {
        for (int i = 0; i < data.getSucursales().size(); i++) {
            if (Objects.equals(data.getSucursales().get(i).getReferencia(), referencia)) {
                for (int j = 0; j < data.getEmpleados().size(); j++) {
                    if(Objects.equals(data.getEmpleados().get(j).getSucursal().referencia, referencia)){
                        throw new Exception("Sucursal tiene empleados");
                    }
                }
                data.getSucursales().remove(i);
                persister.store(getData());
                return data.getSucursales();
            }
        }
        throw new Exception("Sucursal no existe");
    }
}