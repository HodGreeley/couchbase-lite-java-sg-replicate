package com.couchbase.lite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static com.couchbase.lite.Runtime.mapper;

public class DBExplorer {
    private static final String PRIMARY_URL = "http://localhost:4984/" + DBService.DATABASE;
    private static final String BACKUP_URL = "http://localhost:5984/" + DBService.DATABASE;
    private JPanel explorerView;
    private JButton toggleReplication;
    private JTextArea editor;
    private JButton saveButton;
    private JButton exitButton;
    private JTextArea primaryChanges;
    private JTextArea backupChanges;

    private Database db = DBService.getInstance().getDatabase();

    private SGMonitor primaryMonitor = new SGMonitor(PRIMARY_URL, primaryChanges);
    private SGMonitor backupMonitor = new SGMonitor(BACKUP_URL, backupChanges);

    private DBExplorer() {
        toggleReplication.addActionListener(new replicationButtonClicked());
        saveButton.addActionListener(new saveButtonClicked());
        exitButton.addActionListener(new exitButtonClicked());

        primaryMonitor.execute();
        backupMonitor.execute();
    }

    private class replicationButtonClicked implements ActionListener {
        private boolean replicating = false;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!replicating) {
                try {
                    DBService.getInstance().startReplication(new URL(PRIMARY_URL), true);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }

                toggleReplication.setText("Stop");
            }
            else {
                DBService.getInstance().stopReplication();

                toggleReplication.setText("Start");
            }

            replicating = !replicating;
        }
    }

    private class saveButtonClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Map<String, Object> properties = null;

            try {
                properties = mapper.readValue(editor.getText(), Map.class);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            Document document = db.createDocument();

            try {
                document.putProperties(properties);
            } catch (CouchbaseLiteException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class exitButtonClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Component component = (Component) e.getSource();
            JFrame frame = (JFrame) SwingUtilities.getRoot(component);
            frame.dispose();
        }
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("DBExplorer");
        frame.setContentPane(new DBExplorer().explorerView);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
