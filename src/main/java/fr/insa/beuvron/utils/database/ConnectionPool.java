/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.beuvron.utils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Définition d'une "connecion pool" à l'aide de la librairie HikariCP.
 * <pre>
 * repris de https://www.baeldung.com/hikaricp.
 * Voir https://github.com/brettwooldridge/hikaricp/wiki/MYSQL-Configuration pour
 * une explication de certains paramètres
 * </pre>
 *
 * @author francois
 */
public class ConnectionPool {

    /**
     * permet de selectionner le SGBD utilisé
     */
    public static SGBDPossibles SGBD = SGBDPossibles.H2MEMORY;

    // à modifier
    public static String LOGIN_SUR_SERVEUR_M3 = "m3_fdebertranddeb01";
    public static String PASS_SUR_SERVEUR_M3 = "donnez le votre";
    
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    // un bloc static directement dans une classe est exécuté au chargement
    // de la classe
    // pour une BdD en mémoire en utilisant le sgbd H2
    static {
        if (SGBD == SGBDPossibles.MYSQL) {
            initForMysql();
        } else if (SGBD == SGBDPossibles.H2FILE) {
            initForH2InFile("./h2BdD/bdd");
        } else if (SGBD == SGBDPossibles.H2MEMORY) {
            initForH2Mem();
        } else {
            throw new Error("SGBD non couvert par ConnectionSimpleSGBD");
        }
        
    }

    /**
     * pour une BdD en utilisant le sgbd mysql pour module M3
     */
    private static void initForMysql() {
        Class dmysql = com.mysql.cj.jdbc.Driver.class;
        Logger.getGlobal().log(Level.INFO, "Driver mysql {0} found", dmysql.getCanonicalName());
        config.setJdbcUrl("jdbc:mysql://92.222.25.165:3306/" + LOGIN_SUR_SERVEUR_M3);
        config.setUsername(LOGIN_SUR_SERVEUR_M3);
        config.setPassword(PASS_SUR_SERVEUR_M3);
        config.setMaximumPoolSize(3);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");
        ds = new HikariDataSource(config);
    }

    /**
     * pour une BdD en utilisant le sgbd h2 avec base de donnée en mémoire
     */
    private static void initForH2Mem() {
        Class dh2 = org.h2.Driver.class;
        Logger.getGlobal().log(Level.INFO, "Driver h2 {0} found", dh2.getCanonicalName());
        config.setJdbcUrl("jdbc:h2:mem:h2BdD");
        // peut être pas indispensable, mais dans le doute...
        config.setUsername("H2User");
        config.setPassword("H2Pass");
        config.setMaximumPoolSize(3);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");
        ds = new HikariDataSource(config);
    }

    /**
     * pour une BdD en utilisant le sgbd h2 avec base de donnée en mémoire
     */
    
    private static void initForH2InFile(String path) {
        Class dh2 = org.h2.Driver.class;
        Logger.getGlobal().log(Level.INFO, "Driver h2 {0} found", dh2.getCanonicalName());
        config.setJdbcUrl("jdbc:h2:file:" + path);
        // peut être pas indispensable, mais dans le doute...
        config.setUsername("H2User");
        config.setPassword("H2Pass");
        config.setMaximumPoolSize(3);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
