
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
                    System.out.println("Introduza as idades:");
                    int idades = sc.nextInt();
                    matriz = new double[idades];

                    System.out.println("Distribuição inicial da população :");
                    for (int i = 0; i < idades; i++) {
                        System.out.println("Introduza valor:");
                        matriz[i] = sc.nextDouble();
                    }

                    System.out.println("Taxa de Sobrevivência :");
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
    * Verifica��o se o ficheiro obtido � v�lido ou n�o
     */
    private static boolean isFileValid(String result) {
        return ((result != null) && (new File(result).isFile()));
    }

    //Tentativa de cria��o do m�todo para criar um o gr�fico(incompleto)
//    public static JavaPLot plotOneGraph(double auxArrPopulacao[], int auxLenght, boolean showOnScreen) throws IOException {
//
//
//	JavaPlot p = new JavaPlot();
//
//	PlotStyle myPlotStyle = new PlotStyle();
//        myPlotStyle.setStyle(Style.LINESPOINTS);
//        myPlotStyle.setLineWidth(1);
//        //RgbPlotColor = new RgbPlotColor(
//        myPlotStyle.setLineType(NamedPlotColor.BLUE);
//        myPlotStyle.setPointType(7);
//        myPlotStyle.setPointSize(1);
//
//	double[] totalPopulacao = //Parametro String passado no getTotalPopulacao ...
//	double matriz[][] = preencherGraphMatriz(auxArrPopulacao, auxLenght);
//	//Falta a normalizada
//
//	p.getAxis("x").setLabel("", "Arial", 15);
//        p.getAxis("y").setLabel("", "Arial", 15);
//
//        DataSetPlot s = new DataSetPlot(matriz);
//        s.setTitle("Graph");
//        s.setPlotStyle(myPlotStyle);
//
//        p.getAxis("x").setBoundaries(0, auxLength);
//
//        // p.newGraph();
//        p.set("lmargin", "at screen 0.15");
//        p.set("rmargin", "at screen 0.85");
//        p.set("bmargin", "at screen 0.15");
//        p.set("tmargin", "at screen 0.85");
//        p.addPlot(s);
//
//        p.newGraph();
//        if (showOnScreen) {
//            p.plot();
//        }
//
//        return p;
//
//    }

    public static void savePNG(JavaPlot p, String name) {

        FileTerminal png = new FileTerminal("png", name);
        File file = new File(name);

        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            System.err.print(ex);
        } catch (IOException ex) {
            System.err.print(ex);
        }

        p.setTerminal(png);
        p.plot();
/*
*/
    }

    //======================================================================================================================

    public static double[][] multArrays(double[][]arr1, double[][]arr2){

        double[][]arrM = new double[arr1.length][arr1.length];

        for(int i = 0; i < arr1.length; i++){
            for(int j = 0; j < arr1.length; j++){
                for(int k = 0; k < arr1.length; k++){
                    arrM[i][j] = arrM[i][j] + arr1[i][k] * arr2[k][j];
                }
            }
        }
        return arrM;
    }
}
