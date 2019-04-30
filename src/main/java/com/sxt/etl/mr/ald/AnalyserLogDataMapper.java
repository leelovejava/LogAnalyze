package com.sxt.etl.mr.ald;

import java.io.IOException;
import java.util.Map;
import java.util.zip.CRC32;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import com.sxt.common.EventLogConstants;
import com.sxt.common.EventLogConstants.EventEnum;
import com.sxt.etl.util.LoggerUtil;

/**
 * 自定义数据解析map类
 *
 * @author root
 */
public class AnalyserLogDataMapper extends Mapper<Object, Text, NullWritable, Put> {
    private final Logger logger = Logger.getLogger(AnalyserLogDataMapper.class);
    // 主要用于标志，方便查看过滤数据
    // 输入数 过滤数 输出数
    private int inputRecords, filterRecords, outputRecords;
    private byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME);
    private CRC32 crc32 = new CRC32();

    /**
     * 主要作用：开始清洗HDFS中的日志数据
     * 重要细节：
     * 开始清洗数据，首先使用LoggerUtil将数据转换成Map集合
     * 将得到的存放原始数据的Map集合封装成事件以用于事件数据合法性的过滤（事件的封装依赖于一个枚举类，使用事件的alias别名来区分匹配事件）
     * ->事件的封装要按照平台来区分
     * -->平台区分完成后，按照事件类型来区分（例如en=e_l等)
     * 事件封装过程中涉及到事件数据完整性的清洗操作
     * 数据输出：创建rowKey，创建Put对象，等待输出到HBase
     */


    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        this.inputRecords++;
        this.logger.debug("Analyse data of :" + value);

        try {
            // 解析日志
            Map<String, String> clientInfo = LoggerUtil.handleLog(value.toString());

            // 过滤解析失败的数据
            if (clientInfo.isEmpty()) {
                this.filterRecords++;
                return;
            }

            // 获取事件名称
            String eventAliasName = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_EVENT_NAME);
            EventEnum event = EventEnum.valueOfAlias(eventAliasName);
            switch (event) {
                case LAUNCH:
                case PAGEVIEW:
                case CHARGEREQUEST:
                case CHARGEREFUND:
                case CHARGESUCCESS:
                case EVENT:
                    // 处理数据
                    this.handleData(clientInfo, event, context);
                    break;
                default:
                    this.filterRecords++;
                    this.logger.warn("该事件没法进行解析，事件名称为:" + eventAliasName);
            }
        } catch (Exception e) {
            this.filterRecords++;
            this.logger.error("处理数据发出异常，数据:" + value, e);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
        logger.info("输入数据:" + this.inputRecords + "；输出数据:" + this.outputRecords + "；过滤数据:" + this.filterRecords);
    }

    /**
     * 具体处理数据的方法
     *
     * @param clientInfo
     * @param context
     * @param event
     * @throws InterruptedException
     * @throws IOException
     */
    private void handleData(Map<String, String> clientInfo, EventEnum event, Context context) throws IOException, InterruptedException {
        String uuid = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_UUID);
        String memberId = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_MEMBER_ID);
        String serverTime = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME);
        if (StringUtils.isNotBlank(serverTime)) {
            // 要求服务器时间不为空
            // 浏览器信息去掉(已解析完)
            clientInfo.remove(EventLogConstants.LOG_COLUMN_NAME_USER_AGENT);
            String rowkey = this.generateRowKey(uuid, memberId, event.alias, serverTime); // timestamp
            // +
            // (uuid+memberid+event).crc
            Put put = new Put(Bytes.toBytes(rowkey));
            for (Map.Entry<String, String> entry : clientInfo.entrySet()) {
                if (StringUtils.isNotBlank(entry.getKey()) && StringUtils.isNotBlank(entry.getValue())) {
                    put.addColumn(family, Bytes.toBytes(entry.getKey()), Bytes.toBytes(entry.getValue()));
                }
            }
            context.write(NullWritable.get(), put);
            this.outputRecords++;
        } else {
            this.filterRecords++;
        }
    }

    /**
     * 根据uuid memberid servertime创建rowkey
     *
     * @param uuid
     * @param memberId
     * @param eventAliasName
     * @param serverTime
     * @return
     */
    private String generateRowKey(String uuid, String memberId, String eventAliasName, String serverTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(serverTime).append("_");

        // 重置
        this.crc32.reset();

        if (StringUtils.isNotBlank(uuid)) {
            // 转成字节数组
            this.crc32.update(uuid.getBytes());
        }
        if (StringUtils.isNotBlank(memberId)) {
            this.crc32.update(memberId.getBytes());
        }
        this.crc32.update(eventAliasName.getBytes());
        // 缩短 取8位数字
        sb.append(this.crc32.getValue() % 100000000L);
        return sb.toString();
    }
}
