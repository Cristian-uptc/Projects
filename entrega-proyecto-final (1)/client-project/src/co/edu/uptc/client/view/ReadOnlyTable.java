package co.edu.uptc.client.view;

import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class ReadOnlyTable extends JTable {

    private static final long serialVersionUID = 1L;

    public ReadOnlyTable(String... columns) {
        super(new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setReorderingAllowed(false);
    }

    public void setRows(List<Object[]> rows) {
        DefaultTableModel model = (DefaultTableModel) getModel();
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    public void setSelectionListener(Runnable listener) {
        getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                listener.run();
            }
        });
    }
}
