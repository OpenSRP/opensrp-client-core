package org.smartregister.dao;

import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.Repository;

import java.util.Date;
import java.util.List;

public class SampleAbstractDaoImp extends AbstractDao {

    public static void setRepository(Repository repository) {
        AbstractDao.setRepository(repository);
    }

    public static int getCountOfEvents() {
        String sql = "select count(*) as count from event";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        return readSingleValue(sql, dataMap, 0);
    }

    public static List<Alert> getAllAlerts() {
        String sql = "select * from alerts order by startDate asc";

        DataMap<Alert> dataMap = c -> new Alert(
                getCursorValue(c, AlertRepository.ALERTS_CASEID_COLUMN),
                getCursorValue(c, AlertRepository.ALERTS_SCHEDULE_NAME_COLUMN),
                getCursorValue(c, AlertRepository.ALERTS_VISIT_CODE_COLUMN),
                AlertStatus.from(getCursorValue(c, AlertRepository.ALERTS_STATUS_COLUMN)),
                getCursorValue(c, AlertRepository.ALERTS_STARTDATE_COLUMN),
                getCursorValue(c, AlertRepository.ALERTS_EXPIRYDATE_COLUMN),
                c.getInt(c.getColumnIndex(AlertRepository.ALERTS_OFFLINE_COLUMN)) == 1)
                .withCompletionDate(
                        getCursorValue(c, AlertRepository.ALERTS_COMPLETIONDATE_COLUMN));


        return AbstractDao.readData(sql, dataMap);
    }

    public static List<SampleObject> getSamples() {
        String sql = "select * from alerts order by startDate asc";

        DataMap<SampleObject> dataMap = c -> {
            SampleObject sampleObject = new SampleObject();

            sampleObject.setSampleInt(getCursorIntValue(c, "int", 1));
            sampleObject.setSampleDate(getCursorValueAsDate(c, "date", getDobDateFormat()));
            sampleObject.setSampleLong(getCursorLongValue(c, "long"));
            sampleObject.setSampleString(getCursorValue(c, "string"));
            sampleObject.setSampleLongDate(getCursorValueAsDate(c, "sample_long_date"));
            sampleObject.setSampleStringWithDefault(getCursorValue(c, "sample_string_value", "default"));

            return sampleObject;
        };


        return AbstractDao.readData(sql, dataMap);
    }

    public static class SampleObject {
        private Integer sampleInt;
        private Date sampleDate;
        private Date sampleLongDate;
        private long sampleLong;
        private String sampleString;
        private String sampleStringWithDefault;

        public Integer getSampleInt() {
            return sampleInt;
        }

        public void setSampleInt(Integer sampleInt) {
            this.sampleInt = sampleInt;
        }

        public Date getSampleDate() {
            return sampleDate;
        }

        public void setSampleDate(Date sampleDate) {
            this.sampleDate = sampleDate;
        }

        public Date getSampleLongDate() {
            return sampleLongDate;
        }

        public void setSampleLongDate(Date sampleLongDate) {
            this.sampleLongDate = sampleLongDate;
        }

        public long getSampleLong() {
            return sampleLong;
        }

        public void setSampleLong(long sampleLong) {
            this.sampleLong = sampleLong;
        }

        public String getSampleString() {
            return sampleString;
        }

        public void setSampleString(String sampleString) {
            this.sampleString = sampleString;
        }

        public String getSampleStringWithDefault() {
            return sampleStringWithDefault;
        }

        public void setSampleStringWithDefault(String sampleStringWithDefault) {
            this.sampleStringWithDefault = sampleStringWithDefault;
        }
    }

}
