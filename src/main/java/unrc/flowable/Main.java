package unrc.flowable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando motor de Flowable en Main...");
        
        FlowableManager flowableManager = FlowableManager.createWithMemoryEngine();
        
        try {
            flowableManager.registerBPMN("processes/MultipleInstances.bpmn20.xml");
            
            Map<String, Object> variables = new LinkedHashMap<>();
            flowableManager.startInstance("multipleinstances", variables);
            
            flowableManager.getTaskService().createTaskQuery().list().forEach(task -> {
                System.out.println("Tarea pendiente: " + task.getName());
            });

        } finally {
            flowableManager.closeEngine();
        }
    }
}