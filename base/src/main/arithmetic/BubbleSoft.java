/**
* 分类 -------------- 内部比较排序
* 数据结构 ---------- 数组
* 最差时间复杂度 ---- O(n^2)
* 最优时间复杂度 ---- 如果能在内部循环第一次运行时,使用一个旗标来表示有无需要交换的可能,可以把最优时间复杂度降低到O(n)
* 平均时间复杂度 ---- O(n^2)
* 所需辅助空间 ------ O(1)
* 稳定性 ------------ 稳定
*/
public class BubbleSoft {

    /**
     * 1. 比较相邻的元素，如果前一个比后一个大，就把它们两个调换位置。
     * 2. 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。这步做完后，最后的元素会是最大的数。
     * 3. 针对所有的元素重复以上的步骤，除了最后一个。
     * 4. 持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。
     */
    public static int[] bubbleSoft1(int[] intArrays) {
        for (int i = 0; i < intArrays.length - 1; i++) {
            for (int j = 0; j < intArrays.length - i - 1; j++) {
                if (intArrays[j] > intArrays[j + 1]) {
                    int temp = intArrays[j];
                    intArrays[j] = intArrays[j + 1];
                    intArrays[j + 1] = temp;
                }
            }
        }
        return intArrays;
    }

    public static void main(String[] args) {
        int[] intArrays = {22, 1, 3, 88, 3, 45, 11, 21, 12};
        intArrays = bubbleSoft1(intArrays);
        String sb = "";
        for (int aa : intArrays) {
            sb += aa + ",";
        }
        System.out.println(sb.substring(0, sb.length() - 1));
        // 压力测试
        int sd = 100000;
        int[] sources = new int[sd];
        Random rand = new Random();
        for (int i = 0; i < sd; i++) {
            sources[i] = rand.nextInt();
        }

        long start = System.currentTimeMillis();
        bubbleSoft1(sources);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");
    }
}

