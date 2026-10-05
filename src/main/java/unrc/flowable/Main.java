package unrc.flowable;

import org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntityImpl;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        FlowableManager flowableManager = FlowableManager.createFromXmlConfig();
        try {
          flowableManager.printTimeStatsForProcessId("2505");
        } finally {
            flowableManager.closeEngine();
        }
    }
}