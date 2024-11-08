package com.dogfood.aa20240808.processV2.system.controller;

import com.dogfood.aa20240808.processV2.system.controller.dto.*;
import com.dogfood.aa20240808.processV2.system.domain.structure.TaskCommentReq;
import com.dogfood.aa20240808.processV2.system.exception.ProcessException;
import com.dogfood.aa20240808.processV2.system.service.logic.SystemProcessHandlingLogicService;
import com.dogfood.aa20240808.processV2.system.service.logic.SystemProcessInitiationLogicService;
import com.dogfood.aa20240808.processV2.system.service.logic.SystemProcessInstanceSearchLogicService;
import com.dogfood.aa20240808.processV2.system.service.logic.SystemTaskInstanceSearchLogicService;
import com.dogfood.aa20240808.web.ApiReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

@RestController
@RequestMapping("/api/system/processV2")
public class SystemProcessLogicController {

    @Autowired
    private SystemProcessInitiationLogicService systemProcessInitiationLogicService;

    @Autowired
    private SystemTaskInstanceSearchLogicService systemTaskInstanceSearchLogicService;

    @Autowired
    private SystemProcessInstanceSearchLogicService systemProcessInstanceSearchLogicService;

    @Autowired
    private SystemProcessHandlingLogicService systemProcessHandlingLogicService;

    @PostMapping("/getMyPendingTasks")
    public ApiReturn getMyPendingTasks(@RequestBody GetMyPendingTaskDto getMyPendingTaskDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getMyPendingTasks(getMyPendingTaskDto.getProcDefKey(),
                    getMyPendingTaskDto.getProcInstStartTimeAfter(), getMyPendingTaskDto.getProcInstStartTimeBefore(), getMyPendingTaskDto.getProcInstInitiator(),
                    getMyPendingTaskDto.getPage(), getMyPendingTaskDto.getSize(), getMyPendingTaskDto.getSearch()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getMyCompletedTasks")
    public ApiReturn getMyCompletedTasks(@RequestBody GetMyCompletedTaskDto getMyCompletedTaskDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getMyCompletedTasks(getMyCompletedTaskDto.getProcDefKey(),
                    getMyCompletedTaskDto.getProcInstStartTimeAfter(), getMyCompletedTaskDto.getProcInstStartTimeBefore(), getMyCompletedTaskDto.getProcInstInitiator(),
                    getMyCompletedTaskDto.getPage(), getMyCompletedTaskDto.getSize(), getMyCompletedTaskDto.getSearch()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getMyInitiatedTasks")
    public ApiReturn getMyInitiatedTasks(@RequestBody GetMyInitiatedTaskDto getMyInitiateTaskDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getMyInitiatedTasks(getMyInitiateTaskDto.getProcDefKey(),
                    getMyInitiateTaskDto.getProcInstStartTimeAfter(), getMyInitiateTaskDto.getProcInstStartTimeBefore(), getMyInitiateTaskDto.getProcInstInitiator(),
                    getMyInitiateTaskDto.getPage(), getMyInitiateTaskDto.getSize(), getMyInitiateTaskDto.getSearch()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getMyCCTasks")
    public ApiReturn getMyCCTasks(@RequestBody GetMyCCTaskDto getMyCCTaskDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getMyCCTasks(getMyCCTaskDto.getProcDefKey(),
                    getMyCCTaskDto.getProcInstStartTimeAfter(), getMyCCTaskDto.getProcInstStartTimeBefore(), getMyCCTaskDto.getProcInstInitiator(),
                    getMyCCTaskDto.getViewed(), getMyCCTaskDto.getPage(), getMyCCTaskDto.getSize(), getMyCCTaskDto.getSearch()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getTaskDestinationUrl")
    public ApiReturn getTaskDestinationUrl(@RequestBody GetTaskDestinationUrlDto getTaskDestinationUrlDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getTaskDestinationUrl(getTaskDestinationUrlDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getProcDefInfos")
    public ApiReturn getProcDefInfos(@RequestBody GetProcDefListDto getProcDefListDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getProcDefInfos(getProcDefListDto.getSearch()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getProcInstInitiators")
    public ApiReturn getProcInstInitiators() {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getProcInstInitiators());
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getProcInstInfo")
    public ApiReturn getProcInstInfo(@RequestBody GetProcInstInfoDto getProcInstInfoDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getProcInstInfo(getProcInstInfoDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getProcInstInfoByInstId")
    public ApiReturn getProcInstInfoByInstId(@RequestBody GetProcInstInfoByInstIdDto getProcInstInfoByInstIdDto) {
        try {
            return ApiReturn.of(systemProcessInstanceSearchLogicService.getProcInstInfoByInstId(getProcInstInfoByInstIdDto.getProcInstId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/approveTask")
    public ApiReturn approveTask(@RequestBody ApproveTaskDto approveTaskDto) {
        try {
            systemProcessHandlingLogicService.approveTask(approveTaskDto.getData(), approveTaskDto.getProcDefKey(), approveTaskDto.getTaskId(),
                    approveTaskDto.getComment());
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/rejectTask")
    public ApiReturn rejectTask(@RequestBody RejectTaskDto rejectTaskDto) {
        try {
            systemProcessHandlingLogicService.rejectTask(rejectTaskDto.getData(), rejectTaskDto.getProcDefKey(), rejectTaskDto.getTaskId(),
                    rejectTaskDto.getComment());
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/submitTask")
    public ApiReturn submitTask(@RequestBody SubmitTaskDto submitTaskDto) {
        try {
            systemProcessHandlingLogicService.submitTask(submitTaskDto.getData(), submitTaskDto.getProcDefKey(), submitTaskDto.getTaskId());
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("reassignTask")
    public ApiReturn reassignTask(@RequestBody ReassignTaskDto reassignTaskDto) {
        try {
            systemProcessHandlingLogicService.reassignTask(reassignTaskDto.getTaskId(), reassignTaskDto.getUserForReassign());
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/launchProcess")
    public ApiReturn launchProcess(@RequestBody LaunchProcessDTO launchProcessDTO) {
        try {
            return ApiReturn.of(systemProcessInitiationLogicService.launchProcess(launchProcessDTO.getData(), launchProcessDTO.getProcDefKey()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getTaskOperationPermissions")
    public ApiReturn getTaskOperationPermissions(@RequestBody GetTaskOptPermissionDto getTaskOptPermissionDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getTaskOperationPermissions(getTaskOptPermissionDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getProcInstGraph")
    public ApiReturn getProcInstGraph(@RequestBody GetProcInstGraphDto getProcInstGraphDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getProcInstGraph(getProcInstGraphDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getTaskFormDef")
    public ApiReturn getTaskFormDef(@RequestBody GetTaskFormDefDto getTaskFormDefDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getTaskFormDef(getTaskFormDefDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getDataPropertyPermissions")
    public ApiReturn getDataPropertyPermissions(@RequestBody GetDataPropertiesPermissionDto getDataPropertiesPermissionDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getDataPropertyPermissions(getDataPropertiesPermissionDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
    @PostMapping("/getFormDataPropertyPermissions")
    public ApiReturn getFormDataPermissions(@RequestBody GetDataPropertiesPermissionDto getDataPropertiesPermissionDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getFormDataPropertyPermissions(getDataPropertiesPermissionDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getTaskData")
    public ApiReturn getTaskData(@RequestBody GetTaskDataLogicDto getTaskDataLogicDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getTaskDataByFronted(getTaskDataLogicDto.getTaskId(), getTaskDataLogicDto.getProcDefKey()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getProcVariable")
    public ApiReturn getProcVariable(@RequestBody GetProcVariableDto getProcVariableDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getProcVariable(getProcVariableDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/withdrawTask")
    public ApiReturn withdrawTask(@RequestBody WithdrawTaskDto withdrawTaskDto) {
        try {
            systemProcessHandlingLogicService.withdrawTask(withdrawTaskDto.getTaskId());
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/revertTask")
    public ApiReturn revertTask(@RequestBody RevertTaskDto revertTaskDto) {
        try {
            systemProcessHandlingLogicService.revertTask(revertTaskDto.getTaskId());
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/terminateProcess")
    public ApiReturn terminateProcess(@RequestBody TerminateProcessDto terminateProcessDto) {
        try {
            systemProcessHandlingLogicService.terminateProcess(terminateProcessDto.getProcInstId());
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getProcInstRecordsByInstId")
    public ApiReturn getProcInstRecordsByInstId(@RequestBody GetProcInstRecordsByInstIdDto getProcInstRecordsByInstIdDto) {
        try {
            return ApiReturn.of(systemProcessInstanceSearchLogicService.getProcInstRecordsByInstId(getProcInstRecordsByInstIdDto.getProcInstId(), getProcInstRecordsByInstIdDto.getPage(), getProcInstRecordsByInstIdDto.getSize()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getProcInstRecords")
    public ApiReturn getProcInstRecords(@RequestBody GetProcInstRecordsDto getProcInstRecordsDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getProcInstRecords(getProcInstRecordsDto.getTaskId(), getProcInstRecordsDto.getPage(), getProcInstRecordsDto.getSize()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getUsersForReassign")
    public ApiReturn getUsersForReassign(@RequestBody GetUsersForReassignDto getUsersForReassignDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getUsersForReassign(getUsersForReassignDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/addSignTask")
    public ApiReturn addSignTask(@RequestBody AddSignTaskDto addSignTaskDto) {
        try {
            if (StringUtils.isNotBlank(addSignTaskDto.getPolicyForAddSign())) {
                systemProcessHandlingLogicService.addSignTask(addSignTaskDto.getTaskId(), addSignTaskDto.getUserForAddSign(),addSignTaskDto.getPolicyForAddSign());
            }else {
                systemProcessHandlingLogicService.addSignTask(addSignTaskDto.getTaskId(), addSignTaskDto.getUserForAddSign());
            }
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getUsersForAddSign")
    public ApiReturn getUsersForAddSign(@RequestBody GetUsersForAddSignDto getUsersForAddSignDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getUsersForAddSign(getUsersForAddSignDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getTaskInfo")
    public ApiReturn getTask(@RequestBody GetTaskDataDto getTaskDataDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getTaskInfo(getTaskDataDto.getTaskId()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/viewCCTask")
    public ApiReturn viewCCTask(@RequestBody ViewCCTaskDto viewCCTaskDto) {
        try {
            systemProcessHandlingLogicService.viewCCTask(viewCCTaskDto.getTaskId());
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getTaskInstanceList")
    public ApiReturn getTaskInstanceList(@RequestBody GetTaskListDto getTaskListDto) {
        try {
            return ApiReturn.of(systemTaskInstanceSearchLogicService.getTaskInstanceList(getTaskListDto.getProcDefKey(), getTaskListDto.getProcInstId(), getTaskListDto.getProcInstInitiator(), getTaskListDto.getUserId(), getTaskListDto.getFinished(), getTaskListDto.getPage(), getTaskListDto.getSize()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/getProcessInstanceList")
    public ApiReturn getProcessInstanceList(@RequestBody GetProcessInstanceListDto getProcessInstanceListDto) {
        try {
            return ApiReturn.of(systemProcessInstanceSearchLogicService.getProcessInstanceList(getProcessInstanceListDto.getProcessInstanceId(), getProcessInstanceListDto.getStartBy(),
                getProcessInstanceListDto.getStartTimeBefore(),getProcessInstanceListDto.getStartTimeAfter(),getProcessInstanceListDto.getFinished(),
                getProcessInstanceListDto.getProcessDefName(), getProcessInstanceListDto.getPage(), getProcessInstanceListDto.getSize()));
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/addTaskComment")
    public ApiReturn addTaskComment(@RequestBody TaskCommentReq commentReq) {
        try {
            systemTaskInstanceSearchLogicService.addTaskComment(commentReq.getCurNode(),commentReq.getUserName(), commentReq.getRecordCreatedTime(),
                    commentReq.getAction(), commentReq.getMessage(),commentReq.getTaskId());
            return ApiReturn.of(null);
        } catch (ProcessException e) {
            return ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
}