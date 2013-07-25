package org.openiot.gsn.storage.hibernate;

public class DBConnectionInfo {
        private String driverClass, url, userName, password;

        public DBConnectionInfo(String driverClass, String url, String userName, String password) {
            this.driverClass = driverClass;
            this.url = url;
            this.userName = userName;
            this.password = password;
        }

        //public BasicDataSource createDataSource() {
        //    return createDataSource(25, 5, -1); //TODO: tune parameters
        //}

        //public BasicDataSource createDataSource(int maxActive, int maxIde, int maxWait) {
        //    return createDataSource(maxActive, maxIde, maxWait, 1000 * 60 * 30, -1);
        //}

        /**
         * //@param maxActive                     The maximum number of active connections that can be allocated from this pool at the same time, or negative for no limit. (def: 8)
         * //@param maxIde                        The maximum number of connections that can remain idle in the pool, without extra ones being released, or negative for no limit. (def: 8)
         * //@param maxWait                       The maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection to be returned before throwing an exception, or -1 to wait indefinitely. (def:indefinitely)
         * //@param minEvictableIdleTimeMillis    The minimum amount of time an object may sit idle in the pool before it is eligable for eviction by the idle object evictor (if any). (def: 1000 * 60 * 30)
         * //@param timeBetweenEvictionRunsMillis The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle object evictor thread will be run. (def: -1)
         * //@return the configured BasicDataSource
         */
        /*
        public BasicDataSource createDataSource(int maxActive, int maxIde, int maxWait, long minEvictableIdleTimeMillis, long timeBetweenEvictionRunsMillis) {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(driverClass);
            ds.setUsername(userName);
            ds.setPassword(password);
            ds.setUrl(url);

            ds.setMaxActive(maxActive);
            ds.setMaxIdle(maxIde);
            ds.setMaxWait(maxWait);
            ds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
            ds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            return ds;
        }
        */

        public boolean equals(Object o) {
            if (null == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DBConnectionInfo that = (DBConnectionInfo) o;

            if (driverClass != null ? !driverClass.equals(that.driverClass) : that.driverClass != null) return false;
            if (password != null ? !password.equals(that.password) : that.password != null) return false;
            if (url != null ? !url.equals(that.url) : that.url != null) return false;
            if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;

            return true;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public String getUrl() {
            return url;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public int hashCode() {
            int result = driverClass != null ? driverClass.hashCode() : 0;
            result = 31 * result + (url != null ? url.hashCode() : 0);
            result = 31 * result + (userName != null ? userName.hashCode() : 0);
            result = 31 * result + (password != null ? password.hashCode() : 0);
            return Math.abs(result);
        }

    @Override
    public String toString() {
        return "DBConnectionInfo{" +
                "driverClass='" + driverClass + '\'' +
                ", url='" + url + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}