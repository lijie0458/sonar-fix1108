package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.util.List;

public class ProcInstGraph {
    public String procInstId;
    public List<Element> elements;
    public List<Flow> flows;
    public double diagramBeginX;
    public double diagramBeginY;
    public double diagramWidth;
    public double diagramHeight;

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows(List<Flow> flows) {
        this.flows = flows;
    }

    public double getDiagramBeginX() {
        return diagramBeginX;
    }

    public void setDiagramBeginX(double diagramBeginX) {
        this.diagramBeginX = diagramBeginX;
    }

    public double getDiagramBeginY() {
        return diagramBeginY;
    }

    public void setDiagramBeginY(double diagramBeginY) {
        this.diagramBeginY = diagramBeginY;
    }

    public double getDiagramWidth() {
        return diagramWidth;
    }

    public void setDiagramWidth(double diagramWidth) {
        this.diagramWidth = diagramWidth;
    }

    public double getDiagramHeight() {
        return diagramHeight;
    }

    public void setDiagramHeight(double diagramHeight) {
        this.diagramHeight = diagramHeight;
    }

    public static class Element {
        public boolean current;
        public boolean completed;
        public String name;
        public String title;
        public String type;
        public List<String> incomingFlows;
        public List<String> outcomingFlows;
        public List<CompleteInfo> completeInfos;
        public double x;
        public double y;
        public double width;
        public double height;

        public boolean isCurrent() {
            return current;
        }

        public void setCurrent(boolean current) {
            this.current = current;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getIncomingFlows() {
            return incomingFlows;
        }

        public void setIncomingFlows(List<String> incomingFlows) {
            this.incomingFlows = incomingFlows;
        }

        public List<String> getOutcomingFlows() {
            return outcomingFlows;
        }

        public void setOutcomingFlows(List<String> outcomingFlows) {
            this.outcomingFlows = outcomingFlows;
        }

        public List<CompleteInfo> getCompleteInfos() {
            return completeInfos;
        }

        public void setCompleteInfos(List<CompleteInfo> completeInfos) {
            this.completeInfos = completeInfos;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }

    public static class Flow {
        public boolean current;
        public boolean completed;
        public String name;
        public String title;
        public String type;
        public String sourceRef;
        public String targetRef;
        public List<WayPoint> wayPoints;

        public boolean isCurrent() {
            return current;
        }

        public void setCurrent(boolean current) {
            this.current = current;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSourceRef() {
            return sourceRef;
        }

        public void setSourceRef(String sourceRef) {
            this.sourceRef = sourceRef;
        }

        public String getTargetRef() {
            return targetRef;
        }

        public void setTargetRef(String targetRef) {
            this.targetRef = targetRef;
        }

        public List<WayPoint> getWayPoints() {
            return wayPoints;
        }

        public void setWayPoints(List<WayPoint> wayPoints) {
            this.wayPoints = wayPoints;
        }
    }

    public static class WayPoint {
        public double x;
        public double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }

    public static class CompleteInfo {
        public String assignee;
        public String completeTime;
        public boolean completed;
        public List<String> candidates;
        public String addSignTag;

        public String getAssignee() {
            return assignee;
        }

        public void setAssignee(String assignee) {
            this.assignee = assignee;
        }

        public String getCompleteTime() {
            return completeTime;
        }

        public void setCompleteTime(String completeTime) {
            this.completeTime = completeTime;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public List<String> getCandidates() {
            return candidates;
        }

        public void setCandidates(List<String> candidates) {
            this.candidates = candidates;
        }

        public String getAddSignTag() {
            return addSignTag;
        }

        public void setAddSignTag(String addSignTag) {
            this.addSignTag = addSignTag;
        }
    }
}
