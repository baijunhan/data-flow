package ink.andromeda.dataflow.canal.table;

import lombok.Data;

@Data
public class TableEntry {

    CanalMetaInfo header;

    TableRowChange rowChange;

}