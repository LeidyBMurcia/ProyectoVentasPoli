import java.io.*;
import java.util.*;

/**
 * Clase principal encargada del procesamiento de archivos de ventas,
 * consolidacion de informacion y generacion de reportes finales.
 */
public class ProcessSales {

    // Estructuras de datos para almacenar informacion en memoria
    private static Map<Long, Salesman> salesmenMap = new HashMap<>();
    private static Map<Integer, Product> productsMap = new HashMap<>();

    // Clases internas para representar las entidades
    static class Salesman {
        String docType;
        long docNumber;
        String name;
        String lastName;
        double totalSales;

        public Salesman(String docType, long docNumber, String name, String lastName) {
            this.docType = docType;
            this.docNumber = docNumber;
            this.name = name;
            this.lastName = lastName;
            this.totalSales = 0.0;
        }
    }

    static class Product {
        int id;
        String name;
        double price;
        int totalQuantitySold;

        public Product(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.totalQuantitySold = 0;
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando procesamiento de ventas (Entrega 2)...");

        try {
            // 1. Cargar datos maestros
            loadSalesmenInfo("vendedores.txt");
            loadProductsInfo("productos.txt");

            // 2. Procesar todos los archivos de ventas individuales
            processSalesFiles();

            // 3. Generar reportes finales consolidando la informacion
            generateSalesmenReport("reporte_vendedores.csv");
            generateProductsReport("reporte_productos.csv");

            System.out.println("Procesamiento completado con exito. Reportes generados correctamente.");

        } catch (Exception e) {
            System.err.println("Error durante el procesamiento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga la informacion del archivo maestro vendedores.txt
     */
    private static void loadSalesmenInfo(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("El archivo " + filename + " no existe.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    String docType = parts[0];
                    long docNum = Long.parseLong(parts[1]);
                    String name = parts[2];
                    String lastName = parts[3];

                    salesmenMap.put(docNum, new Salesman(docType, docNum, name, lastName));
                }
            }
        }
    }

    /**
     * Carga la informacion del archivo maestro productos.txt
     */
    private static void loadProductsInfo(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("El archivo " + filename + " no existe.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);

                    productsMap.put(id, new Product(id, name, price));
                }
            }
        }
    }

    /**
     * Busca y procesa todos los archivos de ventas en la carpeta raiz
     */
    private static void processSalesFiles() {
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".txt") 
                && !name.equals("vendedores.txt") 
                && !name.equals("productos.txt"));

        if (listOfFiles == null || listOfFiles.length == 0) {
            System.out.println("No se encontraron archivos de ventas individuales para procesar.");
            return;
        }

        for (File file : listOfFiles) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String header = br.readLine();
                if (header == null) continue;

                String[] headerParts = header.split(";");
                if (headerParts.length < 2) continue;

                long salesmanDoc = Long.parseLong(headerParts[1]);
                Salesman salesman = salesmenMap.get(salesmanDoc);

                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(";");
                    if (parts.length >= 2) {
                        int productId = Integer.parseInt(parts[0]);
                        int quantity = Integer.parseInt(parts[1]);

                        Product product = productsMap.get(productId);
                        if (product != null && salesman != null) {
                            double saleAmount = product.price * quantity;
                            salesman.totalSales += saleAmount;
                            product.totalQuantitySold += quantity;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Advertencia al procesar archivo " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Genera el reporte consolidado de vendedores ordenado de mayor a menor ventas
     */
    private static void generateSalesmenReport(String filename) throws IOException {
        List<Salesman> salesmenList = new ArrayList<>(salesmenMap.values());
        salesmenList.sort((s1, s2) -> Double.compare(s2.totalSales, s1.totalSales));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("TipoDocumento;NumeroDocumento;NombreCompleto;TotalVendido");
            bw.newLine();

            for (Salesman s : salesmenList) {
                bw.write(s.docType + ";" + s.docNumber + ";" + s.name + " " + s.lastName + ";" + String.format("%.2f", s.totalSales));
                bw.newLine();
            }
        }
    }

    /**
     * Genera el reporte consolidado de productos ordenado por cantidad vendida
     */
    private static void generateProductsReport(String filename) throws IOException {
        List<Product> productsList = new ArrayList<>(productsMap.values());
        productsList.sort((p1, p2) -> Integer.compare(p2.totalQuantitySold, p1.totalQuantitySold));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("IDProducto;NombreProducto;PrecioUnitario;CantidadTotalVendida;MontoTotal");
            bw.newLine();

            for (Product p : productsList) {
                double totalAmount = p.price * p.totalQuantitySold;
                bw.write(p.id + ";" + p.name + ";" + String.format("%.2f", p.price) + ";" + p.totalQuantitySold + ";" + String.format("%.2f", totalAmount));
                bw.newLine();
            }
        }
    }
}
