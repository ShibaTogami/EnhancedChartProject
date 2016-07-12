package cp.bean;

import cp.ejb.TareaFacade;
import cp.entity.Tarea;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;
 
@ManagedBean(name="chartViewBean")
@RequestScoped
public class ChartViewBean implements Serializable {

    
    @EJB
    private TareaFacade tareaFacade;
 
    private LineChartModel animatedModel1;
    private BarChartModel animatedModel2;
    protected int tareasPorHacer;
    protected int tareasEnProceso;
    protected int tareasHecho;
    protected int tareasTesteando;
 
    @PostConstruct
    public void init() {
        createAnimatedModels();
    }
 
    public LineChartModel getAnimatedModel1() {
        return animatedModel1;
    }
 
    public BarChartModel getAnimatedModel2() {
        return animatedModel2;
    }
 
    public int maximo() {
        int array[] = {tareasPorHacer, tareasEnProceso, tareasHecho, tareasTesteando};
        int res = array[0];
        for(int i = 1; i < 4; i++) {
            if(res < array[i]) {
                res = array[i];
            }
        }
        return res;
    }
    
    private void createAnimatedModels() {
        animatedModel1 = initLinearModel();
        animatedModel1.setTitle("Line Chart");
        animatedModel1.setAnimate(true);
        animatedModel1.setLegendPosition("se");
        Axis yAxis = animatedModel1.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(10);
         
        animatedModel2 = initBarModel();
        animatedModel2.setTitle("Nº Tareas por estado");
        animatedModel2.setAnimate(true);
        animatedModel2.setLegendPosition("ne");
        
        yAxis = animatedModel2.getAxis(AxisType.Y);
        yAxis.setMin(0);
        
        yAxis.setMax(maximo() + 1);
        yAxis.setTickFormat("%d");
        
    }
     
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
        
        FacesContext context2 = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context2.getExternalContext().getSession(true);
        BigDecimal id =(BigDecimal) session.getAttribute("proyectoSeleccionado");
        
        ChartSeries tareas = new ChartSeries();
        tareasPorHacer = this.tareaFacade.findByEstado("Por hacer", id).size();
        tareasEnProceso = this.tareaFacade.findByEstado("En proceso", id).size();
        tareasHecho = this.tareaFacade.findByEstado("Hecho", id).size();
        tareasTesteando = this.tareaFacade.findByEstado("Testeando", id).size();
        
        tareas.setLabel("Nº tareas");
        
        tareas.set("Por Hacer", tareasPorHacer);
        tareas.set("En proceso", tareasEnProceso);
        tareas.set("Hecho", tareasHecho);
        tareas.set("Testeando", tareasTesteando);

        model.addSeries(tareas);         
        return model;
    }
     
    private LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();
 
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Series 1");
 
        series1.set(1, 2);
        series1.set(2, 1);
        series1.set(3, 3);
        series1.set(4, 6);
        series1.set(5, 8);
 
        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("Series 2");
 
        series2.set(1, 6);
        series2.set(2, 3);
        series2.set(3, 2);
        series2.set(4, 7);
        series2.set(5, 9);
 
        model.addSeries(series1);
        model.addSeries(series2);
         
        return model;
    }
    
}