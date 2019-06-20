package sort.test;

import java.util.Arrays;
import java.util.Random;

/**
 * @author cff
 * @version 1.0
 * @description @TODO
 * @date 2018/2/26 下午4:16
 */
public class MeargeSort {
    public static void main(String[] args) {
        int[] sources = new int[]{5, 1, 6, 2, 9, 7};
        mergeSort(sources);
        System.out.println(Arrays.toString(sources));
        //performance();
    }

    /**
     * @param sources
     * @Description 关键点:算法采用经典的分治（divide-and-conquer）策略（分治法将问题分(divide)成一些小的问题然后递归求解，而治(conquer)的阶段则将分的阶段得到的各答案"修补"在一起，即分而治之)<br/>
     * 实现策略，先中间分隔成两个有序数组，再按大小将两个有序数组合并，该两个有序数组是通过不断递归直到只有一个数字时，即为有序数组。
     */
    public static void mergeSort(int[] sources) {
        int[] temp = new int[sources.length];
        mergeSort(sources, 0, sources.length - 1, temp);
    }

    public static void mergeSort(int[] sources, int left, int right, int[] temp) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(sources, left, mid, temp);
            mergeSort(sources, mid + 1, right, temp);
            mergeArray(sources, left, mid, right, temp);
        }
    }

    public static void mergeArray(int[] sources, int left, int mid, int right, int[] temp) {
        int l = left;
        int r = mid + 1;
        int index = 0;
        while (l <= mid && r <= right) {
            if (sources[l] <= sources[r]) {   //从大到小，从小到大。控制处
                temp[index] = sources[l];
                l++;
                index++;
            } else {
                temp[index] = sources[r];
                r++;
                index++;
            }
        }
        while (l <= mid) {
            temp[index] = sources[l];
            l++;
            index++;
        }
        while (r <= right) {
            temp[index] = sources[r];
            r++;
            index++;
        }
        index = 0;
        while (left <= right) {
            sources[left++] = temp[index++];
        }
    }

    /**
     * 性能测试
     */
    public static void performance() {
        int sd = 10000000;
        int[] sources = new int[sd];
        Random rand = new Random();
        for (int i = 0; i < sd; i++) {
            sources[i] = rand.nextInt();
        }
        long start = System.currentTimeMillis();
        mergeSort(sources);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");
    }
}
