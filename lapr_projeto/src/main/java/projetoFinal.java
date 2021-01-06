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
            System.out.println("Escolha uma opção :");
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
                    //sem leitura de ficheiro
            }
        }
    }
    //======================================================================================================================
}