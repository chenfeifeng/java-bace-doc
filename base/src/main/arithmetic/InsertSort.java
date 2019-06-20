package sort.test;

import java.util.Arrays;
import java.util.Random;

/**
 * @author cff
 * @version 1.0
 * @description 直接插入排序
 * @date 2018/2/24 下午2:22
 */
public class InsertSort {

    /**
     * 直接插入排序(Straight Insertion Sort)的基本思想是：
     * 把n个待排序的元素看成为一个有序表和一个无序表。
     * 开始时有序表中只包含1个元素，无序表中包含有n-1个元素，排序过程中每次从无序表中取出第一个元素，
     * 将它插入到有序表中的适当位置，使之成为新的有序表，重复n-1次可完成排序过程。
     */
    public static void insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            //i从1开始，表示第一个数已经当做有序表
            int x = arr[i];
            //x 为新拿出的数据
            int j = i - 1;
            //j为左边有序表中的数
            //当有序表中的数大于当前x的数，则需要跳转位置，否则不变
            while (j >= 0 && arr[j] > x) {
                // 左边有序集合中的数往后移动一位
                arr[j + 1] = arr[j];
                j--;
            }
            //把x数放到对的位置
            arr[j + 1] = x;
        }
    }

    /**
     * 对于插入排序，如果比较操作的代价比交换操作大的话，可以采用二分查找法来减少比较操作的次数，我们称为二分插入排序。
     */
    public static void insertSortDichotomy(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int x = arr[i];
            int left = 0;
            int right = i - 1;
            while (left <= right) {
                int mid = (left + right) / 2;
                if (arr[mid] > x) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            for (int j = i - 1; j >= left; j--) {
                arr[j + 1] = arr[j];
            }
            arr[left] = x;
        }
    }


    public static void main(String[] args) {
        int[] intArrays = {22, 1, 3, 88, 3, 45, 11, 21, 12};
        insertSort(intArrays);
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
        int[] sources2 = Arrays.copyOf(sources, sources.length);
        int[] sources3 = Arrays.copyOf(sources, sources.length);

        long start = System.currentTimeMillis();
        insertSortDichotomy(sources2);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");

        start = System.currentTimeMillis();
        insertSort(sources);
        System.out.println((System.currentTimeMillis() - start) + "毫秒");
    }
}
