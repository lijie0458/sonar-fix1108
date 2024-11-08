package com.dogfood.aa20240808.domain.structure.apis.codewave2024; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ResultStructure {

    public String nuimsDomain;

    public String kubeInsightAppCluster;

    public String officialTenantId;

    public Boolean quickInstallSupport;

    public Boolean enableOpenAiCompletionForLogic;

    public PwdSecurityStructure pwdSecurity;

    public Long tenantAndAppMaxLength;

    public Boolean platformCreateDatabaseOpen;

    public DocumentCenterStructure documentCenter;

    public Boolean nuimsFrontendActive;

    public Boolean lcpRedirectSwitch;

    public String kubeInsightAddress;

    public String gatewayType;

    public String STATIC_URL;

    public Boolean traceSwitch;

    public String resourceAssignStrategy;

    public Boolean creatorIsManager;

    public Boolean ipAccessSupport;

    public String websqlSwitch;

    public Boolean enableCodeWaveMarket;

    public String platformEnv;

    public String IDE_STATIC_URL;

    public String isPrivatized;

    public String clientToken;

    public String appDomainPrefixSeparator;

    public Boolean biConfig;

    public String platformExtraAbilityDetail;

    public String USER_STATIC_URL;

    public String kubeInsightServiceName;

    public String consoleDomain;

    public Boolean enableOpenAiForLogic;

    public String lowcodeDomain;

    public String name;

    public Boolean tenantIntegrationSwitch;

    public EnvNuimsDomainStructure envNuimsDomain;

    public EnvLcpDomainStructure envLcpDomain;

    public String kubeClientApplicationKey;

    public Boolean actionContextSwitch;

    public String getNuimsDomain() {
        return nuimsDomain;
    } 

    public void setNuimsDomain(String nuimsDomain) {
        this.nuimsDomain = nuimsDomain; 
    } 

    public String getKubeInsightAppCluster() {
        return kubeInsightAppCluster;
    } 

    public void setKubeInsightAppCluster(String kubeInsightAppCluster) {
        this.kubeInsightAppCluster = kubeInsightAppCluster; 
    } 

    public String getOfficialTenantId() {
        return officialTenantId;
    } 

    public void setOfficialTenantId(String officialTenantId) {
        this.officialTenantId = officialTenantId; 
    } 

    public Boolean getQuickInstallSupport() {
        return quickInstallSupport;
    } 

    public void setQuickInstallSupport(Boolean quickInstallSupport) {
        this.quickInstallSupport = quickInstallSupport; 
    } 

    public Boolean getEnableOpenAiCompletionForLogic() {
        return enableOpenAiCompletionForLogic;
    } 

    public void setEnableOpenAiCompletionForLogic(Boolean enableOpenAiCompletionForLogic) {
        this.enableOpenAiCompletionForLogic = enableOpenAiCompletionForLogic; 
    } 

    public PwdSecurityStructure getPwdSecurity() {
        return pwdSecurity;
    } 

    public void setPwdSecurity(PwdSecurityStructure pwdSecurity) {
        this.pwdSecurity = pwdSecurity; 
    } 

    public Long getTenantAndAppMaxLength() {
        return tenantAndAppMaxLength;
    } 

    public void setTenantAndAppMaxLength(Long tenantAndAppMaxLength) {
        this.tenantAndAppMaxLength = tenantAndAppMaxLength; 
    } 

    public Boolean getPlatformCreateDatabaseOpen() {
        return platformCreateDatabaseOpen;
    } 

    public void setPlatformCreateDatabaseOpen(Boolean platformCreateDatabaseOpen) {
        this.platformCreateDatabaseOpen = platformCreateDatabaseOpen; 
    } 

    public DocumentCenterStructure getDocumentCenter() {
        return documentCenter;
    } 

    public void setDocumentCenter(DocumentCenterStructure documentCenter) {
        this.documentCenter = documentCenter; 
    } 

    public Boolean getNuimsFrontendActive() {
        return nuimsFrontendActive;
    } 

    public void setNuimsFrontendActive(Boolean nuimsFrontendActive) {
        this.nuimsFrontendActive = nuimsFrontendActive; 
    } 

    public Boolean getLcpRedirectSwitch() {
        return lcpRedirectSwitch;
    } 

    public void setLcpRedirectSwitch(Boolean lcpRedirectSwitch) {
        this.lcpRedirectSwitch = lcpRedirectSwitch; 
    } 

    public String getKubeInsightAddress() {
        return kubeInsightAddress;
    } 

    public void setKubeInsightAddress(String kubeInsightAddress) {
        this.kubeInsightAddress = kubeInsightAddress; 
    } 

    public String getGatewayType() {
        return gatewayType;
    } 

    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType; 
    } 

    public String getSTATIC_URL() {
        return STATIC_URL;
    } 

    public void setSTATIC_URL(String STATIC_URL) {
        this.STATIC_URL = STATIC_URL; 
    } 

    public Boolean getTraceSwitch() {
        return traceSwitch;
    } 

    public void setTraceSwitch(Boolean traceSwitch) {
        this.traceSwitch = traceSwitch; 
    } 

    public String getResourceAssignStrategy() {
        return resourceAssignStrategy;
    } 

    public void setResourceAssignStrategy(String resourceAssignStrategy) {
        this.resourceAssignStrategy = resourceAssignStrategy; 
    } 

    public Boolean getCreatorIsManager() {
        return creatorIsManager;
    } 

    public void setCreatorIsManager(Boolean creatorIsManager) {
        this.creatorIsManager = creatorIsManager; 
    } 

    public Boolean getIpAccessSupport() {
        return ipAccessSupport;
    } 

    public void setIpAccessSupport(Boolean ipAccessSupport) {
        this.ipAccessSupport = ipAccessSupport; 
    } 

    public String getWebsqlSwitch() {
        return websqlSwitch;
    } 

    public void setWebsqlSwitch(String websqlSwitch) {
        this.websqlSwitch = websqlSwitch; 
    } 

    public Boolean getEnableCodeWaveMarket() {
        return enableCodeWaveMarket;
    } 

    public void setEnableCodeWaveMarket(Boolean enableCodeWaveMarket) {
        this.enableCodeWaveMarket = enableCodeWaveMarket; 
    } 

    public String getPlatformEnv() {
        return platformEnv;
    } 

    public void setPlatformEnv(String platformEnv) {
        this.platformEnv = platformEnv; 
    } 

    public String getIDE_STATIC_URL() {
        return IDE_STATIC_URL;
    } 

    public void setIDE_STATIC_URL(String IDE_STATIC_URL) {
        this.IDE_STATIC_URL = IDE_STATIC_URL; 
    } 

    public String getIsPrivatized() {
        return isPrivatized;
    } 

    public void setIsPrivatized(String isPrivatized) {
        this.isPrivatized = isPrivatized; 
    } 

    public String getClientToken() {
        return clientToken;
    } 

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken; 
    } 

    public String getAppDomainPrefixSeparator() {
        return appDomainPrefixSeparator;
    } 

    public void setAppDomainPrefixSeparator(String appDomainPrefixSeparator) {
        this.appDomainPrefixSeparator = appDomainPrefixSeparator; 
    } 

    public Boolean getBiConfig() {
        return biConfig;
    } 

    public void setBiConfig(Boolean biConfig) {
        this.biConfig = biConfig; 
    } 

    public String getPlatformExtraAbilityDetail() {
        return platformExtraAbilityDetail;
    } 

    public void setPlatformExtraAbilityDetail(String platformExtraAbilityDetail) {
        this.platformExtraAbilityDetail = platformExtraAbilityDetail; 
    } 

    public String getUSER_STATIC_URL() {
        return USER_STATIC_URL;
    } 

    public void setUSER_STATIC_URL(String USER_STATIC_URL) {
        this.USER_STATIC_URL = USER_STATIC_URL; 
    } 

    public String getKubeInsightServiceName() {
        return kubeInsightServiceName;
    } 

    public void setKubeInsightServiceName(String kubeInsightServiceName) {
        this.kubeInsightServiceName = kubeInsightServiceName; 
    } 

    public String getConsoleDomain() {
        return consoleDomain;
    } 

    public void setConsoleDomain(String consoleDomain) {
        this.consoleDomain = consoleDomain; 
    } 

    public Boolean getEnableOpenAiForLogic() {
        return enableOpenAiForLogic;
    } 

    public void setEnableOpenAiForLogic(Boolean enableOpenAiForLogic) {
        this.enableOpenAiForLogic = enableOpenAiForLogic; 
    } 

    public String getLowcodeDomain() {
        return lowcodeDomain;
    } 

    public void setLowcodeDomain(String lowcodeDomain) {
        this.lowcodeDomain = lowcodeDomain; 
    } 

    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name; 
    } 

    public Boolean getTenantIntegrationSwitch() {
        return tenantIntegrationSwitch;
    } 

    public void setTenantIntegrationSwitch(Boolean tenantIntegrationSwitch) {
        this.tenantIntegrationSwitch = tenantIntegrationSwitch; 
    } 

    public EnvNuimsDomainStructure getEnvNuimsDomain() {
        return envNuimsDomain;
    } 

    public void setEnvNuimsDomain(EnvNuimsDomainStructure envNuimsDomain) {
        this.envNuimsDomain = envNuimsDomain; 
    } 

    public EnvLcpDomainStructure getEnvLcpDomain() {
        return envLcpDomain;
    } 

    public void setEnvLcpDomain(EnvLcpDomainStructure envLcpDomain) {
        this.envLcpDomain = envLcpDomain; 
    } 

    public String getKubeClientApplicationKey() {
        return kubeClientApplicationKey;
    } 

    public void setKubeClientApplicationKey(String kubeClientApplicationKey) {
        this.kubeClientApplicationKey = kubeClientApplicationKey; 
    } 

    public Boolean getActionContextSwitch() {
        return actionContextSwitch;
    } 

    public void setActionContextSwitch(Boolean actionContextSwitch) {
        this.actionContextSwitch = actionContextSwitch; 
    } 

}
