package com.dogfood.aa20240808.processV2.system.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.dogfood.aa20240808.processV2.system.domain.structure.ProcInstGraph;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class SystemProcessUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
    }

    public static ZonedDateTime DateToZonedDateTime(Date date){
        if (date == null) return null;
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date ZonedDateTimeToDate(ZonedDateTime zonedDateTime){
        if (zonedDateTime == null) return null;
        return Date.from(zonedDateTime.toInstant());
    }

    public static List<String> removeComma(String userList){
        if(StringUtils.isBlank(userList))   return Collections.emptyList();
        return Arrays.asList(userList.split(","));
    }

    public static List<String> removeComma(List<String> userList){
        if(CollectionUtils.isEmpty(userList))    return Collections.emptyList();
        List<String> result = new ArrayList<>();
        userList.forEach(user -> result.addAll(Arrays.asList(user.split(","))));
        return result;
    }

    public static String getFrontendKey(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(Objects.isNull(requestAttributes)){
            return "";
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String frontendKey = request.getHeader("LCAP-FRONTEND");
        if (StringUtils.isEmpty(frontendKey)) {
            return "default";
        }
        return frontendKey;
    }

    public static void processProcessElements(BpmnModel pojoModel, ProcInstGraph displayNode, GraphicInfo diagramInfo, Set<String> currentElements) {
        if (pojoModel.getLocationMap().isEmpty())
            return;

        List<ProcInstGraph.Element> elements = new ArrayList<>();
        List<ProcInstGraph.Flow> flows = new ArrayList<>();

        diagramInfo.setX(9999);
        diagramInfo.setY(1000);

        for (Process process : pojoModel.getProcesses()) {
            processElements(process.getFlowElements(), pojoModel, elements, flows, diagramInfo, currentElements);
        }

        displayNode.setElements(elements);
        displayNode.setFlows(flows);
    }

    private static void processElements(Collection<FlowElement> elementList, BpmnModel model,
                                        List<ProcInstGraph.Element> elementArray, List<ProcInstGraph.Flow> flowArray,
                                        GraphicInfo diagramInfo, Set<String> currentElements) {

        String processUniqueKey = model.getMainProcess().getId();
        for (FlowElement element : elementList) {
            //将发起任务产生的开始节点（_Start）、顺序流（_Start_to_InitiateTask）、默认流（_DefaultFlow）过滤
            if (element.getId().equalsIgnoreCase(processUniqueKey + "_Start")
                    || element.getId().equalsIgnoreCase(processUniqueKey + "_Start_to_InitiateTask")
                    || element.getId().endsWith("_DefaultFlow")) continue;

            if (element instanceof SequenceFlow) {
                ProcInstGraph.Flow flowNode = new ProcInstGraph.Flow();
                if (currentElements != null) {
                    flowNode.setCurrent(currentElements.contains(element.getId()) || currentElements.contains(element.getId() + "_DefaultFlow"));
                }
                SequenceFlow flow = (SequenceFlow) element;
                String elementTitle = element.getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_ELEMENT_TITLE);
                flowNode.setTitle(elementTitle);
                flowNode.setType("sequenceFlow");
                flowNode.setSourceRef(flow.getSourceRef());
                flowNode.setTargetRef(flow.getTargetRef());
                flowNode.setName(flow.getId());

                List<GraphicInfo> flowInfo = model.getFlowLocationGraphicInfo(flow.getId());
                List<ProcInstGraph.WayPoint> wayPoints = new ArrayList<>();
                for (GraphicInfo graphicInfo : flowInfo) {
                    ProcInstGraph.WayPoint wayPoint = new ProcInstGraph.WayPoint();
                    wayPoint.setX(graphicInfo.getX());
                    wayPoint.setY(graphicInfo.getY());
                    wayPoints.add(wayPoint);
                    fillDiagramInfo(graphicInfo, diagramInfo);
                }
                flowNode.setWayPoints(wayPoints);
                flowArray.add(flowNode);

            } else {
                ProcInstGraph.Element elementNode = new ProcInstGraph.Element();
                if (currentElements != null) {
                    elementNode.setCurrent(currentElements.contains(element.getId()));
                }
                String elementTitle = element.getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_ELEMENT_TITLE);
                elementNode.setName(element.getId());
                elementNode.setTitle(elementTitle);

                if (element instanceof FlowNode) {
                    FlowNode flowNode = (FlowNode) element;
                    List<String> incomingFlows = new ArrayList<>();
                    for (SequenceFlow flow : flowNode.getIncomingFlows()) {
                        if (flow.getId().endsWith("_DefaultFlow")) continue;
                        incomingFlows.add(flow.getId());

                    }
                    List<String> outcomingFlows = new ArrayList<>();
                    for (SequenceFlow flow : flowNode.getOutgoingFlows()) {
                        if (flow.getId().endsWith("_DefaultFlow")) continue;
                        outcomingFlows.add(flow.getId());

                    }
                    elementNode.setIncomingFlows(incomingFlows);
                    elementNode.setOutcomingFlows(outcomingFlows);
                }

                GraphicInfo graphicInfo = model.getGraphicInfo(element.getId());
                if (graphicInfo != null) {
                    elementNode.setX(graphicInfo.getX());
                    elementNode.setY(graphicInfo.getY());
                    elementNode.setHeight(graphicInfo.getHeight());
                    elementNode.setWidth(graphicInfo.getWidth());
                    fillDiagramInfo(graphicInfo, diagramInfo);
                }

                String className = element.getClass().getSimpleName();
                elementNode.setType(className);

                if (element instanceof UserTask) {
                    UserTask userTask = (UserTask) element;
                    elementNode.setType(userTask.getCategory());
                }
                elementArray.add(elementNode);
            }
        }
    }

    private static void fillDiagramInfo(GraphicInfo graphicInfo, GraphicInfo diagramInfo) {
        double rightX = graphicInfo.getX() + graphicInfo.getWidth();
        double bottomY = graphicInfo.getY() + graphicInfo.getHeight();
        double middleX = graphicInfo.getX() + (graphicInfo.getWidth() / 2);
        if (middleX < diagramInfo.getX()) {
            diagramInfo.setX(middleX);
        }
        if (graphicInfo.getY() < diagramInfo.getY()) {
            diagramInfo.setY(graphicInfo.getY());
        }
        if (rightX > diagramInfo.getWidth()) {
            diagramInfo.setWidth(rightX);
        }
        if (bottomY > diagramInfo.getHeight()) {
            diagramInfo.setHeight(bottomY);
        }
    }

    public static Map<String, Object> dataToMap(Object data) {
        return objectMapper.convertValue(data, new TypeReference<Map<String, Object>>() {});
    }

    public static <T> T mapToData(Map<String, Object> data, Class <T> clazz) {
        return objectMapper.convertValue(data, clazz);
    }
}