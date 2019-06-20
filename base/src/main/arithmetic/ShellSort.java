package sort.test;

import java.util.Arrays;
import java.util.Random;

/**
 * @author cff
 * @version 1.0
 * @description 希尔排序
 * @date 2018/2/26 下午2:45
 */
public class ShellSort {

    /**
     * 希尔排序实质上是一种分组插入方法。
     * 它的基本思想是：对于n个待排序的数列，取一个小于n的整数gap(gap被称为步长)将待排序元素分成若干个组子序列，
     * 所有距离为gap的倍数的记录放在同一个组中；
     * 然后，对各组内的元素进行直接插入排序。
     * 这一趟排序完成之后，每一个组的元素都是有序的。
     * 然后减小gap的值，并重复执行上述的分组和排序。重复这样的操作，当gap=1时，整个数列就是有序的。
     */
    public static void shellSort(int[] a) {
        int count = 0;
        int n = a.length;
        // gap为步长，每次减为原来的一半。
        for (int gap = n / 2; gap > 0; gap /= 2) {
            // 共gap个组，对每一组都执行直接插入排序
            for (int i = 0; i < gap; i++) {
                for (int j = i + gap; j < n; j += gap) {
                    // 如果a[j] < a[j-gap]，则寻找a[j]位置，并将后面数据的位置都后移。
                    if (a[j] < a[j - gap]) {
                        int tmp = a[j];
                        int k = j - gap;
                        while (k >= 0 && a[k] > tmp) {
                            a[k + gap] = a[k];
                            k -= gap;
                            count++;
                        }
                        a[k + gap] = tmp;
                    }
                }
            }
        }
        System.out.println("1count:" + count);
    }

    /**
     * 希尔排序
     * <p>
     * 参数说明：
     * a -- 待排序的数组
     */
    public static void shellSort2(int[] a) {
        // gap为步长，每次减为原来的一半。
        int n = a.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            // 共gap个组，对每一组都执行直接插入排序
            for (int i = 0; i < gap; i++)
                groupSort(a, n, i, gap);
        }
    }

    /**
     * 对希尔排序中的单个组进行排序
     * <p>
     * 参数说明：
     * a -- 待排序的数组
     * n -- 数组总的长度
     * i -- 组的起始位置
     * gap -- 组的步长
     * <p>
     * 组是"从i开始，将相隔gap长度的数都取出"所组成的！
     */
    public static void groupSort(int[] a, int n, int i, int gap) {
        for (int j = i + gap; j < n; j += gap) {
            // 如果a[j] < a[j-gap]，则寻找a[j]位置，并将后面数据的位置都后移。
            if (a[j] < a[j - gap]) {
                int tmp = a[j];
                int k = j - gap;
                while (k >= 0 && a[k] > tmp) {
                    a[k + gap] = a[k];
                    k -= gap;
                }
                a[k + gap] = tmp;
            }
        }
    }

    /**
     * 最快
     */
    public static void shellSort3(int[] sources) {
        int step = sources.length / 2; // 这里步长取总长度的二分之一，直到步长变为1为止。
        while (step >= 1) {
            for (int i = step; i < sources.length; i++) { // 因为数组是从0开始的，所以step的值可以等价于（step+1）,所以可以设置i=step
                int base = sources[i];
                int j = i;
                while (j >= step && base < sources[j - step]) {
                    sources[j] = sources[j - step];
                    j = j - step;
                }
                sources[j] = base;
            }
            step = step / 2;
        }
    }

    public static void main(String[] args) {
        int[] intArrays = {22, 1, 3, 88, 3, 45, 11, 21, 12};
        shellSort(intArrays);
        String sb = "";
        for (int aa : intArrays) {
            sb += aa + ",";
        }
        System.out.println(sb.substring(0, sb.length() - 1));
        // 压力测试
        int sd = 10000000;
        int[] sources = new int[sd];
        Random rand = new Random();
        for (int i = 0; i < sd; i++) {
            sources[i] = rand.nextInt();
        }

        int[] sources2 = Arrays.copyOf(sources, sources.length);
        int[] sources3 = Arrays.copyOf(sources, sources.length);

        long start = System.currentTimeMillis();
        shellSort(sources);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");

        start = System.currentTimeMillis();
        shellSort2(sources2);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");

        start = System.currentTimeMillis();
        shellSort3(sources3);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");
    }
}
