package org.michael.common;

import com.codahale.metrics.Timer;
import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author jackcptdev<jackcptdev               @               gmail.com>
 * @version Mar 9, 2018 10:59:40 AM
 *
 */
public class MetricMySQLAndLoggerReporter extends ScheduledReporter {
    /**
     * Returns a new {@link Builder} for {@link ConsoleReporter}.
     *
     * @param registry the registry to report
     * @return a {@link Builder} instance for a {@link ConsoleReporter}
     */
    public static Builder forRegistry(MetricRegistry registry) {
        return new Builder(registry);
    }

    /**
     * A builder for {@link ConsoleReporter} instances. Defaults to using the default locale and
     * time zone, writing to {@code System.out}, converting rates to events/second, converting
     * durations to milliseconds, and not filtering metrics.
     */
    public static class Builder {
        private final MetricRegistry registry;
        private PrintStream output;
        private Locale locale;
        private Clock clock;
        private TimeZone timeZone;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private ScheduledExecutorService executor;
        private boolean shutdownExecutorOnStop;
        private Set<MetricAttribute> disabledMetricAttributes;
        private String dbUrl;
        private String dbUser;
        private String dbPassword;
        private String dbName;
        private String instance;

        private Builder(MetricRegistry registry) {
            this.registry = registry;
            this.locale = Locale.getDefault();
            this.clock = Clock.defaultClock();
            this.timeZone = TimeZone.getDefault();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
            this.executor = null;
            this.shutdownExecutorOnStop = true;
            disabledMetricAttributes = Collections.emptySet();
            this.dbUrl = null;
            this.dbUser = null;
            this.dbPassword = null;
            this.dbName = null;
        }

        /**
         * Specifies whether or not, the executor (used for reporting) will be stopped with same time with reporter.
         * Default value is true.
         * Setting this parameter to false, has the sense in combining with providing external managed executor via {@link #scheduleOn(ScheduledExecutorService)}.
         *
         * @param shutdownExecutorOnStop if true, then executor will be stopped in same time with this reporter
         * @return {@code this}
         */
        public Builder shutdownExecutorOnStop(boolean shutdownExecutorOnStop) {
            this.shutdownExecutorOnStop = shutdownExecutorOnStop;
            return this;
        }

        /**
         * Specifies the executor to use while scheduling reporting of metrics.
         * Default value is null.
         * Null value leads to executor will be auto created on start.
         *
         * @param executor the executor to use while scheduling reporting of metrics.
         * @return {@code this}
         */
        public Builder scheduleOn(ScheduledExecutorService executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Write to the given {@link PrintStream}.
         *
         * @param output a {@link PrintStream} instance.
         * @return {@code this}
         */
        public Builder outputTo(PrintStream output) {
            this.output = output;
            return this;
        }

        /**
         * Format numbers for the given {@link Locale}.
         *
         * @param locale a {@link Locale}
         * @return {@code this}
         */
        public Builder formattedFor(Locale locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Use the given {@link Clock} instance for the time.
         *
         * @param clock a {@link Clock} instance
         * @return {@code this}
         */
        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        /**
         * Use the given {@link TimeZone} for the time.
         *
         * @param timeZone a {@link TimeZone}
         * @return {@code this}
         */
        public Builder formattedFor(TimeZone timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Don't report the passed metric attributes for all metrics (e.g. "p999", "stddev" or "m15").
         * See {@link MetricAttribute}.
         *
         * @param disabledMetricAttributes a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder disabledMetricAttributes(Set<MetricAttribute> disabledMetricAttributes) {
            this.disabledMetricAttributes = disabledMetricAttributes;
            return this;
        }

        public Builder dbUrl(String dbUrl) {
            this.dbUrl = dbUrl;
            return this;
        }

        public Builder dbUser(String dbuser) {
            this.dbUser = dbuser;
            return this;
        }

        public Builder dbPassword(String dbPassword) {
            this.dbPassword = dbPassword;
            return this;
        }

        public Builder dbName(String dbName) {
            this.dbName = dbName;
            return this;
        }

        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        /**
         * Builds a {@link ConsoleReporter} with the given properties.
         *
         * @return a {@link ConsoleReporter}
         */
        public MetricMySQLAndLoggerReporter build() {
            return new MetricMySQLAndLoggerReporter(registry, output, locale, clock, timeZone, rateUnit, durationUnit, filter, executor, shutdownExecutorOnStop,
                    disabledMetricAttributes, dbUrl, dbUser, dbPassword, dbName, instance);
        }
    }

    private final static Logger LOG = LoggerFactory.getLogger(MetricMySQLAndLoggerReporter.class);

    //    private final PrintStream output;
    private final Locale locale;
    private final Clock clock;
    private final DateFormat dateFormat;
    private final JdbcConfig jdbcConn;
    private final String dbName;
    private final String instance;

    private MetricMySQLAndLoggerReporter(MetricRegistry registry, PrintStream output, Locale locale, Clock clock, TimeZone timeZone, TimeUnit rateUnit,
                                         TimeUnit durationUnit, MetricFilter filter, ScheduledExecutorService executor, boolean shutdownExecutorOnStop,
                                         Set<MetricAttribute> disabledMetricAttributes, String dbUrl, String dbUser, String dbPassword, String dbName, String instance) {
        super(registry, "console-reporter", filter, rateUnit, durationUnit, executor, shutdownExecutorOnStop, disabledMetricAttributes);
        //        this.output = output;
        this.locale = locale;
        this.clock = clock;
        //        this.dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(timeZone);
        this.jdbcConn = (dbUrl == null || dbUser == null || dbPassword == null || dbName == null || instance == null) ?
                null :
                new JdbcConfig(dbUrl, dbUser, dbPassword);
        this.dbName = dbName;
        this.instance = instance;
    }

    @Override public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms,
            SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        final String dateTime = dateFormat.format(new Date(clock.getTime()));

        Map<String, GaugeSnapshot> gaugeSnapshots = gaugeSnapshots(gauges);
        Map<String, CounterSnaphot> counterSnapshots = counterSnapshots(counters);
        Map<String, MeterSnapshot> meterSnapshots = meterSnapshots(meters);
        Map<String, HistogramSnapshot> histogramSnapshots = histogramSnapshots(histograms);
        Map<String, TimerSnapshot> timerSnapshots = timerSnapshots(timers);
        this.report0(dateTime, gaugeSnapshots, counterSnapshots, meterSnapshots, histogramSnapshots, timerSnapshots);
    }

    protected void report0(String dateTime, Map<String, GaugeSnapshot> gaugeSnapshots, Map<String, CounterSnaphot> counterSnapshots,
            Map<String, MeterSnapshot> meterSnapshots, Map<String, HistogramSnapshot> histogramSnapshots, Map<String, TimerSnapshot> timerSnapshots) {
        this.loggerReport(dateTime, gaugeSnapshots, counterSnapshots, meterSnapshots, histogramSnapshots, timerSnapshots);
        this.mysqlReport(dateTime, gaugeSnapshots, counterSnapshots, meterSnapshots, histogramSnapshots, timerSnapshots);
    }

    protected void mysqlReport(String dateTime, Map<String, GaugeSnapshot> gaugeSnapshots, Map<String, CounterSnaphot> counterSnapshots,
            Map<String, MeterSnapshot> meterSnapshots, Map<String, HistogramSnapshot> histogramSnapshots, Map<String, TimerSnapshot> timerSnapshots) {
        if (this.jdbcConn == null) {
            LOG.info("mysql connection info is empty.");
            return;
        }

        List<String> gaugeSQLS = generateGaugeSQLs(gaugeSnapshots, dbName, dateTime, this.instance);
        List<String> counterSQLS = generateCounterSQLs(counterSnapshots, dbName, dateTime, this.instance);
        List<String> meterSQLs = generateMeterSQLs(meterSnapshots, dbName, dateTime, this.instance);
        List<String> histogramSQLs = generateHistogramSQLs(histogramSnapshots, dbName, dateTime, this.instance);
        List<String> timerSQLs = generateTimerSQLs(timerSnapshots, dbName, dateTime, this.instance);

        Connection conn = null;
        try {
            conn = this.jdbcConn.createConnection();
            this.jdbcConn.executeBatchUpdate(conn, gaugeSQLS);
            this.jdbcConn.executeBatchUpdate(conn, counterSQLS);
            this.jdbcConn.executeBatchUpdate(conn, meterSQLs);
            this.jdbcConn.executeBatchUpdate(conn, histogramSQLs);
            this.jdbcConn.executeBatchUpdate(conn, timerSQLs);
            LOG.info("report to mysql succ");
        } catch (Exception e) {
            LOG.error("report to mysql fail", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    protected void loggerReport(String dateTime, Map<String, GaugeSnapshot> gaugeSnapshots, Map<String, CounterSnaphot> counterSnapshots,
            Map<String, MeterSnapshot> meterSnapshots, Map<String, HistogramSnapshot> histogramSnapshots, Map<String, TimerSnapshot> timerSnapshots) {
        LOG.info("MetricReportTime: " + dateTime);
        if (!gaugeSnapshots.isEmpty()) {
            for (Entry<String, GaugeSnapshot> e : gaugeSnapshots.entrySet()) {
                String str = String.format("Gauge[ %s -> %s ]", e.getKey(), e.getValue().value);
                LOG.info(str);
            }
        }

        if (!counterSnapshots.isEmpty()) {
            for (Entry<String, CounterSnaphot> e : counterSnapshots.entrySet()) {
                String str = String.format("Counter[ %s -> %s ]", e.getKey(), e.getValue().value);
                LOG.info(str);
            }
        }

        if (!meterSnapshots.isEmpty()) {
            for (Entry<String, MeterSnapshot> e : meterSnapshots.entrySet()) {
                String str = meterString(e.getValue());
                String tmp = String.format("Meter[ %s -> %s ]", e.getKey(), str);
                LOG.info(tmp);
            }
        }

        if (!histogramSnapshots.isEmpty()) {
            for (Entry<String, HistogramSnapshot> e : histogramSnapshots.entrySet()) {
                String str = histogramString(e.getValue());
                String tmp = String.format("Histogram[ %s -> %s ]", e.getKey(), str);
                LOG.info(tmp);
            }
        }

        if (!timerSnapshots.isEmpty()) {
            for (Entry<String, TimerSnapshot> e : timerSnapshots.entrySet()) {
                String str = timerString(e.getValue());
                String tmp = String.format("Timer[ %s -> %s ]", e.getKey(), str);
                LOG.info(tmp);
            }
        }
    }

    private List<String> generateGaugeSQLs(Map<String, GaugeSnapshot> gaugeSnapshots, String dbName, String datetime, String instance) {
        String template = "insert into %s.metricgauge(dt,instance,name,val) values('%s','%s','%s',%d);";
        List<String> sqls = new ArrayList<>(64);
        for (Entry<String, GaugeSnapshot> e : gaugeSnapshots.entrySet()) {
            String name = e.getKey();
            long value = e.getValue().value;
            String sql = String.format(template, dbName, datetime, instance, name, value);
            sqls.add(sql);
        }
        return sqls;
    }

    protected Map<String, GaugeSnapshot> gaugeSnapshots(SortedMap<String, Gauge> gauges) {
        Map<String, GaugeSnapshot> gaugeSnapshots = new HashMap<String, GaugeSnapshot>();
        if (!gauges.isEmpty()) {
            for (Entry<String, Gauge> entry : gauges.entrySet()) {
                String name = entry.getKey();
                Gauge gauge = entry.getValue();
                if (gauge != null) {
                    GaugeSnapshot gs = new GaugeSnapshot(gauge);
                    gaugeSnapshots.put(name, gs);
                }
            }
        }
        return gaugeSnapshots;
    }

    private List<String> generateCounterSQLs(Map<String, CounterSnaphot> counterSnapshots, String dbName, String datetime, String instance) {
        String template = "insert into %s.metriccounter(dt,instance, name,val) values('%s','%s','%s',%d);";
        List<String> sqls = new ArrayList<>();
        for (Entry<String, CounterSnaphot> e : counterSnapshots.entrySet()) {
            String name = e.getKey();
            long value = e.getValue().value;
            String sql = String.format(template, dbName, datetime, instance, name, value);
            sqls.add(sql);
        }
        return sqls;
    }

    protected Map<String, CounterSnaphot> counterSnapshots(SortedMap<String, Counter> counters) {
        Map<String, CounterSnaphot> counterSnapshots = new HashMap<String, CounterSnaphot>();
        if (!counters.isEmpty()) {
            for (Entry<String, Counter> entry : counters.entrySet()) {
                String name = entry.getKey();
                Counter c = entry.getValue();
                CounterSnaphot cs = new CounterSnaphot(c);
                counterSnapshots.put(name, cs);
            }
        }
        return counterSnapshots;
    }

    protected Map<String, HistogramSnapshot> histogramSnapshots(SortedMap<String, Histogram> histograms) {
        Map<String, HistogramSnapshot> histogramSnapshots = new HashMap<>();
        if (!histograms.isEmpty()) {
            for (Entry<String, Histogram> entry : histograms.entrySet()) {
                String name = entry.getKey();
                HistogramSnapshot hs = new HistogramSnapshot(entry.getValue());
                histogramSnapshots.put(name, hs);
            }
        }
        return histogramSnapshots;
    }

    protected Map<String, MeterSnapshot> meterSnapshots(SortedMap<String, Meter> meters) {
        Map<String, MeterSnapshot> meterSnapshots = new HashMap<String, MeterSnapshot>();
        if (!meters.isEmpty()) {
            for (Entry<String, Meter> entry : meters.entrySet()) {
                String name = entry.getKey();
                MeterSnapshot ms = new MeterSnapshot(entry.getValue());
                meterSnapshots.put(name, ms);
            }
        }

        return meterSnapshots;
    }

    protected Map<String, TimerSnapshot> timerSnapshots(SortedMap<String, Timer> timers) {
        Map<String, TimerSnapshot> timerSnapshots = new HashMap<String, TimerSnapshot>();
        if (!timers.isEmpty()) {
            for (Entry<String, Timer> entry : timers.entrySet()) {
                String name = entry.getKey();
                TimerSnapshot ts = new TimerSnapshot(entry.getValue());
                timerSnapshots.put(name, ts);
            }
        }
        return timerSnapshots;
    }

    public static class GaugeSnapshot {
        final long value;

        public GaugeSnapshot(Gauge g) {
            this.value = Long.valueOf(g.getValue().toString());
        }
    }

    public static class CounterSnaphot {
        final long value;

        public CounterSnaphot(Counter c) {
            this.value = c.getCount();
        }
    }

    public static class MeterSnapshot {
        final long count;
        final double meanRate;
        final double oneMR;
        final double fiveMR;
        final double fifteenMR;

        public MeterSnapshot(Meter meter) {
            this.count = meter.getCount();
            this.meanRate = meter.getMeanRate();
            this.oneMR = meter.getOneMinuteRate();
            this.fiveMR = meter.getFiveMinuteRate();
            this.fifteenMR = meter.getFifteenMinuteRate();
        }
    }

    private List<String> generateMeterSQLs(Map<String, MeterSnapshot> meters, String dbName, String datetime, String instance) {
        String template = "insert into %s.metricmeter(dt,instsance,name,count,mr,m1r,m5r,m15r) values('%s','%s','%s',%d,%.2f,%.2f,%.2f,%.2f)";
        List<String> sqls = new ArrayList<>();
        for (Entry<String, MeterSnapshot> e : meters.entrySet()) {
            String name = e.getKey();
            MeterSnapshot m = e.getValue();
            long count = m.count;
            double mr = convertRate(m.meanRate);
            double m1r = convertRate(m.oneMR);
            double m5r = convertRate(m.fiveMR);
            double m15r = convertRate(m.fifteenMR);
            String sql = String.format(template, dbName, datetime, instance, name, count, mr, m1r, m5r, m15r);
            sqls.add(sql);
        }
        return sqls;
    }

    protected String meterString(MeterSnapshot m) {
        long count = m.count;
        String rateUnit = getRateUnit();
        double meanRate = convertRate(m.meanRate);
        double oneMR = convertRate(m.oneMR);
        double fiveMR = convertRate(m.fiveMR);
        double fifteenMR = convertRate(m.fifteenMR);
        String str = String
                .format(locale, "count[%d], mr[%2.2f], 1mr[%2.2f], 5mr[%2.2f], 15mr[%2.2f] e/%s", count, meanRate, oneMR, fiveMR, fifteenMR, rateUnit);
        return str;
    }

    public static class HistogramSnapshot {
        final long count;
        final long min;
        final long max;
        final double mean;
        final double stdDev;
        final double median;
        final double p75;
        final double p95;
        final double p98;
        final double p99;
        final double p999;

        public HistogramSnapshot(Histogram h) {
            this.count = h.getCount();
            Snapshot s = h.getSnapshot();
            this.min = s.getMin();
            this.max = s.getMax();
            this.mean = s.getMean();
            this.stdDev = s.getStdDev();
            this.median = s.getMedian();
            this.p75 = s.get75thPercentile();
            this.p95 = s.get95thPercentile();
            this.p98 = s.get98thPercentile();
            this.p99 = s.get999thPercentile();
            this.p999 = s.get999thPercentile();
        }

    }

    private List<String> generateHistogramSQLs(Map<String, HistogramSnapshot> hs, String dbName, String datetime, String instance) {
        String template = "insert into %s.metrichistogram(dt,instance,name,count,min,max,mean,stddev,median,p75,p95,p98,p99,p999) values('%s','%s','%s',%d,%d,%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f)";
        List<String> sqls = new ArrayList<>();
        for (Entry<String, HistogramSnapshot> e : hs.entrySet()) {
            String name = e.getKey();
            HistogramSnapshot h = e.getValue();
            long count = h.count;
            long min = h.min;
            long max = h.max;
            double mean = h.mean;
            double stddev = h.stdDev;
            double median = h.median;
            double p75 = h.p75;
            double p95 = h.p95;
            double p98 = h.p98;
            double p99 = h.p99;
            double p999 = h.p999;
            String sql = String.format(template, dbName, datetime, instance, name, count, min, max, mean, stddev, median, p75, p95, p98, p99, p999);
            sqls.add(sql);
        }
        return sqls;
    }

    protected String histogramString(HistogramSnapshot hs) {
        long count = hs.count;
        long min = hs.min;
        long max = hs.max;
        double mean = hs.mean;
        double stdDev = hs.stdDev;
        double median = hs.median;
        double p75 = hs.p75;
        double p95 = hs.p95;
        double p98 = hs.p98;
        double p99 = hs.p99;
        double p999 = hs.p999;
        String str = String.format(locale,
                "count[%d],min[%d],max[%d],mean[%2.2f],stddev[%2.2f],median[%2.2f], 75%%<=%2.2f, 95%%<=%2.2f, 98%%<=%2.2f, 99%%<=%2.2f, 99.9%%<=%2.2f", count,
                min, max, mean, stdDev, median, p75, p95, p98, p99, p999);
        return str;
    }

    public static class TimerSnapshot {
        final long count;
        final double meanRate;
        final double m1r;
        final double m5r;
        final double m15r;
        final double min;
        final double max;
        final double mean;
        final double stddev;
        final double median;
        final double p75;
        final double p95;
        final double p98;
        final double p99;
        final double p999;

        public TimerSnapshot(Timer timer) {
            this.count = timer.getCount();

            this.meanRate = timer.getMeanRate();
            this.m1r = timer.getOneMinuteRate();
            this.m5r = timer.getFiveMinuteRate();
            this.m15r = timer.getFifteenMinuteRate();

            Snapshot s = timer.getSnapshot();
            this.min = s.getMin();
            this.max = s.getMax();
            this.mean = s.getMean();
            this.stddev = s.getStdDev();
            this.median = s.getMedian();
            this.p75 = s.get75thPercentile();
            this.p95 = s.get95thPercentile();
            this.p98 = s.get98thPercentile();
            this.p99 = s.get999thPercentile();
            this.p999 = s.get999thPercentile();
        }
    }

    private List<String> generateTimerSQLs(Map<String, TimerSnapshot> timers, String dbName, String datetime, String instance) {
        String template = "insert into %s.metrictimer(dt,instance,name,count,mr,m1r,m5r,m15r,min,max,mean,stddev,median,p75,p95,p98,p99,p999) values('%s','%s','%s',%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f)";
        List<String> sqls = new ArrayList<>();
        for (Entry<String, TimerSnapshot> e : timers.entrySet()) {
            String name = e.getKey();
            TimerSnapshot ts = e.getValue();
            long count = ts.count;
            double mr = convertRate(ts.meanRate);
            double m1r = convertRate(ts.m1r);
            double m5r = convertRate(ts.m5r);
            double m15r = convertRate(ts.m15r);
            double min = convertDuration(ts.min);
            double max = convertDuration(ts.max);
            double mean = convertDuration(ts.mean);
            double stddev = convertDuration(ts.stddev);
            double median = convertDuration(ts.median);
            double p75 = convertDuration(ts.p75);
            double p95 = convertDuration(ts.p95);
            double p98 = convertDuration(ts.p98);
            double p99 = convertDuration(ts.p99);
            double p999 = convertDuration(ts.p999);
            String sql = String
                    .format(template, dbName, datetime, instance, name, count, mr, m1r, m5r, m15r, min, max, mean, stddev, median, p75, p95, p98, p99, p999);
            sqls.add(sql);
        }
        return sqls;
    }

    protected String timerString(TimerSnapshot ts) {
        long count = ts.count;

        String ru = getRateUnit();
        double meanRate = convertRate(ts.meanRate);
        double m1r = convertRate(ts.m1r);
        double m5r = convertRate(ts.m5r);
        double m15r = convertRate(ts.m15r);

        String du = getDurationUnit();
        double min = convertDuration(ts.min);
        double max = convertDuration(ts.max);
        double mean = convertDuration(ts.mean);
        double stddev = convertDuration(ts.stddev);
        double median = convertDuration(ts.median);
        double p75 = convertDuration(ts.p75);
        double p95 = convertDuration(ts.p95);
        double p98 = convertDuration(ts.p98);
        double p99 = convertDuration(ts.p99);
        double p999 = convertDuration(ts.p999);
        return String.format(locale,
                "count[%d],meanRate[%2.2f] 1mr[%2.2f] 5mr[%2.2f] 15mr[%2.2f] calls/%s, min[%2.2f] max[%2.2f] mean[%2.2f] stddev[%2.2f] median[%2.2f] %s, 75%%<=%2.2f 95%%<=%2.2f 98%%<=%2.2f 99%%<=%2.2f 99.9%%<%2.2f %s",
                count, meanRate, m1r, m5r, m15r, ru, min, max, mean, stddev, median, du, p75, p95, p98, p99, p999, du);
    }
    //  private String generateMeter(Meter meter) {
    //  long count = meter.getCount();
    //  String rateUnit = getRateUnit();
    //  double meanRate = convertRate(meter.getMeanRate());
    //  double oneMR = convertRate(meter.getOneMinuteRate());
    //  double fiveMR = convertRate(meter.getFiveMinuteRate());
    //  double fifteenMR = convertRate(meter.getFifteenMinuteRate());
    //  String str = String.format(locale, "count[%d], mr[%2.2f], 1mr[%2.2f], 5mr[%2.2f], 15mr[%2.2f] e/%s", count, meanRate, oneMR, fiveMR, fifteenMR,
    //          rateUnit);
    //  return str;
    //}

    //    private void printMeter(Meter meter) {
    //        printIfEnabled(MetricAttribute.COUNT, String.format(locale, "             count = %d", meter.getCount()));
    //        printIfEnabled(MetricAttribute.MEAN_RATE,
    //                String.format(locale, "         mean rate = %2.2f events/%s", convertRate(meter.getMeanRate()), getRateUnit()));
    //        printIfEnabled(MetricAttribute.M1_RATE,
    //                String.format(locale, "     1-minute rate = %2.2f events/%s", convertRate(meter.getOneMinuteRate()), getRateUnit()));
    //        printIfEnabled(MetricAttribute.M5_RATE,
    //                String.format(locale, "     5-minute rate = %2.2f events/%s", convertRate(meter.getFiveMinuteRate()), getRateUnit()));
    //        printIfEnabled(MetricAttribute.M15_RATE,
    //                String.format(locale, "    15-minute rate = %2.2f events/%s", convertRate(meter.getFifteenMinuteRate()), getRateUnit()));
    //    }
    //
    //    private void printCounter(Map.Entry<String, Counter> entry) {
    //        output.printf(locale, "             count = %d%n", entry.getValue().getCount());
    //    }
    //
    //    private void printGauge(Map.Entry<String, Gauge> entry) {
    //        output.printf(locale, "             value = %s%n", entry.getValue().getValue());
    //    }
    //    private String generateHistogram(Histogram histogram) {
    //        long count = histogram.getCount();
    //        Snapshot snapshot = histogram.getSnapshot();
    //        long min = snapshot.getMin();
    //        long max = snapshot.getMax();
    //        double mean = snapshot.getMean();
    //        double stdDev = snapshot.getStdDev();
    //        double median = snapshot.getMedian();
    //        double p75 = snapshot.get75thPercentile();
    //        double p95 = snapshot.get95thPercentile();
    //        double p98 = snapshot.get98thPercentile();
    //        double p99 = snapshot.get99thPercentile();
    //        double p999 = snapshot.get999thPercentile();
    //        String str = String.format(locale,
    //                "count[%d],min[%d],max[%d],mean[%2.2f],stddev[%2.2f],median[%2.2f], 75%%<=%2.2f, 95%%<=%2.2f, 98%%<=%2.2f, 99%%<=%2.2f, 99.9%%<=%2.2f", count,
    //                min, max, mean, stdDev, median, p75, p95, p98, p99, p999);
    //        return str;
    //    }

    //    private void printHistogram(Histogram histogram) {
    //        printIfEnabled(MetricAttribute.COUNT, String.format(locale, "             count = %d", histogram.getCount()));
    //        Snapshot snapshot = histogram.getSnapshot();
    //        printIfEnabled(MetricAttribute.MIN, String.format(locale, "               min = %d", snapshot.getMin()));
    //        printIfEnabled(MetricAttribute.MAX, String.format(locale, "               max = %d", snapshot.getMax()));
    //        printIfEnabled(MetricAttribute.MEAN, String.format(locale, "              mean = %2.2f", snapshot.getMean()));
    //        printIfEnabled(MetricAttribute.STDDEV, String.format(locale, "            stddev = %2.2f", snapshot.getStdDev()));
    //        printIfEnabled(MetricAttribute.P50, String.format(locale, "            median = %2.2f", snapshot.getMedian()));
    //        printIfEnabled(MetricAttribute.P75, String.format(locale, "              75%% <= %2.2f", snapshot.get75thPercentile()));
    //        printIfEnabled(MetricAttribute.P95, String.format(locale, "              95%% <= %2.2f", snapshot.get95thPercentile()));
    //        printIfEnabled(MetricAttribute.P98, String.format(locale, "              98%% <= %2.2f", snapshot.get98thPercentile()));
    //        printIfEnabled(MetricAttribute.P99, String.format(locale, "              99%% <= %2.2f", snapshot.get99thPercentile()));
    //        printIfEnabled(MetricAttribute.P999, String.format(locale, "            99.9%% <= %2.2f", snapshot.get999thPercentile()));
    //    }
    //    private String generateTimer(Timer timer) {
    //        final Snapshot snapshot = timer.getSnapshot();
    //        long count = timer.getCount();
    //        String ru = getRateUnit();
    //        double meanRate = convertRate(timer.getMeanRate());
    //        double m1r = convertRate(timer.getOneMinuteRate());
    //        double m5r = convertRate(timer.getFiveMinuteRate());
    //        double m15r = convertRate(timer.getFifteenMinuteRate());
    //        String du = getDurationUnit();
    //        double min = convertDuration(snapshot.getMin());
    //        double max = convertDuration(snapshot.getMax());
    //        double mean = convertDuration(snapshot.getMean());
    //        double stddev = convertDuration(snapshot.getStdDev());
    //        double median = convertDuration(snapshot.getMedian());
    //
    //        double p75 = convertDuration(snapshot.get75thPercentile());
    //        double p95 = convertDuration(snapshot.get95thPercentile());
    //        double p98 = convertDuration(snapshot.get98thPercentile());
    //        double p99 = convertDuration(snapshot.get99thPercentile());
    //        double p999 = convertDuration(snapshot.get999thPercentile());
    //        return String
    //                .format(locale,
    //                        "count[%d],meanRate[%2.2f] 1mr[%2.2f] 5mr[%2.2f] 15mr[%2.2f] calls/%s, min[%2.2f] max[%2.2f] mean[%2.2f] stddev[%2.2f] median[%2.2f] %s, 75%%<=%2.2f 95%%<=%2.2f 98%%<=%2.2f 99%%<=%2.2f 99.9%%<%2.2f %s",
    //                        count, meanRate, m1r, m5r, m15r, ru, min, max, mean, stddev, median, du, p75, p95, p98, p99, p999, du);
    //    }
    //
    //    private void printTimer(Timer timer) {
    //        final Snapshot snapshot = timer.getSnapshot();
    //        printIfEnabled(MetricAttribute.COUNT, String.format(locale, "             count = %d", timer.getCount()));
    //        printIfEnabled(MetricAttribute.MEAN_RATE, String.format(locale, "         mean rate = %2.2f calls/%s", convertRate(timer.getMeanRate()), getRateUnit()));
    //        printIfEnabled(MetricAttribute.M1_RATE,
    //                String.format(locale, "     1-minute rate = %2.2f calls/%s", convertRate(timer.getOneMinuteRate()), getRateUnit()));
    //        printIfEnabled(MetricAttribute.M5_RATE,
    //                String.format(locale, "     5-minute rate = %2.2f calls/%s", convertRate(timer.getFiveMinuteRate()), getRateUnit()));
    //        printIfEnabled(MetricAttribute.M15_RATE,
    //                String.format(locale, "    15-minute rate = %2.2f calls/%s", convertRate(timer.getFifteenMinuteRate()), getRateUnit()));
    //
    //        printIfEnabled(MetricAttribute.MIN, String.format(locale, "               min = %2.2f %s", convertDuration(snapshot.getMin()), getDurationUnit()));
    //        printIfEnabled(MetricAttribute.MAX, String.format(locale, "               max = %2.2f %s", convertDuration(snapshot.getMax()), getDurationUnit()));
    //        printIfEnabled(MetricAttribute.MEAN, String.format(locale, "              mean = %2.2f %s", convertDuration(snapshot.getMean()), getDurationUnit()));
    //        printIfEnabled(MetricAttribute.STDDEV, String.format(locale, "            stddev = %2.2f %s", convertDuration(snapshot.getStdDev()), getDurationUnit()));
    //        printIfEnabled(MetricAttribute.P50, String.format(locale, "            median = %2.2f %s", convertDuration(snapshot.getMedian()), getDurationUnit()));
    //        printIfEnabled(MetricAttribute.P75,
    //                String.format(locale, "              75%% <= %2.2f %s", convertDuration(snapshot.get75thPercentile()), getDurationUnit()));
    //        printIfEnabled(MetricAttribute.P95,
    //                String.format(locale, "              95%% <= %2.2f %s", convertDuration(snapshot.get95thPercentile()), getDurationUnit()));
    //        printIfEnabled(MetricAttribute.P98,
    //                String.format(locale, "              98%% <= %2.2f %s", convertDuration(snapshot.get98thPercentile()), getDurationUnit()));
    //        printIfEnabled(MetricAttribute.P99,
    //                String.format(locale, "              99%% <= %2.2f %s", convertDuration(snapshot.get99thPercentile()), getDurationUnit()));
    //        printIfEnabled(MetricAttribute.P999,
    //                String.format(locale, "            99.9%% <= %2.2f %s", convertDuration(snapshot.get999thPercentile()), getDurationUnit()));
    //    }

    //    private void printWithBanner(String s, char c) {
    //        output.print(s);
    //        output.print(' ');
    //        for (int i = 0; i < (CONSOLE_WIDTH - s.length() - 1); i++) {
    //            output.print(c);
    //        }
    //        output.println();
    //    }

    /**
     * Print only if the attribute is enabled
     * @param type Metric attribute
     * @param status Status to be logged
     */
    //    private void printIfEnabled(MetricAttribute type, String status) {
    //        if (getDisabledMetricAttributes().contains(type)) {
    //            return;
    //        }
    //
    //        //        output.println(status);
    //        LOG.info(status);
    //    }
}