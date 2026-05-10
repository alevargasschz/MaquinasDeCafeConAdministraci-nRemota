package gui;

import controlAlarma.ControlLogistica;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * InterfazLogistica
 *
 * GUI Swing para el tecnico de logistica. Permite:
 *   - Ver las maquinas asignadas con alarmas activas.
 *   - Ver el inventario actual de la Bodega.
 *   - Resolver alarmas con un click (retira de bodega y abastece la maquina).
 */
public class InterfazLogistica extends JFrame {

    private final ControlLogistica control;

    // Tabla de maquinas con alarmas
    private DefaultTableModel modeloMaquinas;
    private JTable            tablaMaquinas;

    // Tabla de inventario de bodega
    private DefaultTableModel modeloBodega;
    private JTable            tablaBodega;

    // Campos de resolucion manual
    private JTextField campoIdMaquina;
    private JComboBox<String> comboTipoAlarma;
    private JTextField campoIpMaquina;
    private JTextField campoPuertoMaq;
    private JTextArea  areaResultado;

    public InterfazLogistica(ControlLogistica control) {
        this.control = control;

        setTitle("Logistica — Terminal Tecnico (Op. #" + control.getIdOperador() + ")");
        setSize(720, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(6, 6));

        JSplitPane splitTablas = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                construirPanelMaquinas(),
                construirPanelBodega());
        splitTablas.setResizeWeight(0.5);

        add(splitTablas,               BorderLayout.CENTER);
        add(construirPanelResolucion(), BorderLayout.SOUTH);

        // Carga inicial
        refrescarMaquinas();
        refrescarBodega();

        setVisible(true);
    }

    // ------------------------------------------------------------------
    // Paneles de la UI
    // ------------------------------------------------------------------

    private JPanel construirPanelMaquinas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Maquinas con alarmas activas"));

        String[] cols = {"ID Maquina", "Ubicacion", "Fecha", "Tipo", "Descripcion"};
        modeloMaquinas = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaMaquinas = new JTable(modeloMaquinas);
        tablaMaquinas.setRowHeight(22);
        tablaMaquinas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Al seleccionar una fila, pre-llena el campo ID maquina
        tablaMaquinas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaMaquinas.getSelectedRow();
            if (fila >= 0) {
                Object idVal = modeloMaquinas.getValueAt(fila, 0);
                campoIdMaquina.setText(idVal.toString());

                Object tipoVal = modeloMaquinas.getValueAt(fila, 3);
                if (tipoVal != null) {
                    try {
                        seleccionarTipoAlarma(Integer.parseInt(tipoVal.toString()));
                    } catch (NumberFormatException ignore) {
                        // Se mantiene el valor manual del combo
                    }
                }
            }
        });

        JButton btnRefrescar = new JButton("Actualizar lista");
        btnRefrescar.addActionListener(e -> refrescarMaquinas());

        panel.add(new JScrollPane(tablaMaquinas), BorderLayout.CENTER);
        panel.add(btnRefrescar,                   BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelBodega() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Inventario Bodega Central"));

        String[] cols = {"Item", "Cantidad disponible"};
        modeloBodega = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaBodega = new JTable(modeloBodega);
        tablaBodega.setRowHeight(22);

        JButton btnRefrescar = new JButton("Actualizar inventario");
        btnRefrescar.addActionListener(e -> refrescarBodega());

        panel.add(new JScrollPane(tablaBodega), BorderLayout.CENTER);
        panel.add(btnRefrescar,                 BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelResolucion() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Resolver alarma"));

        // --- Campos ---
        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(3, 6, 3, 6);
        gc.anchor  = GridBagConstraints.WEST;

        // Fila 0: ID maquina
        gc.gridx = 0; gc.gridy = 0;
        campos.add(new JLabel("ID Maquina:"), gc);
        gc.gridx = 1;
        campoIdMaquina = new JTextField(6);
        campos.add(campoIdMaquina, gc);

        // Fila 0: tipo alarma
        gc.gridx = 2;
        campos.add(new JLabel("Tipo alarma:"), gc);
        gc.gridx = 3;
        comboTipoAlarma = new JComboBox<>(new String[]{
                "1 - Ingredientes",
                "2 - Moneda $100",
                "3 - Moneda $200",
                "4 - Moneda $500",
                "5 - Suministros",
                "6 - Mal funcionamiento"
        });
        campos.add(comboTipoAlarma, gc);

        // Fila 1: IP maquina
        gc.gridx = 0; gc.gridy = 1;
        campos.add(new JLabel("IP Maquina:"), gc);
        gc.gridx = 1;
        campoIpMaquina = new JTextField("localhost", 10);
        campos.add(campoIpMaquina, gc);

        // Fila 1: Puerto
        gc.gridx = 2;
        campos.add(new JLabel("Puerto:"), gc);
        gc.gridx = 3;
        campoPuertoMaq = new JTextField("12346", 6);
        campos.add(campoPuertoMaq, gc);

        // Botones
        JButton btnResolver   = new JButton("Resolver Alarma");
        JButton btnResolverCfg = new JButton("Resolver (cfg)");
        gc.gridx = 4; gc.gridy = 0;
        campos.add(btnResolver, gc);
        gc.gridy = 1;
        campos.add(btnResolverCfg, gc);

        btnResolver.addActionListener(this::accionResolver);
        btnResolverCfg.addActionListener(this::accionResolverCfg);

        // Area de resultado
        areaResultado = new JTextArea(3, 50);
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);

        panel.add(campos,                         BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultado), BorderLayout.CENTER);
        return panel;
    }

    // ------------------------------------------------------------------
    // Acciones
    // ------------------------------------------------------------------

    private void refrescarMaquinas() {
        modeloMaquinas.setRowCount(0);
        try {
            List<String> alarmas = control.getMaquinasConAlarmas();
            for (String entrada : alarmas) {
                if (entrada.contains("#")) {
                    // Formato real del servidor: id#ubicacion#fecha#idAlarma#descripcion
                    String[] partes = entrada.split("#", 5);
                    String id          = partes.length > 0 ? partes[0] : "?";
                    String ubicacion   = partes.length > 1 ? partes[1] : "(sin ubicacion)";
                    String fecha       = partes.length > 2 ? partes[2] : "-";
                    String tipoAlarma  = partes.length > 3 ? partes[3] : "-";
                    String descripcion = partes.length > 4 ? partes[4] : "-";
                    modeloMaquinas.addRow(new Object[]{id, ubicacion, fecha, tipoAlarma, descripcion});
                } else {
                    // Compatibilidad: formato antiguo id-ubicacion
                    String[] partes = entrada.split("-", 2);
                    String id  = partes.length > 0 ? partes[0] : entrada;
                    String ubi = partes.length > 1 ? partes[1] : "(sin ubicacion)";
                    modeloMaquinas.addRow(new Object[]{id, ubi, "-", "-", "-"});
                }
            }
            if (alarmas.isEmpty()) {
                mostrarResultado("Sin alarmas activas para el operador #"
                        + control.getIdOperador() + ".");
            }
        } catch (Exception ex) {
            mostrarResultado("ERROR al consultar alarmas: " + ex.getMessage());
        }
    }

    private void refrescarBodega() {
        modeloBodega.setRowCount(0);
        try {
            Map<String, Integer> inv = control.getInventarioBodega();
            inv.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> modeloBodega.addRow(new Object[]{e.getKey(), e.getValue()}));
        } catch (Exception ex) {
            mostrarResultado("ERROR al consultar bodega: " + ex.getMessage());
        }
    }

    private void accionResolver(ActionEvent e) {
        int id = parsearIdMaquina();
        if (id < 0) return;

        int tipo   = comboTipoAlarma.getSelectedIndex() + 1;
        String ip  = campoIpMaquina.getText().trim();
        int puerto;
        try {
            puerto = Integer.parseInt(campoPuertoMaq.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarResultado("ERROR: Puerto invalido.");
            return;
        }

        String resultado = control.resolverAlarma(id, tipo, ip, puerto);
        mostrarResultado(resultado);
        refrescarMaquinas();
        refrescarBodega();
    }

    private void accionResolverCfg(ActionEvent e) {
        int id = parsearIdMaquina();
        if (id < 0) return;
        int tipo = comboTipoAlarma.getSelectedIndex() + 1;
        String resultado = control.resolverAlarmaCfg(id, tipo);
        mostrarResultado(resultado);
        refrescarMaquinas();
        refrescarBodega();
    }

    private int parsearIdMaquina() {
        try {
            return Integer.parseInt(campoIdMaquina.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarResultado("ERROR: ID de maquina invalido.");
            return -1;
        }
    }

    private void mostrarResultado(String msg) {
        areaResultado.setText(msg);
    }

    private void seleccionarTipoAlarma(int tipo) {
        if (tipo >= 1 && tipo <= comboTipoAlarma.getItemCount()) {
            comboTipoAlarma.setSelectedIndex(tipo - 1);
        }
    }
}
