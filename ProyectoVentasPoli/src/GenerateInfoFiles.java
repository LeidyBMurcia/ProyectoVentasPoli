import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Clase principal encargada de la generacion de archivos de prueba
 * pseudoaleatorios para el sistema de ventas.
 */
public class GenerateInfoFiles {

    private static final String[] TIPOS_DOC = {"CC", "CE", "NIT"};
    private static final String[] NOMBRES = {"Carlos", "Ana", "Luis", "Maria", "Jorge", "Diana", "Pedro", "Sofia"};
    private static final String[] APELLIDOS = {"Gomez", "Rodriguez", "Lopez", "Perez", "Martinez", "Garcia", "Torres"};
    private static final String[] PRODUCTOS = {"Portatil", "Mouse", "Teclado", "Monitor", "Diadema", "Impresora", "Escaner"};

    public static void main(String[] args) {
        try {
            // Generar archivo maestro de vendedores y guardar sus IDs[cite: 1]
            long[] idsVendedores = createSalesManInfoFile(5);
            
            // Generar archivo maestro de productos[cite: 1]
            createProductsFile(10);
            
            // Generar un archivo de ventas individual por cada vendedor[cite: 1]
            for (int i = 0; i < idsVendedores.length; i++) {
                String nombreVendedor = NOMBRES[i % NOMBRES.length] + "_" + APELLIDOS[i % APELLIDOS.length];
                createSalesMenFile(8, nombreVendedor, idsVendedores[i]);
            }
            
            System.out.println("Proceso finalizado exitosamente. Archivos generados correctamente.");
        } catch (Exception e) {
            System.err.println("Ocurrio un error al generar los archivos: " + e.getMessage());
        }
    }

    /**
     * Crea un archivo con la informacion de los vendedores[cite: 1].
     * Formato: Tipo Documento;Numero Documento;Nombres;Apellidos[cite: 1]
     */
    public static long[] createSalesManInfoFile(int salesmanCount) throws IOException {
        long[] ids = new long[salesmanCount];
        Random rand = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("vendedores.txt"))) {
            for (int i = 0; i < salesmanCount; i++) {
                String tipoDoc = TIPOS_DOC[rand.nextInt(TIPOS_DOC.length)];
                long numDoc = 1000000000L + rand.nextInt(900000000);
                String nombre = NOMBRES[rand.nextInt(NOMBRES.length)];
                String apellido = APELLIDOS[rand.nextInt(APELLIDOS.length)];

                ids[i] = numDoc;
                writer.write(tipoDoc + ";" + numDoc + ";" + nombre + ";" + apellido);
                writer.newLine();
            }
        }
        return ids;
    }

    /**
     * Crea un archivo con informacion pseudoaleatoria de productos[cite: 1].
     * Formato: IDProducto;NombreProducto;PrecioPorUnidad[cite: 1]
     */
    public static void createProductsFile(int productsCount) throws IOException {
        Random rand = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("productos.txt"))) {
            for (int i = 1; i <= productsCount; i++) {
                String nombreProd = PRODUCTOS[rand.nextInt(PRODUCTOS.length)] + "_" + i;
                int precio = (rand.nextInt(50) + 1) * 10000;

                writer.write(i + ";" + nombreProd + ";" + precio);
                writer.newLine();
            }
        }
    }

    /**
     * Crea un archivo pseudoaleatorio de ventas para un vendedor en especifico[cite: 1].
     * Formato 1ra linea: Tipo Documento;Numero Documento[cite: 1]
     * Formato siguientes lineas: IDProducto;CantidadVendida;[cite: 1]
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        Random rand = new Random();
        String tipoDoc = TIPOS_DOC[rand.nextInt(TIPOS_DOC.length)];
        String filename = name + "_" + id + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(tipoDoc + ";" + id);
            writer.newLine();

            for (int i = 0; i < randomSalesCount; i++) {
                int idProducto = rand.nextInt(10) + 1;
                int cantidad = rand.nextInt(15) + 1;

                writer.write(idProducto + ";" + cantidad + ";");
                writer.newLine();
            }
        }
    }
}