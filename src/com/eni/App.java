package com.eni;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App {

        Scanner input;

        // {{1,2,3},{4,5,6}} --- > {{1,4}, {2,5}, {3,6}}
        private int[][] transposeMat(int[][] matrix) {
            int[][] transposedMat = new int[matrix[0].length][matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    transposedMat[j][i] = matrix[i][j];
                }
            }
            return transposedMat;
        }

        private int[][] populateRandomly(int[][] matrix) {
            Random rand = new Random();
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    matrix[i][j] = rand.nextInt(9);
                }
            }
            return matrix;
        }

        private int getDimension() throws InputMismatchException {
            int n = 0;
            do {
                try {
                    n = input.nextInt();
                    if (n <= 0) {
                        System.out.println("Dimension must be positive. Try again");
                    }
                } catch (InputMismatchException exception) {
                    System.out.println("Dimension must be number. Try again");
                    input.next();
                }
            } while (n <= 0);
            return n;
        }

        private int[][] getMatrix(String message, int p) {
            int n = 0;
            int[][] matrix;
            System.out.println(message);
            if (p <= 0) {
                n = getDimension();
                p = getDimension();
                matrix = new int[n][p];
            } else {
                System.out.print(p + " ");
                n = getDimension();
                matrix = new int[p][n];
            }
            matrix = populateRandomly(matrix);
            return matrix;
        }

        private void printMatrix(int[][] matrix) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    System.out.print(matrix[i][j] + " ");
                }
                System.out.println("");
            }
        }

        /**
         * NOTE: Si le temps d'execution de cette function est très vite i.e utilisant
         * un algorithme simple, il est préferable d'utiliser la version synchrone sans
         * multithread. Mais si la function prend plus de temps comme des functions qui
         * fait de requête sur une base de donnée, un serveur, il est impérative
         * d'utiliser la version asynchrone en multithread.
         */
        /**
         * Thread.sleep() est pour une simulation. Si on l'enlève, ce sera la version
         * non multithread qui sera plus performant.
         */
        private int multiplyVector(int[] vector1, int[] vector2) {
            int res = 0;
            for (int i = 0; i < vector1.length; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                res += vector1[i] * vector2[i];
            }
            return res;
        }

        private void waitThreadsExecution(List<Thread> threadList) throws InterruptedException {
            for (Thread thread : threadList) {
                thread.join();
            }
        }

        private int[][] multiplyMatrixAsync(int[][] matrix1, int[][] matrix2) throws InterruptedException {
            int n = matrix1.length;
            int[][] matrix2Trans = transposeMat(matrix2);
            int m = matrix2Trans.length;
            int[][] matrixRes = new int[n][m];
            List<Thread> threadList = new ArrayList<Thread>();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    final int k = i;
                    final int l = j;
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            matrixRes[k][l] = multiplyVector(matrix1[k], matrix2Trans[l]);
                        }
                    };
                    thread.start();
                    threadList.add(thread);
                }
            }
            waitThreadsExecution(threadList);

            return matrixRes;
        }

        private int[][] multiplyMatrix(int[][] matrix1, int[][] matrix2) {
            int n = matrix1.length;
            int[][] matrix2Trans = transposeMat(matrix2);
            int m = matrix2Trans.length;
            int[][] matrixRes = new int[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    matrixRes[i][j] = multiplyVector(matrix1[i], matrix2Trans[j]);
                }
            }
            return matrixRes;
        }

        private App() throws InterruptedException {
            input = new Scanner(System.in);

            int[][] matrix1 = getMatrix("First matrix dimensions [ex: '15 20']: ", 0);
            int p = matrix1[0].length;
            int[][] matrix2 = getMatrix("Second matrix dimensions [ex: '" + p + " 40']: ", p);

            System.out.println("matrix1 :");
            printMatrix(matrix1);

            System.out.println("matrix2 :");
            printMatrix(matrix2);

            System.out.print("Do you want to use multithreads: (y/n): ");
            boolean async = false;
            int[][] matRes;
            input = new Scanner(System.in);
            String response = input.nextLine();
            if (response.toLowerCase().contentEquals("y")) {
                async = true;
            }

            long startTime = System.currentTimeMillis();

            if (async) {
                matRes = multiplyMatrixAsync(matrix1, matrix2);
            } else {
                matRes = multiplyMatrix(matrix1, matrix2);
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.println("matrix1 x matrix2 =");
            printMatrix(matRes);
            System.out.println("Duration : " + duration + " milliseconds");

            input.close();
        }

        public static void main(String[] args) throws InterruptedException {
            new App();
        }

}
