package ink.andromeda.dataflow.core.converter;

import ink.andromeda.dataflow.core.Registry;
import ink.andromeda.dataflow.core.SpringELExpressionService;
import ink.andromeda.dataflow.core.SourceEntity;
import ink.andromeda.dataflow.core.TransferEntity;
import ink.andromeda.dataflow.core.converter.configuarion.SpringELConfigurationResolver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * 配置化的转换器
 */
@Slf4j
public class ConfigurableDataConverter implements DataConverter {

    private final Registry<SpringELConfigurationResolver> convertResolverRegistry;

    private final Registry<SpringELConfigurationResolver> exportResolverRegistry;

    private final SpringELExpressionService expressionService;

    @Setter
    @Getter
    private Map<String, Object> config;

    public ConfigurableDataConverter(Registry<SpringELConfigurationResolver> convertResolverRegistry,
                                     Registry<SpringELConfigurationResolver> exportResolverRegistry,
                                     SpringELExpressionService expressionService) {
        this.convertResolverRegistry = convertResolverRegistry;
        this.exportResolverRegistry = exportResolverRegistry;

        this.expressionService = expressionService;
    }

    @Override
    @Nullable
    public TransferEntity convert(SourceEntity sourceEntity, TransferEntity transferEntity) {
        for (SpringELConfigurationResolver resolver : convertResolverRegistry.get()) {
            try {
                resolver.resolve(sourceEntity, transferEntity, config.get(resolver.getName()),
                        expressionService.evaluationContext(), expressionService.expressionParser());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transferEntity;
    }

    @Override
    public int export(SourceEntity sourceEntity, TransferEntity transferEntity) {
        for (SpringELConfigurationResolver resolver : exportResolverRegistry.get()) {
            try {
                resolver.resolve(sourceEntity, transferEntity, config.get(resolver.getName()),
                        expressionService.evaluationContext(), expressionService.expressionParser());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
