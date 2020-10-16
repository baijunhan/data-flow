package ink.andromeda.dataflow.core.node.resolver;

import ink.andromeda.dataflow.core.SourceEntity;
import ink.andromeda.dataflow.core.SpringELExpressionService;
import ink.andromeda.dataflow.core.TransferEntity;
import ink.andromeda.dataflow.core.mq.KafkaInstance;
import ink.andromeda.dataflow.core.mq.MessageQueueContainer;
import ink.andromeda.dataflow.core.mq.RocketInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

import static ink.andromeda.dataflow.util.GeneralTools.GSON;

@Slf4j
public class ExportToKafkaResolver extends DefaultConfigurationResolver {

    private final MessageQueueContainer messageQueueContainer;

    public ExportToKafkaResolver(SpringELExpressionService expressionService,
                                 MessageQueueContainer messageQueueContainer) {
        super(expressionService);
        this.messageQueueContainer = messageQueueContainer;
    }

    @Override
    public String getName() {
        return "export_to_kafka";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void resolve(SourceEntity source, TransferEntity input, TransferEntity target, Object config, Map<String, Object> rootData) throws Exception {
        if (config == null) return;
        checkConfigType(config, Map.class, "Map<String, Object>");
        Map<String, Object> data = input.getData();
        String dataExpression;
        if ((dataExpression = (String) ((Map<?, ?>) config).get("data")) != null) {
            data = Objects.requireNonNull((Map<String, Object>) expressionService.executeExpression(dataExpression, Map.class));
        }
        String sendData = GSON().toJson(data);

        String topic = Objects.requireNonNull((String) ((Map<?, ?>) config).get("topic"));
        String mqName = Objects.requireNonNull((String) ((Map<?, ?>) config).get("mq_name"));
        String mqType = (String) ((Map<String, Object>) config).getOrDefault("mq_type", "kafka");
        mqType = mqType.toLowerCase();


        switch (mqType) {
            case "kafka": {
                KafkaInstance instance =
                        Objects.requireNonNull(messageQueueContainer.getByName(mqName, KafkaInstance.class),
                                String.format("not found mq instance: name=%s, type=%s", mqName, mqType));
                Future<RecordMetadata> send = instance.send(topic, sendData);
                RecordMetadata recordMetadata = send.get();
                log.info("send kafka message success, offset: {}", recordMetadata.offset());
                break;
            }
            case "rocket": {
                RocketInstance instance = Objects.requireNonNull(
                        messageQueueContainer.getByName(mqName, RocketInstance.class),
                        String.format("not found mq instance: name=%s, type=%s", mqName, mqType)
                );
                String tag = (String) ((Map<?, ?>) config).get("tag");
                Message message = new Message();
                message.setBody(sendData.getBytes(StandardCharsets.UTF_8));
                message.setTopic(topic);
                message.setTags(tag);
                SendResult send = instance.send(message);
                log.info("send rocket message success, {}", send);
                break;
            }
            case "rabbit": {
                throw new UnsupportedOperationException("rabbit mq not support for the moment");
            }
            default:
                throw new IllegalArgumentException("unknown mq type: " + mqType);
        }

    }
}
