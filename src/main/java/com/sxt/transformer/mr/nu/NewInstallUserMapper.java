package com.sxt.transformer.mr.nu;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.log4j.Logger;

import com.sxt.common.DateEnum;
import com.sxt.common.KpiType;
import com.sxt.transformer.model.dim.StatsCommonDimension;
import com.sxt.transformer.model.dim.StatsUserDimension;
import com.sxt.transformer.model.dim.base.BrowserDimension;
import com.sxt.transformer.model.dim.base.DateDimension;
import com.sxt.transformer.model.dim.base.KpiDimension;
import com.sxt.transformer.model.dim.base.PlatformDimension;
import com.sxt.transformer.model.value.map.TimeOutputValue;
import com.sxt.transformer.mr.TransformerBaseMapper;

/**
 * 自定义的计算新用户的mapper类
 * 
 * @author root
 *
 */
public class NewInstallUserMapper extends TransformerBaseMapper<StatsUserDimension, TimeOutputValue> {
    /**
     * 1、从Hbase中读取数据开始分析，输出Key的类型为总维度，输出Value的类型为Text（保存的是uuid）读取数据时，要验证数据有效性。
     * 2、创建总维度对象，Text对象。
     * 3、拼装维度
     * 4、按照总维度聚合Text(uuid)
     */

    /**
     * 任务组装
     * 1、ICollector.java：将数据最终插入到Mysql时用到的SQL语句的拼装接口
     * 2、NewInstallUserCollector.java：拼装用于插入new_install_user表的SQL语句
     * 3、BrowserNewInstallUserCollector.java：拼装用于插入browser_new_install_user表的SQL语句
     * 4、IDimensionConverter.java：接口，通过维度对象（每个维度对象中保存着不同的维度数据），得到维度对应的维度id。
     * 5、DimensionConverterImpl.java：接口的具体实现类
     * 6、TransformerMySQLOutputFormat.java：自定义OutputFormat，用于将数据写入到Mysql中
     * 自定义TransformerRecordWriter
     */

    private static final Logger logger = Logger.getLogger(NewInstallUserMapper.class);
    private StatsUserDimension statsUserDimension = new StatsUserDimension();
    private TimeOutputValue timeOutputValue = new TimeOutputValue();
    private KpiDimension newInstallUserKpi = new KpiDimension(KpiType.NEW_INSTALL_USER.name);
    private KpiDimension newInstallUserOfBrowserKpi = new KpiDimension(KpiType.BROWSER_NEW_INSTALL_USER.name);

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        this.inputRecords++;
        String uuid = super.getUuid(value);
        String serverTime = super.getServerTime(value);
        String platform = super.getPlatform(value);
        if (StringUtils.isBlank(uuid) || StringUtils.isBlank(serverTime) || StringUtils.isBlank(platform)) {
            this.filterRecords++;
            logger.warn("uuid&servertime&platform不能为空");
            return;
        }
        long longOfTime = Long.valueOf(serverTime.trim());
        timeOutputValue.setId(uuid); // 设置id为uuid
        timeOutputValue.setTime(longOfTime); // 设置时间为服务器时间
        DateDimension dateDimension = DateDimension.buildDate(longOfTime, DateEnum.DAY);
        List<PlatformDimension> platformDimensions = PlatformDimension.buildList(platform);

        // 设置date维度
        StatsCommonDimension statsCommonDimension = this.statsUserDimension.getStatsCommon();
        statsCommonDimension.setDate(dateDimension);
        // 写browser相关的数据
        String browserName = super.getBrowserName(value);
        String browserVersion = super.getBrowserVersion(value);
        List<BrowserDimension> browserDimensions = BrowserDimension.buildList(browserName, browserVersion);
        BrowserDimension defaultBrowser = new BrowserDimension("", "");
        for (PlatformDimension pf : platformDimensions) {
            // 1. 设置为一个默认值
            statsUserDimension.setBrowser(defaultBrowser);
            // 2. 解决有空的browser输出的bug
            // statsUserDimension.getBrowser().clean();
            statsCommonDimension.setKpi(newInstallUserKpi);
            statsCommonDimension.setPlatform(pf);
            context.write(statsUserDimension, timeOutputValue);
            this.outputRecords++;
            for (BrowserDimension br : browserDimensions) {
                statsCommonDimension.setKpi(newInstallUserOfBrowserKpi);
                // 1.
                statsUserDimension.setBrowser(br);
                // 2. 由于上面需要进行clean操作，故将该值进行clone后填充
                // statsUserDimension.setBrowser(WritableUtils.clone(br,
                // context.getConfiguration()));
                context.write(statsUserDimension, timeOutputValue);
                this.outputRecords++;
            }
        }
    }
}
