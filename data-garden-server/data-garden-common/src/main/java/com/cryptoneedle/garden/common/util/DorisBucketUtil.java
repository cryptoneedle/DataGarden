package com.cryptoneedle.garden.common.util;

/**
 * <p>description: Doris桶计算工具 </p>
 *
 * @author CryptoNeedle
 * @date 2026-01-14
 */
public class DorisBucketUtil {

    public static Integer estimateBucket(Double estimateMegaBytes, double compressRatio) {
        // 表预估占用空间 MB

        // 是否需要分区
        boolean needPartition = false;
        // 分桶数量
        Integer bucketNum;


        // 原始数压缩率（压缩采用LZ4，实际压缩率约为0.1~0.3）
        estimateMegaBytes = estimateMegaBytes * compressRatio;

        // 1.分区策略
        // 单分区大小不超过50GB
        // 为了方便 1000W~2亿 可不分区，直接分桶
        // 2000W以内禁止使用动态分区（容易创建出大量不使用的分区）
        // 2亿以上必须使用分区分桶
        // 单节点分区数 < 20000

        // 2.分桶策略
        // 单分桶大小在1-10GB（官方建议，实际使用0.5-3GB进行估算）
        // 充分利用机器资源：分桶数量 = BE节点数量 * CPU核数 / 2
        // 禁止采用 AUTO，会产生过多的tablet小文件
        // 如果分桶字段存在30%的数据倾斜，禁止使用Hash，改为RANDOM

        // 3.官方建议
        // 单表大小      官方建议
        // 100M < 500M  4-8
        // 500M < 5GB   6-16
        // 5GB  < 50GB  32
        // 50GB < 500GB 每50GB 16~32
        // 500GB < 5TB  每50GB 16~32

        // 4.特殊场景
        // 时序场景 "compaction_policy"="time_series" 降低写入放大

        // 5.项目策略 T
        // 单表大小      实际采用     采用原因
        // < 100M       1          无需考虑并行性能
        // 100M < 500M  3          兼顾并发数和查询效率
        // 500M < 1GB   4          兼顾并发数和查询效率
        // 1GB  < 3GB   6
        // 3GB  < 5GB   8
        // 5GB  < 10GB  10
        // 10GB < 50GB  GB数量
        // 50GB < 500GB 每50GB GB数量
        // 500GB < 5TB  每50GB GB数量

        // 6.计算方法
        // T = 策略值
        // S = BE节点树 * 磁盘块数(磁盘块大小/50GB)
        // C = BE节点数量 * CPU核数 / 2，为最大分桶数
        // 最终分桶数 P

        // 7.实际计算方法（做了一些简化）
        if (estimateMegaBytes >= 1024 * 50) {
            needPartition = true;
        }
        // 以下不考虑分区
        if (needPartition) {
            // todo 考虑分区
            bucketNum = null;
        } else {
            //int s; // 暂不启用
            int c = 5 * 20 / 2;
            if (estimateMegaBytes < 100) {
                bucketNum = 1;
            } else if (estimateMegaBytes < 500) {
                bucketNum = 3;
            } else if (estimateMegaBytes < 1024) {
                bucketNum = 4;
            } else if (estimateMegaBytes < 1024 * 3) {
                bucketNum = 6;
            } else if (estimateMegaBytes < 1024 * 5) {
                bucketNum = 8;
            } else if (estimateMegaBytes < 1024 * 10) {
                bucketNum = 10;
            } else if (estimateMegaBytes < 1024 * 64) {
                bucketNum = (int) Math.ceil(estimateMegaBytes / 1024);
            } else {
                bucketNum = Math.max(Math.max((int) Math.ceil(estimateMegaBytes / 1024), c), 64);
            }
        }
        return bucketNum;
    }
}