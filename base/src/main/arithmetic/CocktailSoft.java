/**
* 分类 -------------- 内部比较排序
* 数据结构 ---------- 数组
* 最差时间复杂度 ---- O(n^2)
* 最优时间复杂度 ---- 如果序列在一开始已经大部分排序过的话,会接近O(n)
* 平均时间复杂度 ---- O(n^2)
* 所需辅助空间 ------ O(1)
*  稳定性 ------------ 稳定
*/
public class CocktailSoft {

    /**
     * 鸡尾酒排序。
     * 1. 鸡尾酒排序，也叫定向冒泡排序，是冒泡排序的一种改进。
     * 2. 此算法与冒泡排序的不同处在于从低到高然后从高到低，而冒泡排序则仅从低到高去比较序列里的每个元素。
     * 3. 他可以得到比冒泡排序稍微好一点的效能。
     */
    public static int[] cocktailSoft(int[] intArrayS) {
        int length = intArrayS.length;
        int temp;
        for (int i = 0; i < length - 1; i++) {
            //前半轮,将最大元素放到后面
            for (int j = 0; j < length - 1 - i; j++) {
                if (intArrayS[j] > intArrayS[j + 1]) {
                    temp = intArrayS[j];
                    intArrayS[j] = intArrayS[j + 1];
                    intArrayS[j + 1] = temp;
                }
            }
            //后半轮,将最小元素放到前面
            //length-1-i-1:前一波的时候吧大的数已经排好了
            //k>i：前面数字以及排好，只要排到当前
            for (int k = length - 1 - i - 1; k > i; k--) {
                if (intArrayS[k] < intArrayS[k - 1]) {
                    temp = intArrayS[k];
                    intArrayS[k] = intArrayS[k - 1];
                    intArrayS[k - 1] = temp;
                }
            }
        }
        return intArrayS;
    }


    public static void main(String[] args) {
        int[] intArrays = {22, 1, 3, 88, 3, 45, 11, 21, 12};
        intArrays = cocktailSoft(intArrays);
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
        cocktailSoft(sources);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");
    }
}


```