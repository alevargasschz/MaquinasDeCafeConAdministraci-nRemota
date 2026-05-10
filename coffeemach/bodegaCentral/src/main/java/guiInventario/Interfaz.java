package guiInventario;

import bodega.ServicioBodegaImp;
import mantenimientoExistencias.Inventario;
import mantenimientoExistencias.InventarioImp;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Interfaz
 *
 * Panel de administracion de la Bodega Central.
 * Permite al administrador:
 *   - Ver el inventario completo en tiempo real.
 *   - Retirar o abastecer un item manualmente.
 *   - Reabastecimiento en bloque (ingredientes / monedas / suministros).
 */
public class Interfaz extends JFrame {

    private final ServicioBodegaImp bodegaLogic;
    private final Inventario      inventario;

    // Tabla de inventario
    private DefaultTableModel tableModel;
    private JTable            tabla;

    // Campos para operacion manual
    private JTextField campoItem;
    private JTextField campoCantidad;

    // Etiqueta de estado
    private JLabel etiquetaEstado;

    public Interfaz(ServicioBodegaImp logic) {
        this.bodegaLogic = logic;
        this.inventario  = new InventarioImp(logic);

        setTitle("Bodega Central — Panel de Administracion");
        setSize(600, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(construirPanelTabla(),       BorderLayout.CENTER);
        add(construirPanelOperaciones(), BorderLayout.SOUTH);

        actualizarTabla();
        setVisible(true);
    }

    // ------------------------------------------------------------------
    // Construccion de la UI
    // ------------------------------------------------------------------

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Inventario actual"));

        String[] columnas = {"Item", "Cantidad"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(tableModel);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);

        JButton btnRefrescar = new JButton("Actualizar");
        btnRefrescar.addActionListener(e -> actualizarTabla());

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(btnRefrescar,            BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelOperaciones() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // --- Fila superior: operacion manual ---
        JPanel filaManual = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaManual.setBorder(new TitledBorder("Operacion manual"));

        campoItem     = new JTextField(12);
        campoCantidad = new JTextField(6);

        JButton btnRetirar    = new JButton("Retirar");
        JButton btnAbastecer  = new JButton("Abastecer");

        btnRetirar.addActionListener(e -> ejecutarOperacion(false));
        btnAbastecer.addActionListener(e -> ejecutarOperacion(true));

        filaManual.add(new JLabel("Item:"));
        filaManual.add(campoItem);
        filaManual.add(new JLabel("Cantidad:"));
        filaManual.add(campoCantidad);
        filaManual.add(btnRetirar);
        filaManual.add(btnAbastecer);

        // --- Fila inferior: reabastecimiento en bloque ---
        JPanel filaBloque = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaBloque.setBorder(new TitledBorder("Reabastecimiento en bloque"));

        JButton btnIngredientes = new JButton("Abastecer Ingredientes");
        JButton btnMonedas      = new JButton("Abastecer Monedas");
        JButton btnSuministros  = new JButton("Abastecer Suministros");

        btnIngredientes.addActionListener(e -> {
            inventario.abastecerIngredientes();
            actualizarTabla();
            mostrarEstado("Ingredientes reabastecidos.");
        });
        btnMonedas.addActionListener(e -> {
            inventario.abastecerMonedas();
            actualizarTabla();
            mostrarEstado("Monedas reabastecidas.");
        });
        btnSuministros.addActionListener(e -> {
            inventario.abastecerSuministros();
            actualizarTabla();
            mostrarEstado("Suministros reabastecidos.");
        });

        filaBloque.add(btnIngredientes);
        filaBloque.add(btnMonedas);
        filaBloque.add(btnSuministros);

        // --- Etiqueta de estado ---
        etiquetaEstado = new JLabel(" ");
        etiquetaEstado.setForeground(new Color(0, 100, 0));
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));

        panel.add(filaManual,     BorderLayout.NORTH);
        panel.add(filaBloque,     BorderLayout.CENTER);
        panel.add(etiquetaEstado, BorderLayout.SOUTH);
        return panel;
    }

    // ------------------------------------------------------------------
    // Logica de la GUI
    // ------------------------------------------------------------------

    /** Refresca la tabla con los valores actuales del inventario. */
    public void actualizarTabla() {
        tableModel.setRowCount(0);
        Map<String, Integer> datos = bodegaLogic.getInventarioCompleto();
        datos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> tableModel.addRow(new Object[]{e.getKey(), e.getValue()}));
    }

    /** Ejecuta retirar (abastecer=false) o abastecer (abastecer=true). */
    private void ejecutarOperacion(boolean abastecer) {
        String item = campoItem.getText().trim();
        String cantStr = campoCantidad.getText().trim();

        if (item.isEmpty() || cantStr.isEmpty()) {
            mostrarError("Ingrese el nombre del item y la cantidad.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            mostrarError("La cantidad debe ser un numero entero positivo.");
            return;
        }

        if (abastecer) {
            bodegaLogic.abastecerExistencia(item, cantidad);
            mostrarEstado("Abastecidos " + cantidad + " de '" + item + "'.");
        } else {
            bodegaLogic.retirarExistencias(item, cantidad);
            mostrarEstado("Retirados " + cantidad + " de '" + item + "'.");
        }
        actualizarTabla();
    }

    private void mostrarEstado(String msg) {
        etiquetaEstado.setForeground(new Color(0, 100, 0));
        etiquetaEstado.setText(msg);
    }

    private void mostrarError(String msg) {
        etiquetaEstado.setForeground(Color.RED);
        etiquetaEstado.setText("Error: " + msg);
    }
}
