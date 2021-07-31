package com.rukawa.algorithm.types.advanced;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @className: Code_A7_TargetSum
 * @description: TODO 类描述
 * @author: 鎏川疯
 * @date: 2021/7/31 0031 19:30
 **/
public class Code_A7_TargetSum {
    /**
     * 给定一个数组，你可以在每个数字之前决定用+或者-
     * 但是必须所有的数字都参与
     * 再给定一个数target，请问最后算出target的方法数是多少？
     */

    // 暴力尝试
    public static int findTargetSumWays1(int[] arr, int s) {
        return process1(arr, 0, s);
    }

    // 可以使用arr[index...]所有的数字
    // 得出rest这个数，方法数是多少？
    public static int process1(int[] arr, int index, int rest) {
        if (index == arr.length) {
            return rest == 0 ? 1 : 0;
        }
        return process1(arr, index + 1, rest - arr[index]) + process1(arr, index + 1, rest + arr[index]);
    }

    // 暴力递归改傻缓存
    public static int findTargetSumWays2(int[] arr, int s) {
        return process2(arr, 0, s, new HashMap<>());
    }

    // index = 7, rest = 13, 返回值是256
    // map中记为  key="7_13" value=256
    public static int process2(int[] arr, int index, int rest, HashMap<Integer, HashMap<Integer, Integer>> dp) {
        if (dp.containsKey(index) && dp.get(index).containsKey(rest)) {
            return dp.get(index).get(rest);
        }
        int ans = 0;
        if (index == arr.length) {
            ans = rest == 0 ? 1 : 0;
        } else {
            ans = process2(arr, index + 1, rest - arr[index], dp) + process2(arr, index + 1, rest + arr[index], dp);
        }
        if (!dp.containsKey(index)) {
            dp.put(index, new HashMap<>());
        }
        dp.get(index).put(rest, ans);
        return ans;
    }

    // 最终版本
    /**
     * 优化：
     *  1、数组可以全部处理成非负的，不会影响最终结果
     *  2、如果出现数组全部加起来的和sum<target，则0种方法
     *  3、如果出现数组全部加起来的和sum和target的奇偶性不一样，则0种方法
     *  4、数组中所有取正数的数组定义为集合P，所有取负数的数组定义为集合N，一定有 P-N=target
     *     再等式两边同时加P+N,得出P-N+P+N=target+P+N, 2P=target+sum, P=(target+sum)/2
     *     原来的动态规划的规模是index*rest的大小，现在是index*sum的规模，表的大小减小一半
     *  5、填写整张二维表的时候，空间压缩技巧。原来的二维表可以用一个数组滚动下去得到答案即可。
     */
    public static int findTargetSumWays3(int[] arr, int target) {
        int sum = 0;
        for (int n : arr) {
            sum += n;
        }
        return sum < target || ((target & 1) ^ (sum & 1)) != 0 ? 0 : subSet(arr, (sum + target) >> 1);
    }

    // 经典背包动态规划
    // 在nums中自由选择数字，得出target的方法数有多少种？
    public static int subSet(int[] nums, int target) {
        int N = nums.length;
        int[][] dp = new int[N][target + 1];
        // nums 0~0范围上，怎么得出0的累加和，一种方法
        dp[0][0] = nums[0] == 0 ? 2 : 1;
        for (int j = 1; j <= target; j++) {
            dp[0][j] = nums[0] == j ? 1 : 0;
        }
        // TODO 此处有问题，如果都是0，此次循环有问题
        for (int i = 1; i < nums.length; i++) {
            dp[i][0] = nums[i] == 0 ? 2 : 1;
        }
        // dp[i][j] -> nums[0...i]上怎么得出j
        // 完全不使用i位置的数，dp[0...i-1][j]
        // 使用i位置的数，dp[0...i-1][j-nums[i]]
        for (int i = 1; i < N; i++) {
            for (int j = 1; j <= target; j++) {
                dp[i][j] = dp[i - 1][j];
                if (j - nums[i] >= 0) {
                    dp[i][j] += dp[i - 1][j - nums[i]];
                }
            }
        }
        return dp[N - 1][target];
    }

    // 动态规划进行空间压缩
    public static int findTargetSumWays4(int[] arr, int target) {
        int sum = 0;
        for (int n : arr) {
            sum += n;
        }
        return sum < target || ((target & 1) ^ (sum & 1)) != 0 ? 0 : subSet2(arr, (sum + target) >> 1);
    }
    // 上述方法的空间压缩技巧
    public static int subSet2(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1;
        for (int num : nums) {
            for (int i = target; i >= num; i--) {
                dp[i] += dp[i - num];
            }
        }
        return dp[target];
    }

    public static void main(String[] args) {
        int[] nums = {0,0,0,0,0,0,0,0,1};
        int target = 1;
        System.out.println(findTargetSumWays3(nums, target));
//        System.out.println(findTargetSumWays4(nums, target));
    }
}
