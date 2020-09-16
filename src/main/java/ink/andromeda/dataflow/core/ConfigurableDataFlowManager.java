package ink.andromeda.dataflow.core;


import com.google.gson.reflect.TypeToken;
import ink.andromeda.dataflow.util.ConfigValidationException;
import ink.andromeda.dataflow.util.JSONValidator;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static ink.andromeda.dataflow.util.GeneralTools.GSON;

@Slf4j
public abstract class ConfigurableDataFlowManager implements DataFlowManager {

    protected final Map<String, List<DataFlow>> flowMap = new ConcurrentHashMap<>();

    @Override
    public List<DataFlow> getFlow() {
        return null;
    }

    @Override
    public List<DataFlow> getFlow(String source, String schema, String name) {
        return null;
    }

    public DataFlow getFlow(String flowName) {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public void reload(String source, String schema, String name) {

    }

    @Override
    public void reload(String flowName) {

    }

    /**
     * 获取所有的flow配置
     * get all flow config
     *
     * @return JSON形式的配置
     */
    protected abstract List<Map<String, Object>> getFlowConfig();

    /**
     * 获取某个namespace下的flow配置
     *
     * @param source 源名称
     * @param schema 库名称
     * @param name   表名称
     * @return JSON形式的配置
     */
    protected abstract List<Map<String, Object>> getFlowConfig(String source, String schema, String name);

    /**
     * 根据flowName获取flow配置
     *
     * @param flowName flow名称
     * @return JSON配置
     */
    protected abstract Map<String, Object> getFlowConfig(String flowName);

    /**
     * 根据namespace批量添加flow配置
     *
     * @param configs JSON配置内容
     * @return 新增条数
     */
    protected abstract int addFlowConfig(List<Map<String, Object>> configs);

    /**
     * 新增一个flow配置
     *
     * @param config flow配置
     * @return 新增条数
     */
    protected abstract int addFlowConfig(Map<String, Object> config);

    /**
     * 更新一个flow配置
     *
     * @param flowName flow名称
     * @param update   新的flow配置
     * @return 更新条数
     */
    protected abstract int updateFlowConfig(String flowName, Map<String, Object> update);

    /**
     * 删除给定namespace下的flow配置
     *
     * @param source 源名称
     * @param schema 库名称
     * @param name   表名称
     * @return 删除数量
     */
    protected abstract int deleteFlowConfig(String source, String schema, String name);

    /**
     * 删除flow配置
     *
     * @param flowName flow名称
     * @return 删除数量
     */
    protected abstract int deleteFlowConfig(String flowName);

    /**
     * 为flow新增一个节点配置
     *
     * @param flowName   flow名称
     * @param nodeConfig 新节点配置
     * @return 新增节点数
     */
    protected abstract int addNodeConfig(String flowName, Map<String, Object> nodeConfig);

    /**
     * 更新flow节点配置
     *
     * @param flowName flow名称
     * @param nodeName 节点名称
     * @param update   该节点新配置
     * @return 更新的节点数量
     */
    protected abstract int updateNodeConfig(String flowName, String nodeName, Map<String, Object> update);

    /**
     * 更新指定位置的flow节点配置
     * 默认不可用
     *
     * @param flowName  flow名称
     * @param nodeIndex 节点位置
     * @param update    该节点新配置
     * @return 更新数量
     */
    protected int updateNodeConfig(String flowName, int nodeIndex, Map<String, Object> update) {
        throw new UnsupportedOperationException();
    }

    /**
     * 删除一个flow节点配置
     * 默认不可用
     *
     * @param flowName flow名称
     * @param nodeName 节点名称
     * @return 删除的额节点个数
     */
    protected abstract int deleteNodeConfig(String flowName, String nodeName);

    /**
     * 删除一个flow指定位置的节点配置
     *
     * @param flowName  flow名称
     * @param nodeIndex 节点位置
     * @return 删除的节点数量
     */
    protected int deleteNodeConfig(String flowName, int nodeIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取一个节点的配置
     *
     * @param flowName flow名称
     * @param nodeName 节点名称
     * @return 节点配置
     */
    protected Map<String, Object> getNodeConfig(String flowName, String nodeName) {
        //noinspection unchecked
        return Optional.ofNullable((List<Map<String, Object>>) getFlowConfig(flowName).get("execution_chain"))
                .orElse(Collections.emptyList())
                .stream()
                .filter(m -> Objects.equals(m.get("node_name"), nodeName))
                .findFirst()
                .orElse(null);
    }

    private JSONValidator flowConfigvalidator;

    public ConfigurableDataFlowManager() {
        try {
            String str = Files.lines(
                    Paths.get(this.getClass().getResource("/config-regular/flow_config_regular.json").toURI()))
                    .collect(Collectors.joining());
            Map<String, Object> template = GSON().fromJson(str, new TypeToken<Map<String, Object>>() {
            }.getType());
            flowConfigvalidator = new JSONValidator(template);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    /**
     * flow配置校验
     *
     * @param flowConfig flow配置
     * @throws ConfigValidationException 校验未通过
     */
    public void validateFlowConfig(Map<String, Object> flowConfig) throws ConfigValidationException {
        Objects.requireNonNull(flowConfigvalidator, "validator is null");
        Map<String, String> errors = flowConfigvalidator.validate(flowConfig);
        if (!errors.isEmpty()) {
            throw new ConfigValidationException(errors);
        }
    }

}