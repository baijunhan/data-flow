package ink.andromeda.dataflow.server.canal.table;

import lombok.Data;

import java.util.List;

@Data
public class TableRow {

    List<TableField> row;

    List<TableField> beforeRow;

}