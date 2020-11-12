import com.typesafe.config.ConfigFactory
import org.h2.jdbcx.JdbcDataSource
import javax.sql.DataSource


class KuadroDatasource {
    companion object {
        var instance: DataSource? = null
            get() {
                if (field == null) {
                    val ds = JdbcDataSource()
                    ds.setURL(ConfigFactory.load().getString("datasource.url"))
                    ds.user = (ConfigFactory.load().getString("datasource.user"))
                    ds.password = (ConfigFactory.load().getString("datasource.password"))

                    field = ds
                }
                return field
            }
    }
}
