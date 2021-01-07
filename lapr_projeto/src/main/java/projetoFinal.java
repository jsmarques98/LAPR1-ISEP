
import org.la4j.Matrix;
import org.la4j.*;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class projetoFinal {

    static boolean Interacao = true;

    public static void main(String[] args) throws FileNotFoundException {
        String file = null;
        readFile(file);
    }

    //======================================================================================================================
    public static void readFile(String file) throws FileNotFoundException {
        double[] matriz;
        double[] matrizSobrevivencia;
        double[] matrizNatalidade;
        Matrix matrizLeslie;
        if (Interacao) {
            System.out.println("Escolha uma op√ß√£o :");
            System.out.println("Carregar Ficheiro - 1");
            System.out.println("Introduzir Valores - 2");
            Scanner sc = new Scanner(System.in);
            int opcao = sc.nextInt();

            switch (opcao) {
                case 1:
                    Scanner lerFicheiro = new Scanner(new File(file));
                    String[] primeiraLinha = lerFicheiro.nextLine().trim().split(",");
                    matriz = getTotalPopulacao(primeiraLinha);

                    String[] segundaLinha = lerFicheiro.nextLine().trim().split(",");
                    matrizSobrevivencia = getValores(segundaLinha);

                    String[] terceiraLinha = lerFicheiro.nextLine().trim().split(",");
                    matrizNatalidade = getValores(terceiraLinha);

                    matrizLeslie = criarMatrizLeslie(matrizSobrevivencia, matrizNatalidade);
                    System.out.println(matrizLeslie);
                    break;
                case 2:
                    System.out.println("Introduza as idades:");
                    int idades = sc.nextInt();
                    matriz = new double[idades];

                    System.out.println("Distribui√ß√£o inicial da popula√ß√£o :");
                    for (int i = 0; i < idades; i++) {
                        System.out.println("Introduza valor:");
                        matriz[i] = sc.nextDouble();
                    }

                    System.out.println("Taxa de Sobreviv√™ncia :");
                    matrizSobrevivencia = new double[idades];
                    for (int i = 0; i < idades; i++) {
                        System.out.println("Introduzir valor:");
                        matrizSobrevivencia[i] = sc.nextDouble();
                    }

                    System.out.println("Taxa de Natalidade :");
                    matrizNatalidade = new double[idades];
                    for (int i = 0; i < idades; i++) {
                        System.out.println("Introduza valor :");
                        matrizNatalidade[i] = sc.nextDouble();
                    }

            }
        }
    }
    //======================================================================================================================

    /*
    * VerificaÁ„o se o ficheiro obtido È v·lido ou n„o
     */
    private static boolean isFileValid(String result) {
        return ((result != null) && (new File(result).isFile()));
    }

}
