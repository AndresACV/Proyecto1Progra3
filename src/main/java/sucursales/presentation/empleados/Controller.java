package sucursales.presentation.empleados;

import sucursales.Application;
import sucursales.logic.Empleado;
import sucursales.logic.Service;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.util.List;

public class Controller {

    View view;
    Model model;

    public Controller(View view, Model model) {
        model.setEmpleados(Service.instance().empleadosSearch(""));
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
    }

    public void buscar(String filtro){
        List<Empleado> rows = Service.instance().empleadosSearch(filtro);
        model.setEmpleados(rows);
        model.commit();
    }

    public void eliminar(String cedula) throws Exception {
        List<Empleado> rows = Service.instance().eliminarEmpleado(cedula);
        model.setEmpleados(rows);
        this.buscar("");
        model.commit();
    }

    public void preAgregar(){
        Application.controllerEmpleado.preAgregar();
    }

    public void editar(int row){
        String cedula = model.getEmpleados().get(row).getCedula();
        Empleado e=null;
        try {
            e= Service.instance().empleadoGet(cedula);
            Application.controllerEmpleado.editar(e);
        } catch (Exception ex) {}
    }

    private Cell getCell( Paragraph paragraph,TextAlignment alignment,boolean hasBorder) {
        Cell cell = new Cell().add(paragraph);
        cell.setPadding(0);
        cell.setTextAlignment(alignment);
        if(!hasBorder) cell.setBorder(Border.NO_BORDER);
        return cell;
    }

    private Cell getCell( Image image,HorizontalAlignment alignment,boolean hasBorder) {
        Cell cell = new Cell().add(image);
        image.setHorizontalAlignment(alignment);
        cell.setPadding(0);
        if(!hasBorder) cell.setBorder(Border.NO_BORDER);
        return cell;
    }

    public void imprimir()throws Exception{
        String dest="empleados.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(10, 10, 10, 10);

        Table header = new Table(1);
        Image image = new Image(ImageDataFactory.create("src/main/resources/CompanyLogo.jpg"));
        image = image.scaleToFit(300, 300);

        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("SECR: Sucursales y Empleados Costa Rica").setFont(font).setBold().setFontSize(20f), TextAlignment.CENTER,false));
        header.addCell(getCell(image, HorizontalAlignment.CENTER,false));

        document.add(header);

        document.add(new Paragraph(""));document.add(new Paragraph(""));

        Color bkg = ColorConstants.BLUE;
        Color frg= ColorConstants.BLACK;
        Table body = new Table(7);
        body.setWidth(500);
        body.setHorizontalAlignment(HorizontalAlignment.CENTER);
        body.addCell(getCell(new Paragraph("Cedula").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("Nombre").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("Telefono").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("Salario").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("Sucursal").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("%Zonaje").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("Sal. Total").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));

        for(Empleado e: model.getEmpleados()){
            body.addCell(getCell(new Paragraph(e.getCedula()),TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(e.getNombre()),TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(e.getTelefono()),TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.getSalarioBase())),TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(e.getSucursal().getReferencia()),TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.getSucursal().getPorcentajeZonaje())),TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.getSalarioTotal())),TextAlignment.CENTER,true));
        }
        document.add(body);
        document.close();
    }

}