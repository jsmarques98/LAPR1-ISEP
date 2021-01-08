import org.la4j.Matrix;
import org.la4j.*;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;


import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.FileTerminal;
import com.panayotis.gnuplot.terminal.GNUPlotTerminal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Scanner;

public class projetoFinal {

    final static int MAX = 100; //dimensao maxima do array
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {
        double[] matriz = new double[MAX]; //Array que guarda as informacoes da quantidade de populacao recebido como parametro
        double[] matrizSobrevivencia = new double[MAX]; //Array que guarda as informacoes das taxas de sobrevivencia recebidos como parametro
        double[] matrizNatalidade = new double[MAX]; //Array que guarda as informacoes das taxas de natalidade recebidas como parametro
        String[] outputFileInfo = new String[3]; //Usado para definir o nome dos ficheiros de output
        boolean endProgram = false, modoInterativo; //usado para definir quando acabar o programa e se os modulo funcionam em modo interativo ou nao interativo

        if (args.length == 2 && args[0].compareTo("-nome") == 0) { //Modo interativo pede, 2 argumentos nome e nomeFicheiro
            modoInterativo = true;

            while (endProgram == false) { //Repetir até o programa ser fechado
                /*
                Imprimir instruções do menu
                 */
                System.out.println("0 - Fechar o programa.");
                System.out.println("1 - Ler Informações de um ficheiro.");
                System.out.println("2 - Carregar Informações a partir de um ficheiro");

                int opcao = sc.nextInt(); //selecionar opcao no menu
                while (opcao < 0 || opcao < 2) { //confirmar se a opcao e valida
                    System.out.println("Opção inválida, por favor tente novamente!"); //pedir nova selecao
                    opcao = sc.nextInt(); //nova selecao
                }

                switch (opcao) {
                    case 0:
                        endProgram = true;
                        break;
                    case 1: //carregar informações a partir de um ficheiro
                        readFile(outputFileInfo, args[1], matriz, matrizSobrevivencia, matrizNatalidade, modoInterativo); //le organiza dados do ficheiro
                        break;
                    case 2: //introduzir informacoes nos contentores
                        introduzirDados(matriz, matrizSobrevivencia, matrizNatalidade);
                        break;
                }
            }
        } else if (args.length == 11) {//Modo não interativo pede 11 argumentos
            modoInterativo = false;
            int Y = Integer.parseInt(args[5]); //Guarda o formato de saída do ficheiro
            int numeroGeracoes = Integer.parseInt(args[3]); //Guarda o numero de geracoes
            int dimensaoPopulacaoCadaGeracao = Integer.parseInt(args[7]); //Guarda dimensão da população a cada geração
            int variacaoPopulacaoGeracoes = Integer.parseInt(args[8]); //Guarda da variação da popuação entre gerações.


        } else {
            System.out.println("Numero de argumentos incorreto ou argumentos invalidos.");
        }
    }

    //========================================================================================================================
    public static void readFile(String[] outputFileInfo, String fileName, double[] matriz, double[] matrizSobrevivencia, double[] matrizNatalidade, boolean modoInterativo) throws FileNotFoundException {
        while (new File(fileName).isFile() == false) {
            if (modoInterativo == true) {
                System.out.println("Ficheiro inválido ou inexistente, por favor inserir novo ficheiro"); //Pede um ficheiro novo
                fileName = sc.nextLine();
            } else { //Se nao for modo interativo, programa encerra
                System.out.println("Ficheiro inválido ou inexistente."); //Avisa que o ficheiro não existe
                System.exit(0); //fecha o programa
            }
        }

        String[] outputFileName = fileName.split("\\."); //Separar o nome do ficheiro da extensao
        outputFileInfo[0] = outputFileName[0]; //guarda o nome do ficheiro, sem extensao

        storeFileInfo(fileName, matriz, matrizSobrevivencia, matrizNatalidade);//Ler e guardar dados do ficheiro

    }

    //======================================================================================================================
    public static void storeFileInfo(String fileName, double[] matriz, double[] matrizSobrevivencia, double[] matrizNatalidade) throws FileNotFoundException {

        Matrix matrizLeslie; //Variável que contem os dados referentes ao modelo matricial de Leslie
        Scanner lerFicheiro = new Scanner(new File(fileName));
        String[] primeiraLinha = lerFicheiro.nextLine().trim().split(","); //Divide a informação da população inicial
        matriz = getTotalPopulacao(primeiraLinha); //Guarda a informação da população Inicial

        String[] segundaLinha = lerFicheiro.nextLine().trim().split(","); //Divide a informação da Taxa de Sobrevivência
        matrizSobrevivencia = getValores(segundaLinha); //Guarda a informação da Taxa de Sobrevivência

        String[] terceiraLinha = lerFicheiro.nextLine().trim().split(","); //Divide a informalão da Taxa de Natalidade
        matrizNatalidade = getValores(terceiraLinha); //Guarda a Informação da Taxa de Natalidade

        matrizLeslie = criarMatrizLeslie(matrizSobrevivencia, matrizNatalidade); //Cria a matriz de Leslie com os valores atribuidos pelo ficheiro
        System.out.println(matrizLeslie);

        lerFicheiro.close(); //Fecha o ficheiro
    }

    //======================================================================================================================
    public static void introduzirDados(double[] matriz, double[] matrizSobrevivencia, double[] matrizNatalidade) {
        String newFileName; //Variável que guarda o nome da espécie quando o utilizador insere os dados
        System.out.println("Identifique a nova espécie a ser estudada :");
        newFileName = sc.nextLine(); //Ler o nome da espécie

        System.out.println("Introduza as idades:");
        int idades = sc.nextInt(); //Lê a quantidade de idades, ou faixas etárias
        matriz = new double[idades];//Define a quantidade de faixas etárias

        System.out.println("Distribuição inicial da população :");
        for (int i = 0; i < idades; i++) {
            System.out.println("Introduza valor:");
            matriz[i] = sc.nextDouble(); //Lê a distribuição inicial da população, respetivamente.
        }

        System.out.println("Taxa de Sobrevivência :");
        matrizSobrevivencia = new double[idades];
        for (int i = 0; i < idades; i++) {
            System.out.println("Introduzir valor:");
            matrizSobrevivencia[i] = sc.nextDouble(); //Lê a taxa de sobrevivência, respetivamente.
        }

        System.out.println("Taxa de Natalidade :");
        matrizNatalidade = new double[idades];
        for (int i = 0; i < idades; i++) {
            System.out.println("Introduza valor :");
            matrizNatalidade[i] = sc.nextDouble(); //Lê a taxa de natalidade, respetivamente.
        }
    }
    //======================================================================================================================
    public static double[] getTotalPopulacao(String[] linha) {
        double[] matriz = new double[linha.length];
        for (int i = 0; i < linha.length; i++) {
            String[] objeto = linha[i].split("=");
            matriz[i] = Double.parseDouble(objeto[i]);
        }
        return matriz;
    }

    //======================================================================================================================
    public static double[] getValores(String[] linha) {
        double[] matriz = new double[linha.length];
        for (int i = 0; i < linha.length; i++) {
            String[] objeto = linha[i].trim().split("=");
            matriz[i] = Double.parseDouble(objeto[i]);
        }
        return matriz;
    }

    //======================================================================================================================
        public static Matrix criarMatrizLeslie ( double[] matrizSobrevivencia, double[] matrizNatalidade){
            double[][] matrizCriada = new double[matrizSobrevivencia.length][matrizNatalidade.length];
            for (int i = 0; i < matrizNatalidade.length; i++) {
                matrizCriada[0][i] = matrizNatalidade[i];
            }
            int contador = 0;
            for (int i = 0; i < matrizSobrevivencia.length; i++) {
                for (int j = 0; j < matrizSobrevivencia.length; i++) {
                    if (i == j + 1) {
                        matrizCriada[i][j] = matrizSobrevivencia[contador];
                        contador++;
                    } else {
                        matrizCriada[i][j] = 0;
                    }
                }
            }
            return Matrix.from2DArray(matrizCriada);
        }
        //=====================================================================================================================

    //Multiplica duas matrizes da mesma ordem
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

    //======================================================================================================================

    //Retorna uma matriz identidade
    private static double[][] arrId(int n){

        double[][] I = new double[n][n];
        for(int i = 0; i < n; i++){
            I[i][i] = 1;
        }
        return I;
    }

    //======================================================================================================================

    //Calcula arr elevado a n
    public static double[][] elevarArr(double arr[][], int n){

        double[][] arrEl = arrId(arr.length);
        for(int i = 0; i < n; i++){
            arrEl = multArrays(arrEl, arr);
        }
        return arrEl;
    }

    //======================================================================================================================

    //Calcula a distribuição normal da população
    public static double[] distPopulacao(double[][]arr, double[]vetor){

        double[] resultado = new double[vetor.length];
        for(int i = 0; i < vetor.length; i++){
            for(int k = 0; k < vetor.length; k++){
                resultado[i] = resultado[i] + arr[i][k] * vetor[k];
            }
        }
        return resultado;
    }

    //======================================================================================================================

    //Metodo tentativa de gerar grafico
    public static JavaPLot plotOneGraph(double auxArrPopulacao[], int auxLenght, boolean showOnScreen) throws IOException {


        JavaPlot p = new JavaPlot();

        PlotStyle myPlotStyle = new PlotStyle();
        myPlotStyle.setStyle(Style.LINESPOINTS);
        myPlotStyle.setLineWidth(1);
        //RgbPlotColor = new RgbPlotColor(
        myPlotStyle.setLineType(NamedPlotColor.BLUE);
        myPlotStyle.setPointType(7);
        myPlotStyle.setPointSize(1);

       // double[] totalPopulacao = //Parametro String passado no getTotalPopulacao ...
        double matriz[][] = preencherGraphMatriz(auxArrPopulacao, auxLenght);
        //Falta a normalizada

        p.getAxis("x").setLabel("", "Arial", 15);
        p.getAxis("y").setLabel("", "Arial", 15);

        DataSetPlot s = new DataSetPlot(matriz);
        s.setTitle("Graph");
        s.setPlotStyle(myPlotStyle);

        p.getAxis("x").setBoundaries(0, auxLength);

        // p.newGraph();
        p.set("lmargin", "at screen 0.15");
        p.set("rmargin", "at screen 0.85");
        p.set("bmargin", "at screen 0.15");
        p.set("tmargin", "at screen 0.85");
        p.addPlot(s);

        p.newGraph();
        if (showOnScreen) {
            p.plot();
        }

        return p;
    }

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

    }
}