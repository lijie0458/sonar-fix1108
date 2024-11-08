package com.dogfood.aa20240808.processV2.system.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.eventbus.EventBus;
import com.dogfood.aa20240808.context.UserContext;
import com.dogfood.aa20240808.processV2.system.event.ProcessRecordEvent;
import com.dogfood.aa20240808.processV2.system.util.SystemProcessUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class ProcessRecordAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRecordAspect.class);
    private static final Map<ProcessRecordAction, parseDataCmd> parseCmdMap = new HashMap<>();

    protected static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    EventBus eventBus;

    static {
        parseCmdMap.put(ProcessRecordAction.LAUNCH,
                ProcessRecordAspect::parseDataToLaunchEvent);
        parseCmdMap.put(ProcessRecordAction.APPROVE,
                ProcessRecordAspect::parseDataToApproveTaskInstanceEvent);
        parseCmdMap.put(ProcessRecordAction.REJECT,
                ProcessRecordAspect::parseDataToRejectTaskInstanceEvent);
        parseCmdMap.put(ProcessRecordAction.SUBMIT,
                ProcessRecordAspect::parseDataToSubmitTaskInstanceEvent);
        parseCmdMap.put(ProcessRecordAction.REASSIGN,
                ProcessRecordAspect::parseDataToReassignTaskInstanceEvent);
        parseCmdMap.put(ProcessRecordAction.REVERT,
                ProcessRecordAspect::parseDataToRevertTaskInstanceEvent);
        parseCmdMap.put(ProcessRecordAction.WITHDRAW,
                ProcessRecordAspect::parseDataToWithdrawTaskInstanceEvent);
        parseCmdMap.put(ProcessRecordAction.TERMINATE,
                ProcessRecordAspect::parseDataToTerminateProcessInstanceEvent);
        parseCmdMap.put(ProcessRecordAction.ADDSIGN,
                ProcessRecordAspect::parseDataToAddSignTaskInstanceEvent);
    }

    @Pointcut(value = "@annotation(com.dogfood.aa20240808.processV2.system.aspect.ProcessRecord)")
    public void processRecordPoint(){}

    @Around(value = "processRecordPoint()&&@annotation(comment)")
    public Object afterReturning(ProceedingJoinPoint proceedingJoinPoint, ProcessRecord comment) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        Object result = proceedingJoinPoint.proceed();

        parseDataCmd cmd = parseCmdMap.get(comment.action());
        if(cmd != null){
            ProcessRecordEvent event = cmd.execute(args, result, UserContext.getCurrentUserInfo().getUserName());
            if(event != null){
                eventBus.post(event);
                LOGGER.info("Post event {}", event);
            }
        }
        return result;
    }

    private interface parseDataCmd {
        ProcessRecordEvent execute(Object[] args, Object result, String userName);
    }

    private static ProcessRecordEvent parseDataToLaunchEvent(Object[] args, Object result, String userName) {
        ProcessRecordEvent event = new ProcessRecordEvent();
        event.setAction(ProcessRecordAction.LAUNCH.value());
        event.setUserName(userName);
        event.setRecordCreatedTime(new Date());
        if (result instanceof String) {
            event.setProcessInstanceId((String) result);
        }
        String fullMessage = getFullMessage(args);
        event.setFullMessage(fullMessage);
        return event;
    }

    private static ProcessRecordEvent parseDataToApproveTaskInstanceEvent(Object[] args, Object result, String userName) {
        ProcessRecordEvent event = new ProcessRecordEvent();
        event.setTaskId((String) args[2]);
        event.setAction(ProcessRecordAction.APPROVE.value());
        event.setUserName(userName);
        event.setRecordCreatedTime(new Date());
        String message = (String) args[3];
        event.setMessage(message);
        String fullMessage = getFullMessage(args);
        event.setFullMessage(fullMessage);
        return event;
    }

    private static ProcessRecordEvent parseDataToRejectTaskInstanceEvent(Object[] args, Object result, String userName) {
        ProcessRecordEvent event = new ProcessRecordEvent();
        event.setTaskId((String) args[2]);
        event.setAction(ProcessRecordAction.REJECT.value());
        event.setUserName(userName);
        event.setRecordCreatedTime(new Date());
        String message = (String) args[3];
        event.setMessage(message);
        String fullMessage = getFullMessage(args);
        event.setFullMessage(fullMessage);
        return event;
    }

    private static ProcessRecordEvent parseDataToSubmitTaskInstanceEvent(Object[] args, Object result, String userName) {
        ProcessRecordEvent event = new ProcessRecordEvent();
        event.setTaskId((String) args[2]);
        event.setAction(ProcessRecordAction.SUBMIT.value());
        event.setUserName(userName);
        event.setRecordCreatedTime(new Date());
        event.setMessage(null);
        String fullMessage = getFullMessage(args);
        event.setFullMessage(fullMessage);
        return event;
    }

    private static ProcessRecordEvent parseDataToReassignTaskInstanceEvent(Object[] args, Object result, String userName) {
        ProcessRecordEvent event = new ProcessRecordEvent();
        event.setTaskId((String) args[0]);
        event.setAction(ProcessRecordAction.REASSIGN.value());
        event.setUserName(userName);
        event.setRecordCreatedTime(new Date());
        event.setMessage(null);
        event.setFullMessage(null);
        return event;
    }

    private static ProcessRecordEvent parseDataToRevertTaskInstanceEvent(Object[] args, Object result, String userName) {
        ProcessRecordEvent event = new ProcessRecordEvent();
        event.setTaskId((String) args[0]);
        event.setAction(ProcessRecordAction.REVERT.value());
        event.setUserName(userName);
        event.setRecordCreatedTime(new Date());
        event.setMessage(null);
        event.setFullMessage(null);
        return event;
    }

    private static ProcessRecordEvent parseDataToWithdrawTaskInstanceEvent(Object[] args, Object result, String userName) {
        ProcessRecordEvent event = new ProcessRecordEvent();
        event.setTaskId((String) args[0]);
        event.setAction(ProcessRecordAction.WITHDRAW.value());
        event.setUserName(userName);
        event.setRecordCreatedTime(new Date());
        event.setMessage(null);
        event.setFullMessage(null);
        return event;
    }

    private static ProcessRecordEvent parseDataToTerminateProcessInstanceEvent(Object[] args, Object result, String userName) {
        ProcessRecordEvent event = new ProcessRecordEvent();
        event.setProcessInstanceId((String) args[0]);
        event.setAction(ProcessRecordAction.TERMINATE.value());
        event.setUserName(userName);
        event.setRecordCreatedTime(new Date());
        event.setMessage(null);
        event.setFullMessage(null);
        return event;
    }

    private static ProcessRecordEvent parseDataToAddSignTaskInstanceEvent(Object[] args, Object result, String userName) {
        ProcessRecordEvent event = new ProcessRecordEvent();
        event.setTaskId((String) args[0]);
        event.setAction(ProcessRecordAction.ADDSIGN.value());
        event.setUserName(userName);
        event.setRecordCreatedTime(new Date());
        event.setMessage(null);
        event.setFullMessage(null);
        return event;
    }

    private static String getFullMessage(Object[] args) {
        mapper.registerModule(new JavaTimeModule());
        String fullMessage = null;
        try {
            if (args[0] == null ) {
                return null;
            }
            LOGGER.info("args[0] is {}", args[0]);
            Map<String, Object> data = SystemProcessUtil.dataToMap(args[0]);
            LOGGER.info("data is {}", data);
            fullMessage = mapper.writeValueAsString(data);
            LOGGER.info("fullMessage is {}", fullMessage);
        } catch (Exception e) {
            LOGGER.error("Parse data to json error", e);
        }
        return fullMessage;
    }

}
