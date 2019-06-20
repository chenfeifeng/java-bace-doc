package sort.test;

import java.util.Arrays;
import java.util.Random;

/**
 * @author cff
 * @version 1.0
 * @description 快速排序
 * @date 2018/2/23 下午3:49
 */
public class QuickSort {

    /**
     * 快速排序的基本思想是：通过一趟排序将要排序的数据分割成独立的两部分，其中一部分的所有数据都比另外一部分的所有数据都要小，
     * 然后再按此方法对这两部分数据分别进行快速排序，整个排序过程可以递归进行，以此达到整个数据变成有序序列。
     *
     * @param arr 待排序数组
     * @param lt  左边坐标
     * @param rt  右边开始坐标
     */
    public static int[] quickSort(int[] arr, int lt, int rt) {
        int x = arr[lt];
        if (rt > lt) {
            int i = lt;
            int j = rt;
            while (i < j) {
                // 从右往左查询比x小的数的坐标
                while (i < j && arr[j] >= x)
                    j--;
                //int tmp=arr[i];
                //arr[i]=arr[j];
                //arr[j]=tmp;
                //System.out.println("=");
                // 从左往右查询比x大的数的坐标
                while (i < j && arr[i] < x)
                    i++;
                int tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
            }
            quickSort(arr, lt, i - 1);
            quickSort(arr, i + 1, rt);
        }
        return arr;
    }

    public static int[] quickSort2(int[] arr, int lt, int rt) {
        if (rt > lt) {
            int i = lt;
            int j = rt;
            int x = arr[lt];
            while (i < j) {
                // 从右往左查询比x小的数的坐标
                while (i < j && arr[j] >= x)
                    j--;
                if (i < j)
                    arr[i++] = arr[j];
                // 从左往右查询比x大的数的坐标
                while (i < j && arr[i] < x)
                    i++;
                if (i < j)
                    arr[j--] = arr[i];
            }
            arr[i] = x;
            quickSort2(arr, lt, i - 1);
            quickSort2(arr, i + 1, rt);
        }
        return arr;
    }

    private static void printArr(int[] arr) {
        String sb = "";
        for (int aa : arr) {
            sb += aa + ",";
        }
        System.out.println(sb.substring(0, sb.length() - 1));
    }

    public static void main(String[] args) {
        int[] intArrays = {22, 1, 3, 88, 3, 45, 11, 21, 12};
        quickSort2(intArrays, 0, intArrays.length - 1);
        printArr(intArrays);
        // 压力测试
        int sd = 10000;
        int[] sources = new int[sd];
        Random rand = new Random();
        for (int i = 0; i < sd; i++) {
            sources[i] = rand.nextInt();
        }
        long start = System.currentTimeMillis();
        sources = quickSort(sources, 0, intArrays.length - 1);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");
        printArr(sources);
    }

}
