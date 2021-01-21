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
    public static void main(String[] args) throws FileNotFoundException {
        double[] matriz = new double[MAX]; //Array que guarda as informacoes da quantidade de populacao recebido como parametro
        double[] matrizSobrevivencia = new double[MAX]; //Array que guarda as informacoes das taxas de sobrevivencia recebidos como parametro
        double[] matrizNatalidade = new double[MAX]; //Array que guarda as informacoes das taxas de natalidade recebidas como parametro
        String[] outputFileInfo = new String[3]; //Usado para definir o nome dos ficheiros de output
        boolean endProgram = true, modoInterativo; //usado para definir quando acabar o programa e se os modulo funcionam em modo interativo ou nao interativo

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
                while (opcao < -1 || opcao > 3) { //confirmar se a opcao e valida
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
        } else if (args.length == 7) {//Modo não interativo pede 10 argumentos
            int numeroGeracoes = Integer.parseInt(args[0]); //Guarda o numero de geracoes
            int Y = Integer.parseInt(args[1]); //Guarda o formato de saída do ficheiro
            int vetorProprio = Integer.parseInt(args[2]); //Guarda o valor do vetor próprio
            int dimensaoPopulacaoCadaGeracao = Integer.parseInt(args[3]); //Guarda dimensão da população a cada geração
            int variacaoPopulacaoGeracoes = Integer.parseInt(args[4]); //Guarda da variação da popuação entre gerações.
            String nomeEstudo = args[5];
            String nomeSaida = args[6];
            modoInterativo = false;
            verificarArgumentos(Y,numeroGeracoes,dimensaoPopulacaoCadaGeracao,vetorProprio,variacaoPopulacaoGeracoes);


        } else {
            System.out.println("Numero de argumentos incorreto ou argumentos invalidos.");
        }
        
        /*
        String[] linha1 = new String[0];
        String[] linha2 = new String[0];
        String[] linha3 = new String[0];
        String linha = ler.nextLine();
        int z = 1;

        while (z != 4) {
            if (linha.trim().length() != 0){        //se a linha não for nula (se tiver espaços em branco corta-os)
                if(z == 1){
                    linha1 = linha.split(", ");
                }else if(z == 2){
                    linha2 = linha.split(", ");
                }else{
                    linha3 = linha.split(", ");
                }
                z++;
                linha = ler.nextLine();
            } else {
                linha = ler.nextLine();
            }
        }

        double[] vetor = toMatrix(linha1, linha2, linha3, 'x');
        double[] matrizSobrevivencia = toMatrix(linha1, linha2, linha3, 's');
        double[] matrizNatalidade = toMatrix(linha1, linha2, linha3, 'f');


        double[][]arrElevado;

        //para a distribuição da população
        for(int i = 0; i <= n; i++){
           System.out.println("t = " + i);
           arrElevado = elevarArr(mLeslie, i);
           for(int z = 0; z <  distPopulacao(arrElevado, vetor).length; z++){
               System.out.println(distPopulacao(arrElevado, vetor)[z]);
           }
           System.out.println();
        }

        //para a distribuição normalizada
        arrElevado = elevarArr(mLeslie, n);
        distPopulacaoN(distPopulacao(arrElevado, vetor), dimPopulacao(mLeslie, vetor, n));

        //para a dimensão da população num determinado momento
        System.out.println(dimPopulacao(mLeslie, vetor, n));

        //para a taxa de variação da população
         System.out.printf("%.2f", taxaVarPopulacao(mLeslie, vetor, n));*/

    }
    //======================================================================================================================
    /**
     * Este método verifica se os argumentos de saída e os próprios valores introduzidos são verdadeiros ou falsos.
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
            System.out.println("Escolha de ficheiro de saída inválida.");
            argumentosValidos = false;
        }

        if (argumentosValidos = false) { //Caso haja deficiencia nos valores introduzidos, o programa fecha
            System.exit(0);
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
    /**
     * Este método faz a leitura separadamente dos diferentes tópicos existentes na matriz, ou seja a distribuição
     * inicial da população, a taxa de sobrevivência e a taxa de natalidade numa população segundo as gerações.
     *
     * @param matriz
     * @param matrizSobrevivencia
     * @param matrizNatalidade
     */
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
     * Este método faz a criação da matriz de leslie.
     *
     * @param matrizSobrevivencia
     * @param matrizNatalidade
     * @return matriz de leslie
     */
    public static Matrix criarMatrizLeslie(double[] matrizSobrevivencia, double[] matrizNatalidade) {
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

    //======================================================================================================================
    /**
     * Este método devolve o total de população.
     *
     * @param linha
     * @return matriz
     */
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
    /**
     * Este metodo faz o cálculo do produto entre duas matrizes de igual dimensões e devolve a matriz resultante desse
     * mesmo produto.
     *
     * @param arr1
     * @param arr2
     * @return
     */
    public static double[][] multArrays(double[][] arr1, double[][] arr2) {//Possivelmente um metodo private, uma vez que só é um método de auxilio

        double[][] arrM = new double[arr1.length][arr1.length]; //Multiplica duas matrizes de ordem igual

        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1.length; j++) {
                for (int k = 0; k < arr1.length; k++) {
                    arrM[i][j] = arrM[i][j] + arr1[i][k] * arr2[k][j];
                }
            }
        }
        return arrM; //devolve o produto
    }

    //======================================================================================================================
    /**
     * Este método devolve a matriz identidade.
     *
     * @param n
     * @return matriz identidade
     */
    public static double[][] arrId(int n) {

        double[][] I = new double[n][n]; //Calculo da matriz identidade
        for (int i = 0; i < n; i++) {
            I[i][i] = 1;
        }
        return I; //devolve uma matriz identidade
    }

    //======================================================================================================================
    /**
     * Este método faz o cálculo da matriz levantado a um valor n.
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
     * Este método mostra os dados da matriz.
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
     * Este método calcula a distribuição não normalizada da população para um determinado tempo
     *
     * @param arr
     * @param vetor
     * @return distribuição não normalizada da população
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

    //Calcula os valores proprios e dá return do valor com maior módulo
    public static double maiorValorProprio(Matrix matrizLeslie){

        double maior = -1;
        EigenDecompositor eigenD = new EigenDecompositor(matrizLeslie);
        Matrix [] mattD = eigenD.decompose();

        double matA [][] = mattD[0].toDenseMatrix().toArray();
        double matB [][] = mattD[1].toDenseMatrix().toArray();

        for(int i = 0; i < matB.length; i++) {
            for(int j = 0; j < matB.length; j++){
                if(Math.abs(matB[i][j]) > maior){
                    maior = Math.abs(matB[i][j]);
                }
            }
        }
        return maior;
    }

    //======================================================================================================================
    //Calcula a distribuição normalizada da população para um determinado t ?????????????????????????????????
    public static double[] distPopulacaoN(double[] dist, double total){

        double[] distribuicaoN = new double[dist.length];
        for(int i = 0; i < distribuicaoN.length; i++){
            distribuicaoN[i] = dist[i]/total;
        }
        return distribuicaoN;
    }

    //======================================================================================================================

    /**
     * Este método calcula a dimensão da população num determinado instante  t(tempo)
     *
     * @param arr
     * @param vetor
     * @param t
     * @return dimensão da população num determinado instante
     */
    public static double dimPopulacao(double[][]arr, double[]vetor, int t){

        double total = 0;
        double[][]arrElevado;

        for(int i = 0; i <= t; i++){
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
     * Este método devolve uma matriz de uma dimensão de uma população ao longo do tempo.
     *
     * @param arrayTarget
     * @param t
     * @return matriz de uma dimensão de uma população ao longo do tempo
     */
    public static int[][] dimPopulacaoPorInstante(int[] arrayTarget, int t) {

        int[][] graphArray = new int[t][2];

        for (int i = 0; i < t; i++) {
            //graphArray[i][0] = (i + 1);
            graphArray[i][0] = arrayTarget[i];
        }
        return graphArray;
    }

    //======================================================================================================================
    /**
     * Este método executa o cálculo da taxa de variação da população.
     *
     * @param arr
     * @param vetor
     * @param t
     * @return taxa de variação
     */
    public static double taxaVarPopulacao(double[][]arr, double[]vetor, int t){

        double taxa = dimPopulacao(arr, vetor, t) / dimPopulacao(arr, vetor, t-1);
        return taxa;
    }

    //======================================================================================================================
    public static int[][] taxaVarPopulacaoPorInstante(int[] arrayTarget, int t) {

        int[][] arr = new int[t][2];
//        double taxaVariacao = taxaVarPopulacao(arr, arrayTarget, t);

        for (int i = 0; i < t; i++) {
            //System.out.println("Momento :" + i);
            //taxaPopulacaoPorInstante = taxaVarPopulacao(arr, arrayTarget, i);
            // graphArray[i][0] = (i + 1);
            arr[i][1] = arrayTarget[i];

        }
        return arr;
    }

    //======================================================================================================================

    /**
     * Este método mostra a distribuição da população ao longo do tempo.
     *
     * @param L
     * @param geracao
     * @param matrizLeslie
     */
    public static void distribuicaoPopulacaoPorInstante(Matrix L, int geracao, Matrix matrizLeslie) {
        for (int j = 0; j <= geracao; j++) {
            System.out.println("Momento :" + j);
            System.out.println(populacaoDistribuicaoNormalizada(L, j, matrizLeslie));
        }
    }

    //public static double[][] distribuicaoPopulacaoPorInstante(Matrix L, int geracao, Matrix matrizLeslie) {
    // double[][] distPopulacaoPorInstante = new double[geracao][2];
    //
    //        for (int j = 0; j <= geracao; j++) {
    //            System.out.println("Momento :" + j);
    //            distPopulacaoPorInstante = populacaoDistribuicaoNormalizada(L, j, matrizLeslie);
    //        }
    //        return distPopulacaoPorInstante;
    //
    //    }




    //======================================================================================================================
    /**
     * Este método faz o cálculo e devolve a distribuição da população normalizada.
     *
     * @param L
     * @param geracao
     * @param matrizLeslie
     * @return distribuição da população normalizada
     */
    public static Matrix populacaoDistribuicaoNormalizada(Matrix L, int geracao, Matrix matrizLeslie) {
        Matrix matrix = L.power(geracao);
        return matrix.multiply(matrizLeslie);
    }

    //======================================================================================================================
    /**
     * Este método verifica se o ficheiro é válido ou não.
     *
     * @param result
     * @return true se o ficheiro for válido, false se o ficheiro for null ou inválido
     */
    private static boolean isFileValid(String result) {
        return ((result != null) && (new File(result).isFile()));
    }

    //======================================================================================================================
    /**
     * Método que devolve o nome do ficheiro.
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
     * Método que constrói os gráficos do total de população, da taxa de variação, da evolução da população e da
     * distribuição normalizada da população ao longo do tempo.
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
        int[] vetor = new int[0];

        if (modoInterativo == true) {
            System.out.println("==============================");
            System.out.println("Gráfico total da população");
            System.out.println("==============================");



            if (modoInterativo == true) {
                System.out.println("==============================");
                System.out.println("Gráfico total da população");
                System.out.println("==============================");

                JavaPlot p = new JavaPlot();

                PlotStyle myPlotStyle = new PlotStyle();
                myPlotStyle.setStyle(Style.LINESPOINTS);
                myPlotStyle.setLineWidth(1);
                //RgbPlotColor = new RgbPlotColor(
                myPlotStyle.setLineType(NamedPlotColor.BLUE);
                myPlotStyle.setPointType(7);
                myPlotStyle.setPointSize(1);

                int[][] getTotalPopulacaoPorInstante = dimPopulacaoPorInstante(vetor, geracao); //Ver o metodo, uma vez que ele so preenche basicamente a matriz

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


                System.out.println("Deseja guardar o gráfico?");
                System.out.println("Sim -» 1 || Não -» 2");
                int opcao = sc.nextInt();
                while ( opcao < 0 || opcao > 3) {
                    System.out.println("Escolha Inválida. Tente Novamente!");
                    opcao = sc.nextInt();
                }
                if (opcao == 1) {
                    System.out.println("==============================");
                    System.out.println("Escolha a opção de saída desejada :");
                    System.out.println("1 - .PNG");
                    System.out.println("2 - .TXT");
                    System.out.println("3 - .EPS");
                    System.out.println("==============================");
                    opcao = sc.nextInt();
                    while ( opcao < 0 || opcao > 3) {
                        System.out.println("Opção Inválida. Tente Novamente!");
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
                System.out.println("Gráfico crescimento da população");
                System.out.println("==============================");

                JavaPlot p = new JavaPlot();

                PlotStyle myPlotStyle = new PlotStyle();
                myPlotStyle.setStyle(Style.LINESPOINTS);
                myPlotStyle.setLineWidth(1);
                //RgbPlotColor = new RgbPlotColor(
                myPlotStyle.setLineType(NamedPlotColor.BLUE);
                myPlotStyle.setPointType(7);
                myPlotStyle.setPointSize(1);

                int[][] getTotalVariacaoPorInstante = taxaVarPopulacaoPorInstante(vetor, geracao); //devolver matriz****

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

                System.out.println("Deseja guardar o gráfico?");
                System.out.println("Sim -» 1 || Não -» 2");
                int opcao = sc.nextInt();
                while ( opcao < 0 || opcao > 3) {
                    System.out.println("Escolha Inválida. Tente Novamente!");
                    opcao = sc.nextInt();
                }
                if (opcao == 1) {
                    System.out.println("==============================");
                    System.out.println("Escolha a opção de saída desejada :");
                    System.out.println("1 - .PNG");
                    System.out.println("2 - .TXT");
                    System.out.println("3 - .EPS");
                    System.out.println("==============================");
                    opcao = sc.nextInt();
                    while ( opcao < 0 || opcao > 3) {
                        System.out.println("Opção Inválida. Tente Novamente!");
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
                System.out.println("Gráfico : Distribuição do total de cada classe (Não Normalizado)");
                System.out.println("==============================");

                JavaPlot p = new JavaPlot();

                PlotStyle myPlotStyle = new PlotStyle();
                myPlotStyle.setStyle(Style.LINESPOINTS);
                myPlotStyle.setLineWidth(1);
                //RgbPlotColor = new RgbPlotColor(
                myPlotStyle.setLineType(NamedPlotColor.BLUE);
                myPlotStyle.setPointType(7);
                myPlotStyle.setPointSize(1);

                //Falta possivelmente o metodo para a evolucao de uma especie ao longo do tempo, devolver em matriz para preencher o grafo*
                int[][] getEvolucaoPopulacaoPorInstante; // falta o metodo

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

                System.out.println("Deseja guardar o gráfico?");
                System.out.println("Sim -» 1 || Não -» 2");
                int opcao = sc.nextInt();
                while ( opcao < 0 || opcao > 3) {
                    System.out.println("Escolha Inválida. Tente Novamente!");
                    opcao = sc.nextInt();
                }
                if (opcao == 1) {
                    System.out.println("==============================");
                    System.out.println("Escolha a opção de saída desejada :");
                    System.out.println("1 - .PNG");
                    System.out.println("2 - .TXT");
                    System.out.println("3 - .EPS");
                    System.out.println("==============================");
                    opcao = sc.nextInt();
                    while ( opcao < 0 || opcao > 3) {
                        System.out.println("Opção Inválida. Tente Novamente!");
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
                System.out.println("Gráfico : Distribuição do Total Normalizado");
                System.out.println("==============================");


                JavaPlot p = new JavaPlot();

                PlotStyle myPlotStyle = new PlotStyle();
                myPlotStyle.setStyle(Style.LINESPOINTS);
                myPlotStyle.setLineWidth(1);
                //RgbPlotColor = new RgbPlotColor(
                myPlotStyle.setLineType(NamedPlotColor.BLUE);
                myPlotStyle.setPointType(7);
                myPlotStyle.setPointSize(1);

                double[][] L = new Matrix[0][0];
                double[][] matrizLeslie = new Matrix[0][0];

                //double[][] graphArray = distribuicaoPopulacaoPorInstante(L, geracao, matrizLeslie); Chamar a função distribuicao normalizada com o passado das geraçoes
                Matrix graphArray = distribuicaoPopulacaoPorInstante(L, geracao, matrizLeslie); //Chamar a função distribuicao normalizada com o passado das geraçoes
                // Tem erro ainda precisamos de passar uma matriz*

                p.getAxis("x").setLabel("Tempo", "Arial", 15);
                p.getAxis("y").setLabel("DistribuicaoPopulacaoNormalizada", "Arial", 15);

                DataSetPlot s = new DataSetPlot(graphArray);
                s.setTitle("Graph");
                s.setPlotStyle(myPlotStyle);

                p.set("lmargin", "at screen 0.15");
                p.set("rmargin", "at screen 0.85");
                p.set("bmargin", "at screen 0.15");
                p.set("tmargin", "at screen 0.85");
                p.addPlot(s);

                p.newGraph();


                System.out.println("Deseja guardar o gráfico?");
                System.out.println("Sim -» 1 || Não -» 2");
                int opcao = sc.nextInt();
                while ( opcao < 0 || opcao > 3) {
                    System.out.println("Escolha Inválida. Tente Novamente!");
                    opcao = sc.nextInt();
                }
                if (opcao == 1) {
                    System.out.println("==============================");
                    System.out.println("Escolha a opção de saída desejada :");
                    System.out.println("1 - .PNG");
                    System.out.println("2 - .TXT");
                    System.out.println("3 - .EPS");
                    System.out.println("==============================");
                    opcao = sc.nextInt();
                    while ( opcao < 0 || opcao > 3) {
                        System.out.println("Opção Inválida. Tente Novamente!");
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
    //=====================================================================================================================
    public static void confirmarContinuar() { //regressar ao menu
        System.out.println("Enter para continuar.");
        sc.nextLine();
    }
    //======================================================================================================================
    private static void savePNG(JavaPlot p, String name) {

        FileTerminal png = new FileTerminal("png", name);
        File file = new File(name);

        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (FileNotFoundException ex) { // caso nao encontre o ficherio com o catch lança uma exceção de erro
            System.err.print(ex);
        } catch (IOException ex) {
            System.err.print(ex);
        }

        p.setTerminal(png);
        p.plot();

    }
    //=======================================================================================================================

}