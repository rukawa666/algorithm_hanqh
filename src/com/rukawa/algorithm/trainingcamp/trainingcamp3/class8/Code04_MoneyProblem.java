package com.rukawa.algorithm.trainingcamp.trainingcamp3.class8;

/**
 * Created with Intellij IDEA
 *
 * @Author：SuperHai
 * @Date：2020-10-08 21:44
 * @Version：1.0
 */
public class Code04_MoneyProblem {
    /**
     * int[] d,d[i]:i号怪兽的能力
     * int[] p,p[i]:i号怪兽要求的钱
     * 开始时你的能力是0，你的目标是从0号怪兽开始，通过所有的怪兽
     * 如果你当前的能力，小于i号怪兽的能力，你必须付出p[i]的钱，贿赂这个怪兽，然后
     * 怪兽就会加入你，它的能力直接累加到你的能力上；如果你当前的能力，大于等于i号怪兽的
     * 能力，你可以直接通过，你的能力并不会下降，你也可以选择贿赂这个怪兽，然后怪兽就会
     * 加入你，它的能力直接累加到你的能力上。
     * 返回通过所有怪兽，需要花的最小钱数
     */


    public static long minMoney1(int[] d, int[] p) {
        return process(d, p, 0, 0);
    }

    /**
     *
     * @param d i号怪兽的能力
     * @param p 贿赂i号怪兽需要花的钱数
     * @param ability   当前你所具备的能力
     * @param index 来到了第index个怪兽的面前
     * @return  目前，你的能力是ability，你来到了index号怪兽的面前，如果要通过后续所有的怪兽，
     * 请返回需要花的最少的钱数
     */
    public static long process(int[] d, int[] p, int ability, int index) {
        if (index == d.length) {
            return 0;
        }

        if (ability < d[index]) {
            return p[index] + process(d, p, ability + d[index], index + 1);
        } else {    // 能力足够，可以贿赂，也可以不贿赂
            return Math.min(
                    // 贿赂
                    p[index] + process(d, p, ability + d[index], index + 1),
                    // 不贿赂
                    process(d, p, ability, index + 1));
        }
    }

    // 时间复杂度O（N * 钱数的累加和），适用于钱数和不是特大
    public static long minMoney2(int[] d, int[] p) {
        int sum = 0;
        for (int num : d) {
            sum += num;
        }
        // 怪兽能力为行，严格花的钱数为列
        // 含义：从0号怪兽通关到i号怪兽，要求严格花的就是j元，得到的最大能力是多少
        long[][] dp = new long[d.length + 1][sum + 1];
        for (int i = 0; i <= sum; i++) {
            dp[0][i] = 0;
        }
        for (int cur = d.length - 1; cur >= 0; cur--) {
            for (int hp = 0; hp <= sum; hp++) {
                if (hp + d[cur] > sum) {
                    continue;
                }
                if (hp < d[cur]) {
                    dp[cur][hp] = p[cur] + dp[cur + 1][hp + d[cur]];
                } else {
                    dp[cur][hp] = Math.min(p[cur] + dp[cur + 1][hp + d[cur]], dp[cur + 1][hp]);
                }
            }
        }
        return dp[0][0];
    }


    public static long minMoney3(int[] d, int[] p) {
        int sum = 0;
        for (int num : p) {
            sum += num;
        }
        // dp[i][j]含义：
        // 能经过0～i的怪兽，且花钱为j（花钱的严格等于j）时的武力值最大是多少？
        // 如果dp[i][j]==-1，表示经过0～i的怪兽，花钱为j是无法通过的，或者之前的钱怎么组合也得不到正好为j的钱数
        int[][] dp = new int[d.length][sum + 1];
        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j <= sum; j++) {
                dp[i][j] = -1;
            }
        }
        // 经过0～i的怪兽，花钱数一定为p[0]，达到武力值d[0]的地步。其他第0行的状态一律是无效的
        dp[0][p[0]] = d[0];
        for (int i = 1; i < d.length; i++) {
            for (int j = 0; j <= sum; j++) {
                // 可能性一，为当前怪兽花钱
                // 存在条件：
                // j - p[i]要不越界，并且在钱数为j - p[i]时，要能通过0～i-1的怪兽，并且钱数组合是有效的。
                if (j >= p[i] && dp[i - 1][j - p[i]] != -1) {
                    dp[i][j] = dp[i - 1][j - p[i]] + d[i];
                }
                // 可能性二，不为当前怪兽花钱
                // 存在条件：
                // 0~i-1怪兽在花钱为j的情况下，能保证通过当前i位置的怪兽
                if (dp[i - 1][j] >= d[i]) {
                    // 两种可能性中，选武力值最大的
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j]);
                }
            }
        }
        int ans = 0;
        // dp表最后一行上，dp[N-1][j]代表：
        // 能经过0～N-1的怪兽，且花钱为j（花钱的严格等于j）时的武力值最大是多少？
        // 那么最后一行上，最左侧的不为-1的列数(j)，就是答案
        for (int j = 0; j <= sum; j++) {
            if (dp[d.length - 1][j] != -1) {
                ans = j;
                break;
            }
        }
        return ans;
    }

    public static int[][] generateTwoRandomArray(int len, int value) {
        int size = (int) (Math.random() * len) + 1;
        int[][] arrs = new int[2][size];
        for (int i = 0; i < size; i++) {
            arrs[0][i] = (int) (Math.random() * value) + 1;
            arrs[1][i] = (int) (Math.random() * value) + 1;
        }
        return arrs;
    }

    public static void main(String[] args) {
        int len = 10;
        int value = 20;
        int testTimes = 1000000;
        for (int i = 0; i < testTimes; i++) {
            int[][] arrs = generateTwoRandomArray(len, value);
            int[] d = arrs[0];
            int[] p = arrs[1];
            long ans1 = minMoney1(d, p);
            long ans2 = minMoney2(d, p);
            long ans3 = minMoney3(d, p);
            if (ans1 != ans2 || ans2 != ans3) {
                System.out.println("oops!");
            }
        }
    }
}