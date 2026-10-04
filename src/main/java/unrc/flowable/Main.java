package unrc.flowable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando motor de Flowable...");
        Map<String, Object> variables = new LinkedHashMap<>();
        BpmnProcessRunner executeProcess = new BpmnProcessRunner("processes/MultipleInstances.bpmn20.xml", new Connection(true),
                variables, "multipleinstances");
        executeProcess.run();
    }
}