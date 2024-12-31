import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class ToDoListApp {
    public static void main(String[] args) {
        // main J frame
        JFrame frame = new JFrame("To-Do List");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // panel for the list and controls
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.decode("#add8e6")); // Light blue background

        // model and JList for the tasks
        DefaultListModel<String> taskModel = new DefaultListModel<>();
        JList<String> taskList = new JList<>(taskModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 16));
        taskList.setBackground(Color.decode("#e0f7fa")); // Slightly lighter blue
        taskList.setSelectionBackground(Color.decode("#d1e7f2")); // Highlight color
        taskList.setDragEnabled(true);
        taskList.setDropMode(DropMode.INSERT);
        taskList.setTransferHandler(new TaskTransferHandler(taskModel));

        JScrollPane scrollPane = new JScrollPane(taskList);

        // input field for adding tasks
        JTextField taskInput = new JTextField();
        taskInput.setFont(new Font("Arial", Font.PLAIN, 16));
        taskInput.setBackground(Color.decode("#d1e7f2"));

        // add button
        JButton addButton = new JButton("Add");
        addButton.setFont(new Font("Arial", Font.PLAIN, 16));
        addButton.setBackground(Color.decode("#b3e5fc"));

        // remove button
        JButton removeButton = new JButton("Remove Selected");
        removeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        removeButton.setBackground(Color.decode("#b3e5fc"));

        // panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.decode("#add8e6"));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        // add components to the main panel
        panel.add(taskInput, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // add the main panel to the frame
        frame.add(panel);

        // Action to add tasks (shared logic for button and Enter key)
        Runnable addTaskAction = () -> {
            String task = taskInput.getText().trim();
            if (!task.isEmpty()) {
                taskModel.addElement(task);
                taskInput.setText(""); // Clear input field
            }
        };

        // Add button action listener
        addButton.addActionListener(e -> addTaskAction.run());

        // Add Enter key listener to the task input field
        taskInput.addActionListener(e -> addTaskAction.run());

        // Remove button action listener
        removeButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                taskModel.remove(selectedIndex);
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }

    
    // TransferHandler for drag-and-drop functionality in the JList
    
    static class TaskTransferHandler extends TransferHandler {
        private final DataFlavor localObjectFlavor;
        private final DefaultListModel<String> listModel;
        private int fromIndex;

        public TaskTransferHandler(DefaultListModel<String> listModel) {
            this.listModel = listModel;
            this.localObjectFlavor = new DataFlavor(String.class, "String");
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<?> list = (JList<?>) c;
            fromIndex = list.getSelectedIndex();
            return new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{localObjectFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return localObjectFlavor.equals(flavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) {
                    return list.getSelectedValue();
                }
            };
        }

        @Override
        public boolean canImport(TransferSupport info) {
            return info.isDataFlavorSupported(localObjectFlavor);
        }

        @Override
        public boolean importData(TransferSupport info) {
            if (!canImport(info)) {
                return false;
            }
            try {
                JList<?> target = (JList<?>) info.getComponent();
                int toIndex = ((JList.DropLocation) info.getDropLocation()).getIndex();
                String draggedItem = (String) info.getTransferable().getTransferData(localObjectFlavor);
                if (fromIndex != -1 && toIndex != fromIndex) {
                    listModel.remove(fromIndex);
                    listModel.add(toIndex > fromIndex ? toIndex - 1 : toIndex, draggedItem);
                }
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }
    }
}
