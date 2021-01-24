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

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class projetoFinal {
    //======================================================================================================================
    final static int MAX = 100; //dimensao maxima do array
    static Scanner sc = new Scanner(System.in);

    //======================================================================================================================
    public static void main(String[] args) throws IOException {
        double[] matriz = new double[MAX]; //Array que guarda as informacoes da quantidade de populacao recebido como parametro
        double[] matrizSobrevivencia = new double[MAX]; //Array que guarda as informacoes das taxas de sobrevivencia recebidos como parametro
        double[] matrizNatalidade = new double[MAX]; //Array que guarda as informacoes das taxas de natalidade recebidas como parametro
        String[] outputFileInfo = new String[3]; //Usado para definir o nome dos ficheiros de output
        boolean endProgram = false, modoInterativo; //usado para definir quando acabar o programa e se os modulo funcionam em modo interativo ou nao interativo
        int escolha = 0;

        if (args.length == 2)  { //Modo interativo pede, 2 argumentos nome e nomeFicheiro
            modoInterativo = true;

            while (endProgram == false) { //Repetir ate o programa ser fechado
                /*
                Imprimir instrucoes do menu
                 */
                System.out.println("0 - Fechar o programa.");
                System.out.println("1 - Ler Informacoes de um ficheiro.");
                System.out.println("2 - Introduzir as informações manualmente.");

                int opcao = sc.nextInt(); //selecionar opcao no menu
                while (opcao < -1 || opcao > 3) { //confirmar se a opcao e valida
                    System.out.println("Opcao invalida, por favor tente novamente!"); //pedir nova selecao
                    opcao = sc.nextInt(); //nova selecao
                }
                switch (opcao) {
                    case 0:
                        endProgram = true;
                        break;
                    case 1: //carregar informacoes a partir de um ficheiro
                        readFile(outputFileInfo, "test.txt", matriz, matrizSobrevivencia, matrizNatalidade, modoInterativo); //le organiza dados do ficheiro
                        break;
                    case 2: //introduzir informacoes nos contentores
                        introduzirDados(matriz, matrizSobrevivencia, matrizNatalidade);
                        break;
                }
                gnuPlot(escolha, outputFileInfo, true);
            }
        } else if (args.length == 7) {//Modo nao interativo pede 10 argumentos
            int numeroGeracoes = Integer.parseInt(args[0]); //Guarda o numero de geracoes
            int Y = Integer.parseInt(args[1]); //Guarda o formato de saida do ficheiro
            int vetorProprio = Integer.parseInt(args[2]); //Guarda o valor do vetor proprio
            int dimensaoPopulacaoCadaGeracao = Integer.parseInt(args[3]); //Guarda dimensao da populacao a cada geracao
            int variacaoPopulacaoGeracoes = Integer.parseInt(args[4]); //Guarda da variacao da popuacao entre geracoes.
            String nomeEstudo = args[5];
            String nomeSaida = args[6];
            modoInterativo = false;
            verificarArgumentos(Y,numeroGeracoes,dimensaoPopulacaoCadaGeracao,vetorProprio,variacaoPopulacaoGeracoes);


        } else {
            System.out.println("Numero de argumentos incorreto ou argumentos invalidos.");
        }
    }
    //======================================================================================================================
    /**
     * Este metodo verifica se os argumentos de saida e os proprios valores introduzidos sao verdadeiros ou falsos.
     *
     * @param Y
     * @param numeroGeracoes
     * @param dimensaoPopulacaoCadaGeracao
     * @param vetorProprio
     * @param variacaoPopulacaoGeracoes
     */
    public static void verificarArgumentos(int Y,int numeroGeracoes, int dimensaoPopulacaoCadaGeracao,int vetorProprio, int variacaoPopulacaoGeracoes) {
        boolean argumentosValidos = true;

        if ( Y != 1 || Y != 2 || Y != 3 ) {
            System.out.println("Escolha de ficheiro de saida invalida.");
            argumentosValidos = false;
        }

        if (argumentosValidos = false) { //Caso haja deficiencia nos valores introduzidos, o programa fecha
            System.exit(0);
        }

    }
    //========================================================================================================================
    /**
     * Este metodo faz a leitura do ficheiro recebido por parametro e as suas validacoes caso este nao seja encontrado
     * ou seja invalido
     *
     * @param outputFileInfo
     * @param fileName
     * @param matriz
     * @param matrizSobrevivencia
     * @param matrizNatalidade
     * @param modoInterativo
     * @throws FileNotFoundException
     */
    public static void readFile(String[] outputFileInfo, String fileName, double[] matriz, double[] matrizSobrevivencia, double[] matrizNatalidade, boolean modoInterativo) throws FileNotFoundException {
        String[] outputFileName = fileName.split("\\."); //Separar o nome do ficheiro da extensao
        outputFileInfo[0] = outputFileName[0]; //guarda o nome do ficheiro, sem extensao

        storeFileInfo(fileName);//Ler e guardar dados do ficheiro
    }
    //======================================================================================================================
    public static void storeFileInfo(String filename) throws FileNotFoundException {

        System.out.println();
        System.out.println("Introduza o número de gerações a estimar");
        int n = sc.nextInt();

        File ficheiro = new File(filename);
        Scanner ler = new Scanner(ficheiro);

        String[] linha1 = new String[0];
        String[] linha2 = new String[0];
        String[] linha3 = new String[0];
        int z = 1;

        while (z != 4) {
            String linha = ler.nextLine();
            if(linha.trim().length() != 0) {        //se a linha nao for nula (se tiver espacos em branco corta-os)
                if (z == 1) {
                    linha1 = linha.split(", ");
                } else if (z == 2) {
                    linha2 = linha.split(", ");
                } else {
                    linha3 = linha.split(", ");
                }
                z++;
            }
        }

        double[] vetor = toMatrix(linha1, linha2, linha3, 'x');
        double[] matrizSobrevivencia = toMatrix(linha1, linha2, linha3, 's');
        double[] matrizNatalidade = toMatrix(linha1, linha2, linha3, 'f');
        double[][] mLeslie = criarMatrizLeslie(matrizSobrevivencia, matrizNatalidade);  //arr2 tem que ser matrizNatalidade

        ler.close(); //Fecha o ficheiro
        System.out.println("Distribuição inicial: ");
        printArray(vetor);
        System.out.println("Matriz natalidade: ");
        printArray(matrizNatalidade);
        System.out.println("Matriz sobrevivência: ");
        printArray(matrizSobrevivencia);
        System.out.println("Matriz Leslie");
        printMatriz(mLeslie);



        double[][] arrElevado;
        System.out.println("Distribuicao nao normalizada");
        for(int i = 0; i <= n; i++){
           arrElevado = elevarArr(mLeslie, i);
           printMatriz2CasasDecimais(distPopulacao(arrElevado, vetor),i);
        }

        System.out.println();
        System.out.println("Distribuicao normalizada");
        for(int i = 0; i <= n; i++){
            arrElevado = elevarArr(mLeslie, i);
            printMatriz2CasasDecimais( distPopulacaoNormalizada(distPopulacao(arrElevado, vetor), dimPopulacao(mLeslie, vetor, i)), i);
        }

        System.out.println();
        System.out.println("Numero total de individuos");
        for(int i = 0; i <= n; i++){
            printValores2CasasDecimais(dimPopulacao(mLeslie, vetor, i), i);
        }

        System.out.println();
        System.out.println("Taxa de variacao");
        for(int i = 0; i < n; i++){
            printValores2CasasDecimais(taxaVarPopulacao(mLeslie, vetor, i), i);
        }

        System.out.println();
        Matrix matrizLeslie = new Basic2DMatrix(mLeslie);
        maiorValorProprio(matrizLeslie);

    }
    //======================================================================================================================
    /**
     * Este metodo faz a leitura separadamente dos diferentes topicos existentes na matriz, ou seja a distribuicao
     * inicial da populacao, a taxa de sobrevivencia e a taxa de natalidade numa populacao segundo as geracoes.
     *
     * @param matriz
     * @param matrizSobrevivencia
     * @param matrizNatalidade
     */
    public static void introduzirDados(double[] matriz, double[] matrizSobrevivencia, double[] matrizNatalidade) {
        String newFileName; //Variavel que guarda o nome da especie quando o utilizador insere os dados
        System.out.println("Identifique o número de gerações a estimar :");
        int n = sc.nextInt();

        System.out.println("Introduza a quantidade de faixas etárias:");
        int idades = sc.nextInt(); //da a quantidade de idades, ou faixas etarias
        matriz = new double[idades];//Define a quantidade de faixas etarias

        System.out.println("Distribuicao inicial da populacao :");
        for (int i = 0; i < idades; i++) {
            System.out.println("Introduza valor:");
            matriz[i] = sc.nextDouble(); //da a distribuicao inicial da populacao, respetivamente.
        }

        System.out.println("Taxa de Sobrevivencia :");
        matrizSobrevivencia = new double[idades];
        for (int i = 0; i < idades; i++) {
            System.out.println("Introduzir valor:");
            matrizSobrevivencia[i] = sc.nextDouble(); //da a taxa de sobrevivencia, respetivamente.
        }

        System.out.println("Taxa de Natalidade :");
        matrizNatalidade = new double[idades];
        for (int i = 0; i < idades; i++) {
            System.out.println("Introduza valor :");
            matrizNatalidade[i] = sc.nextDouble(); //da a taxa de natalidade, respetivamente.
        }
        double[][] mLeslie = criarMatrizLeslie(matrizSobrevivencia, matrizNatalidade);

        System.out.println("Distribuição inicial: ");
        printArray(matriz);
        System.out.println("Matriz natalidade: ");
        printArray(matrizNatalidade);
        System.out.println("Matriz sobrevivência: ");
        printArray(matrizSobrevivencia);
        System.out.println("Matriz Leslie");
        printMatriz(mLeslie);

        double[][] arrElevado;
        System.out.println("Distribuicao nao normalizada");
        for(int i = 0; i <= n; i++){
           arrElevado = elevarArr(mLeslie, i);
           printMatriz2CasasDecimais(distPopulacao(arrElevado, matriz),i);
        }

        System.out.println();
        System.out.println("Distribuicao normalizada");
        for(int i = 0; i <= n; i++){
            arrElevado = elevarArr(mLeslie, i);
            printMatriz2CasasDecimais( distPopulacaoNormalizada(distPopulacao(arrElevado, matriz), dimPopulacao(mLeslie, matriz, i)), i);
        }

        System.out.println();
        System.out.println("Numero total de individuos");
        for(int i = 0; i <= n; i++){
            printValores2CasasDecimais(dimPopulacao(mLeslie, matriz, i), i);
        }

        System.out.println();
        System.out.println("Taxa de variacao");
        for(int i = 0; i < n; i++){
            printValores2CasasDecimais(taxaVarPopulacao(mLeslie, matriz, i), i);
        }

        System.out.println();
        Matrix matrizLeslie = new Basic2DMatrix(mLeslie);
        maiorValorProprio(matrizLeslie);

    }
    public static void printArray(double[] matriz){
        for (int i = 0; i < matriz.length; i++){
            System.out.println(matriz[i]);
        }
    }
    //======================================================================================================================
    /**
     * Este metodo imprime os valores de uma matriz com duas casas decimais.
     *
     * @param matriz
     * @param i
     */
    public static void printMatriz2CasasDecimais(double[] matriz, int i) {
        System.out.println("t = " + i);
        for (i = 0; i < matriz.length; i++){
            System.out.printf("%.2f", matriz[i]);
            System.out.print("  ");
        }
        System.out.println();
    }
    //======================================================================================================================
    /**
     * Este metodo imprime o valor com duas casas decimais.
     *
     * @param valor
     * @param i
     */
    public static void printValores2CasasDecimais(double valor, int i){
        System.out.println("t = " + i);
        System.out.printf("%.2f", valor);
        System.out.println();
    }
    //======================================================================================================================
    /**
     * Este metodo pega nas linhas do ficheiro lido e, basicamente formata-as da forma que nos queremos com o uso do
     * "replaceAll", onde ha a substituicao de espacos e do carater "=" como se de um split se tratasse.
     *
     * @param arr1
     * @param arr2
     * @param arr3
     * @param letra
     * @return arrays com a formatacao.
     */
    public static double[] toMatrix(String[] arr1, String[] arr2, String[] arr3, char letra){

        double[] convert = new double[0];

        if(arr1[0].charAt(0) == letra){
            convert = new double[arr1.length];
            for(int i = 0; i < arr1.length; i++){
                arr1[i] = arr1[i].replaceAll(".*=", "");
                convert[i] = Double.valueOf(arr1[i]);
            }
        }else if(arr2[0].charAt(0) == letra){
            convert = new double[arr2.length];
            for(int i = 0; i < arr2.length; i++){
                arr2[i] = arr2[i].replaceAll(".*=", "");
                convert[i] = Double.valueOf(arr2[i]);
            }
        }else if(arr3[0].charAt(0) == letra){
            convert = new double[arr3.length];
            for(int i = 0; i < arr3.length; i++){
                arr3[i] = arr3[i].replaceAll(".*=", "");
                convert[i] = Double.valueOf(arr3[i]);
            }
        }
        return convert;
    }
    //======================================================================================================================
    /**
     * Este metodo faz a criacao da matriz de leslie
     *
     * @param arr1
     * @param arr2
     * @return matriz de leslie
     */
    public static double[][] criarMatrizLeslie(double[]arr1, double[]arr2){

        double[][] mLeslie = new double[arr2.length][arr2.length];
        int z = 0;
        for(int i = 0; i < mLeslie.length; i++){
            if(i == 0){
                for(int j = 0; j < mLeslie.length; j++){
                    mLeslie[i][j] = arr2[j];
                }
            }else if(z != arr1.length){
                mLeslie[i][z] = arr1[z];
                z++;
            }
        }
        return mLeslie;
    }
    //======================================================================================================================
    /**
     * Este metodo faz o calculo do produto entre duas matrizes de igual dimensoes e devolve a matriz resultante desse
     * mesmo produto.
     *
     * @param arr1
     * @param arr2
     * @return devolve o produto
     */
    public static double[][] multArrays(double[][] arr1, double[][] arr2) {//Possivelmente um metodo private, uma vez que e so um metodo de auxilio

        double[][] arrM = new double[arr1.length][arr1.length]; //Multiplica duas matrizes de ordem igual

        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1.length; j++) {
                for (int k = 0; k < arr1.length; k++) {
                    arrM[i][j] = arrM[i][j] + arr1[i][k] * arr2[k][j];
                }
            }
        }
        return arrM;
    }

    //======================================================================================================================
    /**
     * Este metodo devolve a matriz identidade.
     *
     * @param n
     * @return matriz identidade
     */
    private static double[][] arrId(int n) {

        double[][] I = new double[n][n]; //Calculo da matriz identidade
        for (int i = 0; i < n; i++) {
            I[i][i] = 1;
        }
        return I; //devolve uma matriz identidade
    }

    //======================================================================================================================
    /**
     * Este metodo faz o calculo da matriz levantado a um valor n.
     *
     * @param arr
     * @param n
     * @return matriz^n
     */
    public static double[][] elevarArr(double arr[][], int n){

        double[][] arrEl = arrId(arr.length);
        for(int i = 0; i < n; i++){
            arrEl = multArrays(arrEl, arr);
        }
        return arrEl;
    }
    //======================================================================================================================
    /**
     * Este metodo mostra os dados da matriz.
     *
     * @param matriz
     */
    public static void printMatriz(double[][] matriz) {
        for (double[] numeros : matriz) {
            System.out.println(Arrays.toString(numeros));
        }
    }

    //======================================================================================================================
    /**
     * Este metodo calcula a distribuicao nao normalizada da populacao para um determinado tempo
     *
     * @param arr
     * @param vetor
     * @return distribuicao nao normalizada da populacao
     */
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

    /**
     * Este metodo faz o  calculo dos valores proprios e da return do valor com maior modulo.
     *
     * @param matrizLeslie
     * @return valor com maior modulo
     */
    public static double maiorValorProprio(Matrix matrizLeslie){

        EigenDecompositor eigenD = new EigenDecompositor(matrizLeslie);
        Matrix [] mattD = eigenD.decompose();

        double matA [][] = mattD[0].toDenseMatrix().toArray();  //vetores próprios
        double matB [][] = mattD[1].toDenseMatrix().toArray();  //valores próprios
        double maior = matB [0][0];
        int colunaVetor = 0;

        for(int i = 0; i < matB.length; i++) {
            if(Math.abs(matB[i][i]) > Math.abs(maior)){
                maior = matB[i][i];
                colunaVetor = i;
            }
        }

        System.out.print("Vetor próprio: ");
        for(int i = 0; i < matA.length; i++) {
            if(i == 0){
                System.out.print("(" + String.format("%.2f", matA[i][colunaVetor]) + ", ");
            }else if (i == matA.length - 1){
                System.out.print(String.format("%.2f", matA[i][colunaVetor]) + ")");
            }else{
                System.out.print(String.format("%.2f", matA[i][colunaVetor]) + ", ");
            }
        }
        System.out.println();
        System.out.print("Valor próprio: ");
        System.out.printf("%.4f",+ maior);
        return maior;
    }

    //======================================================================================================================

    /**
     * Este metodo calcula a distribuicao normalizada da populacao para um determinado t.
     *
     * @param dist
     * @param total
     * @return distribuicao normalizada da populacao para um determinado t
     */
    public static double[] distPopulacaoNormalizada(double[] dist, double total){

        double[] distribuicaoN = new double[dist.length];
        for(int i = 0; i < distribuicaoN.length; i++){
            distribuicaoN[i] = (dist[i]/total) * 100;
        }
        return distribuicaoN;
    }

    //======================================================================================================================

    /**
     * Este metodo calcula a dimensao da populacao num determinado instante  t(tempo)
     *
     * @param arr
     * @param vetor
     * @param geracao
     * @return dimensso da populacao num determinado instante
     */
    public static double dimPopulacao(double[][]arr, double[]vetor, int geracao){

        double total = 0;
        double[][]arrElevado;

        for(int i = 0; i <= geracao; i++){
            arrElevado = elevarArr(arr, i);
            total = 0;
            for(int z = 0; z <  distPopulacao(arrElevado, vetor).length; z++){
                total = total + distPopulacao(arrElevado, vetor)[z];
            }
        }
        return total;
    }

    //======================================================================================================================

    /**
     * Este metodo devolve uma matriz de uma dimensao de uma populacao ao longo do tempo.
     *
     * @param arr
     * @param vetor
     * @param geracao
     * @return dimensao de uma populacao ao longo do tempo
     */
    private static double[][] dimPopulacaoPorInstante(double[][] arr, double[] vetor, int geracao) {

        double[][] matriz = new double[geracao + 1][geracao + 1];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < 2; j++) {
                if (j == 0) {
                    matriz[i][j] = i;
                } else if (j == 1) {
                    matriz[i][j] = dimPopulacao(arr, vetor, i);
                }
            }
        }
        return matriz;
    }

    //======================================================================================================================
    /**
     * Este metodo executa o calculo da taxa de variacao da populacao.
     *
     * @param arr
     * @param vetor
     * @param geracao
     * @return taxa de variacao
     */
    public static double taxaVarPopulacao(double[][]arr, double[]vetor, int geracao){

        double taxa = dimPopulacao(arr, vetor, geracao + 1) / dimPopulacao(arr, vetor, geracao);
        return taxa;
    }

    //======================================================================================================================

    /**
     * Este metodo devolve uma matriz da taxa de variacao da populacao ao longo do tempo/geracoes.
     *
     * @param arr
     * @param vetor
     * @param geracao
     * @return matriz da taxa de variacao da populacao ao longo do tempo/geracoes
     */
    private static double[][] taxaVarPopulacaoPorInstante(double[][] arr, double[] vetor, int geracao) {

        double[][] matriz = new double[geracao][geracao];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < 2; j++) {
                if (j == 0) {
                    matriz[i][j] = i;
                } else if (j == 1) {
                    matriz[i][j] = taxaVarPopulacao(arr, vetor, i);
                }
            }
        }
        return matriz;
    }
    //=====================================================================================================================

    /**
     * Este metodo devolve uma matriz bidimensional da distribuicao da populacao ao longo do tempo(nao normalizada).
     *
     * @param arr
     * @param vetor
     * @param geracao
     * @return distribuicao da populacao ao longo do tempo
     */
    private static double[][] distribuicaoPopulacaoPorInstante(double[][] arr, double[] vetor, int geracao) {
        double[][] matriz = new double[geracao + 1][geracao + 1];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < 2; j++) {
                if (j == 0) {
                    matriz[i][j] = i;
                } else if (j == 1) {
                    // matriz[i][j] = populacaoDistribuicaoNormalizada(arr, geracao, vetor);
                }
            }
        }
        return matriz;
    }
    //======================================================================================================================
    /**
     * Este metodo faz o calculo e devolve a distribuicao da populacao normalizada.
     *
     * @param L
     * @param geracao
     * @param matrizLeslie
     * @return distribuicao da populacao normalizada
     */
    public static Matrix populacaoDistribuicaoNormalizada(Matrix L, int geracao, Matrix matrizLeslie) {
        Matrix matrix = L.power(geracao);
        return matrix.multiply(matrizLeslie);
    }
    //======================================================================================================================
    /**
     * Metodo que devolve o nome do ficheiro.
     *
     * @param outputFileInfo
     * @param extensaoSaida
     * @return fileName
     */
    public static String getFileName(String[] outputFileInfo, String extensaoSaida) {
        String fileName = (outputFileInfo[0]);

        return (fileName);
    }
    //======================================================================================================================
    /**
     * Metodo que constroi os graficos do total de populacao, da taxa de variacao, da evolucao da populacao e da
     * distribuicao normalizada da populacao ao longo do tempo.
     *
     * @param Y
     * @param outputFileInfo
     * @param modoInterativo
     * @throws IOException
     */
    public static void gnuPlot(int Y, String[] outputFileInfo, boolean modoInterativo) throws IOException {
        int escolha = Y;
        String[] linha = new String[0];
        int geracao = 0;
        double[] vetor = new double[0];
        double[][] arr = new double[0][0];

        if (modoInterativo == true) {
            System.out.println("==============================");
            System.out.println("Grafico total da populacao");
            System.out.println("==============================");



            if (modoInterativo == true) {
                System.out.println("==============================");
                System.out.println("Grafico total da populacao");
                System.out.println("==============================");

                JavaPlot p = new JavaPlot();

                PlotStyle myPlotStyle = new PlotStyle();
                myPlotStyle.setStyle(Style.LINESPOINTS);
                myPlotStyle.setLineWidth(1);
                myPlotStyle.setLineType(NamedPlotColor.BLUE);
                myPlotStyle.setPointType(7);
                myPlotStyle.setPointSize(1);

                double[][] getTotalPopulacaoPorInstante = dimPopulacaoPorInstante(arr,vetor, geracao);

                p.getAxis("x").setLabel("Tempo", "Arial", 15);
                p.getAxis("y").setLabel("TotalPopulacao", "Arial", 15);

                DataSetPlot s = new DataSetPlot(getTotalPopulacaoPorInstante);
                s.setTitle("Graph");
                s.setPlotStyle(myPlotStyle);

                p.set("lmargin", "at screen 0.15");
                p.set("rmargin", "at screen 0.85");
                p.set("bmargin", "at screen 0.15");
                p.set("tmargin", "at screen 0.85");
                p.addPlot(s);

                p.newGraph();

//            if (showOnScreen) {
//            p.plot();
//        }
//
//        return p;


                System.out.println("Deseja guardar o grafico?");
                System.out.println("Sim » 1 || Nao » 2");
                int opcao = sc.nextInt();
                while ( opcao < 0 || opcao > 3) {
                    System.out.println("Escolha Invalida. Tente Novamente!");
                    opcao = sc.nextInt();
                }
                if (opcao == 1) {
                    System.out.println("==============================");
                    System.out.println("Escolha a opcao de saida desejada :");
                    System.out.println("1 - .PNG");
                    System.out.println("2 - .TXT");
                    System.out.println("3 - .EPS");
                    System.out.println("==============================");
                    opcao = sc.nextInt();
                    while ( opcao < 0 || opcao > 3) {
                        System.out.println("Opcao Invalida. Tente Novamente!");
                    }
                    if (opcao == 1) {
                        String fileName = getFileName(outputFileInfo,".png");

                        //GNUPlotTerminal png = new FileTerminal("png", fileName + ".png");

                        JavaPlot saveImage = new JavaPlot();
                        //saveImage.setTerminal(png);

                        //saveImage.addPlot("s");
                        //saveImage.plot();

                        savePNG(saveImage, fileName); //Senao utilizarmos LIMPAR
                    }
                    if (opcao == 2) {
                        String fileName = getFileName(outputFileInfo,".txt");

                        FileWriter file = new FileWriter(fileName + ".txt");
                        BufferedWriter buffWriter = new BufferedWriter(file);
                        //ciclo for para escrever no ficheiro txt asteriscos

                        buffWriter.close();
                    }
                    if (opcao == 3) {
                        //guarda ficheiro em .epg
                    }
                }
            }
            if (modoInterativo == true) {
                System.out.println("==============================");
                System.out.println("Grafico crescimento da populacao");
                System.out.println("==============================");

                JavaPlot p = new JavaPlot();

                PlotStyle myPlotStyle = new PlotStyle();
                myPlotStyle.setStyle(Style.LINESPOINTS);
                myPlotStyle.setLineWidth(1);
                //RgbPlotColor = new RgbPlotColor(
                myPlotStyle.setLineType(NamedPlotColor.BLUE);
                myPlotStyle.setPointType(7);
                myPlotStyle.setPointSize(1);

                double[][] getTotalVariacaoPorInstante = taxaVarPopulacaoPorInstante(arr,vetor, geracao);

                p.getAxis("x").setLabel("Tempo", "Arial", 15);
                p.getAxis("y").setLabel("TaxaVariacaoDaPopulacao", "Arial", 15);

                DataSetPlot s = new DataSetPlot(getTotalVariacaoPorInstante);
                s.setTitle("Graph");
                s.setPlotStyle(myPlotStyle);

                p.set("lmargin", "at screen 0.15");
                p.set("rmargin", "at screen 0.85");
                p.set("bmargin", "at screen 0.15");
                p.set("tmargin", "at screen 0.85");
                p.addPlot(s);

                p.newGraph();

                System.out.println("Deseja guardar o grafico?");
                System.out.println("Sim » 1 || Nao » 2");
                int opcao = sc.nextInt();
                while ( opcao < 0 || opcao > 3) {
                    System.out.println("Escolha Invalida. Tente Novamente!");
                    opcao = sc.nextInt();
                }
                if (opcao == 1) {
                    System.out.println("==============================");
                    System.out.println("Escolha a opcao de saida desejada :");
                    System.out.println("1 - .PNG");
                    System.out.println("2 - .TXT");
                    System.out.println("3 - .EPS");
                    System.out.println("==============================");
                    opcao = sc.nextInt();
                    while ( opcao < 0 || opcao > 3) {
                        System.out.println("Opcao Invalida. Tente Novamente!");
                    }
                    if (opcao == 1) {
                        String fileName = getFileName(outputFileInfo,".png");

                        //GNUPlotTerminal png = new FileTerminal("png", fileName + ".png");

                        JavaPlot saveImage = new JavaPlot();
                        //saveImage.setTerminal(png);

                        //saveImage.addPlot("s");
                        //saveImage.plot();

                        savePNG(saveImage, fileName); //Senao utilizarmos LIMPAR
                    }
                    if (opcao == 2) {
                        String fileName = getFileName(outputFileInfo,".txt");

                        FileWriter file = new FileWriter(fileName + ".txt");
                        BufferedWriter buffWriter = new BufferedWriter(file);
                        //ciclo for para escrever no ficheiro txt asteriscos

                        buffWriter.close();
                    }
                    if (opcao == 3) {
                        //guarda ficheiro em .epg
                    }
                }
            }
            if (modoInterativo == true) {
                System.out.println("==============================");
                System.out.println("Grafico : Distribuicao do total de cada classe (Nao Normalizado)");
                System.out.println("==============================");

                JavaPlot p = new JavaPlot();

                PlotStyle myPlotStyle = new PlotStyle();
                myPlotStyle.setStyle(Style.LINESPOINTS);
                myPlotStyle.setLineWidth(1);
                //RgbPlotColor = new RgbPlotColor(
                myPlotStyle.setLineType(NamedPlotColor.BLUE);
                myPlotStyle.setPointType(7);
                myPlotStyle.setPointSize(1);

                double[][] getEvolucaoPopulacaoPorInstante = distribuicaoPopulacaoPorInstante(arr, vetor, geracao);

                p.getAxis("x").setLabel("Tempo", "Arial", 15);
                p.getAxis("y").setLabel("EvolucaoDaEspecie", "Arial", 15);

                DataSetPlot s = new DataSetPlot(getEvolucaoPopulacaoPorInstante);
                s.setTitle("Graph");
                s.setPlotStyle(myPlotStyle);

                p.set("lmargin", "at screen 0.15");
                p.set("rmargin", "at screen 0.85");
                p.set("bmargin", "at screen 0.15");
                p.set("tmargin", "at screen 0.85");
                p.addPlot(s);

                p.newGraph();

                System.out.println("Deseja guardar o grafico?");
                System.out.println("Sim » 1 || Nao » 2");
                int opcao = sc.nextInt();
                while ( opcao < 0 || opcao > 3) {
                    System.out.println("Escolha Invalida. Tente Novamente!");
                    opcao = sc.nextInt();
                }
                if (opcao == 1) {
                    System.out.println("==============================");
                    System.out.println("Escolha a opcao de saida desejada :");
                    System.out.println("1 - .PNG");
                    System.out.println("2 - .TXT");
                    System.out.println("3 - .EPS");
                    System.out.println("==============================");
                    opcao = sc.nextInt();
                    while ( opcao < 0 || opcao > 3) {
                        System.out.println("Opcao Invalida. Tente Novamente!");
                    }
                    if (opcao == 1) {
                        String fileName = getFileName(outputFileInfo,".png");

                        //GNUPlotTerminal png = new FileTerminal("png", fileName + ".png");

                        JavaPlot saveImage = new JavaPlot();
                        //saveImage.setTerminal(png);

                        //saveImage.addPlot("s");
                        //saveImage.plot();

                        savePNG(saveImage, fileName); //Senao utilizarmos LIMPAR
                    }
                    if (opcao == 2) {
                        String fileName = getFileName(outputFileInfo,".txt");

                        FileWriter file = new FileWriter(fileName + ".txt");
                        BufferedWriter buffWriter = new BufferedWriter(file);
                        //ciclo for para escrever no ficheiro txt asteriscos

                        buffWriter.close();
                    }
                    if (opcao == 3) {
                        //guarda ficheiro em .epg
                    }
                }
            }
            if (modoInterativo == true) {
                System.out.println("==============================");
                System.out.println("Grafico : Distribuicao do Total Normalizado");
                System.out.println("==============================");


                JavaPlot p = new JavaPlot();

                PlotStyle myPlotStyle = new PlotStyle();
                myPlotStyle.setStyle(Style.LINESPOINTS);
                myPlotStyle.setLineWidth(1);
                //RgbPlotColor = new RgbPlotColor(
                myPlotStyle.setLineType(NamedPlotColor.BLUE);
                myPlotStyle.setPointType(7);
                myPlotStyle.setPointSize(1);


                //double[][] graphArray = distribuicaoPopulacaoPorInstante(L, geracao, matrizLeslie); Chamar a funcao distribuicao normalizada com o passado das geraÃƒÂ§oes
                Matrix graphArray;//Chamar a funcao distribuicao normalizada com o passado das geracoes
                // Tem erro ainda precisamos de passar uma matriz*

                p.getAxis("x").setLabel("Tempo", "Arial", 15);
                p.getAxis("y").setLabel("DistribuicaoPopulacaoNormalizada", "Arial", 15);

                DataSetPlot s = new DataSetPlot();//colocar o grafico necessario
                s.setTitle("Graph");
                s.setPlotStyle(myPlotStyle);

                p.set("lmargin", "at screen 0.15");
                p.set("rmargin", "at screen 0.85");
                p.set("bmargin", "at screen 0.15");
                p.set("tmargin", "at screen 0.85");
                p.addPlot(s);

                p.newGraph();


                System.out.println("Deseja guardar o grafico?");
                System.out.println("Sim » 1 || Nao » 2");
                int opcao = sc.nextInt();
                while ( opcao < 0 || opcao > 3) {
                    System.out.println("Escolha Invalida. Tente Novamente!");
                    opcao = sc.nextInt();
                }
                if (opcao == 1) {
                    System.out.println("==============================");
                    System.out.println("Escolha a opcao de saida desejada :");
                    System.out.println("1 - .PNG");
                    System.out.println("2 - .TXT");
                    System.out.println("3 - .EPS");
                    System.out.println("==============================");
                    opcao = sc.nextInt();
                    while ( opcao < 0 || opcao > 3) {
                        System.out.println("Opcao Invalida. Tente Novamente!");
                    }
                    if (opcao == 1) {
                        String fileName = getFileName(outputFileInfo,".png");

                        //GNUPlotTerminal png = new FileTerminal("png", fileName + ".png");

                        JavaPlot saveImage = new JavaPlot();
                        //saveImage.setTerminal(png);
                        //saveImage.addPlot("s");
                        //saveImage.plot();

                        savePNG(saveImage, fileName); //Senao utilizarmos LIMPAR
                    }
                    if (opcao == 2) {
                        String fileName = getFileName(outputFileInfo,".txt");

                        FileWriter file = new FileWriter(fileName + ".txt");
                        BufferedWriter buffWriter = new BufferedWriter(file);
                        //ciclo for para escrever no ficheiro txt asteriscos

                        buffWriter.close();
                    }
                    if (opcao == 3) {
                        //guarda ficheiro em .epg
                    }
                }
            }
            if (modoInterativo == false) {
                if ( Y == 1) {
                    String fileName = getFileName(outputFileInfo,".png");

                    //GNUPlotTerminal png = new FileTerminal("png", fileName + ".png");

                    JavaPlot saveImage = new JavaPlot();
                    //saveImage.setTerminal(png);

                    //saveImage.addPlot("s");
                    //saveImage.plot();

                    savePNG(saveImage, fileName); //Senao utilizarmos LIMPAR
                }
                if ( Y == 2) {
                    String fileName = getFileName(outputFileInfo,".txt");

                    FileWriter file = new FileWriter(fileName + ".txt");
                    BufferedWriter buffWriter = new BufferedWriter(file);
                    //ciclo for para escrever no ficheiro txt asteriscos

                    buffWriter.close();
                }
                if ( Y == 3) {
                    //mostrar grafico

                    //guarda grafico em .epS
                }
                if (modoInterativo == true) {
                    //limpar dados do java plot

                    confirmarContinuar(); //Continuar para o menu
                }
            }
        }
    }
    //=====================================================================================================================
    public static void confirmarContinuar() { //regressar ao menu
        System.out.println("Enter para continuar.");
        sc.nextLine();
    }
    //======================================================================================================================

    /**
     * Este metodo salva o ficheiro png criado(com o grafico especifico) no metodo gnuplot.
     *
     * @param p
     * @param name
     */
    private static void savePNG(JavaPlot p, String name) {

        FileTerminal png = new FileTerminal("png", name);
        File file = new File(name);

        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (FileNotFoundException ex) { // caso nao encontre o ficherio com o catch lancaa uma excecao de erro
            System.err.print(ex);
        } catch (IOException ex) {
            System.err.print(ex);
        }

        p.setTerminal(png);
        p.plot();

    }
    //=======================================================================================================================


}