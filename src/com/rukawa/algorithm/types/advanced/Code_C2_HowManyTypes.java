package com.rukawa.algorithm.types.advanced;

import java.util.HashSet;

/**
 * @className: Code_C2_HowManyTypes
 * @description: TODO 类描述
 * @author: 鎏川疯
 * @date: 2021/8/9 0009 7:48
 **/
public class Code_C2_HowManyTypes {
    /**
     * 只有小写字母(a~z)组成的一批字符串
     * 都放在字符类型的数组String[] arr中
     * 如果其中某两个字符串所含有的字符种类完全一样
     * 就将两个字符串算作一类
     * 比如：baacbba和bac就算作一类
     * 返回arr中有多少类？
     */

    public static int types1(String[] arr) {
        HashSet<String> types = new HashSet<>();
        for (String str : arr) {
            char[] chs = str.toCharArray();
            boolean[] map = new boolean[26];
            for (int i = 0; i < chs.length; i++) {
                map[chs[i] - 'a'] = true;
            }
            String key = "";
            for (int i = 0; i < 26; i++) {
                if (map[i]) {
                    key += String.valueOf((char) (i + 'a'));
                }
            }
            types.add(key);
        }
        return types.size();
    }

    public static int types2(String[] arr) {
        HashSet<Integer> types = new HashSet<>();
        for (String str : arr) {
            char[] chs = str.toCharArray();
            int key = 0;
            for (int i = 0; i < chs.length; i++) {
                // 每个位置|进去
                key |= (1 << (chs[i] - 'a'));
            }
            types.add(key);
        }
        return types.size();
    }

    // for test
    public static String[] getRandomStringArray(int possibilities, int strMaxSize, int arrMaxSize) {
        String[] ans = new String[(int) (Math.random() * arrMaxSize) + 1];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = getRandomString(possibilities, strMaxSize);
        }
        return ans;
    }

    // for test
    public static String getRandomString(int possibilities, int strMaxSize) {
        char[] ans = new char[(int) (Math.random() * strMaxSize) + 1];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = (char) ((int) (Math.random() * possibilities) + 'a');
        }
        return String.valueOf(ans);
    }

    public static void main(String[] args) {
        int possibilities = 5;
        int strMaxSize = 10;
        int arrMaxSize = 100;
        int testTimes = 500000;
        System.out.println("test begin, test time : " + testTimes);
        for (int i = 0; i < testTimes; i++) {
            String[] arr = getRandomStringArray(possibilities, strMaxSize, arrMaxSize);
            int ans1 = types1(arr);
            int ans2 = types2(arr);
            if (ans1 != ans2) {
                System.out.println("Oops!");
            }
        }
        System.out.println("test finish");

    }
}
