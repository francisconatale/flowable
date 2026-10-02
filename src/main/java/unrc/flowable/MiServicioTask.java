package unrc.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class MiServicioTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("\n================================================");
        System.out.println("¡Ejecutando mi tarea de Flowable con Java Puro!");
        System.out.println("================================================\n");
        
        // Ejemplo: Leer una variable del proceso si existe
        String variable = (String) execution.getVariable("miVariable");
        if (variable != null) {
            System.out.println("Variable recibida: " + variable);
        }
        
        // Ejemplo: Escribir una variable en el proceso
        execution.setVariable("resultado", "Procesado correctamente");
    }
}
