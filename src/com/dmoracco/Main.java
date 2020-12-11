package com.dmoracco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {


    public static void main(String[] args) {

        long previousTime = 0;
        long maxTime = 900000;
        long startTime = 0, endTime = 0, totalTime = 0;

        double ratio, expectedRatio;
        String ratioS, expectedRatioS;

        //EXACT TSP TEST
        int testsPerIteration = 50;
        long[] testResults = new long[100];

        System.out.printf("\n%8s%s", "", "Brute Force");
        System.out.printf("\n%8s%15s%15s%15s", "N", "Time", "Ratio", "Ex. Ratio");
        for (int n = 4; n < 100; n++){
            if (previousTime > maxTime) break;

            double[][] costMatrix = GenerateRandomCostMatrix(n, 100);
            //printCostMatrix(costMatrix);


            totalTime = 0;
            for (int x = 0; x < testsPerIteration; x++){
                startTime = GetCpuTime.getCpuTime();
                TspBruteForce(costMatrix);
                endTime = GetCpuTime.getCpuTime();
                totalTime = totalTime + (endTime - startTime);
            }
            previousTime = totalTime / testsPerIteration;
            testResults[n] = previousTime;

            if (n % 2 == 0 && n >= 8){
                ratio = (double)(testResults[n]) / testResults[n/2];
                expectedRatio = (double)(calculateFactorial(n)) / (calculateFactorial(n/2));
                ratioS = String.format("%10.2f", ratio);
                expectedRatioS = String.format("%10.2f", expectedRatio);

            } else {
                ratioS = "na";
                expectedRatioS = "na";
            }

            System.out.printf("\n%8d%15d%15s%15s", n, previousTime, ratioS, expectedRatioS);

        }

        //HEURISTIC TSP TEST
        maxTime = 90000000;
        long currentTime = 0;
        previousTime = 0;
        testsPerIteration = 100;

        System.out.printf("\n\n%8s%s", "", "Greedy");
        System.out.printf("\n%8s%15s%15s%15s", "N", "Time", "Ratio", "Ex. Ratio");
        for (int n = 50; n < 1000000; n = n*2){
            if (previousTime > maxTime) break;

            double[][] costMatrix = GenerateRandomCostMatrix(n, 100);
            //printCostMatrix(costMatrix);

            totalTime = 0;
            for (int x = 0; x < testsPerIteration; x++){
                startTime = GetCpuTime.getCpuTime();
                TspGreedy(costMatrix);
                endTime = GetCpuTime.getCpuTime();
                totalTime = totalTime + (endTime - startTime);
            }
            previousTime = currentTime;
            currentTime = totalTime / testsPerIteration;

            if (n % 2 == 0 && n >= 100){
                ratio = (double)(currentTime) / previousTime;
                // n^2*log_2(n);
                expectedRatio = (Math.pow(n, 2) * Math.log(n)/Math.log(2)) /
                        (Math.pow(n/2, 2) * Math.log(n/2)/Math.log(2));
                ratioS = String.format("%10.2f", ratio);
                expectedRatioS = String.format("%10.2f", expectedRatio);

            } else {
                ratioS = "na";
                expectedRatioS = "na";
            }

            System.out.printf("\n%8d%15d%15s%15s", n, currentTime, ratioS, expectedRatioS);

        }

        //HEURISTIC QUALITY TEST
        maxTime = 90000000;

        double totalDistance = 0;
        double avgHeuristicDistance = 0;
        double avgExactDistance = 0;
        double SQR = 0;

        previousTime = 0;
        testsPerIteration = 100;

        System.out.printf("\n\n%8s%s", "", "Heuristic Quality Test");
        System.out.printf("\n%8s%15s%15s%15s", "N", "Avg Cost", "Avg Exact", "Avg SQR");
        for (int n = 4; n < 50; n++){

            if (previousTime > maxTime) break;

            double[][] costMatrix = GenerateRandomCostMatrix(n, 100);

            totalTime = 0;
            for (int x = 0; x < testsPerIteration; x++){
                startTime = GetCpuTime.getCpuTime();
                totalDistance = totalDistance + TspBruteForce(costMatrix);
                endTime = GetCpuTime.getCpuTime();
                totalTime = totalTime + (endTime - startTime);
            }
            previousTime = totalTime / testsPerIteration;
            avgExactDistance = totalDistance / testsPerIteration;

            for (int y = 0; y < testsPerIteration; y++){
                totalDistance = totalDistance + TspGreedy(costMatrix);
            }
            avgHeuristicDistance = totalDistance / testsPerIteration;

            SQR = (double)(avgHeuristicDistance)/avgExactDistance;

            System.out.printf("\n%8d%15.2f%15.2f%15.2f", n, avgHeuristicDistance, avgExactDistance, SQR);

        }


    }

    public static double TspBruteForce(double[][] costMatrix){
        // https://www.geeksforgeeks.org/write-a-c-program-to-print-all-permutations-of-a-given-string/

        int vertices = costMatrix.length;
        ArrayList<Integer> initialPath = new ArrayList();
        PersistantDouble shortestLength = new PersistantDouble();
        PersistantArrayList bestPath = new PersistantArrayList();
        for (int i = 0; i < vertices; i++){
            initialPath.add(i);
        }
        for (int j = 0; j < vertices - 1; j++){
            shortestLength = shortestLength.add(costMatrix[j][j+1]);
        }
        shortestLength = shortestLength.add(costMatrix[vertices-1][0]);

        permuteList(costMatrix, initialPath, bestPath, 1, vertices-1, shortestLength);
        bestPath.list.add(0);
        return calculateDistance(costMatrix, bestPath.list);

//            System.out.printf("[");
//            for (int number: bestPath
//                 ) {
//                System.out.printf("%d, ", number);
//            }
//            System.out.printf("]\n");

//        System.out.printf("\nPath: ");
//        System.out.print(bestPath.list);
//        System.out.printf("   Distance: %8.2f", shortestLength.value);


    }

    public static ArrayList<Integer> permuteList(double[][] costMatrix, ArrayList<Integer> currentPath,
                                                 PersistantArrayList bestPath, int begin, int end,
                                                 PersistantDouble shortestLength){

        if (begin == end){
            // Determine if this is now the shortest path

//            System.out.printf("[");
//            for (int number: currentPath
//            ) {
//                System.out.printf("%d, ", number);
//            }
//            System.out.printf("]  ");
            // Find travel of current path
            double currentLength = calculateDistance(costMatrix, currentPath);
            //System.out.println(currentLength + " | " + shortestLength.value);

            if (currentLength < shortestLength.value){
                shortestLength.value = currentLength;
                //System.out.println(bestPath.list + " now " + currentPath);
                ArrayList<Integer> newList = new ArrayList<>();
                bestPath.set(currentPath);
            }
            return currentPath;

        } else {
            for (int i = begin; i <= end; i++){
                swapPath(currentPath, begin, i);
                currentPath = permuteList(costMatrix, currentPath, bestPath, begin+1, end, shortestLength);
                swapPath(currentPath, begin, i);
            }
        }
        return currentPath;
    }

    public static void swapPath(ArrayList<Integer>currentPath, int i, int j){
        int temp = currentPath.get(i);
        currentPath.set(i, currentPath.get(j));
        currentPath.set(j, temp);
    }

    public static double TspGreedy(double[][] costMatrix){
       int vertices = costMatrix.length;
       ArrayList visited = new ArrayList();

       int index = 0;
       int nextIndex = -1;
       int vertCount = vertices;
       double shortest;
       boolean deadend;
       int i;


       do {
           deadend = true;
           shortest = Integer.MAX_VALUE;
           // check i = 0 if we are at the end of the path
           i = 1;
           if (--vertCount == 0) i--;
           for (; i < vertices; i++){
               // check if vertex has been visited, skip current iteration if true
               if (visited.contains(i) ) continue;
               // check for null distance, proceed if false
               else if (costMatrix[index][i] != 0){
                   // at least one vertex to travel to.
                   deadend = false;
                   // change if current iteration is shorter
                   if (costMatrix[index][i] < shortest){
                       shortest = costMatrix[index][i];
                       nextIndex = i;
                   }
               }
           }
           if (deadend == true){
               System.out.println("NO SOLUTION!");
               break;
           } else {
               // shift index skipping initial zero so we can go back to it
               index = nextIndex;
               visited.add(index);
               //System.out.printf("\n%d", index);
           }

       } while (index != 0);

       visited.add(0, 0);
//       System.out.printf("\nPath: ");
//       System.out.print(visited);
//       System.out.printf("   Distance: %8.2f", calculateDistance(costMatrix, visited));
        return calculateDistance(costMatrix, visited);

    }

    public static double[][] GenerateRandomCostMatrix(int vertices, double maxEdgeCost){
        Random random = new Random();

        double[][] costMatrix = new double[vertices][vertices];
        double newCost = 0;

        // Generate random values for costMatrix and ensure symmetry
        for (int r = 0; r < vertices; r++){
            for (int c = 0; c <= r-1; c++){
                if (r == c) costMatrix[r][c] = 0;
                else {
                    newCost = maxEdgeCost * random.nextDouble();
                    costMatrix[r][c] = newCost;
                    costMatrix[c][r] = newCost;
                }

            }
        }

        return costMatrix;
    }

    public static double[][] GenerateRandomEuclideanCostMatrix(int vertices, int maxXY){
        Random r = new Random();
        int[][] euclideanGraph = new int[vertices][2];
        double[][] costMatrix = new double[vertices][vertices];

        // Generate random X,Y values
        for (int i = 0; i < vertices; i++){
            euclideanGraph[i][0] = r.nextInt(maxXY);
            euclideanGraph[i][1] = r.nextInt(maxXY);
            System.out.printf("(%d, %d)", euclideanGraph[i][0], euclideanGraph[i][1]);
        }

        // Calculate costMatrix with pythagorean values and ensure symmetry
        double distance = 0;
        for (int j = 0; j < vertices; j++){
           for (int k = 0; k <= j-1; k++){
               if (j == k) costMatrix[j][k] = 0;
               else {
                   distance = getDistance(euclideanGraph[j][0], euclideanGraph[j][1], euclideanGraph[k][0], euclideanGraph[k][1]);
                   costMatrix[j][k] = distance;
                   costMatrix[k][j] = distance;
               }
           }
        }

        return costMatrix;
    }

    public static double[][] GenerateRandomCircularGraphCostMatrix(int vertices, double radius){

        Random r = new Random();
        double[][] costMatrix = new double[vertices][vertices];
        double[] x = new double[vertices];
        double[] y = new double[vertices];

        // Create then shuffle vertices list
        ArrayList<Integer> list = new ArrayList();
        for (int v = 0; v < vertices; v++){
            list.add(v);
        }
        Collections.shuffle(list);
//        System.out.println();
//        System.out.println(list);

        // Generate X,Y values for vertices in a circle
        double stepAngle = (Math.PI * 2) / vertices;
        for (int s = 0; s < vertices; s++){
            // Fill in coordinates based on shuffled vertices
            x[list.get(s)] = radius * Math.sin(s * stepAngle);
            y[list.get(s)] = radius * Math.cos(s * stepAngle);
        }


        // Calculate costMatrix while ensuring symmetry
        double distance = 0;
        for (int i = 0; i < vertices; i++){
            for (int j = 0; j <= i-1; j++){
                distance = getDistance(x[i], y[i], x[j], y[j]);
                costMatrix[i][j] = distance;
                costMatrix[j][i] = distance;
            }
        }

        for (int d = 0; d < vertices; d++){
            System.out.printf("\n%d. (%f, %f) ", d , x[d], y[d]);
        }
        System.out.printf("\n\nExpected cost: %8.2f", (vertices* getDistance(x[list.get(0)], y[list.get(0)], x[list.get(1)], y[list.get(1)])));

        return costMatrix;
    }

    public static void printCostMatrix(double[][] costMatrix){

        int vertices = costMatrix.length;

        System.out.printf("\n%8s", "");
        for (int k = 0; k < vertices; k++){
            System.out.printf("%8d", k);
        }

        for (int i = 0; i < vertices; i++){
            System.out.printf("\n%8d", i);
            for (int j = 0; j < vertices; j++){
                System.out.printf("%8.2f", costMatrix[i][j]);
            }
        }

        System.out.println();
    }

    public static double getDistance(double x1, double y1, double x2, double y2){
        double distance = 0;

        // ((x1 - x2)^2 + (y1 - y2)^2)^0.5
        distance = Math.pow((x1 - x2), 2.0);
        distance = distance + Math.pow((y1 - y2),2.0);
        distance = Math.sqrt((distance));

        return distance;

    }

    public static double calculateDistance(double[][] costMatrix, ArrayList<Integer> path){
        double currentLength = 0;
        for (int j = 0; j < path.size() - 1; j++){
            currentLength = currentLength + costMatrix[path.get(j)][path.get(j+1)];
        }
        // add in travel back to node 0
        currentLength = currentLength + costMatrix[path.get((path.size())-1)][path.get(0)];

        return currentLength;
    }

    public static long calculateFactorial(int f){
        //https://www.baeldung.com/java-calculate-factorial

        long result = 1;
        for (int i = 2; i <= f; i++){
            result = result * i;
        }

        return result;
    }
}

