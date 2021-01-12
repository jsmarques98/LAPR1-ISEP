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
    }
    //======================================================================================================================
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

    public static double[][] multArrays(double[][] arr1, double[][] arr2) {

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
    private static double[][] arrId(int n) {

        double[][] I = new double[n][n]; //Calculo da matriz identidade
        for (int i = 0; i < n; i++) {
            I[i][i] = 1;
        }
        return I; //devolve uma matriz identidade
    }

    //======================================================================================================================
    public static void printMatriz(double[][] matriz) {
        for (double[] numeros : matriz) {
            System.out.println(Arrays.toString(numeros));
        }
    }

    //======================================================================================================================
    public static void distribuicaoPopulacaoPorInstante(Matrix L, int geracao, Matrix matrizLeslie) {
        for (int j = 0; j <= geracao; j++) {
            System.out.println("Momento :" + j);
            System.out.println(populacaoDistribuicaoNormalizada(L, j, matrizLeslie));
        }
    }

    //======================================================================================================================
    public static Matrix populacaoDistribuicaoNormalizada(Matrix L, int geracao, Matrix matrizLeslie) {
        Matrix matrix = L.power(geracao);
        return matrix.multiply(matrizLeslie);
    }

    //======================================================================================================================
    private static boolean isFileValid(String result) {
        return ((result != null) && (new File(result).isFile()));
    }

    //======================================================================================================================
    public static String getFileName(String[] outputFileInfo, String extensaoSaida) {
        String fileName = (outputFileInfo[0]);

        return (fileName);
    }

    //======================================================================================================================
    public static void gnuPlot(int Y, String[] outputFileInfo, boolean modoInterativo) throws IOException {
        int escolha = Y;
        String[] linha = new String[0];
        int geracao = 0;


        if (modoInterativo == true) {
            System.out.println("==============================");
            System.out.println("Gráfico total da população");
            System.out.println("==============================");

            //mostra o grafico da populacao total

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

            //mostra o grafico da populacao total

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

            //mostra o grafico da populacao total

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

            //double[][] graphArray = distribuicaoPopulacaoPorInstante(L, geracao, matrizLeslie); Chamar a função distribuicao normalizada com o passado das geraçoes

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
    public static void savePNG(JavaPlot p, String name) {

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