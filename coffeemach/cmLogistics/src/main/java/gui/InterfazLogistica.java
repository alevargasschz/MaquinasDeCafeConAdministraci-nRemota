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
 * InterfazLogistica — CORREGIDO
 *
 * CAMBIOS EN ESTA VERSION:
 * [BUG 1] El campo IP ahora se inicializa leyendo el proxy
 *   'MaquinaCafe.Proxy' del communicator, en vez de hardcodear "localhost".
 *   Esto soluciona el error "Connection refused" del boton "Resolver Alarma".
 *
 * [MEJORA] El combo de tipo alarma ahora muestra ademas el codigo central (1-6)
 *   para que sea obvio que ese numero corresponde al "Tipo" de la tabla superior.
 *   Al seleccionar una fila de la tabla, el combo se actualiza automaticamente.
 */
public class InterfazLogistica extends JFrame {

    private final ControlLogistica control;

    private DefaultTableModel modeloMaquinas;
    private JTable            tablaMaquinas;

    private DefaultTableModel modeloBodega;
    private JTable            tablaBodega;

    private JTextField        campoIdMaquina;
    private JComboBox<String> comboTipoAlarma;
    private JTextField        campoIpMaquina;
    private JTextField        campoPuertoMaq;
    private JTextArea         areaResultado;

    // Tipos centrales en el mismo orden que el combo (indice + 1 = tipo)
    private static final String[] ETIQUETAS_ALARMA = {
            "1 - Escasez de Ingredientes",
            "2 - Moneda $100 insuficiente",
            "3 - Moneda $200 insuficiente",
            "4 - Moneda $500 insuficiente",
            "5 - Escasez de Suministros",
            "6 - Mal Funcionamiento",
            "8 - Agua bajo",
            "9 - Cafe bajo",
            "10 - Azucar bajo",
            "11 - Vasos bajo",
            "12 - Agua critico",
            "13 - Cafe critico",
            "14 - Azucar critico",
            "15 - Vasos critico"
    };

    private static final int[] TIPOS_ALARMA = {
            1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15
    };

    public InterfazLogistica(ControlLogistica control) {
        this.control = control;

        setTitle("Logistica — Terminal Tecnico (Op. #" + control.getIdOperador() + ")");
        setSize(760, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(6, 6));

        JSplitPane splitTablas = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                construirPanelMaquinas(),
                construirPanelBodega());
        splitTablas.setResizeWeight(0.5);

        add(splitTablas,               BorderLayout.CENTER);
        add(construirPanelResolucion(), BorderLayout.SOUTH);

        refrescarMaquinas();
        refrescarBodega();
        setVisible(true);
    }

    // ------------------------------------------------------------------
    // Paneles
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

        // Al seleccionar fila: pre-llena ID maquina y selecciona tipo en el combo
        tablaMaquinas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaMaquinas.getSelectedRow();
            if (fila >= 0) {
                Object idVal   = modeloMaquinas.getValueAt(fila, 0);
                Object tipoVal = modeloMaquinas.getValueAt(fila, 3);
                campoIdMaquina.setText(idVal != null ? idVal.toString() : "");
                if (tipoVal != null) {
                    try {
                        int tipo = Integer.parseInt(tipoVal.toString());
                        seleccionarTipoCentral(tipo);
                    } catch (NumberFormatException ignore) {}
                }
            }
        });

        JButton btnRefrescar = new JButton("Actualizar lista");
        btnRefrescar.addActionListener(ev -> refrescarMaquinas());

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
        btnRefrescar.addActionListener(ev -> refrescarBodega());

        panel.add(new JScrollPane(tablaBodega), BorderLayout.CENTER);
        panel.add(btnRefrescar,                 BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelResolucion() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Resolver alarma"));

        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        // Fila 0: ID maquina + tipo alarma
        gc.gridx = 0; gc.gridy = 0;
        campos.add(new JLabel("ID Maquina:"), gc);
        gc.gridx = 1;
        campoIdMaquina = new JTextField(6);
        campos.add(campoIdMaquina, gc);

        gc.gridx = 2;
        campos.add(new JLabel("Tipo alarma:"), gc);
        gc.gridx = 3;
        comboTipoAlarma = new JComboBox<>(ETIQUETAS_ALARMA);
        campos.add(comboTipoAlarma, gc);

        // Fila 1: IP + puerto  (solo para "Resolver Alarma" con IP manual)
        gc.gridx = 0; gc.gridy = 1;
        campos.add(new JLabel("IP Maquina:"), gc);
        gc.gridx = 1;
        // [CORRECCION BUG 1] Leer IP del proxy configurado en el .cfg
        campoIpMaquina = new JTextField(extraerIpDeProxy(), 12);
        campos.add(campoIpMaquina, gc);

        gc.gridx = 2;
        campos.add(new JLabel("Puerto:"), gc);
        gc.gridx = 3;
        campoPuertoMaq = new JTextField(extraerPuertoDeProxy(), 6);
        campos.add(campoPuertoMaq, gc);

        // Botones
        JButton btnResolverCfg = new JButton("Resolver (cfg)");   // recomendado
        JButton btnResolver    = new JButton("Resolver (manual)"); // con IP/puerto
        gc.gridx = 4; gc.gridy = 0;
        campos.add(btnResolverCfg, gc);
        gc.gridy = 1;
        campos.add(btnResolver, gc);

        btnResolverCfg.addActionListener(this::accionResolverCfg);
        btnResolver.addActionListener(this::accionResolver);

        // Nota de ayuda
        JLabel lblNota = new JLabel(
                "<html><i>Tip: seleccione una fila de la tabla superior para auto-rellenar ID y Tipo.</i></html>");
        lblNota.setForeground(Color.DARK_GRAY);
        lblNota.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        // Area de resultado
        areaResultado = new JTextArea(3, 50);
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(campos, BorderLayout.NORTH);
        norte.add(lblNota, BorderLayout.SOUTH);

        panel.add(norte,                          BorderLayout.NORTH);
        panel.add(new JScrollPane(areaResultado), BorderLayout.CENTER);
        return panel;
    }

    // ------------------------------------------------------------------
    // Logica de las acciones
    // ------------------------------------------------------------------

    private void refrescarMaquinas() {
        modeloMaquinas.setRowCount(0);
        try {
            List<String> alarmas = control.getMaquinasConAlarmas();
            for (String entrada : alarmas) {
                if (entrada.contains("#")) {
                    // Formato: idMaq#ubicacion#fecha#idAlarma#descripcion
                    String[] p = entrada.split("#", 5);
                    modeloMaquinas.addRow(new Object[]{
                            p.length > 0 ? p[0] : "?",
                            p.length > 1 ? p[1] : "-",
                            p.length > 2 ? p[2] : "-",
                            p.length > 3 ? p[3] : "-",
                            p.length > 4 ? p[4] : "-"
                    });
                } else {
                    // Compatibilidad formato antiguo: id-ubicacion
                    String[] p = entrada.split("-", 2);
                    modeloMaquinas.addRow(new Object[]{
                            p.length > 0 ? p[0] : entrada,
                            p.length > 1 ? p[1] : "-",
                            "-", "-", "-"
                    });
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

    private void accionResolverCfg(ActionEvent e) {
        int id = parsearIdMaquina();
        if (id < 0) return;
        int tipoCentral = tipoSeleccionado();
        String resultado = control.resolverAlarmaCfg(id, tipoCentral);
        mostrarResultado(resultado);
        refrescarMaquinas();
        refrescarBodega();
    }

    private void accionResolver(ActionEvent e) {
        int id = parsearIdMaquina();
        if (id < 0) return;
        int tipoCentral = tipoSeleccionado();
        String ip = campoIpMaquina.getText().trim();
        int puerto;
        try {
            puerto = Integer.parseInt(campoPuertoMaq.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarResultado("ERROR: Puerto invalido.");
            return;
        }
        String resultado = control.resolverAlarma(id, tipoCentral, ip, puerto);
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

    /** Selecciona en el combo el tipo central (1-6). */
    private void seleccionarTipoCentral(int tipoCentral) {
        for (int i = 0; i < TIPOS_ALARMA.length; i++) {
            if (TIPOS_ALARMA[i] == tipoCentral) {
                comboTipoAlarma.setSelectedIndex(i);
                return;
            }
        }
    }

    private int tipoSeleccionado() {
        int idx = comboTipoAlarma.getSelectedIndex();
        if (idx >= 0 && idx < TIPOS_ALARMA.length) {
            return TIPOS_ALARMA[idx];
        }
        return 1;
    }

    // ------------------------------------------------------------------
    // [CORRECCION BUG 1] Extraer IP y puerto del proxy configurado en .cfg
    // para usarlos como valor por defecto en los campos manuales.
    // ------------------------------------------------------------------

    private String extraerIpDeProxy() {
        try {
            String proxy = campoIpDesdePropiedad();
            // Formato esperado: "abastecer:tcp -h <IP> -p <puerto>"
            if (proxy != null) {
                String[] partes = proxy.split("\\s+");
                for (int i = 0; i < partes.length - 1; i++) {
                    if (partes[i].equals("-h")) return partes[i + 1];
                }
            }
        } catch (Exception ignored) {}
        return "localhost"; // fallback
    }

    private String extraerPuertoDeProxy() {
        try {
            String proxy = campoIpDesdePropiedad();
            if (proxy != null) {
                String[] partes = proxy.split("\\s+");
                for (int i = 0; i < partes.length - 1; i++) {
                    if (partes[i].equals("-p")) return partes[i + 1];
                }
            }
        } catch (Exception ignored) {}
        return "12346"; // fallback
    }

    /**
     * Lee el valor de la propiedad MaquinaCafe.Proxy del archivo .cfg
     * a traves del communicator. Retorna null si no esta disponible.
     *
     * Nota: el communicator no expone los valores de propiedades arbitrarias
     * de manera directa en Ice 3.7. Se usa la convencion de leer el proxy
     * como string para extraer IP/puerto.
     */
    private String campoIpDesdePropiedad() {
        // El communicator almacena propiedades y el proxy string completo
        // puede obtenerse via propertyToProxy -> toString en Ice 3.7
        try {
            com.zeroc.Ice.ObjectPrx prx =
                    control.getCommunicator().propertyToProxy("MaquinaCafe.Proxy");
            if (prx != null) {
                return prx.toString(); // "abastecer -t -e 1.1:tcp -h X.X.X.X -p NNNN"
            }
        } catch (Exception ignored) {}
        return null;
    }
}
