package com.rukawa.algorithm.trainingcamp.top100likedquestions;

import java.util.*;

/**
 * Created with Intellij IDEA
 *
 * @Author：SuperHai
 * @Date：2020-09-25 21:52
 * @Version：1.0
 */
public class Problem_0218_TheSkylineProblem {
    /**
     * 天际线问题
     * 城市的天际线是从远处观看该城市中所有建筑物形成的轮廓的外部轮廓。现在，假设您获得了城市风光照片（图A）上显示的所有建筑物的位置和高度，
     * 请编写一个程序以输出由这些建筑物形成的天际线（图B）。
     *
     *
     *
     * 每个建筑物的几何信息用三元组 [Li，Ri，Hi] 表示，其中 Li 和 Ri 分别是第 i 座建筑物左右边缘的 x 坐标，Hi 是其高度。可以保证 0 ≤ Li,
     * Ri ≤ INT_MAX, 0 < Hi ≤
     * INT_MAX 和 Ri - Li > 0。您可以假设所有建筑物都是在绝对平坦且高度为 0 的表面上的完美矩形。
     *
     * 例如，图A中所有建筑物的尺寸记录为：[ [2 9 10], [3 7 15], [5 12 12], [15 20 10], [19 24 8] ] 。
     *
     * 输出是以 [ [x1,y1], [x2, y2], [x3, y3], ... ]
     * 格式的“关键点”（图B
     * 中的红点）的列表，它们唯一地定义了天际线。关键点是水平线段的左端点。请注意，最右侧建筑物的最后一个关键点仅用于标记天际线的终点，
     * 并始终为零高度。此外，任何两个相邻建筑物之间的地面都应被视为天际线轮廓的一部分。
     *
     * 例如，图B中的天际线应该表示为：[ [2 10], [3 15], [7 12], [12 0], [15 10], [20 8], [24, 0] ]。
     *
     * 说明:
     *
     * 任何输入列表中的建筑物数量保证在 [0, 10000] 范围内。
     * 输入列表已经按左 x 坐标 Li  进行升序排列。
     * 输出列表必须按 x 位排序。
     * 输出天际线中不得有连续的相同高度的水平线。例如 [...[2 3], [4 5], [7 5], [11 5], [12 7]...] 是不正确的答案；
     * 三条高度为 5 的线应该在最终输出中合并为一个：[...[2 3],
     * [4 5], [12 7], ...]
     */
    public List<List<Integer>> getSkyline(int[][] buildings) {
        int N = buildings.length;
        Operate[] ops = new Operate[N << 1];
        for (int i = 0; i < N; i++) {
            ops[i * 2] = new Operate(buildings[i][0], true, buildings[i][2]);
            ops[i * 2 + 1] = new Operate(buildings[i][1], false, buildings[i][2]);
        }
        // 把描述高度变化的对象数组，按照规定的排序策略排序
        Arrays.sort(ops, new OperateComparator());
        // TreeMap就是java中的红黑树结构，直接当做有序表使用
        // key -> 高度  value -> 次数
        TreeMap<Integer, Integer> mapHeightTimes = new TreeMap<>();

        TreeMap<Integer, Integer> mapXHeight = new TreeMap<>();

        for (int i = 0; i < ops.length; i++) {
            if (ops[i].isAdd) { // 如果当前操作是加入操作
                if (!mapHeightTimes.containsKey(ops[i].h)) {    // 没有出现此高度直接加入
                    mapHeightTimes.put(ops[i].h, 1);
                } else {    // 之前加入过此高度，次数加1即可
                    mapHeightTimes.put(ops[i].h, mapHeightTimes.get(ops[i].h) + 1);
                }
            } else {   // 如果是删除操作
                if (mapHeightTimes.get(ops[i].h) == 1) {    // 如果当前的高度出现次数为1，直接删除记录
                    mapHeightTimes.remove(ops[i].h);
                } else {    // 如果当前的高度出现次数大于1，次数减1即可
                    mapHeightTimes.put(ops[i].h, mapHeightTimes.get(ops[i].h) - 1);
                }
            }

            if (mapHeightTimes.isEmpty()) {
                mapXHeight.put(ops[i].x, 0);
            } else {
                mapXHeight.put(ops[i].x, mapHeightTimes.lastKey());
            }
        }
        // res为结果数组，每一个List<Integer>代表一个轮廓线，有开始的位置，结束位置，高度，一共三个信息
        List<List<Integer>> res = new ArrayList<>();
        // 根据mapXHeight生成res数组
        for (Map.Entry<Integer, Integer> entry : mapXHeight.entrySet()) {
            // 当前位置
            int curX = entry.getKey();
            // 当前最大高度
            int curMaxHeight = entry.getValue();
            if (res.isEmpty() || res.get(res.size() - 1).get(1) != curMaxHeight) {
                res.add(Arrays.asList(curX, curMaxHeight));
            }
        }
        return res;
    }

    public static class Operate {
        public int x;   // x轴上的值
        public boolean isAdd;   // 高度添加还是减少，true -> +, false -> -
        public int h;   // 高度

        public Operate(int x, boolean isAdd, int h) {
            this.x = x;
            this.isAdd = isAdd;
            this.h = h;
        }
    }

    /**
     * 排序策略
     * 1、第一个维度的x值从小到大排序
     * 2、如果第一个维度的值相等，看第二个维度的值，"加入"排在前，"删除"排在后
     * 3、如果两个对象的第一维度和第二维度的值相等，则认为两个对象相等，谁在前都行
     */
    public static class OperateComparator implements Comparator<Operate> {
        @Override
        public int compare(Operate o1, Operate o2) {
            if (o1.x != o2.x) {
                return o1.x - o2.x;
            }
            // 防止纸片大楼[7,7,6] -> {7,true,6},{7,false,6} 此时要把7处理掉，需要把true排在前面
            if (o1.isAdd != o2.isAdd) {
                return o1.isAdd ? -1 : 1;
            }
            return 0;
        }
    }
}
