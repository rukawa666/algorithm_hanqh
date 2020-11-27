package com.rukawa.algorithm.trainingcamp.trainingcamp5.class4;

import java.util.Arrays;

/**
 * Created with Intellij IDEA
 *
 * @Author：SuperHai
 * @Date：2020-11-05 21:49
 * @Version：1.0
 */
public class Code03_PostOfficeProblem {
    /**
     * 一条直线上有居民点，邮局只能建在居民点上。给定一个有序正数数组arr，每个值表示居民点的一维坐标，
     * 再给定一个正数 num，表示邮局数量。选择num个居民点建立num个邮局，使所有的居民点到最近邮局的
     * 总距离最短，返回最短的总距离
     * 【举例】
     * arr=[1,2,3,4,5,1000]，num=2。 arr必须有序
     * 第一个邮局建立在3位置，第二个邮局建立在1000位置。那么1位置到邮局的距离为2，2位置到邮局距离为1，
     * 3 位置到邮局的距离为0，4位置到邮局的距离为1，5位置到邮局的距离为2，1000 位置到邮局的距离为0。
     * 这种方案下的总距离为6，其他任何方案的总距离都不会比该方案的总距离更短，所以返回6
     */

    // O(N*M*N) 枚举O(N)
    public static int minDis1(int[] arr, int num) {
        if (arr == null || arr.length < 2 || num < 1) {
            return 0;
        }

        // record[L][R]:如果arr[L...R]位置上只建立一个邮局，总距离最小是多少？
        int[][] record = getRecord(arr);

        int n = arr.length;
        // 0~i范围上，建议j个邮局，总距离最小
        // 邮局数量可以到达num，所以num+1
        int[][] dp = new int[n][num + 1];
        // 第0列不考虑，0个邮局，直接放弃, dp[i][0]不考虑
        // 第0行，只有一个居民点，建立多个邮局，总距离都为0, dp[0][i]=0

        // 第1列，在居民点之间放1个邮局，总是放在0~i的中间位置
        for (int i = 0; i < n; i++) {
            // record[0][i]:表示0~i范围上放一个邮局，总距离是多少
            dp[i][1] = record[0][i];
        }
        // 普遍位置，一个样本做行，一个样本做列，总是考虑结尾的情况
        // 普遍1：j个邮局，最后一个邮局放置在最后一个居民点i，之前的j-1个邮局处理0~i-1个居民点，dp[i-1][j-1]
        // 普遍2：j个邮局，最优一个邮局放置在i-1~i之间的居民点，之前的j-1个邮局处理0~i-2个居民点，dp[i-2][j-1]
        // ... 最后一个邮局的情况，都枚举一遍，答案必然在其中
        for (int i = 1; i < n; i++) {
            // 邮局数量>居民点数量，总距离为0，所以邮局数量一定要小于等于居民点数量
            for (int j = 2; j <= Math.min(i, num); j++) {
                // 枚举最后一个邮局负责的范围，k~i的范围上放置一个邮局

                // 0~i位置放置一个邮局
                dp[i][j] = record[0][i];
                for (int k = i; k > 0; k--) {
                    // 0~k-1放置k-1个邮局，k~i放置一个邮局
                    dp[i][j] = Math.min(dp[i][j], dp[k - 1][j - 1] + record[k][i]);
                }
            }
        }
        return dp[n - 1][num];
    }

    public static int[][] getRecord(int[] arr) {
        int N = arr.length;
        int[][] record = new int[N][N];
        // 范围上的尝试，下半区不需要，L>R不需要
        // record[L][R] L==R 只有一个居民点，建立一个邮局，总距离必然是0
        for (int L = 0; L < N; L++) {
            for (int R = L + 1; R < N; R++) {
                /**
                 * 推导：
                 *  L  R  邮局
                 *  0  1  0 -> (0+1)/2
                 *  0  2  1 -> (0+2)/2
                 *  0  3  1 -> (0+3)/2
                 *  ...
                 *  邮局肯定在L...R的中位数, arr[(L+R)/2]：邮局的位置
                 */
                record[L][R] = record[L][R - 1] + Math.abs(arr[R] - arr[(L + R) >> 1]);
            }
        }
        return record;
    }

    // 四边形不等式优化
    // O(N*M)
    public static int minDis2(int[] arr, int num) {
        /**
         * 四边形不等式优化
         * 特征：
         *  1、每一个格子有枚举行为
         *  2、求的动态规划的值dp[i][j]对于i和j有单调关系；
         *    假设二维参数j不变，与一维参数i的关系，i范围增大，结果会变大；
         *    假设一维参数i不变，与二维参数j的关系，i增大，结果会变小；
         *  3、所有的dp值，概念上是一种区间划分问题；
         *  4、单独求一个格子的时候，不同时依赖本行和本列的值，或者都不依赖
         *
         * 结论：
         *  如果你求的动态规划，拥有上面的三个特征，
         *  假设0~4范围给3个邮局，最优划分(最后一个邮局)处理k~4的范围，
         *  当范围变大到5，还是3个邮局，最优划分是k'~5，最优划分位置不会出现在k的左边，一定存在k<=k'
         *
         *  下限：dp[i][j] 的上限 dp[i-1][j] 或者 dp[i][j-1] -> b
         *  上限：dp[i][j] 的下限 dp[i][j+1] 或者 dp[i+1][j] -> a
         *  所以a <= dp[i][j] <= b可以省略很多枚举情况
         */
        if (arr == null || arr.length < 2 || num < 1) {
            return 0;
        }

        // record[L][R]:如果arr[L...R]位置上只建立一个邮局，总距离最小是多少？
        int[][] record = getRecord(arr);

        int n = arr.length;
        // 0~i范围上，建议j个邮局，总距离最小
        // 邮局数量可以到达num，所以num+1
        int[][] dp = new int[n][num + 1];
        // 第0列不考虑，0个邮局，直接放弃, dp[i][0]不考虑
        // 第0行，只有一个居民点，建立多个邮局，总距离都为0, dp[0][i]=0

        // choose[i][j]：当时在求dp[i][j]的时候，k来到哪个位置最优，把此时的k记录下来
        int[][] choose = new int[n][num + 1];

        // 第1列，在居民点之间放1个邮局，总是放在0~i的中间位置
        for (int i = 0; i < n; i++) {
            // record[0][i]:表示0~i范围上放一个邮局，总距离是多少
            dp[i][1] = record[0][i];
        }
        // 普遍位置，一个样本做行，一个样本做列，总是考虑结尾的情况
        // 普遍1：j个邮局，最后一个邮局放置在最后一个居民点i，之前的j-1个邮局处理0~i-1个居民点，dp[i-1][j-1]
        // 普遍2：j个邮局，最优一个邮局放置在i-1~i之间的居民点，之前的j-1个邮局处理0~i-2个居民点，dp[i-2][j-1]
        // ... 最后一个邮局的情况，都枚举一遍，答案必然在其中
        for (int i = 1; i < n; i++) {
            // 邮局数量>居民点数量，总距离为0，所以邮局数量一定要小于等于居民点数量
            for (int j = Math.min(i, num); j >= 2; j--) {
                // 下界：上边的格子告诉我
                int down = choose[i - 1][j];
                // 上界：右侧的格子告诉, j就是最右的位置，认为没有上界，就是i了
                int up = j == Math.min(i, num) ? i : choose[i][j + 1];

                // 1~i位置放置一个邮局
                dp[i][j] = record[0][i];

                // 在下界和上界的区间内尝试
                for (int k = Math.max(1, down); k <= Math.min(up, i); k++) {

                    if (dp[k - 1][j - 1] + record[k][i] < dp[i][j]) {
                        // 1~k-1放置k-1个邮局，k~i放置一个邮局
                        dp[i][j] = dp[k - 1][j - 1] + record[k][i];
                        // choose[i][j]：记录此时的最优选择位置
                        choose[i][j] = k;
                    }
                }
            }
        }
        return dp[n - 1][num];

    }

    public static int minDistances1(int[] arr, int num) {
        if (arr == null || num < 1 || arr.length < num) {
            return 0;
        }
        int[][] w = new int[arr.length + 1][arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                w[i][j] = w[i][j - 1] + arr[j] - arr[(i + j) / 2];
            }
        }
        int[][] dp = new int[num][arr.length];
        for (int j = 0; j != arr.length; j++) {
            dp[0][j] = w[0][j];
        }
        for (int i = 1; i < num; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = 0; k <= j; k++) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 1][k] + w[k + 1][j]);
                }
            }
        }
        return dp[num - 1][arr.length - 1];
    }


    // for test
    public static int[] getSortedArray(int len, int range) {
        int[] arr = new int[len];
        for (int i = 0; i != len; i++) {
            arr[i] = (int) (Math.random() * range);
        }
        Arrays.sort(arr);
        return arr;
    }

    // for test
    public static void printArray(int[] arr) {
        for (int i = 0; i != arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    // for test
    public static void main(String[] args) {
        int[] arr = {1,4,8,10,20};
        int num = 3;
        System.out.println(minDis1(arr, num));
        System.out.println(minDis2(arr, num));

//        int times = 100; // test time
//        int len = 1000; // test array length
//        int range = 2000; // every number in [0,range)
//        int p = 50; // post office number max
//        long time1 = 0; // method1 all run time
//        long time2 = 0;// method2 all run time
//        long time3 = 0;
//        long start = 0;
//        long end = 0;
//        int res1 = 0;
//        int res2 = 0;
//        int res3 = 0;
//        for (int i = 0; i != times; i++) {
//            int office = (int) (Math.random() * p) + 1;
//            arr = getSortedArray(len, range);
//            start = System.currentTimeMillis();
//            res1 = minDistances1(arr, office);
//            end = System.currentTimeMillis();
//            time1 += end - start;
//            start = System.currentTimeMillis();
//            res2 = minDis2(arr, office);
//            end = System.currentTimeMillis();
//            time2 += end - start;
//
//            start = System.currentTimeMillis();
//            res3 = minDis1(arr, office);
//            end = System.currentTimeMillis();
//            time3 += end - start;
//            if (res1 != res2 || res1 != res3) {
//                printArray(arr);
//                break;
//            }
//            if (i % 10 == 0) {
//                System.out.print(". ");
//            }
//        }
//        System.out.println();
//        System.out.println("method1 all run time(ms): " + time1);
//        System.out.println("method2 all run time(ms): " + time2);
//        System.out.println("method3 all run time(ms): " + time3);

    }
}
